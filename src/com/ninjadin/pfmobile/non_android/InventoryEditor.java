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

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

import android.util.Xml;

public class InventoryEditor {
	
	private File inventoryFile;
	public XmlExtractor xmlData;
	public XmlExtractor templateData;
	
	public List<Map<String, String>> equipmentSlots;
	public List<List<Map<String, String>>> equipmentItems;
	
	public InventoryEditor(File fullInventory) {
		inventoryFile = fullInventory;
	}
	
	public void addFromTemplate(InputStream templateFileStream, String templateName, 
			File tempFile) throws FileNotFoundException, XmlPullParserException, IOException {
		File copyTo = tempFile;
		File copyFrom = inventoryFile;
		String startData = "<" + XmlConst.TEMPLATE_TAG + " name=\"" + templateName + "\">";
		String endData = "</" + XmlConst.TEMPLATE_TAG + ">";
		String insertBefore = "</inventory>";
		XmlEditor.copyReplace(copyFrom, copyTo, templateFileStream, startData, endData, insertBefore, insertBefore, null, null);
		tempFile.renameTo(inventoryFile);
	}
	
	public void enchantFromTemplate(InputStream enchantFileStream, String enchantName, 
			String itemName, File tempFile) throws FileNotFoundException, XmlPullParserException, IOException {
		File copyTo = tempFile;
		File copyFrom = inventoryFile;
		String startData = "<" + XmlConst.ENHANCE_TAG + " name=\"" + enchantName;
		String endData = "</" + XmlConst.ENHANCE_TAG + ">";
		String parentTag = XmlConst.ITEM_TAG;
		String parentAttr = XmlConst.NAME_ATTR + "=\"" + itemName;
		XmlEditor.addToParent(copyFrom, copyTo, enchantFileStream, startData, endData, parentTag, parentAttr);
		tempFile.renameTo(inventoryFile);
	}
}
