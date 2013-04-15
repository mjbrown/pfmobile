package com.example.ninjadin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class CharacterData {
	public int charLevel = 0;
	public List<Map<String, String>> levelsNames = new ArrayList<Map<String,String>>();
	public List<List<Map<String, String>>> levelsLists = new ArrayList<List<Map<String, String>>>();

	public List<Map<String, String>> equipmentNames = new ArrayList<Map<String, String>>();
	public List<List<Map<String, String>>> equipmentLists = new ArrayList<List<Map<String, String>>>();

	public CharacterInfo info;
	
	private File charFile;
	private File tempFile;

	public int[] skillRanks = new int[35];
	public int pointBuyRemaining = 20;
	private int base_stats[] = { 10, 10, 10, 10, 10, 10 };
	
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
	
	final private static String TEMPLATE_TAG = "characterTemplate";
	final private static String LEVEL_TAG = "characterLevel";
	final private static String SELECTION_TAG = "selection";
	final public static String CHOICE_TAG = "choice";
	final private static String CHOSEN_TAG = "chosen";
	final private static String POINTBUY_TAG = "pointBuy";
	final public static String INFO_TAG = "info";
	final private static String LEVELS_TAG = "levels";
	final private static String SKILLS_TAG = "skills";
	final public static String EQUIP_TAG = "equipment";
	final public static String EQUIPGRP_TAG = "equipGroup";
	final private static String SPELLS_TAG = "spells";
	final public static String BONUS_TAG = "bonus";
	final public static String PROFICIENCY_TAG = "proficiency";

	final public static String GRPNAME_ATTR = "groupName";
	final public static String SUBGRP_ATTR = "subGroup";
	final private static String NUM_ATTR = "number";
	final private static String NAME_ATTR = "name";
	final public static String TYPE_ATTR = "type";
	final public static String VALUE_ATTR = "value";
	final public static String STACKTYPE_ATTR = "stackType";
	final public static String SOURCE_ATTR = "source";

	public CharacterData(File originalFile, File temporaryFile) throws XmlPullParserException, IOException {
		charFile = originalFile;
		tempFile = temporaryFile;
		info = new CharacterInfo();
		FileInputStream inStream;
		XmlPullParser parser = Xml.newPullParser();
		inStream = new FileInputStream(charFile);
		try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inStream, null);
			parser.nextTag();
			readCharacterData(parser);
		} finally {
			inStream.close();
		}
		
	}
	
	private void readCharacterData(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, TEMPLATE_TAG);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			// Run until the end of characterTemplate or END_DOCUMENT
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null)
					if (parser.getName().equals(TEMPLATE_TAG))
						break;
			} 
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			int choice_id = 1;		// Used to assign a unique choice ID
			if (name != null) {
				if (name.equals(POINTBUY_TAG)) {
					readAbilityScores(parser);
				} else if (name.equals(INFO_TAG)) {
					info.readInfo(parser);
				} else if (name.equals(LEVELS_TAG)) {
					choice_id += readLevels(parser, choice_id);
				} else if (name.equals(SKILLS_TAG)) {
					readSkills(parser);
				} else if (name.equals(EQUIP_TAG)) {
					choice_id += readEquipment(parser, choice_id);
				}
			}
			if (parser.getEventType() == XmlPullParser.END_DOCUMENT)
				break;
		}
	}
	
	// TODO: this can be merged with readLevels because very little is different...
	private int readEquipment(XmlPullParser parser, int choice_id) throws XmlPullParserException, IOException {
		ArrayList<Map<String, String>> singleList = new ArrayList<Map<String, String>>();
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null) {
					if (parser.getName().equals(CharacterData.EQUIP_TAG)) {
						break;
					}
				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals(CharacterData.EQUIPGRP_TAG)) {
					String name = parser.getAttributeValue(null, NAME_ATTR);
					Map<String, String> curGroupMap = new HashMap<String, String>();
					equipmentNames.add(curGroupMap);
					curGroupMap.put("NAME", "Equipment: " + name);
					singleList = new ArrayList<Map<String, String>>();
					equipmentLists.add(singleList);
				} else if ((tag.equals(CharacterData.CHOICE_TAG)) || (tag.equals(CharacterData.CHOSEN_TAG))) {
					String groupName = parser.getAttributeValue(null, CharacterData.GRPNAME_ATTR);
					String subGroup = parser.getAttributeValue(null, CharacterData.SUBGRP_ATTR);
					String chosenName = parser.getAttributeValue(null, CharacterData.NAME_ATTR);
					if (chosenName == null)
						chosenName = "Tap to add...";
					Map<String, String> curChildMap = new HashMap<String, String>();
					singleList.add(curChildMap);
					if (subGroup == null) {
						subGroup = "Any";
						curChildMap.put("DESCRIPTION", groupName);
					} else {
						curChildMap.put("DESCRIPTION", subGroup + " " + groupName);
					}
					curChildMap.put("GROUP", groupName);
					curChildMap.put("SEL", chosenName);
					curChildMap.put("SUBGROUP", subGroup);
					curChildMap.put("ID", Integer.toString(choice_id++));
				}
			}
		}
		return choice_id;
	}
	// Populate level names and the choices associated with each character level
	public int readLevels(XmlPullParser parser, int choice_id) throws XmlPullParserException, IOException{
		ArrayList<Map<String, String>> singleList = new ArrayList<Map<String, String>>();
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null) {
					if (parser.getName().equals(CharacterData.LEVELS_TAG)) {
						break;
					}
				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name != null) {
				if (name.equals(CharacterData.LEVEL_TAG)) {
					charLevel = Integer.parseInt(parser.getAttributeValue(null, CharacterData.NUM_ATTR));
					Map<String, String> curGroupMap = new HashMap<String, String>();
					levelsNames.add(curGroupMap);
					curGroupMap.put("NAME", "Character Level " +Integer.toString(charLevel));
					singleList = new ArrayList<Map<String, String>>();
					levelsLists.add(singleList);
				} else if ((name.equals(CharacterData.CHOICE_TAG)) || (name.equals(CharacterData.CHOSEN_TAG))) {
					String groupName = parser.getAttributeValue(null, CharacterData.GRPNAME_ATTR);
					String subGroup = parser.getAttributeValue(null, CharacterData.SUBGRP_ATTR);
					String chosenName = parser.getAttributeValue(null, CharacterData.NAME_ATTR);
					if (chosenName == null)
						chosenName = "Tap to add...";
					Map<String, String> curChildMap = new HashMap<String, String>();
					singleList.add(curChildMap);
					if (subGroup == null) {
						subGroup = "Any";
						curChildMap.put("DESCRIPTION", groupName);
					} else {
						curChildMap.put("DESCRIPTION", subGroup + " " + groupName);
					}
					curChildMap.put("GROUP", groupName);
					curChildMap.put("SEL", chosenName);
					curChildMap.put("SUBGROUP", subGroup);
					curChildMap.put("ID", Integer.toString(choice_id++));
				}
			}
		}
		return choice_id;
	}
	
	public void writeCharacterData(File temp) throws IOException {
		String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<" + TEMPLATE_TAG + ">\n" + startTag(INFO_TAG);
// Create a temporary file with fresh Info/Stats
		FileOutputStream outStream;
		outStream = new FileOutputStream(temp);
		outStream.write(header.getBytes());
		info.insertInfo(outStream);
		outStream.write((endTag(INFO_TAG) + startTag(POINTBUY_TAG)).getBytes());
		writeAbilityScores(outStream);
		outStream.write((endTag(POINTBUY_TAG) + startTag(SKILLS_TAG)).getBytes());
		writeSkills(outStream);
		outStream.write((endTag(SKILLS_TAG) + startTag(LEVELS_TAG)).getBytes());
		// Equip must follow levels, see copyLevelData
		outStream.write((endTag(LEVELS_TAG) + startTag(EQUIP_TAG)).getBytes());
		outStream.write((endTag(EQUIP_TAG) + startTag(SPELLS_TAG)).getBytes());
		outStream.write((endTag(SPELLS_TAG)).getBytes());
		outStream.write(("</" + TEMPLATE_TAG + ">\n").getBytes());
		outStream.close();
// Copy level data from charFile to tempFile
		copyChoiceData(temp, tempFile, charFile);
		tempFile.renameTo(charFile);
	}
	static private String startTag(String tagName) {
		return "\t<" + tagName + ">\n";
	}
	static private String endTag(String tagName) {
		return "\t</" + tagName + ">\n";
	}

	public void writeSkills(FileOutputStream outStream) throws IOException {
		String tagHeader = "\t\t<" + BONUS_TAG + " " + TYPE_ATTR + "=\"";
		String stackType = "\" " + STACKTYPE_ATTR + "=\"Ranks\" ";
		String value = " " + VALUE_ATTR + "=\"";
		String tagFooter = "\" />\n";
		for (int i = 0; i < CharacterData.skillNames.length; i++) {
			if (skillRanks[i] > 0)
				outStream.write((tagHeader + CharacterData.skillNames[i] + stackType + value + Integer.toString(skillRanks[i]) + tagFooter).getBytes());
		}
	}
	
	public void readSkills(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null) {
					if (parser.getName().equals(CharacterData.SKILLS_TAG)) {
						break;
					}
				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name != null) {
				if (name.equals(BONUS_TAG)) {
					String skill_name = parser.getAttributeValue(null, TYPE_ATTR);
					String value_str = parser.getAttributeValue(null, VALUE_ATTR);
					for (int i = 0; i < CharacterData.skillNames.length; i++) {
						if (skill_name.equals(CharacterData.skillNames[i])) {
							skillRanks[i] = Integer.parseInt(value_str);
							break;
						}
					}
				}
			}
		}
	}

	public void addLevel(InputStream charLevelData) throws IOException {
		String startData = "<" + LEVEL_TAG + " " + NUM_ATTR +"=\"" + Integer.toString(charLevel+1) + "\">";
		String endData = "</" + LEVEL_TAG + ">";
		String insertBefore = "</" + LEVELS_TAG + ">";
		copyReplace(charFile, tempFile, charLevelData, startData, endData, insertBefore, insertBefore);
		tempFile.renameTo(charFile);
	}
	
	public void removeLevel() throws IOException {
		String insertBefore = "<" + LEVEL_TAG + " " + NUM_ATTR + "=\"" + Integer.toString(charLevel) + "\">";
		String continueOn = "</" + LEVELS_TAG + ">";
		copyReplace(charFile, tempFile, null, null, null, insertBefore, continueOn);
		tempFile.renameTo(charFile);
	}

	private void copyChoiceData(File sourceChar, File destChar, File levelData) throws IOException {
		String startData = "<" + LEVELS_TAG + ">";
		String endData = "</" + EQUIP_TAG + ">";
		String insertBefore = startData;
		String continueOn = "<" + SPELLS_TAG + ">";
		InputStream fromStream = new FileInputStream(levelData);
		copyReplace(sourceChar, destChar, fromStream, startData, endData, insertBefore, continueOn);
		fromStream.close();
	}

	// The only way to insert/replace is to copy the entire source file
	// copies lines "startData -> endData" from dataFile, inclusive
	// replaces lines "insertBefore -> continueOn" from sourceChar
	// if insertBefore == continueOn, this function inserts instead of replacing
	private void copyReplace(File copyFrom, File copyTo, InputStream dataFile,  
			String startData, String endData, String insertBefore, String continueOn) throws IOException {
		FileInputStream copyFromStream = new FileInputStream(copyFrom);
		InputStreamReader copyFromSR = new InputStreamReader(copyFromStream);
		BufferedReader sourceChar = new BufferedReader(copyFromSR);
		
		BufferedReader insertData = null;
		if (dataFile != null) {
			InputStreamReader dataFileSR = new InputStreamReader(dataFile);
			insertData = new BufferedReader(dataFileSR);
		}
		
		FileOutputStream copyToStream = new FileOutputStream(copyTo);
		OutputStreamWriter copyToSR = new OutputStreamWriter(copyToStream);
		BufferedWriter destChar = new BufferedWriter(copyToSR);
		
		// Find the first line of the data to be inserted
		String dataLine = null;
		if (dataFile != null) {
			dataLine = insertData.readLine();
			while (dataLine != null) {
				if (dataLine.trim().startsWith(startData)) {
					break;
				}
				dataLine = insertData.readLine();
			}
		}
		//Find the spot in the sourceChar to insert BEFORE
		String sourceLine = sourceChar.readLine();
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith(insertBefore)) {
				break;
			}
			destChar.write(sourceLine);
			destChar.newLine();
			sourceLine = sourceChar.readLine();
		}
		// Insert the new data
		if (dataFile != null) {
			while (dataLine != null) {
				destChar.write(dataLine);
				destChar.newLine();
				if (dataLine.trim().startsWith(endData)) {
					break;
				}
				dataLine = insertData.readLine();
			}
		}
		// Find the spot to continue copying from
		// note that if continueOn == insertBefore, no lines are lost
		
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith(continueOn)) {
				break;
			}
			sourceLine = sourceChar.readLine();
		}
		// Copy the remainder of the source character
		while (sourceLine != null) {
			destChar.write(sourceLine);
			destChar.newLine();
			sourceLine = sourceChar.readLine();
		}
		destChar.close();
		sourceChar.close();
	}

	public void insertChoice(InputStream dataStream, int choiceId, String groupName, String subGroup, String selectionName) throws IOException {
		InputStreamReader dataReader = new InputStreamReader(dataStream);
		BufferedReader choiceInput = new BufferedReader(dataReader);
		
		InputStream fromStream = new FileInputStream(charFile);
		InputStreamReader copyFromSR = new InputStreamReader(fromStream);
		BufferedReader sourceChar = new BufferedReader(copyFromSR);

		FileOutputStream toStream = new FileOutputStream(tempFile);
		OutputStreamWriter copyToSR = new OutputStreamWriter(toStream);
		BufferedWriter destChar = new BufferedWriter(copyToSR);
		
		// Find choiceId in sourceChar, copy as you go
		int currentChoice = 0;
		String sourceLine = sourceChar.readLine();
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith("<" + CHOICE_TAG) || (sourceLine.trim().startsWith("<chosen"))) {
				currentChoice += 1;
				if (currentChoice == choiceId) 
					break;
			}
			destChar.write(sourceLine);
			destChar.newLine();
			sourceLine = sourceChar.readLine();
		}
		// Find groupName in dataFile
		String dataLine = choiceInput.readLine();
		while (dataLine != null) {
			if (dataLine.trim().startsWith("<bonusGroup groupName=\"" + groupName + "\""))
				break;
			dataLine = choiceInput.readLine();
		}
		// Find selectionName in groupName in dataFile
		while (dataLine != null) {
			if (dataLine.trim().startsWith("<" + SELECTION_TAG + " name=\"" + selectionName))
				break;
			dataLine = choiceInput.readLine();
		}
		// Insert chosen in place of choice/chosen
		destChar.write("<chosen groupName=\"" + groupName + "\" subGroup=\"" + subGroup + "\" name=\"" + selectionName + "\">");
		destChar.newLine();
		dataLine = choiceInput.readLine();
		while (dataLine != null) {
			if (dataLine.trim().startsWith("</selection>"))
				break;
			destChar.write(dataLine);
			destChar.newLine();
			dataLine = choiceInput.readLine();
		}
		destChar.write("</chosen>");
		destChar.newLine();
		// Skip to the end of source choice/chosen
		if (sourceLine.trim().startsWith("<" + CHOICE_TAG) == false) {
			while (sourceLine != null) {	// FIXME have to differentiate between CHOICE and CHOSEN due to nested choices
				if (sourceLine.trim().startsWith("</chosen>"))
					break;
				sourceLine = sourceChar.readLine();
			}
		}
		// Copy the remainder of source character
		sourceLine = sourceChar.readLine();
		while (sourceLine != null) {
			destChar.write(sourceLine);
			destChar.newLine();
			sourceLine = sourceChar.readLine();
		}
		destChar.close();
		sourceChar.close();
		tempFile.renameTo(charFile);
	}
	public void writeAbilityScores(FileOutputStream fpOut) throws IOException {
		String tagHeader = "\t\t<" + BONUS_TAG + " " + TYPE_ATTR + "=\"";
		String stackType = "\" " + STACKTYPE_ATTR + "=\"Ranks\" ";
		String value = " " + VALUE_ATTR + "=\"";
		String tagFooter = "\" />\n";
		for (int i = 0; i < abilityScoreNames.length; i++) {
			fpOut.write((tagHeader + abilityScoreNames[i] + stackType + value + Integer.toString(base_stats[i]) + tagFooter).getBytes());
		}
		fpOut.write((tagHeader + "points" + "\"" + value + Integer.toString(pointBuyRemaining) + tagFooter).getBytes());
	}
	
	public void readAbilityScores(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null) {
					if (parser.getName().equals(CharacterData.POINTBUY_TAG)) {
						break;
					}
				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name != null) {
				if (name.equals(BONUS_TAG)) {
					String stat_name = parser.getAttributeValue(null, TYPE_ATTR);
					String value_str = parser.getAttributeValue(null, VALUE_ATTR);
					if ((stat_name != null) && (value_str != null)) {
						for (int i = 0; i < abilityScoreNames.length; i++) {
							if (stat_name.equals(abilityScoreNames[i])) {
								base_stats[i] = Integer.parseInt(value_str);
							}
						}
						if (stat_name.equals("points")) {
							pointBuyRemaining = Integer.parseInt(value_str);
						}
					}
				}
			}
		}
	}
	
	public int abilityScoreIncreaseCost(int stat) {
		int retValue = 50;
		if ((base_stats[stat] == 7) || (base_stats[stat] == 13) || (base_stats[stat] == 14)) {
			retValue = 2;
		} else if (base_stats[stat] < 14) {
			retValue = 1;
		} else if ((base_stats[stat] == 15) || (base_stats[stat] == 16)) {
			retValue = 3;
		} else if (base_stats[stat] == 17) {
			retValue = 4;
		}
		return retValue;
	}
	
	public boolean canIncrementAbilityScore(int stat) {
		boolean retValue = true;
		if (base_stats[stat] == 18) {
			retValue = false;
		} else if (abilityScoreIncreaseCost(stat) > pointBuyRemaining) {
			retValue = false;
		}
		return retValue;
	}
	
	public void incrementAbilityScore(int stat) {
		pointBuyRemaining -= abilityScoreIncreaseCost(stat); 
		base_stats[stat] += 1;
	}
	
	public boolean canDecrementAbilityScore(int stat) {
		if (base_stats[stat] > 7)
			return true;
		return false;
	}
	
	public void decrementAbilityScore(int stat) {
		base_stats[stat] -= 1;
		pointBuyRemaining += abilityScoreIncreaseCost(stat);
	}

	public String abilityScoreModifier(int stat) {
		int modifier = (base_stats[stat] / 2) - 5;
		if (modifier >= 0) {
			return "+".concat(Integer.toString(modifier));
		}
		return "-".concat(Integer.toString(modifier*-1));
	}
	
	public int getAbilityScore(int score) {
		return base_stats[score];
	}

}
