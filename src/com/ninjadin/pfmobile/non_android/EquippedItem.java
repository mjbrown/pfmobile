package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class EquippedItem {
	private String name;
	private Map<String,InventoryItemProperty> masterMap = new HashMap<String,InventoryItemProperty>();
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
				if (tag.equals(XmlConst.ITEMPROPERTY_TAG)) {
					String type = property.get(XmlConst.TYPE_ATTR);
					String value = property.get(XmlConst.VALUE_ATTR);
					if ((type != null) && (value != null)) {
						addBonus(type, value);
					}
				} else if (tag.equals(XmlConst.BONUS_TAG)) {
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
		for (String type: PropertyLists.itemBonuses) {
			appendBonus(type);
		}
		for (String type: PropertyLists.spellFailureNames) {
			appendBonus(type);
		}
	}
	private void appendBonus(String type) {
		InventoryItemProperty itemProperty = masterMap.get(type);
		if (itemProperty != null) {
			Map<String, String> newBonus = new HashMap<String,String>();
			newBonus.put(XmlConst.TYPE_ATTR, type);
			if ((type.equals("Armor Class")) || (type.equals("Flat Footed Armor Class"))) {
				newBonus.put(XmlConst.STACKTYPE_ATTR, "Armor");
			} else {
				newBonus.put(XmlConst.STACKTYPE_ATTR, "Base");
			}
			newBonus.put(XmlConst.VALUE_ATTR, itemProperty.getValue());
			bonusList.add(newBonus);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void addBonus(String property, String value) {
		InventoryItemProperty itemProperty = masterMap.get(property);
		if (itemProperty == null) {
			itemProperty = new InventoryItemProperty("0");
			masterMap.put(property, itemProperty);
		}
		itemProperty.add(value);
	}
}
