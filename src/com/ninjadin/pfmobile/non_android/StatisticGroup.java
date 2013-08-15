package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class StatisticGroup extends Conditional {
	public String type;

	StatisticGroup parent = null;
	private Map<String,StatisticInstance> statistics = new HashMap<String,StatisticInstance>();
	
	public StatisticGroup(StatisticGroup group_parent) {
		parent = group_parent;
	}
	
	public List<String> getKeyList() {
		List<String> key_list = new ArrayList<String>();
		for (Map.Entry<String, StatisticInstance> entry: statistics.entrySet()) {
			key_list.add(entry.getKey());
		}
		return key_list;
	}
	
	public Bonus addBonus(String stat_name, String stack_type, String source, String val) {
		StatisticInstance bonusRecipient = statistics.get(stat_name);
		if (bonusRecipient == null) {// Not an existing target
			bonusRecipient = new StatisticInstance();
			statistics.put(stat_name, bonusRecipient);
		}
		Bonus newBonus = new Bonus(stack_type, source, val);
		// Add the bonus to the statistic
		bonusRecipient.addBonus(newBonus);
		for (String stat: getKeyList()) {
			statistics.get(stat).isDirty = true;
		}
		return newBonus;
	}
	
	public int getValue(String stat_name) {
		return evaluate(getStringValue(stat_name));
	}
	
	public String getStringValue(String stat_name) {
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
}
