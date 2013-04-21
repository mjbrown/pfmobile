package com.ninjadin.pfmobile.non_android;

public class InventoryItemProperty {
	private String strValue;
	
	public InventoryItemProperty(String val) {
		strValue = val;
	}
	
	public InventoryItemProperty(Integer val) {
		strValue = Integer.toString(val);
	}
	
	public void add(Integer val) {
		try {
			Integer intVal = Integer.parseInt(strValue);
			intVal += val;
			strValue = Integer.toString(intVal);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public void add(String val) {
		try {
			// Don't overwrite an integer value
			Integer.parseInt(val);
		} catch (NumberFormatException e) {
			String newValue = strValue + "," + val;
			strValue = newValue;
		}
	}
	
	public String getValue() {
		return strValue;
	}
}
