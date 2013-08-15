package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.XmlConst;

public class OnHitCondition extends Conditional {
	private List<Map<String,String>> added = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> removed = new ArrayList<Map<String,String>>();
	
	public OnHitCondition(String key, String add, String remove) {
		if (add != null) {
			if (remove != null) {
				for (String add_effect: remove.split(",")) {
					Map<String,String> new_effect = new HashMap<String,String>();
					new_effect.put(XmlConst.NAME_ATTR, add_effect);
					new_effect.put(XmlConst.KEY_ATTR, key);
					added.add(new_effect);
				}
			}
			for (String add_effect: add.split(",")) {
				Map<String,String> new_effect = new HashMap<String,String>();
				new_effect.put(XmlConst.NAME_ATTR, add_effect);
				new_effect.put(XmlConst.KEY_ATTR, key);
				new_effect.put(XmlConst.REMOVE_ATTR, remove);
				added.add(new_effect);
			}
		} else {
			if (remove != null) {
				for (String add_effect: remove.split(",")) {
					Map<String,String> new_effect = new HashMap<String,String>();
					new_effect.put(XmlConst.NAME_ATTR, add_effect);
					new_effect.put(XmlConst.KEY_ATTR, key);
					removed.add(new_effect);
				}
			}
		}
	}
	
	public List<Map<String,String>> getAddedConditions() {
		return added;
	}
	
	public List<Map<String,String>> getRemovedConditions() {
		return removed;
	}
}
