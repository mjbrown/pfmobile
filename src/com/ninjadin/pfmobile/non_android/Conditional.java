package com.ninjadin.pfmobile.non_android;


public class Conditional {
	private boolean meetsConditions = true;
	private boolean isActivated = true;
	ConditionList condition_list = null;
	
	public Conditional() {
		this.meetsConditions = true;
	}
	
	public Boolean isActive() {
		return (meetsConditions & isActivated);
	}
	
	public void meetsConditions() {
		this.meetsConditions = true;
	}
	
	public void failsConditions() {
		this.meetsConditions = false;
	}
	
	public ConditionList getConditions() {
		return condition_list;
	}
	
	public void setConditions(ConditionList bonus_conditions) {
		condition_list = new ConditionList(bonus_conditions);
		this.meetsConditions = false;
	}
	
}
