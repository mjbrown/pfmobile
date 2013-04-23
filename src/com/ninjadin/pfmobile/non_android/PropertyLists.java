package com.ninjadin.pfmobile.non_android;

public class PropertyLists {

	final public static String stackableTypes[] = new String[] { "Base", "Racial", "Trait", "Feat",
	"Dodge", "Class", "Inherent", "Ranks"};

	final public static String notStackableTypes[] = new String[] { "Unnamed", "Aid Another", 
	"Flank", "Circumstance", "Divine", "Profane", "Alchemical", "Enhancement", "Class Skill",
	"Competence", "Ability", "Morale", "Armor", "Armor Enhancement", "Deflection", "Luck",
	"Morale", "Natural Armor", "Size", "Range", "High Ground", "Shield", "Shield Enhancement", };

	final public static String[] skillNames = new String[] { "Acrobatics", "Appraise", "Bluff",
	"Climb", "Craft", "Diplomacy", "Disable Device", "Disguise", "Escape Artist", "Fly",
	"Handle Animal", "Heal", "Intimidate", "Arcana", "Dungeoneering", "Engineering",
	"Geography", "History", "Local", "Nature", "Nobility", "Planes", "Religion",
	"Linguistics", "Perception", "Perform", "Profession", "Ride", "Sense Motive",
	"Sleight Of Hand", "Spellcraft", "Stealth", "Survival", "Swim", "Use Magic Device" };

	final public static String[] classLevelNames = new String[] { "Fighter Level", "Rogue Level",
	"Wizard Level", "Sorcerer Level", "Paladin Level", "Ranger Level", "Monk Level", "Barbarian Level",
	"Bard Level" };

	final public static String abilityScoreNames[] = new String[] { "Strength", "Dexterity", 
	"Constitution", "Intelligence", "Wisdom", "Charisma", };

	final public static String abilityModifierNames[] = new String[] {"Strength Modifier", "Dexterity Modifier",
	"Constitution Modifier", "Intelligence Modifier", "Wisdom Modifier", "Charisma Modifier" };

	final public static String saveNames[] = new String[] { "Fortitude", "Reflex", "Will" };

	final public static String armorClassNames[] = new String[] { "Armor Class", 
	"Touch Armor Class", "Flat Footed Armor Class",  "Combat Maneuver Defense"};

	final public static String reductionNames[] = new String[] { "Acid Resistance",
	"Electricity Resistance", "Fire Resistance", "Cold Resistance", "Sonic Resistance",
	"Force Resistance" };
	
	final public static String speedNames[] = new String[] { "Speed", "Fly Speed", 
	"Climb Speed", "Swim Speed", "Burrow Speed", "Stealth Speed", "Crawl Speed"};
	
	// Allows the tracking of all caster level types independently
	// Prestige classes can be implemented as a choice of caster level advancement
	final public static String casterStatisticNames[] = new String[] { "Wizard Caster Level",
		"Sorcerer Caster Level", "Ranger Caster Level", "Paladin Caster Level", "Bard Caster Level",
		"Druid Caster Level", "Cleric Caster Level", "Arcane Caster Level", "Divine Caster Level" };
	
	// Tracking Spell Failure sources allows easier implementation of armored spellcasting classes like Bard
	final public static String spellFailureNames[] = new String[] { "Arcane Spell Failure", "Light Armor Spell Failure",
		"Medium Armor Spell Failure", "Heavy Armor Spell Failure", "Shield Spell Failure", };
	
	final public static String otherStatisticNames[] = new String[] { "Character Level", "Base Attack", 
	"Armor Check", "Hit Points", "Maximum Dexterity Bonus", "Combat Maneuver Bonus", "Skill Points",
	"Spell Resistance", "Initiative", "Spell Penetration", "Encumbrance", "Equipment Cost", "Sneak Attack Dice" };

	final static public String[] slotNames = new String[] { "Head", "Headband", "Wrists", "Feet",
	"Eyes", "Neck", "Shoulders", "Chest", "Body", "Armor", "Hands", "Ring", "Belt", "Held", };

	final static public String[] itemTypes = new String[] { "Item", "Simple Weapon", "Martial Weapon",
	"Exotic Weapon", "Light Shield", "Heavy Shield", "Tower Shield", "Light Armor", "Medium Armor",
	"Heavy Armor", };

	final static public String[] itemQualities = new String[] { "Cost", "Encumbrance", "Hit Points",
	"Hardness" };

	final static public String[] armorQualities = new String[] { "Armor", "Max Dex", "Armor Check",
	"Spell Failure", "Proficiency", "Size"};

	// Weapon specific attributes
	final static public String[] weaponQualities = new String[] { "Range", "To Hit", 
		"Critical Range", "Critical Multiplier", "Proficiency", "Size"};

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

}
