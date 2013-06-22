package com.ninjadin.pfmobile.non_android;


public class AttackGroup extends ActionGroup {
	private String versus = null;

	public AttackGroup(String versus, AttackGroup inherit, StatisticGroup group_parent) {
		super (null, inherit, group_parent);
		this.versus = versus;
	}
	
	public String getVersus() {
		return versus;
	}
	
}
