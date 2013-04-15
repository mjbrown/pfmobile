package com.example.ninjadin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterStatistic {
	private int finalValue;
	private int baseValue;
	private String statisticName;
	private List<StatisticBonus> bonuses;
	
	private static final String stackableTypes[] = new String[] { "Base", "Racial", "Trait", "Feat",
		"Dodge", "Class", "Inherent", "Ranks"};
	private static final String notStackableTypes[] = new String[] { "Unnamed", "Aid Another", 
		"Flank", "Circumstance", "Divine", "Profane", "Alchemical", "Enhancement", "Class Skill",
		"Competence", "Ability", "Morale", "Armor", "Armor Enhancement", "Deflection", "Luck",
		"Morale", "Natural Armor", "Size", "Range", "High Ground", "Shield", "Shield Enhancement", };

	private static final Map<String, Boolean> isStackable;
	static {
		Map<String, Boolean> stacks = new HashMap<String, Boolean>();
		for (String stackable: stackableTypes) {
			stacks.put(stackable, true);
		}
		for (String notStackable: notStackableTypes) {
			stacks.put(notStackable, false);
		}
		isStackable = Collections.unmodifiableMap(stacks);
	}
	
	public CharacterStatistic(String nm) {
		statisticName = nm;
		baseValue = 0;
		bonuses = new ArrayList<StatisticBonus>();
	}
	
	public CharacterStatistic(String nm, int base) {
		statisticName = nm;
		baseValue = base;
		finalValue = baseValue;
		bonuses = new ArrayList<StatisticBonus>();
	}

	public String getName() {
		return statisticName;
	}

	public int getFinalValue() {
		return finalValue;
	}
	
	public Boolean update() {
		int priorValue = finalValue;
		finalValue = baseValue;
		Map<String, Integer> stackTypeValues = new HashMap<String, Integer>();
		for (StatisticBonus bonus: bonuses) {
			Integer priorStackValue = stackTypeValues.get(bonus.stackType);
			if (priorStackValue == null) {
				stackTypeValues.put(bonus.stackType, bonus.getValue());
			} else {
				Integer newStackValue = bonus.getValue();
				if (isStackable.get(bonus.stackType)) {
					stackTypeValues.put(bonus.stackType, newStackValue + priorStackValue);
				} else if (newStackValue > priorStackValue) { 
					stackTypeValues.put(bonus.stackType, newStackValue);
				}
			}
		}
		Integer ranks = stackTypeValues.get("Ranks");
		if (ranks == null) {
			stackTypeValues.put("Class Skill", 0);
		} else if (ranks < 1) {
			stackTypeValues.put("Class Skill", 0);
		}
		for (Map.Entry<String, Integer> entry: stackTypeValues.entrySet()) {
			finalValue += entry.getValue();
		}
		if (priorValue == finalValue)
			return false;
		return true;
	}
	
	public void addBonus(StatisticBonus newBonus) {
		bonuses.add(newBonus);
	}
}
