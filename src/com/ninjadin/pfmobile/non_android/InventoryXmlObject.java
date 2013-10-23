package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ninjadin.pfmobile.data.XmlConst;

public class InventoryXmlObject extends XmlObjectModel {
	public static String EQUIPPED_ATTR = "equipped";
	
	public InventoryXmlObject(File inventory_file, File temp_file) {
		super (inventory_file, temp_file);
		initializeIdMap(XmlConst.ITEM_TAG);
	}
	
	public void createItem(String name, String slot) {
		XmlObjectModel item = new XmlObjectModel(XmlConst.ITEM_TAG);
		String id = getUniqueId();
		item.setAttribute(XmlConst.ID_ATTR, id);
		item.setAttribute(XmlConst.NAME_ATTR, name);
		item.setAttribute(XmlConst.SLOT_ATTR, slot);
		item.setAttribute(EQUIPPED_ATTR, "False");
		addChild(item);
	}
	
	public void equipItem(String item_id, Boolean is_equipped) {
		XmlObjectModel item = id_map.get(item_id);
		if (is_equipped)
			item.setAttribute(EQUIPPED_ATTR, "True");
		else
			item.setAttribute(EQUIPPED_ATTR, "False");
	}
	
	public Boolean isEquipped(String item_id) {
		XmlObjectModel item = id_map.get(item_id);
		String equip = item.getAttribute(EQUIPPED_ATTR);
		if (equip.equals("True"))
			return true;
		else
			return false;
	}
	
	public void setProperty(XmlObjectModel property, String item_id, String property_id) {
		XmlObjectModel item = getItem(item_id);
		if (property_id == null) {
			item.initializeIdMap(XmlConst.PROPERTY_TAG);
			property_id = item.getUniqueId();
		} else {
			deleteProperty(item_id, property_id);
		}
		XmlObjectModel new_property = new XmlObjectModel(XmlConst.PROPERTY_TAG);
		new_property.setAttribute(XmlConst.NAME_ATTR, property.getAttribute(XmlConst.NAME_ATTR));
		new_property.setAttribute(XmlConst.ID_ATTR, property_id);
		for (XmlObjectModel option: property.getChildren()) {
			String option_value = option.getAttribute(XmlConst.VALUE_ATTR);
			XmlObjectModel new_option = new XmlObjectModel(option.getTag());
			new_option.setAttribute(XmlConst.NAME_ATTR, option.getAttribute(XmlConst.NAME_ATTR));
			new_option.setAttribute(XmlConst.VALUE_ATTR, option_value);
			new_property.addChild(new_option);
			for (XmlObjectModel entry: option.getChildren()) {
				String entry_value = entry.getAttribute(XmlConst.VALUE_ATTR);
				if (hasOption(option_value, entry_value)) {
					for (XmlObjectModel entry_data: entry.getChildren()) {
						new_option.addChild(entry_data);
					}
				}
			}
		}
		Map<String,String> option_map = parseOptionList(property);
		insertOptionStrings(option_map, new_property);
		item.addChild(new_property);
		try {
			this.saveChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteProperty(String item_id, String property_id) {
		XmlObjectModel item = getItem(item_id);
		List<XmlObjectModel> properties = item.getChildren();
		for (int list_position = 0; list_position < properties.size(); list_position++) {
			XmlObjectModel child = properties.get(list_position);
			String found_id = child.getAttribute(XmlConst.ID_ATTR);
			if (found_id.equals(property_id)) {
				item.removeChild(list_position);
				break;
			}
		}
		
	}

	public static Boolean hasOption(String values, String value) {
		if (values == null)
			return false;
		for (String val: values.split(",")) {
			if (val.equals(value))
				return true;
		}
		return false;
	}
	
	private XmlObjectModel getItem(String id) {
		return id_map.get(id);
	}
	
	public Map<String,String> getItemPropertyOptionMap(String item_id, String property_id) {
		XmlObjectModel item = getItem(item_id);
		XmlObjectModel property = getItemProperty(item, property_id);
		if (property != null)
			return parseOptionList(property);
		else
			return new HashMap<String,String>();
	}
	
	private XmlObjectModel getItemProperty(XmlObjectModel item, String property_id) {
		for (XmlObjectModel child: item.getChildren()) {
			String id = child.getAttribute(XmlConst.ID_ATTR);
			if (id.equals(property_id))
				return child;
		}
		return null;
	}
	
	private Map<String,String> parseOptionList(XmlObjectModel property) {
		Map<String,String> option_map = new HashMap<String,String>();
		for (XmlObjectModel child: property.getChildren()) {
			if (child.getTag().equals(XmlConst.OPTION_TAG)) {
				option_map.put(child.getAttribute(XmlConst.NAME_ATTR), child.getAttribute(XmlConst.VALUE_ATTR));
			}
		}
		return option_map;
	}
}
