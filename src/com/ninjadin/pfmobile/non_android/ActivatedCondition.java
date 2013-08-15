package com.ninjadin.pfmobile.non_android;

public class ActivatedCondition extends Conditional {
	private String key;
	private String name;

	public ActivatedCondition(String key, String name) {
		this.key = key;
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getName() {
		return name;
	}

}
