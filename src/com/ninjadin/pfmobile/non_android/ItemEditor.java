package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.XmlConst;

public class ItemEditor {
	public XmlExtractor item;
	public String name;
	private String new_name;
	public String slot;
	private String new_slot;
	private File inventory;
	
	// Used by ItemEditFragment and ItemEditDialogFragment to synch item updates
	private Map<String,String> property;
	
	public ItemEditor(String itemName, File inventoryFile) throws XmlPullParserException, IOException {
		inventory = inventoryFile;
		name = itemName;
		FileInputStream inStream;
		inStream = new FileInputStream(inventoryFile);
		item = new XmlExtractor(inStream);
		item.findTagAttr(XmlConst.ITEM_TAG, XmlConst.NAME_ATTR, itemName);
		slot = item.getAttribute(XmlConst.SLOT_ATTR);
		new_name = name;
		new_slot = slot;
		String[] tags = new String[] { XmlConst.ENHANCE_TAG, XmlConst.WEAPON_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR , XmlConst.TYPE_ATTR };
		String[] subtags = new String[] { XmlConst.ITEMPROPERTY_TAG, XmlConst.DAMAGE_TAG,
				XmlConst.BONUS_TAG, XmlConst.CONDITION_TAG, XmlConst.ATTACKBONUS_TAG };
		String[] subtag_attrs = new String[] { XmlConst.TYPE_ATTR, XmlConst.STACKTYPE_ATTR,
				XmlConst.SOURCE_ATTR, XmlConst.VALUE_ATTR, XmlConst.NAME_ATTR, XmlConst.KEY_ATTR };
		item.getData(XmlConst.ITEM_TAG, tags, tag_attrs, subtags, subtag_attrs);
		inStream.close();
	}
	
	public void setPosition(int groupPos, int childPos) {
		property = item.itemData.get(groupPos).get(childPos);
	}
	
	public String getBonusType() {
		return property.get(XmlConst.TYPE_ATTR);
	}
	public void setBonusType(String bonusType) {
		property.put(XmlConst.TYPE_ATTR, bonusType);
	}
	
	public String getStackType() {
		return property.get(XmlConst.STACKTYPE_ATTR);
	}
	public void setStackType(String stackType) {
		property.put(XmlConst.STACKTYPE_ATTR, stackType);
	}
	
	public String getSourceType() {
		return property.get(XmlConst.SOURCE_ATTR);
	}
	public void setSourceType(String sourceType) {
		property.put(XmlConst.SOURCE_ATTR, sourceType);
	}
	
	public String getValue() {
		return property.get(XmlConst.VALUE_ATTR);
	}
	public void setValue(String value) {
		property.put(XmlConst.VALUE_ATTR, value);
	}
	
	public String getXML() throws IOException {
		String itemData = "<" + XmlConst.ITEM_TAG + " " + XmlConst.NAME_ATTR + "=\"" + new_name + "\" "
				+ XmlConst.SLOT_ATTR + "=\"" + new_slot + "\" >\n";
		int i = 0;
		for (Map<String, String> tag: item.groupData) {
			itemData += "<" + XmlConst.ENHANCE_TAG;
			for (Map.Entry<String, String> tag_attr: tag.entrySet()) {
				if ((tag_attr.getKey().equals("tag") || (tag_attr.getKey().equals("number"))))
					continue;
				itemData += " " + tag_attr.getKey() + "=\"" + tag_attr.getValue() + "\"";
			}
			itemData += " >\n";
			List<Map<String,String>> property = item.itemData.get(i++);
			for (Map<String, String> subtag: property) {
				itemData += "<" + subtag.get("tag");
				for (Map.Entry<String, String> subtag_attr: subtag.entrySet()) {
					if ((subtag_attr.getKey().equals("tag") || (subtag_attr.getKey().equals("number"))))
						continue;
					itemData += " " + subtag_attr.getKey() + "=\"" + subtag_attr.getValue() + "\"";
				}
				itemData += " />\n";
			}
			itemData += "</" + XmlConst.ENHANCE_TAG + ">\n";
		}
		itemData += "</" + XmlConst.ITEM_TAG + ">\n";
		return itemData;
	}
	
	public void saveToInventory(File tempFile) throws IOException {
		String itemData = getXML();
		File copyFrom = inventory;
		File copyTo = tempFile;
		String parentAttrs = XmlConst.NAME_ATTR + "=\"" + name;
		XmlEditor.replaceParent(copyFrom, copyTo, XmlConst.ITEM_TAG, parentAttrs, itemData);
		copyTo.renameTo(inventory);
	}
	
	public void rename(String rename) {
		new_name = rename;
	}
	
	public void reslot(String reslot) {
		new_slot = reslot;
	}
}
