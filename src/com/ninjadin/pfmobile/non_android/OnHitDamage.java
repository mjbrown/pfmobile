package com.ninjadin.pfmobile.non_android;



public class OnHitDamage extends ActionGroup {
	
	private String type;

	public OnHitDamage(String type, String uses, OnHitDamage inherit, StatisticGroup group_parent) {
		super (null, uses, inherit, group_parent);
	}
	
	public String getType() {
		return type;
	}
	
}
