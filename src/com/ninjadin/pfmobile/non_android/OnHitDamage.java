package com.ninjadin.pfmobile.non_android;

import java.util.Map;



public class OnHitDamage extends ActionGroup {
	
	private String type;

	public OnHitDamage(Map<String,String> attr, OnHitDamage inherit, StatisticGroup group_parent) {
		super (attr, inherit, group_parent);
	}
	
	public String getType() {
		return type;
	}
	
}
