package com.ninjadin.pfmobile.non_android;

import java.util.HashMap;
import java.util.Map;

import com.ninjadin.pfmobile.data.XmlConst;

public class OnHitCondition extends ConditionalBonus {
	Map<String, String> condition = new HashMap<String,String>();
	
	public OnHitCondition(String key, String name, String unique) {
		condition.put(XmlConst.KEY_ATTR, key);
		condition.put(XmlConst.NAME_ATTR, name);
		condition.put(XmlConst.UNIQUE_ATTR, unique);
	}
	
	public Map<String,String> getConditionMap() {
		return condition;
	}
}
