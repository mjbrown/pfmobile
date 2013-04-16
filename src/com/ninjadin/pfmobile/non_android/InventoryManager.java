package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryManager {
	
	private File inventoryFile;
	
	List<Map<String,String>> itemNames = new ArrayList<Map<String, String>>();
	List<List<Map<String, String>>> itemDetails = new ArrayList<List<Map<String,String>>>();
	
	final static public String[] slotNames = new String[] { "Head", "Headband",
		"Eyes", "Neck", "Shoulders", "Chest", "Body", "Armor", "Hands", "Ring", "Belt", "Held", };
	
	final static public String[] itemTypes = new String[] { "Item", "Simple Weapon", "Martial Weapon",
		"Exotic Weapon", "Light Shield", "Heavy Shield", "Tower Shield", "Light Armor", "Medium Armor",
		"Heavy Armor", };
	
	final static public String[] itemQualities = new String[] { "Cost", "Encumbrance", "Hit Points",
		"Hardness" };
	
	final static public String[] armorQualities = new String[] { "Armor", "Max Dex", "Armor Check",
		"Spell Failure", "Size" };
	
	// Weapon specific attributes
	final static public String[] weaponQualities = new String[] { "Range", "To Hit", 
		"Critical Range", "Critical Multiplier", "Size" };
	
	// Weapon damage effect attributes
	final static public String[] damageEffects = new String[] { "Versus", "Damage Source", 
		"Dice", "Number of Dice", };
	
	// Things that are handled in a special way, weapon can have 1 or more
	final static public String[] weaponSpecials = new String[] { "Light", "One-handed", "Two-handed",
		"Brace", "Double", "Monk", "Disarm", "Trip", "Finesse" };
	
	public InventoryManager(File deviceInventory) {
		inventoryFile = deviceInventory;
		
	}
}
