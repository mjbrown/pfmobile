package com.ninjadin.pfmobile.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyLists {
	
	final public static String checkbox = "Check";
	final public static String spinner = "Spinner";
	final public static String number = "Number";
	final public static String text = "Text";
	final public static String spell = "Spell";
	
	final public static String intelligenceCasters[] = { "Alchemist", "Wizard", "Magus", "Witch",	};
	final public static String wisdomCasters[] = { "Cleric", "Druid", "Inquisitor", "Paladin", "Ranger", };
	final public static String charismaCasters[] = { "Bard", "Oracle", "Summoner", "Sorcerer" };
	
	final public static String manual = "Manual";
	
	final public static String all = "All";
	final public static String actionCosts[] = { "Full Round", "Standard",
		"Move", "Swift", "Immediate", "Free" };

	final public static String equals = "EQ";

	final public static String prerequisite = "Prerequisite";
	final public static String keyNames[] = new String[] { "Statistic", "Status", prerequisite, "Equipment", 
		"Proficiency", "Attack Source", "Attack Target", "Damage Source", "Damage Target", "Activated", };
	
	final public static String ranks = "Ranks";
	final public static String stackableTypes[] = new String[] { "Base", "Racial", "Trait", "Feat",
	"Dodge", "Class", "Inherent", ranks};

	final public static String class_skill = "Class Skill";
	final public static String notStackableTypes[] = new String[] { "Max HP First Level", "Unnamed", "Aid Another", 
	"Flank", "Circumstance", "Divine", "Profane", "Alchemical", "Enhancement", class_skill, "Favored Class",
	"Competence", "Ability", "Morale", "Armor", "Deflection", "Luck", "Armor Enhancement", "Nonproficient",
	"Morale", "Natural Armor", "Size", "Range", "High Ground", "Shield", "Shield Enhancement", };

	final public static String[] skillNames = new String[] { "Acrobatics", "Appraise", "Bluff",
	"Climb", "Craft", "Diplomacy", "Disable Device", "Disguise", "Escape Artist", "Fly",
	"Handle Animal", "Heal", "Intimidate", "Arcana", "Dungeoneering", "Engineering",
	"Geography", "History", "Local", "Nature", "Nobility", "Planes", "Religion",
	"Linguistics", "Perception", "Perform", "Profession", "Ride", "Sense Motive",
	"Sleight Of Hand", "Spellcraft", "Stealth", "Survival", "Swim", "Use Magic Device" };

	final public static String abilityScoreNames[] = new String[] { "Strength", "Dexterity", 
	"Constitution", "Intelligence", "Wisdom", "Charisma", };

	final public static String abilityModifierNames[] = new String[] {"Strength Modifier", "Dexterity Modifier",
	"Constitution Modifier", "Intelligence Modifier", "Wisdom Modifier", "Charisma Modifier" };

	final public static String point_buy_cost = "Point Buy Cost";
	final public static String skill_ranks_used = "Skill Ranks Used";
	final public static String favored_points_used = "Favored Class Points Used";
	final public static String favored_points = "Favored Class Points";
	final public static String hit_points = "Hit Points";
	final public static String skill_points = "Skill Points";
	final public static String pointsNames[] = { hit_points, skill_points };

	final public static String offensiveStats[] = new String[] { "Base Attack",
		"Combat Maneuver Bonus", "Spell Penetration" };
	
	final public static String defensiveStats[] = new String[] { "Fortitude", "Reflex", "Will",
		"Armor Class", "Touch Armor Class", "Flat Footed Armor Class", "Combat Maneuver Defense",
		};
	
	final public static String speedNames[] = new String[] { "Speed", "Fly Speed", 
		"Climb Speed", "Swim Speed", "Burrow Speed", "Stealth Speed", "Crawl Speed"};
	
	// Tracking Spell Failure sources allows easier implementation of armored spellcasting classes like Bard
	final public static String equipRelatedNames[] = new String[] {  "Light Armor Spell Failure",
		"Medium Armor Spell Failure", "Heavy Armor Spell Failure", "Shield Spell Failure", "Armor Check",
		"Maximum Dexterity Bonus", "Encumbrance", "Equipment Cost", };
	
	final public static String otherStatisticNames[] = new String[] { 	
		"Spell Resistance", "Initiative", "Spell Penetration" };

	final static public String inventory = "Inventory";
	final static public String[] slotNames = new String[] { "Head", "Headband", "Wrists", "Feet",
		"Eyes", "Neck", "Shoulders", "Chest", "Body", "Armor", "Hands", "Ring", "Belt", "Held", 
		inventory };

	// Weapon specific attributes
	final static public String to_hit = "To Hit";
	final static public String range = "Range";
	final static public String crit_range = "Critical Range";

	final static public String spell_level = "Spell Level";
	final static public String spell_failure = "Spell Failure";
	final static public String save_dc = "Save DC";
	final static public String caster_level = "Caster Level";
	//final static public String 
	final static public String spell_statistics[] = { spell_level, spell_failure, save_dc,
		caster_level, };
	final static public String self_only = "Self Only";
	final static public String self_weapon = "Self Weapon";
	final static public String target_weapon = "Weapon";
	final static public String target_single = "Single";
	
	final static public String[] criticalRangeStrings = new String[] { "20", "19-20", "18-20", 
		"17-20", "16-20", "15-20", "14-20", "13-20", "12-20", "11-20", "10-20", };
	
	final static public String[] die = {"d2", "d3", "d4", "d6", "d8", "d10", "d12", "d20" };
	final static public String damage = "Damage";

	final static public String character_size = "Character Size";
	final static public String small_damage[] =  {"0", "1",   "1d2", "1d3", "1d4", "1d6", "1d8",  "1d10", "2d6", "2d8","1d10", "1d6" };
	final static public String medium_size_weapon_damage = "Medium Size Weapon Damage";
	final static public String medium_damage[] = {"0", "1d2", "1d3", "1d4", "1d6", "1d8", "1d10", "2d6", "2d8", "2d10", "1d12", "2d4",  };
	final static public String large_damage[] =  {"0", "1d3", "1d4", "1d6", "1d8", "2d6", "2d8",  "3d6", "3d8", "4d8", "3d6",  "2d6",  };

	// Weapon damage sources
	final static public String[] damageSources = new String[] { "Piercing", "Slashing", "Bludgeoning", 
		"Fire", "Cold", "Electricity", "Sonic", "Negative Energy", "Positive Energy", "Holy", "Magic", "Bleed",
		"Adamantine", "Cold Iron", "Silver", "Poison" };

	// If this is changed, you must change statisticData() below
	final static public String ability_scores = "Ability Scores";
	final static public String hit_skill_points = "Hit/Skill Points";
	final public static String categories[] = new String[] { ability_scores, "Offensive Stats", "Defensive Stats",
		hit_skill_points, "Skills", "Speed", "Equipment Related", "Other"};

	
	final static public List<Map<String,String>> categoryData() {
		return toListMap(categories);
	}
	
	final static public List<List<Map<String,String>>> statisticData() {
		List<List<Map<String,String>>> itemData = new ArrayList<List<Map<String,String>>>();
		itemData.add(toListMap(abilityScoreNames));
		itemData.add(toListMap(offensiveStats));
		itemData.add(toListMap(defensiveStats));
		itemData.add(toListMap(pointsNames));
		itemData.add(toListMap(skillNames));
		itemData.add(toListMap(speedNames));
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
	
	final static public List<String> damageDie() {
		List<String> damage_die = new ArrayList<String>();
		for (String dice: die) {
			damage_die.add(damage + " " + dice);
		}
		return damage_die;
	}
}
