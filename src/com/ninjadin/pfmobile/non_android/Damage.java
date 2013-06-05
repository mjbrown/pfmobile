package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.List;

public class Damage {
	public String damaged_statistic;
	public List<String> type_list = new ArrayList<String>();
	public List<String> source_list = new ArrayList<String>();
	public String value;
	public int uses_remaining;
	
	public void addType(String type) {
		type_list.add(type);
	}
	
	public void addSource(String source) {
		source_list.add(source);
	}
}
