package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;

public class Attack {
	private Map<String,String> properties = new HashMap<String,String>();
	private List<Damage> attackDamage = new ArrayList<Damage>();
	
	public Attack () {
		for (String property: PropertyLists.attackProperties) {
			properties.put(property, "0");
		}
	}
	
	public void addProperty(String property, String value) {
		
	}
	
	public void replaceProperty(String property, String value) {
		
	}
	
	public void addDamage(Damage damage) {
		attackDamage.add(damage);
	}
}
