package com.ninjadin.pfmobile.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.non_android.XmlExtractor;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class ExpListData {
	public List<Map<String,String>> groupData = new ArrayList<Map<String,String>>();
	public List<List<Map<String,String>>> itemData = new ArrayList<List<Map<String,String>>>();
	
	public ExpListData() {
		
	}
	
	public ExpListData(List<XmlObjectModel> models) {
		for (XmlObjectModel model: models) {
			groupData.add(model.getAttributes());
			List<Map<String,String>> child_data = new ArrayList<Map<String,String>>();
			itemData.add(child_data);
			for (XmlObjectModel child: model.getChildren()) {
				child_data.add(child.getAttributes());
			}
		}
	}
	
	public static XmlExtractor initInventory(InputStream inventoryStream) throws XmlPullParserException, IOException {
		String[] tags = new String[] { XmlConst.ITEM_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR, };
		String[] subtags = new String[] { XmlConst.ENHANCE_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR,  };
		XmlExtractor full_inventory = new XmlExtractor(inventoryStream);
		full_inventory.getData("inventory", tags, tag_attrs, subtags, subtag_attrs);
		return full_inventory;
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
		String[] subtags = new String[] { XmlConst.DAMAGE_TAG };
		String[] subtag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.TYPE_ATTR, 
				XmlConst.VALUE_ATTR , XmlConst.STATISTIC_ATTR, XmlConst.SOURCE_ATTR, };
		XmlExtractor enchantTemplates = new XmlExtractor(enchantsStream);
		enchantTemplates.getData("enchantmentTemplates", tags, tag_attrs, subtags, subtag_attrs);
		return enchantTemplates;
	}
}
