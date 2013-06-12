package com.ninjadin.pfmobile.non_android;

import com.ninjadin.pfmobile.data.PropertyLists;


public class OnHitEffect extends ActionGroup {
	
	private OnHitEffect inherited = null;
	private String type;
	
	public OnHitEffect(String typ, StatisticGroup group_parent) {
		super (group_parent);
		addStatNames(PropertyLists.damageProperties);
		type = typ;
	}
	
	public OnHitEffect(OnHitEffect inherit, StatisticGroup group_parent) {
		super (group_parent);
		addStatNames(PropertyLists.damageProperties);
		inherited = inherit;
	}
	
	public String getType() {
		if (inherited != null)
			return inherited.getType();
		else
			return type;
	}
	
}
