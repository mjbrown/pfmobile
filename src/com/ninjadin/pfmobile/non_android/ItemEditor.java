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
	private File inventory;
	
	public ItemEditor(String itemName, File inventoryFile) throws XmlPullParserException, IOException {
		inventory = inventoryFile;
		name = itemName;
		FileInputStream inStream;
		inStream = new FileInputStream(inventoryFile);
		item = new XmlExtractor(inStream);
		item.findTagAttr(XmlConst.ITEM_TAG, XmlConst.NAME_ATTR, itemName);
		String[] tags = new String[] { XmlConst.ENHANCE_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR };
		String[] subtags = new String[] { XmlConst.ITEMPROPERTY_TAG, XmlConst.DAMAGE_TAG,
				XmlConst.BONUS_TAG };
		String[] subtag_attrs = new String[] { XmlConst.TYPE_ATTR, XmlConst.STACKTYPE_ATTR,
				XmlConst.SOURCE_ATTR, XmlConst.VALUE_ATTR };
		item.getData(XmlConst.ITEM_TAG, tags, tag_attrs, subtags, subtag_attrs);
		inStream.close();
	}
	
	public void saveChanges(File tempFile) throws IOException {
		String itemData = new String();
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
		File copyFrom = inventory;
		File copyTo = tempFile;
		String parentAttrs = XmlConst.NAME_ATTR + "=\"" + name;
		XmlEditor.replaceParentContent(copyFrom, copyTo, XmlConst.ITEM_TAG, parentAttrs, itemData);
		copyTo.renameTo(inventory);
	}
}
