package com.ninjadin.pfmobile.non_android;

import java.util.HashMap;
import java.util.Map;

public class InventoryItem {
	private String name;
	private String slot;
	private Map<String,InventoryItemProperty> masterMap = new HashMap<String,InventoryItemProperty>();
	
	public InventoryItem(String itemName, String itemSlot) {
		name = itemName;
		slot = itemSlot;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSlot() {
		return slot;
	}
	public void addBonus(String property, Integer value) {
		InventoryItemProperty itemProperty = masterMap.get(property);
		if (itemProperty == null) {
			itemProperty = new InventoryItemProperty(0);
		}
		itemProperty.add(value);
	}
	
	public void addBonus(String property, String value) {
		InventoryItemProperty itemProperty = masterMap.get(property);
		if (itemProperty == null) {
			itemProperty = new InventoryItemProperty("");
		}
		itemProperty.add(value);
	}
}
