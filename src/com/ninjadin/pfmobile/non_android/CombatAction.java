package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombatAction {
	public String type = "Attack";
	public String name;
	public String cost;
	
	// List of attacks
	private List<Attack> attacks = new ArrayList<Attack>();
	// Default damage inherited by all attacks
	private List<Damage> damage = new ArrayList<Damage>();
	// Default properties inherited by all attacks
	private Map<String,String> properties = new HashMap<String,String>();
}
