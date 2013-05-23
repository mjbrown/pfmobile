package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.XmlConst;

public class EquippedItem {
	private String name;
	// List of character bonuses provided by this item
	public List<Map<String,String>> bonusList;
	
	public EquippedItem(String itemName, File inventory) {
		name = itemName;
		bonusList = new ArrayList<Map<String, String>>();
		InputStream inStream;
		XmlExtractor parser;
		try {
			inStream = new FileInputStream(inventory);
			parser = new XmlExtractor(inStream);
			parser.findTagAttr(XmlConst.ITEM_TAG, XmlConst.NAME_ATTR, itemName);
			String[] tags = new String[] { XmlConst.ITEMPROPERTY_TAG, XmlConst.BONUS_TAG };
			String[] tag_attrs = new String[] { XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR, 
					XmlConst.STACKTYPE_ATTR, XmlConst.SOURCE_ATTR, };
			parser.getData(XmlConst.ITEM_TAG, tags, tag_attrs, null, null);
			for (Map<String, String> property: parser.groupData) {
				String tag = property.get("tag");
				if (tag.equals(XmlConst.BONUS_TAG)) {
					bonusList.add(property);
				}
			}
			inStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}
}
