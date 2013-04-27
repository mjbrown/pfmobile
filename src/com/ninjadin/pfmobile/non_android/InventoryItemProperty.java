package com.ninjadin.pfmobile.non_android;

public class InventoryItemProperty {
	private String strValue;
	
	public InventoryItemProperty(String val) {
		strValue = val;
	}
	
	public InventoryItemProperty(Integer val) {
		strValue = Integer.toString(val);
	}
	
	public void add(String val) {
		try {
			// Don't overwrite an integer value
			Integer bonusVal = Integer.parseInt(val);
			Integer currentVal = Integer.parseInt(strValue);
			currentVal += bonusVal;
			strValue = Integer.toString(currentVal);
		} catch (NumberFormatException e) {
			String newValue = strValue + "," + val;
			strValue = newValue;
		}
	}
	
	public String getValue() {
		return strValue;
	}
}
