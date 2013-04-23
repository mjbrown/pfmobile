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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class InventoryEditor {
	
	private File inventoryFile;
	public TwoDimXmlExtractor xmlData;
	public TwoDimXmlExtractor templateData;
	
	public List<Map<String, String>> equipmentSlots;
	public List<List<Map<String, String>>> equipmentItems;
	
	public InventoryEditor(File fullInventory, InputStream templateStream) throws FileNotFoundException, XmlPullParserException, IOException {
		inventoryFile = fullInventory;
		FileInputStream inStream = new FileInputStream(inventoryFile);
		XmlPullParser inventoryParser = Xml.newPullParser();
		try {
			inventoryParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			inventoryParser.setInput(inStream, null);
			inventoryParser.nextTag();
			String[] tags = new String[] { XmlConst.ITEM_TAG };
			String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR, };
			String[] subtags = new String[] { XmlConst.ITEMBONUS_TAG, XmlConst.DAMAGE_TAG };
			String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.TYPE_ATTR, 
					XmlConst.VALUE_ATTR , XmlConst.STATISTIC_ATTR, XmlConst.SOURCE_ATTR, };
			xmlData = new TwoDimXmlExtractor(inventoryParser, "inventory", tags, tag_attrs, subtags, subtag_attrs);
		} finally {
			inStream.close();
		}
		XmlPullParser templateParser = Xml.newPullParser();
		templateParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		templateParser.setInput(templateStream, null);
		templateParser.nextTag();
		String[] tags = new String[] { XmlConst.TEMPLATE_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR };
		String[] subtags = new String[] { XmlConst.ITEM_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, 
				XmlConst.TYPE_ATTR, XmlConst.SLOT_ATTR, XmlConst.SIZE_ATTR };
		templateData = new TwoDimXmlExtractor(templateParser, "equipmentTemplates", tags, tag_attrs, subtags, subtag_attrs);
	}
	
	public void getSlotItems() {
		equipmentSlots = new ArrayList<Map<String, String>>();
		for (String slot: PropertyLists.slotNames) {
			Map<String, String> slotMap = new HashMap<String, String>();
			slotMap.put(XmlConst.NAME_ATTR, slot);
			equipmentSlots.add(slotMap);
			List<Map<String, String>> itemList = new ArrayList<Map<String,String>>();
			for (Map<String, String> itemInfo: xmlData.groupData) {
				String itemSlot = itemInfo.get(XmlConst.SLOT_ATTR);
				if (itemSlot.equals(slot)) {
					itemList.add(itemInfo);
				}
			}
			equipmentItems.add(itemList);
		}
	}
	
	public void addFromTemplate(InputStream templateFileStream, String templateName, File tempFile) throws FileNotFoundException, XmlPullParserException, IOException {
		File copyTo = tempFile;
		File copyFrom = inventoryFile;
		String startData = "<" + XmlConst.TEMPLATE_TAG + " name=\"" + templateName + "\">";
		String endData = "</" + XmlConst.TEMPLATE_TAG + ">";
		String insertBefore = "</inventory>";
		XmlEditor.copyReplace(copyFrom, copyTo, templateFileStream, startData, endData, insertBefore, insertBefore, null, null);
		tempFile.renameTo(inventoryFile);
	}
}
