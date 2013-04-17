package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class InventoryManager {
	
	private File inventoryFile;
	public TwoDimXmlExtractor itemInfo;
	
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
	
	// Weapon damage types
	final static public String[] damageTypes = new String[] { "Subdual", "Normal", 
		"Critical", };
	
	// Weapon damage sources
	final static public String[] damageSources = new String[] { "Piercing", "Slashing", "Bludgeoning", 
		"Fire", "Cold", "Electricity", "Sonic", "Negative Energy", "Positive Energy", "Holy", "Magic", "Bleed",
		"Adamantine", "Cold Iron", "Silver", "Poison" };
	
	// Things that are handled in a special way, weapon can have 1 or more
	final static public String[] weaponSpecials = new String[] { "Light", "One-handed", "Two-handed",
		"Brace", "Double", "Monk", "Disarm", "Trip", "Finesse" };
	
	public InventoryManager(File deviceInventory) throws FileNotFoundException, XmlPullParserException, IOException {
		inventoryFile = deviceInventory;
		FileInputStream inStream;
		XmlPullParser parser = Xml.newPullParser();
		inStream = new FileInputStream(inventoryFile);
		try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inStream, null);
			parser.nextTag();
			String[] tags = new String[] { GlobalConstants.ITEM_TAG };
			String[] tag_attrs = new String[] { GlobalConstants.NAME_ATTR, GlobalConstants.TYPE_ATTR , GlobalConstants.SLOT_ATTR, GlobalConstants.SIZE_ATTR, };
			String[] subtags = new String[] { GlobalConstants.ITEMBONUS_TAG, GlobalConstants.DAMAGE_TAG };
			String[] subtag_attrs = new String[] { GlobalConstants.NAME_ATTR, GlobalConstants.TYPE_ATTR, GlobalConstants.VALUE_ATTR , GlobalConstants.STATISTIC_ATTR, GlobalConstants.SOURCE_ATTR, };
			itemInfo = new TwoDimXmlExtractor(parser, "inventory", tags, tag_attrs, subtags, subtag_attrs);
		} finally {
			inStream.close();
		}
	}
}
