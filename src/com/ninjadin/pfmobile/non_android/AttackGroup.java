package com.ninjadin.pfmobile.non_android;

import java.util.Map;


public class AttackGroup extends ActionGroup {
	private String versus = null;
	private String target = null;
	protected AttackGroup inherited_attack = null;

	public AttackGroup(Map<String,String> attr, AttackGroup inherit, StatisticGroup group_parent) {
		super (attr, inherit, group_parent);
		inherited_attack = inherit;
	}
	
	public String getVersus() {
		return versus;
	}
	
	public String getTarget() {
		if (inherited != null) {
			return inherited_attack.getTarget();
		} else {
			return target;
		}
	}
	
}
