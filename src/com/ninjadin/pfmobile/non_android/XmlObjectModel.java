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
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.XmlConst;

import android.util.Xml;

public class XmlObjectModel {
	private File xml_doc;
	private File temp_file;
	private String tag;
	private List<XmlObjectModel> children = new ArrayList<XmlObjectModel>();
	private Map<String,String> attributes = new HashMap<String,String>();
	
	private int highest_id = 0;
	protected Map<String, XmlObjectModel> id_map = new HashMap<String,XmlObjectModel>();
	
	public XmlObjectModel(String tag) {
		this.tag = tag;
	}
	
	public XmlObjectModel(XmlObjectModel model) {
		tag = model.getTag();
		for (Map.Entry<String, String> attr: model.getAttributes().entrySet())
			setAttribute(attr.getKey(), attr.getValue());
		for (XmlObjectModel child: model.getChildren())
			addChild(child);
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
			parseAttributes(inherited);
			parseChildren(inherited);
		}
	}
	
	protected String getUniqueId() {
		highest_id += 1;
		return "Id#" + Integer.toString(highest_id);
	}
	
	protected void initializeIdMap(String tag_name) {
		for (XmlObjectModel model: getChildren()) {
			String tag = model.getTag();
			String id = model.getAttribute(XmlConst.ID_ATTR);
			if (tag.equals(tag_name)) {
				id_map.put(id, model);
				String id_split[] = id.split("#");
				int id_number = Integer.parseInt(id_split[1]);
				if (id_number > highest_id)
					highest_id = id_number;
			}
		}
	}
	
	public void deleteById(String id) {
		int i = 0;
		for (XmlObjectModel child: getChildren()) {
			String child_id = child.getAttribute(XmlConst.ID_ATTR);
			if (child_id != null)
				if (child_id.equals(id)) {
					removeChild(i);
					return;
				}
			i += 1;
		}
	}
	
	private void readModel(InputStream inStream) {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inStream, null);
			while (parser.next() != XmlPullParser.START_TAG);
			parseAttributes(parser);
			parseChildren(parser);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void insertOptionStrings(Map<String,String> option_map, XmlObjectModel property) {
		Set<Map.Entry<String,String>> entry_set = property.getAttributes().entrySet();
		for (Map.Entry<String, String> attribute: entry_set) {
			String attr = attribute.getKey();
			String value = attribute.getValue();
			for (Map.Entry<String, String> entry: option_map.entrySet()) {
				String dict_attr = entry.getKey();
				if (value.contains("[" + dict_attr + "]")) {
					String dict_value = entry.getValue();
					value = value.replace("[" + dict_attr + "]", dict_value);
				}
			}
			property.setAttribute(attr, value);
		}
		for (XmlObjectModel child: property.getChildren()) {
			insertOptionStrings(option_map, child);
		}
	}
	
	public XmlObjectModel findObject(String tag, String attr, String value) {
		if (tag.equals(getTag())) {
			if (value.equals(attributes.get(attr))) {
				return this;
			}
		} else {
			for (XmlObjectModel child: getChildren()) {
				XmlObjectModel result = child.findObject(tag, attr, value);
				if (result != null)
					return result;
			}
		}
		return null;
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
	
	private void parseAttributes(XmlPullParser parser) {
		int num_attr = parser.getAttributeCount();
		tag = parser.getName();
		for (int i = 0; i < num_attr; i++) {
			String attr = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			attributes.put(attr, value);
		}
	}
	
	private void parseChildren(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.next();
		int event_type = parser.getEventType();
		if (event_type == XmlPullParser.START_TAG) {
			children.add(new XmlObjectModel(parser));
			parseChildren(parser);
		} else if (event_type == XmlPullParser.TEXT) {
			parseChildren(parser);
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
	
	public void setAttribute(String attr, String value) {
		attributes.put(attr, value);
	}
	
	public List<XmlObjectModel> getChildren() {
		return children;
	}
	
	protected void addChild(XmlObjectModel model) {
		children.add(model);
		String id = model.getAttribute(XmlConst.ID_ATTR);
		if (id != null)
			id_map.put(id, model);
	}
	
	protected void removeChild(int position) {
		children.remove(position);
	}
	
	protected void clearChildren() {
		int size = children.size();
		for (int i = size - 1; i >= 0; i--)
			children.remove(i);
	}
	
	public String getTag() {
		return tag;
	}
	
	public void changeTag(String value) {
		tag = value;
	}
	
	private String getXml() {
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
