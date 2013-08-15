package com.ninjadin.pfmobile.non_android;

public class KeyValuePair {
	public String key;
	public String value;
	public String comparator;
	
	public KeyValuePair(KeyValuePair old) {
		key = old.key;
		value = old.value;
		comparator = old.comparator;
	}

	public KeyValuePair(String name, String val, String comp) {
		key = name;
		value = val;
		comparator = comp;
	}
	
}
