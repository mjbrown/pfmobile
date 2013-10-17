package com.ninjadin.pfmobile.non_android;

import java.util.Map;

public class SpellGroup extends StatisticGroup{
	Map<String,String> attributes;
	
	public SpellGroup(Map<String,String> attr, StatisticGroup parent) {
		super (parent);
		attributes = attr;
	}
	
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	
	public int getAttributeValue(String key) {
		String name = attributes.get(key);
		if (name != null)
			return parent.getValue(name);
		else
			return 0;
	}
	
}
