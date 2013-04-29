package com.ninjadin.pfmobile.non_android;

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

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;


import android.util.Xml;

public class CharacterEditor {
	public int charLevel = 0;

	public XmlExtractor levels;
	public Map<String, String> equipment;  // <SLOT, NAME>
	
	public CharacterInfo info;
	
	private File charFile;
	private File tempFile;

	public int[] skillRanks = new int[35];
	public int pointBuyRemaining = 20;
	private int base_stats[] = { 10, 10, 10, 10, 10, 10 };
	
	public CharacterEditor(File originalFile, File temporaryFile) throws XmlPullParserException, IOException {
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
		parser.require(XmlPullParser.START_TAG, null, XmlConst.CHARTEMPLATE_TAG);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			// Run until the end of characterTemplate or END_DOCUMENT
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null)
					if (parser.getName().equals(XmlConst.CHARTEMPLATE_TAG))
						break;
			} 
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String name = parser.getName();
			int choice_id = 1;		// Used to assign a unique choice ID
			if (name != null) {
				if (name.equals(XmlConst.POINTBUY_TAG)) {
					readAbilityScores(parser);
				} else if (name.equals(XmlConst.INFO_TAG)) {
					info.readInfo(parser);
				} else if (name.equals(XmlConst.LEVELS_TAG)) {
					choice_id += readLevels(parser, choice_id);
				} else if (name.equals(XmlConst.SKILLS_TAG)) {
					readSkills(parser);
				} else if (name.equals(XmlConst.EQUIP_TAG)) {
					readEquipment(parser);
				}
			}
			if (parser.getEventType() == XmlPullParser.END_DOCUMENT)
				break;
		}
	}
	public void readEquipment(XmlPullParser parser) throws XmlPullParserException, IOException {
		equipment = new HashMap<String, String> ();
		String[] tag_names = new String[] { XmlConst.EQUIPITEM_TAG };
		String[] tag_attrs = new String[] { XmlConst.SLOT_ATTR, XmlConst.NAME_ATTR };
		XmlExtractor equipItems = new XmlExtractor(parser);
		equipItems.getData(XmlConst.EQUIP_TAG, tag_names, tag_attrs, null, null);
		for (Map<String,String> item: equipItems.groupData) {
			String slot = item.get(XmlConst.SLOT_ATTR);
			String name = item.get(XmlConst.NAME_ATTR);
			if ((slot != null) && (name != null))
				equipment.put(slot, name);
		}
	}
	// Populate level names and the choices associated with each character level
	public int readLevels(XmlPullParser parser, int choice_id) throws XmlPullParserException, IOException{
		String[] tag_names = new String[] { XmlConst.LEVEL_TAG, };
		String[] tag_attrs = new String[] { XmlConst.NUM_ATTR, };
		String[] subtag_names = new String[] { XmlConst.CHOICE_TAG, XmlConst.CHOSEN_TAG, };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.GRPNAME_ATTR, XmlConst.SUBGRP, };
		levels = new XmlExtractor(parser, 0, choice_id);
		levels.getData(XmlConst.LEVELS_TAG, tag_names, tag_attrs, subtag_names, subtag_attrs);
		charLevel = levels.groupData.size();
		return levels.subTagCount;
	}
	
	public void writeCharacterData(File temp) throws IOException {
		String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<" + XmlConst.CHARTEMPLATE_TAG + ">\n" + startTag(XmlConst.INFO_TAG);
// Create a temporary file with fresh Info/Stats
		FileOutputStream outStream;
		outStream = new FileOutputStream(temp);
		outStream.write(header.getBytes());
		info.insertInfo(outStream);
		outStream.write((endTag(XmlConst.INFO_TAG) + startTag(XmlConst.POINTBUY_TAG)).getBytes());
		writeAbilityScores(outStream);
		outStream.write((endTag(XmlConst.POINTBUY_TAG) + startTag(XmlConst.SKILLS_TAG)).getBytes());
		writeSkills(outStream);
		outStream.write((endTag(XmlConst.SKILLS_TAG) + startTag(XmlConst.LEVELS_TAG)).getBytes());
		// Equip must follow levels, see copyLevelData
		outStream.write((endTag(XmlConst.LEVELS_TAG) + startTag(XmlConst.EQUIP_TAG)).getBytes());
		outStream.write((endTag(XmlConst.EQUIP_TAG) + startTag(XmlConst.SPELLS_TAG)).getBytes());
		outStream.write((endTag(XmlConst.SPELLS_TAG)).getBytes());
		outStream.write(("</" + XmlConst.CHARTEMPLATE_TAG + ">\n").getBytes());
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
		String tagHeader = "\t\t<" + XmlConst.BONUS_TAG + " " + XmlConst.TYPE_ATTR + "=\"";
		String stackType = "\" " + XmlConst.STACKTYPE_ATTR + "=\"Ranks\" ";
		String value = " " + XmlConst.VALUE_ATTR + "=\"";
		String tagFooter = "\" />\n";
		for (int i = 0; i < PropertyLists.skillNames.length; i++) {
			if (skillRanks[i] > 0)
				outStream.write((tagHeader + PropertyLists.skillNames[i] + stackType + value + Integer.toString(skillRanks[i]) + tagFooter).getBytes());
		}
	}
	
	public void readSkills(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null) {
					if (parser.getName().equals(XmlConst.SKILLS_TAG)) {
						break;
					}
				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name != null) {
				if (name.equals(XmlConst.BONUS_TAG)) {
					String skill_name = parser.getAttributeValue(null, XmlConst.TYPE_ATTR);
					String value_str = parser.getAttributeValue(null, XmlConst.VALUE_ATTR);
					for (int i = 0; i < PropertyLists.skillNames.length; i++) {
						if (skill_name.equals(PropertyLists.skillNames[i])) {
							skillRanks[i] = Integer.parseInt(value_str);
							break;
						}
					}
				}
			}
		}
	}

	public void addLevel(InputStream charLevelData) throws IOException {
		String startData = "<" + XmlConst.LEVEL_TAG + " " + XmlConst.NUM_ATTR +"=\"" + Integer.toString(charLevel+1) + "\">";
		String endData = "</" + XmlConst.LEVEL_TAG + ">";
		String insertBefore = "</" + XmlConst.LEVELS_TAG + ">";
		XmlEditor.copyReplace(charFile, tempFile, charLevelData, startData, endData, insertBefore, insertBefore, null, null);
		tempFile.renameTo(charFile);
	}
	
	public void removeLevel() throws IOException {
		String insertBefore = "<" + XmlConst.LEVEL_TAG + " " + XmlConst.NUM_ATTR + "=\"" + Integer.toString(charLevel) + "\">";
		String continueOn = "</" + XmlConst.LEVELS_TAG + ">";
		XmlEditor.copyReplace(charFile, tempFile, null, null, null, insertBefore, continueOn, null, null);
		tempFile.renameTo(charFile);
	}
	
	public void equipItem(String slot, String name) throws IOException {
		String insertBefore = "<" + XmlConst.EQUIPITEM_TAG + " " + XmlConst.SLOT_ATTR + "=\"" + slot;
		String continueOn = "</" + XmlConst.EQUIPITEM_TAG + ">";
		String customBefore = "<" + XmlConst.EQUIPITEM_TAG + " " + XmlConst.SLOT_ATTR + "=\"" + slot + 
				"\" " + XmlConst.NAME_ATTR + "=\"" + name + "\">";
		XmlEditor.copyReplace(charFile, tempFile, null, null, null, insertBefore, continueOn, customBefore, null);
		tempFile.renameTo(charFile);
	}

	private void copyChoiceData(File sourceChar, File destChar, File levelData) throws IOException {
		String startData = "<" + XmlConst.LEVELS_TAG + ">";
		String endData = "</" + XmlConst.EQUIP_TAG + ">";
		String insertBefore = startData;
		String continueOn = "<" + XmlConst.SPELLS_TAG + ">";
		InputStream fromStream = new FileInputStream(levelData);
		XmlEditor.copyReplace(sourceChar, destChar, fromStream, startData, endData, insertBefore, continueOn, null, null);
		fromStream.close();
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
			if (sourceLine.trim().startsWith("<" + XmlConst.CHOICE_TAG) || (sourceLine.trim().startsWith("<" + XmlConst.CHOSEN_TAG))) {
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
			if (dataLine.trim().startsWith("<" + XmlConst.BONUSGRP + " " + XmlConst.GRPNAME_ATTR + "=\"" + groupName + "\""))
				break;
			dataLine = choiceInput.readLine();
		}
		// Find selectionName in groupName in dataFile
		while (dataLine != null) {
			if (dataLine.trim().startsWith("<" + XmlConst.SELECTION_TAG + " " + XmlConst.NAME_ATTR + "=\"" + selectionName))
				break;
			dataLine = choiceInput.readLine();
		}
		// Insert chosen in place of choice/chosen
		destChar.write("<" + XmlConst.CHOSEN_TAG + " " + XmlConst.GRPNAME_ATTR + "=\"" + groupName + "\"");
		if (subGroup != null) {
			destChar.write(" " + XmlConst.SUBGRP + "=\"" + subGroup + "\"");
		}
		destChar.write(" " + XmlConst.NAME_ATTR + "=\"" + selectionName + "\">");
		destChar.newLine();
		dataLine = choiceInput.readLine();
		while (dataLine != null) {
			if (dataLine.trim().startsWith("</" + XmlConst.SELECTION_TAG + ">"))
				break;
			destChar.write(dataLine);
			destChar.newLine();
			dataLine = choiceInput.readLine();
		}
		destChar.write(endTag(XmlConst.CHOSEN_TAG));
		destChar.newLine();
		// Skip to the end of source choice/chosen
		if (sourceLine.trim().startsWith("<" + XmlConst.CHOICE_TAG) == false) {
			while (sourceLine != null) {	// FIXME have to differentiate between CHOICE and CHOSEN due to nested choices
				if (sourceLine.trim().startsWith(endTag(XmlConst.CHOSEN_TAG)))
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
		String tagHeader = "\t\t<" + XmlConst.BONUS_TAG + " " + XmlConst.TYPE_ATTR + "=\"";
		String stackType = "\" " + XmlConst.STACKTYPE_ATTR + "=\"Ranks\" ";
		String value = " " + XmlConst.VALUE_ATTR + "=\"";
		String tagFooter = "\" />\n";
		for (int i = 0; i < PropertyLists.abilityScoreNames.length; i++) {
			fpOut.write((tagHeader + PropertyLists.abilityScoreNames[i] + stackType + value + Integer.toString(base_stats[i]) + tagFooter).getBytes());
		}
		fpOut.write((tagHeader + "points" + "\"" + value + Integer.toString(pointBuyRemaining) + tagFooter).getBytes());
	}
	
	public void readAbilityScores(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null) {
					if (parser.getName().equals(XmlConst.POINTBUY_TAG)) {
						break;
					}
				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name != null) {
				if (name.equals(XmlConst.BONUS_TAG)) {
					String stat_name = parser.getAttributeValue(null, XmlConst.TYPE_ATTR);
					String value_str = parser.getAttributeValue(null, XmlConst.VALUE_ATTR);
					if ((stat_name != null) && (value_str != null)) {
						for (int i = 0; i < PropertyLists.abilityScoreNames.length; i++) {
							if (stat_name.equals(PropertyLists.abilityScoreNames[i])) {
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
