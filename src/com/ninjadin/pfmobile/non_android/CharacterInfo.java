package com.ninjadin.pfmobile.non_android;

import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class CharacterInfo {
	public String characterName;
	public String deity;
	public int align;
	public String gender;

	final private static String CHARNAME_TAG = "characterName";
	final private static String CHARDEITY_TAG = "characterDeity";
	final private static String ALIGN_TAG = "alignment";
	final private static String GENDER_TAG = "gender";
	
	final private static String VALUE_ATTR = "value";
	final public static String MALE_VALUE = "male";
	final public static String FEMALE_VALUE = "female";
	final public static String ANDRO_VALUE = "androgynous";
	
	public void insertInfo(FileOutputStream outStream) throws IOException {
		String charName = "\t\t<" + CHARNAME_TAG + ">" + characterName + "</" + CHARNAME_TAG + ">\n";
		String charDeity = "\t\t<" + CHARDEITY_TAG + ">" + deity + "</" + CHARDEITY_TAG + ">\n";
		String alignment = "\t\t<" + ALIGN_TAG + " value=\"" + Integer.toString(align) + "\" />\n";
		String charGender = "\t\t<" + GENDER_TAG + " value=\"" + gender + "\" />\n";
		outStream.write(charName.getBytes());
		outStream.write(charDeity.getBytes());
		outStream.write(alignment.getBytes());
		outStream.write(charGender.getBytes());
	}
	
	public void readInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName() != null) {
					if (parser.getName().equals(XmlConst.INFO_TAG)) {
						break;
					}
				}
			}
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name != null) {
				if (name.equals(CHARNAME_TAG)) {
					parser.next();
					if (parser.getEventType() == XmlPullParser.TEXT) 
						characterName = parser.getText();
					else
						characterName = "New";
				} else if (name.equals(CHARDEITY_TAG)) {
					parser.next();
					if (parser.getEventType() == XmlPullParser.TEXT)
						deity = parser.getText();
					else
						deity = "None";
				} else if (name.equals(ALIGN_TAG)) {
					align = Integer.parseInt(parser.getAttributeValue(null, VALUE_ATTR));
				} else if (name.equals(GENDER_TAG)) {
					gender = parser.getAttributeValue(null, VALUE_ATTR);
				}
			}
		}
		
	}

}
