package com.ninjadin.pfmobile.non_android;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.ninjadin.pfmobile.data.XmlConst;

public class XmlExtractor {
	public List<Map<String,String>> groupData = new ArrayList<Map<String,String>>();
	public List<List<Map<String,String>>> itemData = new ArrayList<List<Map<String,String>>>();
	private List<Map<String,String>> singleList = null;
	public int tagCount = 0;
	public int subTagCount = 0;
	XmlPullParser xmlParser;
	StatisticManager manager;
	
	public XmlExtractor() {
		
	}
	
	public XmlExtractor(XmlPullParser parser, int tagCountStart, int subTagCountStart) throws XmlPullParserException, IOException {
		tagCount = tagCountStart;
		subTagCount = subTagCountStart;
		xmlParser = parser;
	}
	
	public XmlExtractor(XmlPullParser parser) throws XmlPullParserException, IOException {
		tagCount = 0;
		subTagCount = 0;
		xmlParser = parser;
	}
	
	public XmlExtractor(InputStream dataFile) throws XmlPullParserException {
		tagCount = 0;
		subTagCount = 0;
		xmlParser = Xml.newPullParser();
		xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		xmlParser.setInput(dataFile, null);
	}
	
	public XmlExtractor(InputStream dataFile, StatisticManager statManager) throws XmlPullParserException {
		tagCount = 0;
		subTagCount = 0;
		xmlParser = Xml.newPullParser();
		xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		xmlParser.setInput(dataFile, null);
		manager = statManager;
	}
	
	public void findTagAttr(String tag, String attr, String value) throws XmlPullParserException, IOException {
		while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
			if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagType = xmlParser.getName();
			if (tagType != null) {
				if (tagType.equals(tag)) {
					String attrValue = xmlParser.getAttributeValue(null, attr);
					if (attrValue != null) {
						if (attrValue.equals(value))
							return;
					}
				}
			}
		}
	}
	
	public String getAttribute(String attr) {
		String value = xmlParser.getAttributeValue(null, attr);
		return value;
	}

	public void getData(String endTag, String[] tags, String[] tag_attrs, String[] subtags, String[] subtag_attrs) throws XmlPullParserException, IOException {
		while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
			if (xmlParser.getEventType() == XmlPullParser.END_TAG) {
				if (xmlParser.getName() != null) {
					if (xmlParser.getName().equals(endTag)) {
						break;
					}
				}
			}
			if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = xmlParser.getName();
			if (tag != null) {
				getTagData(tag_attrs, tags, subtag_attrs, subtags, tag);
			}
		}
	}
	
	public void getPrereqMetData(String endTag, String[] tags, String[] tag_attrs, String[] subtags, String[] subtag_attrs) throws XmlPullParserException, IOException {
		Stack<String> lastPrereq = new Stack<String>();
		Stack<Boolean> lastPrereqMet = new Stack<Boolean>();
		lastPrereq.push("No Prerequisite.");
		lastPrereqMet.push(true);
		while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
			if (xmlParser.getEventType() == XmlPullParser.END_TAG) {
				if (xmlParser.getName() != null) {
					if (xmlParser.getName().equals(endTag)) {
						break;
					}
					if (xmlParser.getName().equals(XmlConst.PREREQ_TAG)) {
						lastPrereq.pop();
						lastPrereqMet.pop();
					}
				}
			}
			if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = xmlParser.getName();
			if (tag != null) {
				if (tag.equals(XmlConst.PREREQ_TAG)) {
					String types = xmlParser.getAttributeValue(null, XmlConst.TYPE_ATTR);
					String names = xmlParser.getAttributeValue(null, XmlConst.NAME_ATTR);
					boolean meetsPrereq = false;
					String lastPrereqReadable = "Invalid Prerequisite Definition";
					if (types != null) {
						String values = xmlParser.getAttributeValue(null, XmlConst.VALUE_ATTR);
						String compares = xmlParser.getAttributeValue(null, XmlConst.COMPARE_ATTR);
						if (values != null) {
							String[] value = values.split(",");
							String[] type = types.split(",");
							if (value.length != type.length)
								continue;
							if (compares != null) {
								String[] compare = compares.split(",");
								if (value.length != compare.length)
									continue;
								for (int i=0; i < type.length; i++) {
									lastPrereqReadable = type[i] + " " + compare[i] + " " + value[i];
									if (checkPrereq(type[i], compare[i], value[i])) {
										meetsPrereq = true;
										break;
									}
								}
							} else {
								for (int i=0; i < type.length; i++) {
									lastPrereqReadable = type[i] + " Is Greater Than Or Equal To " + value[i];
									if (checkPrereq(type[i], "Is Greater Than Or Equal To", value[i])) {
										meetsPrereq = true;
										break;
									}
								}
							}
						}
					}
					if (names != null) {
						for (String name: names.split(",")) {
							lastPrereqReadable = name;
							if (manager.hasProperty(name)) {
								meetsPrereq = true;
								break;
							}
						}
					}
					lastPrereq.push(lastPrereqReadable);
					lastPrereqMet.push(meetsPrereq && lastPrereqMet.peek());
					continue;
				}
				if (lastPrereqMet.peek())
					getTagData(tag_attrs, tags, subtag_attrs, subtags, tag);
			}
		}
	}
	
	private boolean checkPrereq(String type, String comparator, String value) {
		boolean retVal = false;
		Integer actual = manager.getValue(type);
		if (comparator.equals("Equals")) {
			if (actual == Integer.parseInt(value))
				retVal = true;
		} else {
			if (actual >= Integer.parseInt(value))
				retVal = true;
		}
		return retVal;
	}
	
	private void getTagData(String[] tag_attrs, String[] tags, String[] subtag_attrs, String[] subtags, String tag) {
		for (String tag_name: tags) {
			if (tag.equals(tag_name)) {
				Map<String, String> curGroupMap = new LinkedHashMap<String, String>();
				groupData.add(curGroupMap);
				singleList = new ArrayList<Map<String, String>>();
				itemData.add(singleList);
				curGroupMap.put("tag", tag);
				curGroupMap.put(XmlConst.NUM_ATTR, Integer.toString(tagCount++));
				for (String tag_attr: tag_attrs) {
					String attr_value = xmlParser.getAttributeValue(null, tag_attr);
					if (attr_value != null)
						curGroupMap.put(tag_attr, attr_value);
				}
				return;
			}
		}
		if (subtags == null)
			return;
		for (String tag_name: subtags) {
			if (tag.equals(tag_name)) {
				Map<String, String> curChildMap = new LinkedHashMap<String, String>();
				if (singleList != null)
					singleList.add(curChildMap);
				curChildMap.put("tag", tag_name);
				curChildMap.put("number", Integer.toString(subTagCount++));
				for (String subtag_attr: subtag_attrs) {
					String attr_value = xmlParser.getAttributeValue(null, subtag_attr);
					if (attr_value != null)
						curChildMap.put(subtag_attr, attr_value);
				}
				return;
			}
		}
	}
}
