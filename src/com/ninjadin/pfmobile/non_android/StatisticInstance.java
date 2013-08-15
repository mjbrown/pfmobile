package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;


public class StatisticInstance {
	private String finalStringValue;
	private List<Bonus> bonuses = new ArrayList<Bonus>();
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
	
	public StatisticInstance() {
		finalStringValue = "0";
	}
	
	public String getFinalStringValue() {
		if (isDirty)
			updateFinalStringValue();
		isDirty = false;
		return finalStringValue;
	}
	
	// This pretty much handles stacks vs doesn't
	private void updateFinalStringValue() {
		Map<String,String> stackTypeValues = new HashMap<String,String>();
		for (Bonus bonus: bonuses) {
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
		finalStringValue = "";
		for (Map.Entry<String, String> entry: stackTypeValues.entrySet()) {
			finalStringValue += " + ( " + entry.getValue() + " )";
		}
		if (finalStringValue.equals(" + ( 0 )"))
			finalStringValue = "0";
	}
	
	public void addBonus(Bonus newBonus) {
		bonuses.add(newBonus);
	}
}
