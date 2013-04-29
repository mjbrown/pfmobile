package com.ninjadin.pfmobile.data;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.non_android.XmlExtractor;

public class ExpListData {
	public XmlExtractor levels;
	public XmlExtractor inventory;
	public XmlExtractor itemTemplates;
	public XmlExtractor enchantTemplates;
	
	public ExpListData() {
		
	}
	
	public ExpListData(InputStream character, InputStream inventoryStream, InputStream item_templates, InputStream enchant_templates) {
		
	}
	
	public void initLevels(InputStream character) throws XmlPullParserException, IOException {
		String[] tag_names = new String[] { XmlConst.LEVEL_TAG, };
		String[] tag_attrs = new String[] { XmlConst.NUM_ATTR, };
		String[] subtag_names = new String[] { XmlConst.CHOICE_TAG, XmlConst.CHOSEN_TAG, };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.GRPNAME_ATTR, XmlConst.SUBGRP, };
		levels = new XmlExtractor(character);
		levels.getData(XmlConst.LEVELS_TAG, tag_names, tag_attrs, subtag_names, subtag_attrs);
		//charLevel = levels.groupData.size();
	}
	
	public void initInventory(InputStream inventoryStream) throws XmlPullParserException, IOException {
		String[] tags = new String[] { XmlConst.ITEM_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR, };
		String[] subtags = new String[] { XmlConst.ITEMPROPERTY_TAG, XmlConst.DAMAGE_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.TYPE_ATTR, 
				XmlConst.VALUE_ATTR , XmlConst.STATISTIC_ATTR, XmlConst.SOURCE_ATTR, };
		inventory = new XmlExtractor(inventoryStream);
		inventory.getData("inventory", tags, tag_attrs, subtags, subtag_attrs);
	}
	
	public void initItemTemplates(InputStream templatesStream) throws XmlPullParserException, IOException {
		String[] tags = new String[] { XmlConst.TEMPLATE_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR };
		String[] subtags = new String[] { XmlConst.ITEM_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR, };
		itemTemplates = new XmlExtractor(templatesStream);
		itemTemplates.getData("equipmentTemplates", tags, tag_attrs, subtags, subtag_attrs);
	}
	
	public void initEnchantTemplates(InputStream enchantsStream) throws XmlPullParserException, IOException {
		String[] tags = new String[] { XmlConst.ENCHANT_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR };
		String[] subtags = new String[] { XmlConst.ITEMPROPERTY_TAG, XmlConst.DAMAGE_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.TYPE_ATTR, 
				XmlConst.VALUE_ATTR , XmlConst.STATISTIC_ATTR, XmlConst.SOURCE_ATTR, };
		enchantTemplates = new XmlExtractor(enchantsStream);
		enchantTemplates.getData("enchantmentTemplates", tags, tag_attrs, subtags, subtag_attrs);
	}
}
