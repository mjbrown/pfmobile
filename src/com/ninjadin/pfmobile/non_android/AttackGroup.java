package com.ninjadin.pfmobile.non_android;


public class AttackGroup extends ActionGroup {
	private String versus = null;
	private String target = null;
	protected AttackGroup inherited_attack = null;

	public AttackGroup(String versus, String target, String uses, AttackGroup inherit, StatisticGroup group_parent) {
		super (null, uses, inherit, group_parent);
		inherited_attack = inherit;
		this.versus = versus;
		this.target = target;
	}
	
	public String getVersus() {
		return versus;
	}
	
	public String getTarget() {
		if (inherited_action != null) {
			return inherited_attack.getTarget();
		} else {
			return target;
		}
	}
	
}
