package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatisticInstance {
	private int finalValue;
	private int baseValue;
	private String statisticName;
	private List<StatisticBonus> bonuses;
	
	private static final Map<String, Boolean> isStackable;
	static {
		Map<String, Boolean> stacks = new HashMap<String, Boolean>();
		for (String stackable: PropertyLists.stackableTypes) {
			stacks.put(stackable, true);
		}
		for (String notStackable: PropertyLists.notStackableTypes) {
			stacks.put(notStackable, false);
		}
		isStackable = Collections.unmodifiableMap(stacks);
	}
	
	public StatisticInstance(String nm) {
		statisticName = nm;
		baseValue = 0;
		bonuses = new ArrayList<StatisticBonus>();
	}
	
	public StatisticInstance(String nm, int base) {
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
