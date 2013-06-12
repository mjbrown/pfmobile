package com.ninjadin.pfmobile.non_android;


public class ConditionalBonus {
	private String value;
	private String stackType;
	private String source;
	private boolean meetsConditions = true;
	private boolean isActivated = true;
	ConditionList condition_list = null;
	
	public ConditionalBonus(String stack, String src, String val) {
		this.stackType = stack;
		this.source = src;
		this.value = val;
	}
	
	public ConditionalBonus() {
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
	
	public String getSource() {
		return source;
	}
	
	public String getStackType() {
		return stackType;
	}

	public String getStringValue() {
		if (meetsConditions == false)
			return "0";
		return this.value;
	}
	
	public ConditionList getConditions() {
		return condition_list;
	}
	
	public void setConditions(ConditionList bonus_conditions) {
		condition_list = new ConditionList(bonus_conditions);
		this.meetsConditions = false;
	}
}
