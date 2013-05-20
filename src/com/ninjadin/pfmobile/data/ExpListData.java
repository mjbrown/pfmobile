package com.ninjadin.pfmobile.data;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.non_android.XmlExtractor;

public class ExpListData {
	
	public static XmlExtractor initLevels(InputStream character) throws XmlPullParserException, IOException {
		String[] tag_names = new String[] { XmlConst.LEVEL_TAG, };
		String[] tag_attrs = new String[] { XmlConst.NUM_ATTR, };
		String[] subtag_names = new String[] { XmlConst.CHOICE_TAG, XmlConst.CHOSEN_TAG, };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.GRPNAME_ATTR, XmlConst.SUBGRP, };
		XmlExtractor levels = new XmlExtractor(character);
		levels.getData(XmlConst.LEVELS_TAG, tag_names, tag_attrs, subtag_names, subtag_attrs);
		//charLevel = levels.groupData.size();
		return levels;
	}
	
	public static XmlExtractor initInventory(InputStream inventoryStream) throws XmlPullParserException, IOException {
		String[] tags = new String[] { XmlConst.ITEM_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR, };
		String[] subtags = new String[] { XmlConst.ITEMPROPERTY_TAG, XmlConst.DAMAGE_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.TYPE_ATTR, 
				XmlConst.VALUE_ATTR , XmlConst.STATISTIC_ATTR, XmlConst.SOURCE_ATTR, };
		XmlExtractor inventory = new XmlExtractor(inventoryStream);
		inventory.getData("inventory", tags, tag_attrs, subtags, subtag_attrs);
		return inventory;
	}
	
	public static XmlExtractor initItemTemplates(InputStream templatesStream) throws XmlPullParserException, IOException {
		String[] tags = new String[] { XmlConst.TEMPLATE_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR };
		String[] subtags = new String[] { XmlConst.ITEM_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR, };
		XmlExtractor itemTemplates = new XmlExtractor(templatesStream);
		itemTemplates.getData("equipmentTemplates", tags, tag_attrs, subtags, subtag_attrs);
		return itemTemplates;
	}
	
	public static XmlExtractor initEnchantTemplates(InputStream enchantsStream) throws XmlPullParserException, IOException {
		String[] tags = new String[] { XmlConst.ENHANCE_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR };
		String[] subtags = new String[] { XmlConst.ITEMPROPERTY_TAG, XmlConst.DAMAGE_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.TYPE_ATTR, 
				XmlConst.VALUE_ATTR , XmlConst.STATISTIC_ATTR, XmlConst.SOURCE_ATTR, };
		XmlExtractor enchantTemplates = new XmlExtractor(enchantsStream);
		enchantTemplates.getData("enchantmentTemplates", tags, tag_attrs, subtags, subtag_attrs);
		return enchantTemplates;
	}
}
