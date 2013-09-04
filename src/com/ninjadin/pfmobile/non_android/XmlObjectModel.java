package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlObjectModel {
	private File xml_doc;
	private File temp_file;
	private String tag;
	private List<XmlObjectModel> children = new ArrayList<XmlObjectModel>();
	private Map<String,String> attributes = new HashMap<String,String>();
	
	public XmlObjectModel(String tag) {
		this.tag = tag;
	}
	
	public XmlObjectModel(File xml_doc, File temp) {
		try {
			InputStream inStream = new FileInputStream(xml_doc);
			readModel(inStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.xml_doc = xml_doc;
		this.temp_file = temp;
	}
	
	public XmlObjectModel(InputStream inStream) {
		readModel(inStream);
	}
	
	public XmlObjectModel(XmlPullParser inherited) throws XmlPullParserException, IOException {
		if (inherited.getEventType() != XmlPullParser.END_DOCUMENT) {
			getAttributes(inherited);
			getChildren(inherited);
		}
	}
	
	private void readModel(InputStream inStream) {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inStream, null);
			while (parser.next() != XmlPullParser.START_TAG);
			getAttributes(parser);
			getChildren(parser);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public XmlObjectModel findObject(String tag, Map<String,String> attributes) {
		if (tag.equals(getTag())) {
			Boolean found = true;
			for (Map.Entry<String, String> entry: attributes.entrySet()) {
				if (!getAttribute(entry.getKey()).equals(entry.getValue())) {
					found = false;
					break;
				}
			}
			if (found)
				return this;
		}
		for (XmlObjectModel child: getChildren()) {
			XmlObjectModel result = child.findObject(tag, attributes);
			if (result != null)
				return result;
		}
		return null;
	}
	
	private void getAttributes(XmlPullParser parser) {
		int num_attr = parser.getAttributeCount();
		tag = parser.getName();
		for (int i = 0; i < num_attr; i++) {
			String attr = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			attributes.put(attr, value);
		}
	}
	
	private void getChildren(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.next();
		int event_type = parser.getEventType();
		if (event_type == XmlPullParser.START_TAG) {
			children.add(new XmlObjectModel(parser));
			getChildren(parser);
		} else if (event_type == XmlPullParser.TEXT) {
			getChildren(parser);
		} else if (event_type == XmlPullParser.END_TAG) {
			parser.next();
		}
	}
	
	public String getAttribute(String attribute_name) {
		return attributes.get(attribute_name);
	}
	
	public Map<String,String> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String attr, String value) {
		attributes.put(attr, value);
	}
	
	public List<XmlObjectModel> getChildren() {
		return children;
	}
	
	public void addChild(XmlObjectModel model) {
		children.add(model);
	}
	
	public void removeChild(int position) {
		children.remove(position);
	}
	
	public void clearChildren() {
		int size = children.size();
		for (int i = size - 1; i >= 0; i--)
			children.remove(i);
	}
	
	public String getTag() {
		return tag;
	}
	
	public String getXml() {
		String xml = "";
		xml += "<" + tag + " ";
		for (Map.Entry<String, String> entry: attributes.entrySet()) {
			xml += entry.getKey() + "=\"" + entry.getValue() + "\" ";
		}
		if (children.size() == 0)
			xml += "/>\n";
		else {
			xml += ">\n";
			for (XmlObjectModel model: children) {
				xml += model.getXml();
			}
			xml += "</" + tag + ">\n";
		}
		return xml;
	}
	
	public void saveChanges() throws IOException {
		if ((xml_doc == null) || (temp_file == null))
			return;
		OutputStream outStream = new FileOutputStream(temp_file);
		outStream.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>".getBytes());
		outStream.write(getXml().getBytes());
		outStream.close();
		temp_file.renameTo(xml_doc);
	}
}
