package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class StatisticManager {
	// A map of all CharacterStatistics
	private Map<String, StatisticInstance> statMap;
	// A list of all CharacterStatistics names
	public List<String> stat_list;
	// A list of all Conditional Bonuses (no unconditional bonuses)
	private List<ConditionalBonus> conditional_bonuses;
	// A map of equipment / environment conditions
	Map<String,Map<String,ConditionalBonus>> conditions = new HashMap<String,Map<String,ConditionalBonus>>();; 
	
	public StatisticManager() {
		for (String key: PropertyLists.keyNames) {
			Map<String,ConditionalBonus> condition_map = new HashMap<String,ConditionalBonus>();
			conditions.put(key, condition_map);
		}
		conditional_bonuses = new ArrayList<ConditionalBonus>();
		statMap = new LinkedHashMap<String, StatisticInstance>();
		stat_list = new ArrayList<String>();
		for (String abilityScoreName: PropertyLists.abilityScoreNames) {
			StatisticInstance newStat = new StatisticInstance(abilityScoreName);
			statMap.put(abilityScoreName, newStat);
		}
		for (String abilityModifierName: PropertyLists.abilityModifierNames) {
			StatisticInstance newStat = new StatisticInstance(abilityModifierName);
			statMap.put(abilityModifierName, newStat);
		}
		for (String saveName: PropertyLists.basicStatsNames) {
			StatisticInstance newStat = new StatisticInstance(saveName);
			statMap.put(saveName, newStat);
		}
		for (String otherStatisticName: PropertyLists.otherStatisticNames) {
			StatisticInstance newStat = new StatisticInstance(otherStatisticName);
			statMap.put(otherStatisticName, newStat);
		}
		for (String reductionName: PropertyLists.reductionNames) {
			StatisticInstance newStat = new StatisticInstance(reductionName);
			statMap.put(reductionName, newStat);
		}
		for (String speedName: PropertyLists.speedNames) {
			StatisticInstance newStat = new StatisticInstance(speedName);
			statMap.put(speedName, newStat);
		}
		for (String casterStatisticName: PropertyLists.casterLevelNames) {
			StatisticInstance newStat = new StatisticInstance(casterStatisticName);
			statMap.put(casterStatisticName, newStat);
		}
		for (String skillName: PropertyLists.skillNames) {
			StatisticInstance newStat = new StatisticInstance(skillName);
			statMap.put(skillName, newStat);
		}
		for (String classLevelName: PropertyLists.classLevelNames) {
			StatisticInstance newStat = new StatisticInstance(classLevelName);
			statMap.put(classLevelName, newStat);
		}
		for (String spellFailureName: PropertyLists.equipRelatedNames) {
			StatisticInstance newStat = new StatisticInstance(spellFailureName);
			statMap.put(spellFailureName, newStat);
		}
		for (Map.Entry<String, StatisticInstance> entry: statMap.entrySet()) {
			stat_list.add(entry.getKey());
		}
	}
	
	public ConditionalBonus newBonus(String statisticType, String stackType, String source, String value) {
		StatisticInstance bonusRecipient = statMap.get(statisticType);
		if (bonusRecipient == null) // Not a valid target
			return new ConditionalBonus();	 // return a conditional bonus but don't attach it to anythin
		ConditionalBonus newBonus = new ConditionalBonus(stackType, source, value);
		// Add the bonus to the statistic
		bonusRecipient.addBonus(newBonus);
		for (String stat: stat_list) {
			statMap.get(stat).isDirty = true;
		}
		return newBonus;
	}
	
	public int getValue(String statisticName) {
		StatisticInstance stat = statMap.get(statisticName);
		return evaluateValue(stat.getFinalStringValue());
	}
	
	public boolean hasProperty(String key, String name) {
		ConditionalBonus property = conditions.get(key).get(name);
		if (property != null) {
			return property.isActive();
		} else {
			return false;
		}
	}
	
	public int evaluateValue(String value) {
//		Log.d("PreStripped", value);
		String strippedValue = new String(value);
		for (String operand: stat_list) {
			if (value.contains("[" + operand + "]")) {
				Integer op = getValue(operand);
				if (op != null) {
					strippedValue = strippedValue.replace("[" + operand + "]", Integer.toString(op));
				}
			}
		}
//		Log.d("StrippedEval", strippedValue);
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
	
	public void readXMLBonuses(InputStream inStream, File inventory) throws IOException, XmlPullParserException {
		Stack<String> last_conditional = new Stack<String>();
		last_conditional.push("None.");
		Map<String,List<KeyValuePair>> bonus_conditions = new HashMap<String,List<KeyValuePair>>();
		for (String key: PropertyLists.keyNames) {
			List<KeyValuePair> name_list = new ArrayList<KeyValuePair>();
			bonus_conditions.put(key, name_list);
		}
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(inStream, null);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				String name = parser.getName();
				if (name != null) {
					if (name.equals(XmlConst.CONDITIONAL_TAG)) {
						String last = last_conditional.pop();
						List<KeyValuePair> last_list = bonus_conditions.get(last);
						if (last_list != null)
							last_list.remove(last_list.size()-1);
					}
				}
				continue;
			}
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals(XmlConst.CONDITIONAL_TAG)) {
					String key = parser.getAttributeValue(null, XmlConst.KEY_ATTR);
					String name = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					String type = parser.getAttributeValue(null, XmlConst.TYPE_ATTR);
					String value = parser.getAttributeValue(null, XmlConst.VALUE_ATTR);
					if (key != null) {
						last_conditional.push(key);
						List<KeyValuePair> condition_list = bonus_conditions.get(key); 
						if (condition_list != null) {
							if (name != null) {
								condition_list.add(new KeyValuePair(name, null));
							} else if ((type != null) && (value != null)) {
								condition_list.add(new KeyValuePair(type, value));
							} else {
								Log.d("ReadXML", "Conditional with key, without name or type/value:" + key);
							}
						} else {
							Log.d("ReadXML2", "Conditional for non-existent key: " + key);
						}
					} else {
						last_conditional.push("Invalid conditional key!");
						Log.d("ReadXML3", "Conditional with no key!");
					}
				} else if (tag.equals(XmlConst.BONUS_TAG)) {
					String types = parser.getAttributeValue(null, XmlConst.TYPE_ATTR);
					String stackType = parser.getAttributeValue(null, XmlConst.STACKTYPE_ATTR);
					String value = parser.getAttributeValue(null, XmlConst.VALUE_ATTR);
					if ((types != null) && (value != null)) {
						String source = parser.getAttributeValue(null, XmlConst.SOURCE_ATTR);
						if (stackType == null)
							stackType = "Base";
						if (source == null)
							source = "Natural";
						for (String type: types.split(",")) {
							ConditionalBonus conditionalBonus = newBonus(type, stackType, source, value);
							if (!last_conditional.peek().equals("None.")) {
								Log.d("COND_BNS", "Adding conditional bonus: " + type + " " + value);
								conditionalBonus.setConditions(bonus_conditions);
								conditional_bonuses.add(conditionalBonus);
								updateConditionalBonus(conditionalBonus);
							}
						}
					} else {
						Log.d("ReadXML4", "Bonus missing type/value!");
					}
				} else if (tag.equals(XmlConst.CHOICE_TAG) || (tag.equals(XmlConst.CHOSEN_TAG))) {
					String name = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					if (name != null) {
						conditions.get(PropertyLists.prerequisite).put(name, new ConditionalBonus());
						updateConditionalBonuses();
					}
				} else if (tag.equals(XmlConst.CONDITION_TAG)) {
					String key = parser.getAttributeValue(null, XmlConst.KEY_ATTR);
					String names = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					if ((key != null) && (names != null)) {
						Map<String, ConditionalBonus> condition_map = conditions.get(key);
						if (condition_map != null) {
							for (String name: names.split(",")) {
								Log.d("ADD_COND", "Adding condition: " + key + " " + name);
								if (last_conditional.peek().equals("None.")) {
									conditions.get(key).put(name, new ConditionalBonus());
								} else {
									ConditionalBonus conditionalBonus = new ConditionalBonus();
									conditionalBonus.setConditions(bonus_conditions);
									conditions.get(key).put(key, conditionalBonus);
									conditional_bonuses.add(conditionalBonus);
								}
								// Update other conditional bonuses after a new one is added
								updateConditionalBonuses();
							}
						} else {
							Log.d("ReadXML5", "No condition map for key: " + key);
						}
					} else {
						Log.d("ReadXML5", "Condition without key/name!");
					}
				}
			}
		}
	}
	
	private void updateConditionalBonuses() {
		for (ConditionalBonus bonus: conditional_bonuses) {
			updateConditionalBonus(bonus);
		}
	}
	
	private void updateConditionalBonus(ConditionalBonus bonus) {
		if (checkConditions(bonus.getConditions())) {
			bonus.activate();
		} else {
			bonus.deactivate();
			Log.d("Condition", "Condition not met:" + bonus.getStringValue());
		}
	}
	
	private boolean checkConditions(Map<String,List<KeyValuePair>> conditions) {
		for (String key: PropertyLists.keyNames) {
			if (conditions == null) // If no conditions are set, then conditions are met
				break;
			for (KeyValuePair kv: conditions.get(key)) {
				if (!hasCondition(key, kv)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean hasCondition(String key, KeyValuePair kv) {
		if (kv.value == null)
			for (String property: kv.key.split(",")) {
				if (hasProperty(key, property))
					return true;
			}
		else {
			String[] keys = kv.key.split(",");
			String[] values = kv.value.split(",");
			if (keys.length != values.length) {
				Log.d("hasCondition", "Key Value size mismatch " + kv.key + " " + kv.value);
				return false;
			}
			for (int i = 0; i < keys.length; i++) {
				if (getValue(keys[i]) >= evaluateValue(values[i]))
					return true;
			}
		}
		return false;
	}
}