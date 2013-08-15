package com.ninjadin.pfmobile.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyLists {
	final public static int[] bard_per_day =     { 1,2,3,3,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5 };
	final public static int[] bard_known =       { 2,3,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,5,5 };

	final public static int[] cleric_per_day =   { 1,2,2,3,3,3,4,4,4,4,4,4,4,4,4,4,4,4,4,4 };

	final public static int[] paladin_per_day =  { 0,0,0,0,1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4 };

	final public static int[] sorcerer_per_day = { 3,4,5,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6 };
	final public static int[] sorcerer_known =   { 1,2,2,3,3,4,4,5,5,5,5,5,5,5,5,5,5,5,5,5 };

	final public static String manual = "Manual";
	
	final public static String all = "All";
	final public static String actionCosts[] = { "Full Round", "Standard",
		"Move", "Swift", "Immediate", "Free" };

	final public static String statisticPrereq = "Statistic";
	final public static String prerequisite = "Prerequisite";
	final public static String activated = "Activated";
	final public static String equipment = "Equipment";
	final public static String keyNames[] = new String[] { "Statistic", "Status", prerequisite, "Equipment", 
		"Proficiency", "Attack Source", "Attack Target", "Damage Source", "Damage Target", "Activated", };
	
	final public static String statusNames[] = new String[] { "Unarmored", "Light Armor", "Medium Armor",
		"Heavy Armor", "No Off Hand", "Light Off Hand", "One-handed Off Hand", "No Main Hand", "Light Main Hand",
		"One-handed Main Hand", "Two-handed", };
	
	final public static String bonusSources[] = new String[] { "Natural", "Supernatural", 
		"Magical", };
	
	final public static String ranks = "Ranks";
	final public static String stackableTypes[] = new String[] { "Base", "Racial", "Trait", "Feat",
	"Dodge", "Class", "Inherent", ranks};

	final public static String class_skill = "Class Skill";
	final public static String notStackableTypes[] = new String[] { "Max HP First Level", "Unnamed", "Aid Another", 
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
	"Bard Level", "Druid Level", "Cleric Level", "Oracle Level" };

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

	final static public String inventory = "Inventory";
	final static public String[] slotNames = new String[] { "Head", "Headband", "Wrists", "Feet",
		"Eyes", "Neck", "Shoulders", "Chest", "Body", "Armor", "Hands", "Ring 1", "Ring 2", "Belt", 
		inventory };

	// Weapon specific attributes
	final static public String to_hit = "To Hit";
	final static public String range = "Range";
	final static public String crit_range = "Critical Range";
	final static public String[] attackProperties = new String[] { to_hit, "Range",
		crit_range, "Save DC", "Number Of Uses",};

	final static public String[] criticalRangeStrings = new String[] { "20", "19-20", "18-20", 
		"17-20", "16-20", "15-20", "14-20", "13-20", "12-20", "11-20", "10-20", };
	
	final static public String[] die = {"d2", "d3", "d4", "d6", "d8", "d10", "d12", "d20" };
	final static public String damage = "Damage";

	final static public String crit_multiplier = "Critical Multiplier";
	final static public String[] damageProperties = new String[] { 
		"Condition", crit_multiplier, "Duration", "Number Of Uses", };
	
	// Weapon damage sources
	final static public String[] damageSources = new String[] { "Piercing", "Slashing", "Bludgeoning", 
		"Fire", "Cold", "Electricity", "Sonic", "Negative Energy", "Positive Energy", "Holy", "Magic", "Bleed",
		"Adamantine", "Cold Iron", "Silver", "Poison" };

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
	
	final static public List<String> stat_list() {
		List<String> stat_names = new ArrayList<String>();
		for (String item: abilityScoreNames)
			stat_names.add(item);
		for (String item: abilityModifierNames)
			stat_names.add(item);
		for (String item: basicStatsNames)
			stat_names.add(item);
		for (String item: otherStatisticNames)
			stat_names.add(item);
		for (String item: reductionNames)
			stat_names.add(item);
		for (String item: speedNames)
			stat_names.add(item);
		for (String item: casterLevelNames)
			stat_names.add(item);
		for (String item: skillNames)
			stat_names.add(item);
		for (String item: classLevelNames)
			stat_names.add(item);
		for (String item: equipRelatedNames)
			stat_names.add(item);
		for (String item: attackProperties)
			stat_names.add(item);
		for (String item: damageProperties)
			stat_names.add(item);
		return stat_names;
	}
	
	final static public List<String> damageDie() {
		List<String> damage_die = new ArrayList<String>();
		for (String dice: die) {
			damage_die.add(damage + " " + dice);
		}
		return damage_die;
	}
}
