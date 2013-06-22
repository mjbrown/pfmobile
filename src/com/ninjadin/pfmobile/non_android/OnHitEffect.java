package com.ninjadin.pfmobile.non_android;



public class OnHitEffect extends ActionGroup {
	
	private String type;

	public OnHitEffect(String type, OnHitEffect inherit, StatisticGroup group_parent) {
		super (null, inherit, group_parent);
	}
	
	public String getType() {
		return type;
	}
	
}
