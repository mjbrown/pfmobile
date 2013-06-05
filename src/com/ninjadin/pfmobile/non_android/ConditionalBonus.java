package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;

public class ConditionalBonus {
	private String value;
	private String stackType;
	private String source;
	private boolean isActive;
	Map<String, List<KeyValuePair>> conditions = null;
	
	public ConditionalBonus(String stack, String src, String val) {
		this.stackType = stack;
		this.source = src;
		this.isActive = true;
		this.value = val;
	}
	
	public ConditionalBonus() {
		this.isActive = true;
	}
	
	public Boolean isActive() {
		return isActive;
	}
	
	public void activate() {
		this.isActive = true;
	}
	
	public void deactivate() {
		this.isActive = false;
	}
	
	public String getStackType() {
		return stackType;
	}

	public String getStringValue() {
		if (isActive == false)
			return "0";
		return this.value;
	}
	
	public Map<String,List<KeyValuePair>> getConditions() {
		return conditions;
	}
	
/*	public void setPrerequisites(List<List<KeyValuePair>> prerequisites) {
		prereqs = new ArrayList<List<KeyValuePair>>();
		for (List<KeyValuePair> or_list: )
	}
*/	
	public void setConditions(Map<String,List<KeyValuePair>> bonus_conditions) {
		conditions = new HashMap<String,List<KeyValuePair>>();
		for (String key: PropertyLists.keyNames) {
			List<KeyValuePair> kv_list = new ArrayList<KeyValuePair>();
			for (KeyValuePair condition: bonus_conditions.get(key))
				kv_list.add(condition);
			conditions.put(key, kv_list);
		}
		this.isActive = false;
	}
}
