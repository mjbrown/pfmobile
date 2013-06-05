package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;


public class StatisticInstance {
	private String finalStringValue;
	private String statisticName;
	private List<ConditionalBonus> bonuses;
	boolean isDirty = true;
	
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
		finalStringValue = "0";
		bonuses = new ArrayList<ConditionalBonus>();
	}
	
	public StatisticInstance(String nm, int base) {
		statisticName = nm;
		bonuses = new ArrayList<ConditionalBonus>();
	}

	public String getName() {
		return statisticName;
	}

	public String getFinalStringValue() {
		if (isDirty)
			updateFinalStringValue();
		isDirty = false;
		return finalStringValue;
	}
	
	private void updateFinalStringValue() {
		Map<String,String> stackTypeValues = new HashMap<String,String>();
		for (ConditionalBonus bonus: bonuses) {
			String priorStackValue = stackTypeValues.get(bonus.getStackType());
			if (priorStackValue == null) {
				stackTypeValues.put(bonus.getStackType(), bonus.getStringValue());
			} else {
				if (isStackable.get(bonus.getStackType())) {
					stackTypeValues.put(bonus.getStackType(), bonus.getStringValue() + " + " + priorStackValue);
				} else {
					stackTypeValues.put(bonus.getStackType(), "max( " + bonus.getStringValue() + " , " + priorStackValue + " )");
				}
			}
		}
		if (stackTypeValues.get(PropertyLists.ranks) == null) {
			stackTypeValues.put(PropertyLists.class_skill, "0");
		}
		finalStringValue = "0";
		for (Map.Entry<String, String> entry: stackTypeValues.entrySet()) {
			finalStringValue += " + ( " + entry.getValue() + " )";
		}
	}
	
	public void addBonus(ConditionalBonus newBonus) {
		bonuses.add(newBonus);
	}
}
