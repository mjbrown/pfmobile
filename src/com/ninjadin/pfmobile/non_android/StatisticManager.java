package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	// A map of items worn by the character
	private Map<String, EquippedItem> itemMap;
	
	public StatisticManager() {
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
		for (String saveName: PropertyLists.saveNames) {
			StatisticInstance newStat = new StatisticInstance(saveName);
			statMap.put(saveName, newStat);
		}
		for (String armorClassName: PropertyLists.armorClassNames) {
			StatisticInstance newStat = new StatisticInstance(armorClassName, 10);
			statMap.put(armorClassName, newStat);
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
		for (String casterStatisticName: PropertyLists.casterStatisticNames) {
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
		for (String spellFailureName: PropertyLists.spellFailureNames) {
			StatisticInstance newStat = new StatisticInstance(spellFailureName);
			statMap.put(spellFailureName, newStat);
		}
		for (Map.Entry<String, StatisticInstance> entry: statMap.entrySet()) {
			List<StatisticInstance> dependentsList = new ArrayList<StatisticInstance>();
			statDependencies.put(entry.getKey(), dependentsList);
			statList.add(entry.getKey());
		}
	}
	
	public void newBonus(String statisticType, String stackType, String source, String value) {
		StatisticInstance bonusRecipient = statMap.get(statisticType);
		if (bonusRecipient == null) // Not a valid target
			return;
		List<StatisticInstance> dependentUpon = new ArrayList<StatisticInstance>();
		String strippedValue = new String(value);
		for (String potential: statList) {
			if (value.contains("[" + potential + "]")) {
				strippedValue = strippedValue.replace("[" + potential + "]", potential);
				StatisticInstance targetOfDependency = statMap.get(potential);
				if (targetOfDependency != null) {
					dependentUpon.add(targetOfDependency);
				}
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
	}
	
	public void newBonus(Map<String, String> bonusMap) {
		String type = bonusMap.get(XmlConst.TYPE_ATTR);
		String value = bonusMap.get(XmlConst.VALUE_ATTR);
		if ((type != null) && (value != null)) {
			String stackType = bonusMap.get(XmlConst.STACKTYPE_ATTR);
			if (stackType == null)
				stackType = "Armor";
			String source = bonusMap.get(XmlConst.SOURCE_ATTR);
			if (source == null)
				source = "Natural";
			newBonus(type, stackType, source, value);
		}
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
		return stat.getFinalValue();
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
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(inStream, null);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals(XmlConst.BONUS_TAG)) {
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
							newBonus(type, stackType, source, value);
						}
					}
				} else if (tag.equals(XmlConst.EQUIPITEM_TAG)) {
					String name = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
					if (name != null) {
						EquippedItem newItem = new EquippedItem(name, inventory);
						for (Map<String,String> bonus: newItem.bonusList) {
							newBonus(bonus);
						}
					}
				}
			}
		}
	}
}
