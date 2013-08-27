package com.ninjadin.pfmobile.non_android;

public class SpellGroup extends StatisticGroup{
	String name;
	String source;
	String school;
	
	public SpellGroup(String nm, String src, String schl, StatisticGroup parent) {
		super (parent);
		name = nm;
		source = src;
		school = schl;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getSchool() {
		return school;
	}
}
