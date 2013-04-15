package com.example.ninjadin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class CharacterEquipment {
	public List<Map<String, String>> equipNames = new ArrayList<Map<String,String>>();
	public List<List<Map<String, String>>> equipList = new ArrayList<List<Map<String, String>>>();

	// Populate equipment slots and choices associated with each slot
	public int readEquipment(XmlPullParser parser, int choice_id) throws XmlPullParserException, IOException {
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
			String name = parser.getName();
			if (name != null) {
			}
		}
		return choice_id;
	}
}
