package com.ninjadin.pfmobile.non_android;

public class KeyValuePair {
	public String key;
	public String value;
	
	public KeyValuePair(KeyValuePair old) {
		key = old.key;
		value = old.value;
	}

	public KeyValuePair(String name, String val) {
		key = name;
		value = val;
	}
	
}
