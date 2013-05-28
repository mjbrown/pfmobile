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

import android.util.Xml;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class StatisticManager {
	// A map of all CharacterStatistics
	private Map<String, StatisticInstance> statMap;
	// A list of all CharacterStatistics names
	public List<String> statList;
	// Lists of dependent CharacterStatistics
	private Map<String, List<StatisticInstance>> statDependencies;
	// A map of feat names and proficiencies for determining if prerequisites are met
	public ConditionalBonus prereqs = new ConditionalBonus();
	private Map<String, Boolean> prerequisites;
	// A map of equipment / environment conditions
	public ConditionalBonus conditions = new ConditionalBonus();
	
	public StatisticManager() {
		prerequisites = new HashMap<String, Boolean>();
		statMap = new LinkedHashMap<String, StatisticInstance>();
		statList = new ArrayList<String>();
		statDependencies = new HashMap<String, List<StatisticInstance>>();
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
			List<StatisticInstance> dependentsList = new ArrayList<StatisticInstance>();
			statDependencies.put(entry.getKey(), dependentsList);
			statList.add(entry.getKey());
		}
	}
	
	public StatisticBonus newBonus(String statisticType, String stackType, String source, String value) {
		StatisticInstance bonusRecipient = statMap.get(statisticType);
		if (bonusRecipient == null) // Not a valid target
			return null;
		List<StatisticInstance> dependentUpon = new ArrayList<StatisticInstance>();
		String strippedValue = new String(value);
		for (String potential: statList) {
			if (value.contains("[" + potential + "]")) {
				strippedValue = strippedValue.replace("[" + potential + "]", potential);
				StatisticInstance targetOfDependency = statMap.get(potential);
				if (targetOfDependency != null) {
					dependentUpon.add(targetOfDependency);
				}
				List<StatisticInstance> dependents = statDependencies.get(potential);
				dependents.add(bonusRecipient);
			}
		}
		StatisticBonus newBonus;
		if (dependentUpon.size() > 0) {
			newBonus = new StatisticBonus(stackType, source, strippedValue, dependentUpon);
		} else {
			newBonus = new StatisticBonus(stackType, source, value);
		}
		// Add the bonus and update the Final Value
		bonusRecipient.addBonus(newBonus);
		List<StatisticInstance> dependents = statDependencies.get(bonusRecipient);
		// Trigger update on all Statistics dependent upon bonusRecipient
		if (bonusRecipient.update() && (dependents != null))
			recursiveUpdate(dependents);
		return newBonus;
	}
	
	public StatisticBonus newBonus(Map<String, String> bonusMap) {
		String type = bonusMap.get(XmlConst.TYPE_ATTR);
		String value = bonusMap.get(XmlConst.VALUE_ATTR);
		if ((type != null) && (value != null)) {
			String stackType = bonusMap.get(XmlConst.STACKTYPE_ATTR);
			if (stackType == null)
				stackType = "Armor";
			String source = bonusMap.get(XmlConst.SOURCE_ATTR);
			if (source == null)
				source = "Natural";
			return newBonus(type, stackType, source, value);
		}
		return null;
	}
	
	private void recursiveUpdate(List<StatisticInstance> dependents) {
		for (StatisticInstance dependent: dependents) {
			if (dependent.update()) {
				List<StatisticInstance> moreDependents = statDependencies.get(dependent.getName());
				if (moreDependents != null)
					recursiveUpdate(moreDependents);
			}
		}
	}
	public int getValue(String statisticName) {
		StatisticInstance stat = statMap.get(statisticName);
		stat.update();		// TODO: This update shouldn't be necessary
		return stat.getFinalValue();
	}
	
	public boolean hasProperty(String name) {
		Boolean property = prerequisites.get(name);
		if (property != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public int evaluateValue(String value) {
		String strippedValue = new String(value);
		for (String operand: statList) {
			if (value.contains("[" + operand + "]")) {
				Integer op = statMap.get(operand).getFinalValue();
				if (op != null) {
					strippedValue = strippedValue.replace("[" + operand + "]", Integer.toString(op));
				}
			}
		}
		return StatisticBonus.evaluate(strippedValue);
	}
	public void readXMLBonuses(InputStream inStream, File inventory) throws IOException, XmlPullParserException {
		Stack<String> lastPrereq = new Stack<String>();
		Stack<String> lastConditional = new Stack<String>();
		lastPrereq.push("None.");
		lastConditional.push("None.");
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(inStream, null);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				String name = parser.getName();
				if (name != null) {
					if (name.equals(XmlConst.PREREQ_TAG)) {
						lastPrereq.pop();
					} else if (name.equals(XmlConst.CONDITIONAL_TAG)) {
						lastConditional.pop();
					}
				}
				continue;
			}
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals(XmlConst.PREREQ_TAG)) {
					String name = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					if (name != null) {
						lastPrereq.push(name);
					} else {
						lastPrereq.push("Invalid Prereq!");
					}
				} else if (tag.equals(XmlConst.CONDITIONAL_TAG)) {
					String name = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					if (name != null) {
						lastConditional.push(name);
					} else {
						lastConditional.push("Invalid Conditional!");
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
							StatisticBonus conditionalBonus = newBonus(type, stackType, source, value);
							if (!lastPrereq.peek().equals("None.")) {
								prereqs.addBonus(lastPrereq.peek(), conditionalBonus);
							}
							if (!lastConditional.peek().equals("None.")) {
								conditions.addBonus(lastConditional.peek(), conditionalBonus);
							}
						}
					}
				} else if (tag.equals(XmlConst.EQUIPITEM_TAG)) {
					String name = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					if (name != null) {
						EquippedItem newItem = new EquippedItem(name, inventory);
						for (Map<String,String> bonus: newItem.bonusList) {
							newBonus(bonus);
						}
						if (newItem.condition != null) {
							conditions.putCondition(newItem.condition);
						}
					}
				} else if (tag.equals(XmlConst.CHOICE_TAG) || (tag.equals(XmlConst.CHOSEN_TAG))) {
					String name = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					if (name != null) {
						prerequisites.put(name, true);
						prereqs.putCondition(name);
					}
				} else if (tag.equals(XmlConst.PROFICIENCY_TAG)) {
					String types = parser.getAttributeValue(null, XmlConst.TYPE_ATTR);
					if (types != null) {
						for (String type: types.split(",")) {
							prerequisites.put(type, true);
							prereqs.putCondition(type);
						}
					}
				}
			}
		}
	}
	private class ConditionalBonus {
		Map<String, Boolean> conditions = new HashMap<String,Boolean>();
		List<String> typeList = new ArrayList<String>();
		List<StatisticBonus> bonusList = new ArrayList<StatisticBonus>();
		List<String> invalidList = new ArrayList<String>();
		List<String> invalidCondition = new ArrayList<String>();
		
		public void putCondition(String condition) {
			conditions.put(condition, true);
			refreshConditionals();
		}
		public void putConditionalCondition(String conditional, String condition) {
			if (hasCondition(conditional)) {
				conditions.put(condition, true);
			} else {
				invalidList.add(conditional);
				invalidCondition.add(condition);
			}
		}
		
		
		private boolean hasCondition(String names) {
			boolean ret = false;
			for (String name: names.split(",")) {
				Boolean conditionActive = conditions.get(name);
				if (conditionActive != null) {
					if (conditionActive == true) {
						ret = true;
						break;
					}
				}
			}
			return ret;
		}
		
		private void refreshConditionals() {
			// Check deferred conditions
			for (int i = 0; i < invalidList.size(); i++) {
				String names = invalidList.get(i);
				if (hasCondition(names)) {
					putCondition(invalidCondition.get(i));
					invalidList.remove(i);
					invalidCondition.remove(i);
				}
			}
			for (int i = 0; i < typeList.size(); i++) {
				StatisticBonus bonus = bonusList.get(i);
				String names = typeList.get(i);
				if (hasCondition(names)) {
					bonus.enable();
				} else {
					bonus.disable();
				}
			}
		}
		
		public void addBonus(String condition, StatisticBonus bonus) {
			typeList.add(condition);
			bonusList.add(bonus);
			if (conditions.get(condition) != null) {
				bonus.enable();
			} else {
				bonus.disable();
			}
		}
	}
}
