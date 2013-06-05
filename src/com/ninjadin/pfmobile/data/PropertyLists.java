package com.ninjadin.pfmobile.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyLists {

	final public static String statisticPrereq = "Statistic";
	final public static String prerequisite = "Prerequisite";
	final public static String keyNames[] = new String[] { "Statistic", "Status", prerequisite, "Equipment", 
		"Proficiency", "Attack Source", "Attack Target", "Damage Source", "Damage Target", };
	
	final public static String statusNames[] = new String[] { "Unarmored", "Light Armor", "Medium Armor",
		"Heavy Armor", "No Off Hand", "Light Off Hand", "One-handed Off Hand", "No Main Hand", "Light Main Hand",
		"One-handed Main Hand", "Two-handed", };
	
	final public static String bonusSources[] = new String[] { "Natural", "Supernatural", 
		"Magical", };
	
	final public static String ranks = "Ranks";
	final public static String stackableTypes[] = new String[] { "Base", "Racial", "Trait", "Feat",
	"Dodge", "Class", "Inherent", ranks};

	final public static String class_skill = "Class Skill";
	final public static String notStackableTypes[] = new String[] { "Unnamed", "Aid Another", 
	"Flank", "Circumstance", "Divine", "Profane", "Alchemical", "Enhancement", class_skill,
	"Competence", "Ability", "Morale", "Armor", "Deflection", "Luck", "Armor Enhancement",
	"Morale", "Natural Armor", "Size", "Range", "High Ground", "Shield", "Shield Enhancement", };

	final public static String[] skillNames = new String[] { "Acrobatics", "Appraise", "Bluff",
	"Climb", "Craft", "Diplomacy", "Disable Device", "Disguise", "Escape Artist", "Fly",
	"Handle Animal", "Heal", "Intimidate", "Arcana", "Dungeoneering", "Engineering",
	"Geography", "History", "Local", "Nature", "Nobility", "Planes", "Religion",
	"Linguistics", "Perception", "Perform", "Profession", "Ride", "Sense Motive",
	"Sleight Of Hand", "Spellcraft", "Stealth", "Survival", "Swim", "Use Magic Device" };

	final public static String[] classLevelNames = new String[] { "Fighter Level", "Rogue Level",
	"Wizard Level", "Sorcerer Level", "Paladin Level", "Ranger Level", "Monk Level", "Barbarian Level",
	"Bard Level", "Druid Level", "Cleric Level" };

	final public static String abilityScoreNames[] = new String[] { "Strength", "Dexterity", 
	"Constitution", "Intelligence", "Wisdom", "Charisma", };

	final public static String abilityModifierNames[] = new String[] {"Strength Modifier", "Dexterity Modifier",
	"Constitution Modifier", "Intelligence Modifier", "Wisdom Modifier", "Charisma Modifier" };

	final public static String basicStatsNames[] = new String[] {  "Character Level", "Base Attack", 
		 "Combat Maneuver Bonus", "Hit Points", "Fortitude", "Reflex", "Will", "Armor Class", "Touch Armor Class", 
		 "Flat Footed Armor Class", "Combat Maneuver Defense", };

	final public static String reductionNames[] = new String[] { "Acid Resistance", "Electricity Resistance", 
		"Fire Resistance", "Cold Resistance", "Sonic Resistance", "Force Resistance" };
	
	final public static String speedNames[] = new String[] { "Speed", "Fly Speed", 
		"Climb Speed", "Swim Speed", "Burrow Speed", "Stealth Speed", "Crawl Speed"};
	
	// Allows the tracking of all caster level types independently
	// Prestige classes can be implemented as a choice of caster level advancement
	final public static String casterLevelNames[] = new String[] { "Wizard Caster Level",
		"Sorcerer Caster Level", "Ranger Caster Level", "Paladin Caster Level", "Bard Caster Level",
		"Druid Caster Level", "Cleric Caster Level", "Arcane Caster Level", "Divine Caster Level" };
	
	// Tracking Spell Failure sources allows easier implementation of armored spellcasting classes like Bard
	final public static String equipRelatedNames[] = new String[] { "Bard Spell Failure", "Wizard Spell Failure",
		"Sorcerer Spell Failure", "Magus Spell Failure", "Light Armor Spell Failure",
		"Medium Armor Spell Failure", "Heavy Armor Spell Failure", "Shield Spell Failure", "Armor Check",
		"Maximum Dexterity Bonus", "Encumbrance", "Equipment Cost", };
	
	final public static String otherStatisticNames[] = new String[] { "Skill Points",	
		"Spell Resistance", "Initiative", "Spell Penetration", "Sneak Attack Dice" };

	final static public String[] slotNames = new String[] { "Head", "Headband", "Wrists", "Feet",
		"Eyes", "Neck", "Shoulders", "Chest", "Body", "Armor", "Hands", "Ring 1", "Ring 2", "Belt", "Held 1", "Held 2", };

	final static public String[] itemTypes = new String[] { "Item", "Simple Weapon", "Martial Weapon",
		"Exotic Weapon", "Light Shield", "Heavy Shield", "Tower Shield", "Light Armor", "Medium Armor",
		"Heavy Armor", };

	final static public String[] itemBonuses = new String[] { "Flat Footed Armor Class", "Armor Class", };

	final static public String[] armorQualities = new String[] { "Armor", "Max Dex", "Armor Check",
		"Spell Failure", "Proficiency", "Size"};

	// Weapon specific attributes
	final static public String[] attackProperties = new String[] { "To Hit", "Range",
		"Critical Range", "Critical Multiplier", };

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

	// If this is changed, you must change statisticData() below
	final public static String categories[] = new String[] { "Ability Scores", "Basic Stats", "Skills", "Class Levels",
		"Damage Reduction", "Speed", "Caster Levels", "Equipment", "Equipment Related", "Other"};

	final static public List<Map<String,String>> categoryData() {
		return toListMap(categories);
	}
	
	final static public List<List<Map<String,String>>> statisticData() {
		List<List<Map<String,String>>> itemData = new ArrayList<List<Map<String,String>>>();
		itemData.add(toListMap(abilityScoreNames));
		itemData.add(toListMap(basicStatsNames));
		itemData.add(toListMap(skillNames));
		itemData.add(toListMap(classLevelNames));
		itemData.add(toListMap(reductionNames));
		itemData.add(toListMap(speedNames));
		itemData.add(toListMap(casterLevelNames));
		itemData.add(toListMap(slotNames));
		itemData.add(toListMap(equipRelatedNames));
		itemData.add(toListMap(otherStatisticNames));
		return itemData;
	}
	
	final static private List<Map<String,String>> toListMap(String[] stringArray) {
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		for (String string: stringArray) {
			Map<String,String> newMap = new HashMap<String,String>();
			newMap.put(XmlConst.NAME_ATTR, string);
			listMap.add(newMap);
		}
		return listMap;
	}
}
