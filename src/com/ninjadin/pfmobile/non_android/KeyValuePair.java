package com.ninjadin.pfmobile.non_android;

public class KeyValuePair {
	public String key;
	public String value;
	public String logic;
	
	public KeyValuePair(KeyValuePair old) {
		key = old.key;
		value = old.value;
		logic = old.logic;
	}

	public KeyValuePair(String name, String val, String comp) {
		key = name;
		value = val;
		logic = comp;
	}
	
}
