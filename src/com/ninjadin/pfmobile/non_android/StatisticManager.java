package com.ninjadin.pfmobile.non_android;

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

public class StatisticManager {
	// A map of all CharacterStatistics
	private Map<String, StatisticInstance> masterMap;
	// A list of all CharacterStatistics names
	public List<String> masterList;
	// Lists of dependent CharacterStatistics
	private Map<String, List<StatisticInstance>> dependencyMap;
	
	public StatisticManager() {
		masterMap = new LinkedHashMap<String, StatisticInstance>();
		masterList = new ArrayList<String>();
		dependencyMap = new HashMap<String, List<StatisticInstance>>();
		for (String abilityScoreName: PropertyLists.abilityScoreNames) {
			StatisticInstance newStat = new StatisticInstance(abilityScoreName);
			masterMap.put(abilityScoreName, newStat);
		}
		for (String abilityModifierName: PropertyLists.abilityModifierNames) {
			StatisticInstance newStat = new StatisticInstance(abilityModifierName);
			masterMap.put(abilityModifierName, newStat);
		}
		for (String saveName: PropertyLists.saveNames) {
			StatisticInstance newStat = new StatisticInstance(saveName);
			masterMap.put(saveName, newStat);
		}
		for (String armorClassName: PropertyLists.armorClassNames) {
			StatisticInstance newStat = new StatisticInstance(armorClassName, 10);
			masterMap.put(armorClassName, newStat);
		}
		for (String otherStatisticName: PropertyLists.otherStatisticNames) {
			StatisticInstance newStat = new StatisticInstance(otherStatisticName);
			masterMap.put(otherStatisticName, newStat);
		}
		for (String reductionName: PropertyLists.reductionNames) {
			StatisticInstance newStat = new StatisticInstance(reductionName);
			masterMap.put(reductionName, newStat);
		}
		for (String speedName: PropertyLists.speedNames) {
			StatisticInstance newStat = new StatisticInstance(speedName);
			masterMap.put(speedName, newStat);
		}
		for (String casterStatisticName: PropertyLists.casterStatisticNames) {
			StatisticInstance newStat = new StatisticInstance(casterStatisticName);
			masterMap.put(casterStatisticName, newStat);
		}
		for (String skillName: PropertyLists.skillNames) {
			StatisticInstance newStat = new StatisticInstance(skillName);
			masterMap.put(skillName, newStat);
		}
		for (String classLevelName: PropertyLists.classLevelNames) {
			StatisticInstance newStat = new StatisticInstance(classLevelName);
			masterMap.put(classLevelName, newStat);
		}
		for (String spellFailureName: PropertyLists.spellFailureNames) {
			StatisticInstance newStat = new StatisticInstance(spellFailureName);
			masterMap.put(spellFailureName, newStat);
		}
		for (Map.Entry<String, StatisticInstance> entry: masterMap.entrySet()) {
			List<StatisticInstance> dependentsList = new ArrayList<StatisticInstance>();
			dependencyMap.put(entry.getKey(), dependentsList);
			masterList.add(entry.getKey());
		}
	}
	
	public void newBonus(String statisticType, String stackType, String source, String value) {
		StatisticInstance bonusRecipient = masterMap.get(statisticType);
		if (bonusRecipient == null) // Not a valid target
			return;
		List<StatisticInstance> dependentUpon = new ArrayList<StatisticInstance>();
		String strippedValue = new String(value);
		for (String potential: masterList) {
			if (value.contains("[" + potential + "]")) {
				strippedValue = strippedValue.replace("[" + potential + "]", potential);
				StatisticInstance targetOfDependency = masterMap.get(potential);
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
		List<StatisticInstance> dependents = dependencyMap.get(bonusRecipient);
		// Trigger update on all Statistics dependent upon bonusRecipient
		if (bonusRecipient.update() && (dependents != null))
			recursiveUpdate(dependents);
	}
	private void recursiveUpdate(List<StatisticInstance> dependents) {
		for (StatisticInstance dependent: dependents) {
			if (dependent.update()) {
				List<StatisticInstance> moreDependents = dependencyMap.get(dependent.getName());
				if (moreDependents != null)
					recursiveUpdate(moreDependents);
			}
		}
	}
	public int getValue(String statisticName) {
		StatisticInstance stat = masterMap.get(statisticName);
		return stat.getFinalValue();
	}
	public int evaluateValue(String value) {
		String strippedValue = new String(value);
		for (String operand: masterList) {
			if (value.contains("[" + operand + "]")) {
				Integer op = masterMap.get(operand).getFinalValue();
				if (op != null) {
					strippedValue = strippedValue.replace("[" + operand + "]", Integer.toString(op));
				}
			}
		}
		return StatisticBonus.evaluate(strippedValue);
	}
	public void readXMLBonuses(InputStream inStream) throws IOException, XmlPullParserException {
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
				}
			}
		}
	}
}
