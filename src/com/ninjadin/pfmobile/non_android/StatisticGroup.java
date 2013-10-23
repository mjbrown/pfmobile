package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;

import android.util.Log;

public class StatisticGroup extends Conditional {
	private Boolean isDirty = true;
	public String type;
	// A list of all Conditionals (no unconditional bonuses)
	private List<Conditional> conditional_bonuses = new ArrayList<Conditional>();
	// A map of conditions
	private Map<String,Map<String,Conditional>> conditions = new HashMap<String,Map<String,Conditional>>();;

	StatisticGroup parent = null;
	private Map<String,StatisticInstance> statistics = new HashMap<String,StatisticInstance>();
	
	public StatisticGroup(StatisticGroup group_parent) {
		parent = group_parent;
	}
	
	public void addConditionalBonus(Conditional cond) {
		conditional_bonuses.add(cond);
		isDirty = true;
	}
	
	public Conditional activateCondition(String key, String name, ConditionList currentConditions) {
		Map<String,Conditional> condition_map = conditions.get(key);
		if (condition_map == null) {
			condition_map = new HashMap<String,Conditional>();
			conditions.put(key, condition_map);
		}
		Conditional property = condition_map.get(name);
		if (property == null) {
			property = new Conditional();
			condition_map.put(name, property);
			if (currentConditions.hasConditions()) {
				property.setConditions(currentConditions);
				conditional_bonuses.add(property);
			}
		}
		isDirty = true;
		return property;
	}
	
	public void deactivateCondition(String key, String name) {
		Map<String,Conditional> condition_map = conditions.get(key);
		if (condition_map != null) {
			Conditional property = condition_map.get(name);
			if (property != null) {
				condition_map.remove(name);
	//			updateConditionalBonuses(1);
			}
		}
		isDirty = true;
	}
	
	public List<String> getKeyList() {
		List<String> key_list = new ArrayList<String>();
		for (Map.Entry<String, StatisticInstance> entry: statistics.entrySet()) {
			key_list.add(entry.getKey());
		}
		return key_list;
	}
	
	public Bonus addBonus(String stat_name, String stack_type, String source, String val, ConditionList currentConditions) {
		StatisticInstance bonusRecipient = statistics.get(stat_name);
		if (bonusRecipient == null) {// Not an existing target
			bonusRecipient = new StatisticInstance();
			statistics.put(stat_name, bonusRecipient);
		}
		Bonus newBonus = new Bonus(stack_type, source, val);
		// Add the bonus to the statistic
		bonusRecipient.addBonus(newBonus);
		if (currentConditions.hasConditions()) {
			newBonus.setConditions(currentConditions);
			conditional_bonuses.add(newBonus);
			updateConditionalBonus(newBonus);
		}
		for (String stat: getKeyList()) {
			statistics.get(stat).isDirty = true;
		}
		return newBonus;
	}
	
	public int getValue(String stat_name) {
		if (isDirty)
			updateConditionalBonuses(3);
		return evaluate(getStringValue(stat_name));
	}
	
	public String getStringValue(String stat_name) {
		if (isDirty)
			updateConditionalBonuses(3);
		StatisticInstance stat = statistics.get(stat_name);
		if (stat == null) 
			return "0";
		String ret_string = "";
		String parent_value = "";
		String stat_string = "";
		if (parent != null) {
			parent_value = parent.getStringValue(stat_name);
		}
		stat_string = stat.getFinalStringValue();
		if (parent_value.equals("0") || stat_string.equals("0")) {
			if (!parent_value.equals("0"))
				ret_string = parent_value;
			else
				ret_string = stat_string;
		} else {
			ret_string = parent_value + " + " + stat_string;
		}
		return ret_string;
	}
	
	public int evaluate(String value) {
		if (isDirty)
			updateConditionalBonuses(3);
		if (parent != null) {
			return parent.evaluate(value);
		}
		String strippedValue = new String(value);
		for (String operand: getKeyList()) {
			if (value.contains("[" + operand + "]")) {
				Integer op = getValue(operand);
				if (op != null) {
					strippedValue = strippedValue.replace("[" + operand + "]", Integer.toString(op));
				}
			}
		}
		return recursiveEvaluate(strippedValue);
	}
	
	private int recursiveEvaluate(String expression) {
		String[] tokens = expression.split(" ");
		int value = 0;
		int next_value = 0;
		String lastOperator = "+";
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("(")) {
				String subExpression = "";
				i++;
				int depth = 0;
				while (!tokens[i].equals(")") || (depth > 0)) {
					if (tokens[i].equals("(") || tokens[i].equals("min(") || tokens[i].equals("max("))
						depth += 1;
					if (tokens[i].equals(")"))
						depth -= 1;
					subExpression += tokens[i] + " ";
					i++;
					if (i >= tokens.length) 
						Log.d("RecursiveEval", "Malformed parenteses in expression: " + expression);
				}
				next_value = recursiveEvaluate(subExpression);
			} else if ((tokens[i].equals("min(")) || (tokens[i].equals("max("))) {
				String operation = tokens[i];
				String leftExpression = "";
				i++;
				int depth = 0;
				while (!tokens[i].equals(",") || (depth > 0)) {
					if (tokens[i].equals("(") || tokens[i].equals("min(") || tokens[i].equals("max("))
						depth += 1;
					if (tokens[i].equals(")"))
						depth -= 1;
					leftExpression += " " + tokens[i];
					i++;
					if (i >= tokens.length) 
						Log.d("RecursiveEval2", "Malformed parenteses in expression: " + expression);
				}
				String rightExpression = "";
				depth = 0;
				while (!tokens[i].equals(")") || depth > 0) {
					if (tokens[i].equals("(") || tokens[i].equals("min(") || tokens[i].equals("max("))
						depth += 1;
					if (tokens[i].equals(")"))
						depth -= 1;
					rightExpression += " " + tokens[i];
					i++;
					if (i >= tokens.length) 
						Log.d("RecursiveEval3", "Malformed parenteses in expression: " + expression);
				}
				int left_value = recursiveEvaluate(leftExpression);
				int right_value = recursiveEvaluate(rightExpression);
				if ((left_value > right_value) ^ (operation.equals("min("))) {
					next_value = left_value;
				} else {
					next_value = right_value;
				}
			} else {
				try {
					next_value = Integer.parseInt(tokens[i]);
				} catch (NumberFormatException e) {
					if ((tokens[i].equals("+")) || (tokens[i].equals("-")) || (tokens[i].equals("*")) || (tokens[i].equals("/")))
						lastOperator = tokens[i];
					continue;
				}
			}
			if (lastOperator.equals("+")) {
				value += next_value;
			} else if (lastOperator.equals("-")) {
				value -= next_value;
			} else if (lastOperator.equals("/")) {
				value /= next_value;
			} else if (lastOperator.equals("*")) {
				value *= next_value;
			}
		}
		return value;
	}
	
	public boolean hasProperty(String key, String name) {
		if (isDirty)
			updateConditionalBonuses(3);
		if (parent != null)
			return parent.hasProperty(key, name);
		Map<String, Conditional> key_group = conditions.get(key);
		if (key_group != null) {
			Conditional property = key_group.get(name);
			if (property != null) {
				return property.isActive();
			}
		}
		return false;
	}
	
	private boolean hasCondition(String key, KeyValuePair kv) {
		if (kv.value == null) {
			if (kv.logic == null) { // OR assumed
				for (String property: kv.key.split(",")) {
					if (hasProperty(key, property)) {
						return true;
					}
				}
				return false;
			} else if (kv.logic.equals("NAND")) {
				for (String property: kv.key.split(",")) {
					if (!hasProperty(key, property)) {
						return true;
					}
				}
				return false;
			} else if (kv.logic.equals("NOR")) {
				for (String property: kv.key.split(",")) {
					if (hasProperty(key, property)) {
						return false;
					}
				}
				return true;
			} else if (kv.logic.equals("AND")) {
				for (String property: kv.key.split(",")) {
					if (!hasProperty(key, property)) {
						return false;
					}
				}
				return true;
			}
		} else {
			String[] keys = kv.key.split(",");
			String[] values = kv.value.split(",");
			if (keys.length != values.length) {
				Log.d("hasCondition", "Key Value size mismatch " + kv.key + " " + kv.value);
				return false;
			}
			if (kv.logic == null) { // OR greater than or equal to assumed
				for (int i = 0; i < keys.length; i++) {
					if (getValue(keys[i]) >= evaluate(values[i]))
						return true;
				}
			} else if (kv.logic.equals("OR_EQ")) {
				for (int i = 0; i < keys.length; i++) {
					if (getValue(keys[i]) != evaluate(values[i]))
						return false;
				}
				return true;
			}
		}
		return false;
	}

	private void updateConditionalBonuses(int retries) {
		isDirty = false;
		if (retries == 0)
			return;
		Log.d("RECUR_UPDATE", Integer.toString(retries));
		Boolean run_again = false;
		for (Conditional bonus: conditional_bonuses) {
			Boolean already_active = bonus.isActive();
			updateConditionalBonus(bonus);
			if (already_active ^ bonus.isActive()) {
				run_again = true;
			}
		}
		if (run_again)
			updateConditionalBonuses(retries - 1);
	}
	
	private void updateConditionalBonus(Conditional bonus) {
		if (checkConditions(bonus.getConditions())) {
			bonus.meetsConditions();
		} else {
			bonus.failsConditions();
			//Log.d("Condition", "Condition not met:" + bonus.getStringValue());
		}
	}
	
	private boolean checkConditions(ConditionList conditions) {
		for (String key: PropertyLists.keyNames) {
			if (conditions == null) // If no conditions are set, then conditions are met
				break;
			for (KeyValuePair kv: conditions.getConditionList(key)) {
				if (!hasCondition(key, kv)) {
					return false;
				}
			}
		}
		return true;
	}

}
