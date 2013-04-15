package com.ninjadin.pfmobile.non_android;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import android.util.Xml;

public class FilterSelect {
	public String selectGroupName;
	public String subGroupName;
	public DependencyManager dependencyManager;
	public List<Map<String, String>> selectionNames = new ArrayList<Map<String, String>>();
	public List<List<Map<String, String>>> selectionDesc = new ArrayList<List<Map<String, String>>>();
	public List<List<Map<String, String>>> selectionPrereqs = new ArrayList<List<Map<String, String>>>();
	
	public FilterSelect(InputStream charFile, InputStream dataFile, String groupName, String subGroup, DependencyManager manager) throws XmlPullParserException, IOException {
		selectGroupName = groupName;
		subGroupName = subGroup;
		dependencyManager = manager;
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(dataFile, null);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals("bonusGroup")) {
					String group = parser.getAttributeValue(null, "groupName");
					if (group != null) {
						if (group.equals(selectGroupName)) {
							if (subGroupName.equals("Any"))
								getSelections(parser, "bonusGroup");
							else
								findSubGroup(parser);
							break;
						}
					}
				}
			}
		}
	}
	private void findSubGroup(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals("subGroup")) {
					String group = parser.getAttributeValue(null, "groupName");
					if (group != null) {
						if (group.equals(subGroupName)) {
							getSelections(parser, "subGroup");
							break;
						}
					}
				}
			}
		}
	}
	private void getSelections(XmlPullParser parser, String endTag) throws XmlPullParserException, IOException{
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName().equals(endTag))
					break;
				if (parser.getName().equals("prerequisite")) {

				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals("selection")) {
					String name = parser.getAttributeValue(null, "name");
					if (name != null) {
						Map<String, String> newName = new HashMap<String, String>();
						newName.put("name", name);
						newName.put("description", "Tap here to add.");
						selectionNames.add(newName);
						List<Map<String, String>> newListDesc = getSelectionContent(parser, tag);
						selectionDesc.add(newListDesc);
					}
				} else if (tag.equals("weapon")) {
					String name = parser.getAttributeValue(null, "name");
					String type = parser.getAttributeValue(null, "type");
					String source = parser.getAttributeValue(null, "source");
					String critRange = parser.getAttributeValue(null, "criticalRange");
					String multiplier = parser.getAttributeValue(null, "multiplier");
					String special = parser.getAttributeValue(null, "special");
					String dice = parser.getAttributeValue(null, "dice");
					if ((name != null) && (type != null) && (source != null) && (dice != null)) {
						Map<String, String> newWeapon = new HashMap<String, String>();
						newWeapon.put("name", name);
						newWeapon.put("type", type);
						newWeapon.put("source", source);
						newWeapon.put("dice", dice);
						if (critRange != null)
							newWeapon.put("criticalRange", critRange);
						else
							newWeapon.put("criticalRange", "[20]");
						if (multiplier != null)
							newWeapon.put("multiplier", multiplier);
						else
							newWeapon.put("multiplier", "2");
						if (special != null)
							newWeapon.put("special", special);
						List<Map<String, String>> newListDesc = getSelectionContent(parser, tag);
						selectionNames.add(newWeapon);
						selectionDesc.add(newListDesc);
					}
				} else if (tag.equals("armor")) {
					
				} else if (tag.equals("prerequisite")) {
					
				}
			}
		}
	}
	
	private List<Map<String, String>> getSelectionContent(XmlPullParser parser, String endTag) throws XmlPullParserException, IOException {
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName().equals(endTag)) 
					break;
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = parser.getName();
			if (tag != null) {
				if (tag.equals(CharacterData.BONUS_TAG)) {
					String types = parser.getAttributeValue(null, CharacterData.TYPE_ATTR);
					String value = parser.getAttributeValue(null, CharacterData.VALUE_ATTR);
					String stackType = parser.getAttributeValue(null, CharacterData.STACKTYPE_ATTR);
					if ((types != null) && (value != null)) {
						for (String type: types.split(",")) {
							Map<String, String> description = new HashMap<String, String>();
							int integerValue = dependencyManager.evaluateValue(value);
							if (integerValue > -1)
								description.put("description", type + " +" + Integer.toString(integerValue));
							else
								description.put("description", type + " " + Integer.toString(integerValue));
							if (stackType != null)
								description.put("additional", stackType + " Bonus");
							content.add(description);
						}
					}
				} else if (tag.equals(CharacterData.PROFICIENCY_TAG)) {
					String types = parser.getAttributeValue(null, CharacterData.TYPE_ATTR);
					String value = parser.getAttributeValue(null, CharacterData.VALUE_ATTR);
					if ((types != null) && (value != null)) {
						for (String type: types.split(",")) {
							Map<String, String> description = new HashMap<String, String>();
							description.put("description", type);
							description.put("additional", value);
							content.add(description);
						}
					}
				} else if (tag.equals(CharacterData.CHOICE_TAG)) {
					String groupName = parser.getAttributeValue(null, CharacterData.GRPNAME_ATTR);
					String subGroup = parser.getAttributeValue(null, CharacterData.SUBGRP_ATTR);
					if (groupName != null) {
						Map<String, String> description = new HashMap<String, String>();
						description.put("description", groupName);
						if (subGroup != null) {
							description.put("additional", subGroup);
						} else {
							description.put("additional", "Any");
						}
						content.add(description);
					}
				}
			}
		}
		return content;
	}
	
}
