package com.ninjadin.pfmobile.non_android;

public class Bonus extends Conditional {
	private String value;
	private String stackType;
	private String source;

	public Bonus(String stack, String src, String val) {
		this.stackType = stack;
		this.source = src;
		this.value = val;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getStackType() {
		return stackType;
	}

	public String getStringValue() {
		if (this.isActive() == false)
			return "0";
		return this.value;
	}
	
}
