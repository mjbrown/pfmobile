package com.example.ninjadin;

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

public class DependencyManager {
	// A map of all CharacterStatistics
	private Map<String, CharacterStatistic> masterMap;
	// A list of all CharacterStatistics names
	public List<String> masterList;
	// Lists of dependent CharacterStatistics
	private Map<String, List<CharacterStatistic>> dependencyMap;
	
	public DependencyManager() {
		masterMap = new LinkedHashMap<String, CharacterStatistic>();
		masterList = new ArrayList<String>();
		dependencyMap = new HashMap<String, List<CharacterStatistic>>();
		for (String abilityScoreName: CharacterData.abilityScoreNames) {
			CharacterStatistic newStat = new CharacterStatistic(abilityScoreName);
			masterMap.put(abilityScoreName, newStat);
		}
		for (String abilityModifierName: CharacterData.abilityModifierNames) {
			CharacterStatistic newStat = new CharacterStatistic(abilityModifierName);
			masterMap.put(abilityModifierName, newStat);
		}
		for (String saveName: CharacterData.saveNames) {
			CharacterStatistic newStat = new CharacterStatistic(saveName);
			masterMap.put(saveName, newStat);
		}
		for (String armorClassName: CharacterData.armorClassNames) {
			CharacterStatistic newStat = new CharacterStatistic(armorClassName, 10);
			masterMap.put(armorClassName, newStat);
		}
		for (String otherStatisticName: CharacterData.otherStatisticNames) {
			CharacterStatistic newStat = new CharacterStatistic(otherStatisticName);
			masterMap.put(otherStatisticName, newStat);
		}
		for (String reductionName: CharacterData.reductionNames) {
			CharacterStatistic newStat = new CharacterStatistic(reductionName);
			masterMap.put(reductionName, newStat);
		}
		for (String speedName: CharacterData.speedNames) {
			CharacterStatistic newStat = new CharacterStatistic(speedName);
			masterMap.put(speedName, newStat);
		}
		for (String casterStatisticName: CharacterData.casterStatisticNames) {
			CharacterStatistic newStat = new CharacterStatistic(casterStatisticName);
			masterMap.put(casterStatisticName, newStat);
		}
		for (String skillName: CharacterData.skillNames) {
			CharacterStatistic newStat = new CharacterStatistic(skillName);
			masterMap.put(skillName, newStat);
		}
		for (String classLevelName: CharacterData.classLevelNames) {
			CharacterStatistic newStat = new CharacterStatistic(classLevelName);
			masterMap.put(classLevelName, newStat);
		}
		for (String spellFailureName: CharacterData.spellFailureNames) {
			CharacterStatistic newStat = new CharacterStatistic(spellFailureName);
			masterMap.put(spellFailureName, newStat);
		}
		for (Map.Entry<String, CharacterStatistic> entry: masterMap.entrySet()) {
			List<CharacterStatistic> dependentsList = new ArrayList<CharacterStatistic>();
			dependencyMap.put(entry.getKey(), dependentsList);
			masterList.add(entry.getKey());
		}
	}
	
	public void newBonus(String statisticType, String stackType, String source, String value) {
		CharacterStatistic bonusRecipient = masterMap.get(statisticType);
		if (bonusRecipient == null) // Not a valid target
			return;
		List<CharacterStatistic> dependentUpon = new ArrayList<CharacterStatistic>();
		String strippedValue = new String(value);
		for (String potential: masterList) {
			if (value.contains("[" + potential + "]")) {
				strippedValue = strippedValue.replace("[" + potential + "]", potential);
				CharacterStatistic targetOfDependency = masterMap.get(potential);
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
		List<CharacterStatistic> dependents = dependencyMap.get(bonusRecipient);
		// Trigger update on all Statistics dependent upon bonusRecipient
		if (bonusRecipient.update() && (dependents != null))
			recursiveUpdate(dependents);
	}
	private void recursiveUpdate(List<CharacterStatistic> dependents) {
		for (CharacterStatistic dependent: dependents) {
			if (dependent.update()) {
				List<CharacterStatistic> moreDependents = dependencyMap.get(dependent.getName());
				if (moreDependents != null)
					recursiveUpdate(moreDependents);
			}
		}
	}
	public int getValue(String statisticName) {
		CharacterStatistic stat = masterMap.get(statisticName);
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
				if (tag.equals(CharacterData.BONUS_TAG)) {
					String types = parser.getAttributeValue(null, CharacterData.TYPE_ATTR);
					String stackType = parser.getAttributeValue(null, CharacterData.STACKTYPE_ATTR);
					String value = parser.getAttributeValue(null, CharacterData.VALUE_ATTR);
					if ((types != null) && (value != null)) {
						String source = parser.getAttributeValue(null, CharacterData.SOURCE_ATTR);
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
