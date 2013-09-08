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
	
	public XmlExtractor(InputStream dataFile) throws XmlPullParserException {
		tagCount = 0;
		subTagCount = 0;
		xmlParser = Xml.newPullParser();
		xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		xmlParser.setInput(dataFile, null);
	}
	
	public Boolean findTagAttr(String tag, String attr, String value) throws XmlPullParserException, IOException {
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
							return true;
					}
				}
			}
		}
		return false;
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
