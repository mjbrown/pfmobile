package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ninjadin.pfmobile.data.XmlConst;

public class InventoryXmlObject extends XmlObjectModel {
	int highest_id = 0;
	Map<String, XmlObjectModel> item_id_map = new HashMap<String,XmlObjectModel>();
	XmlObjectModel properties;
	Map<String, XmlObjectModel> properties_map = new HashMap<String,XmlObjectModel>();
	
	public InventoryXmlObject(File inventory_file, File temp_file, InputStream properties_stream) {
		super (inventory_file, temp_file);
		properties = new XmlObjectModel(properties_stream);
		initialize();
	}
	
	private void initialize() {
		for (XmlObjectModel model: getChildren()) {
			String tag = model.getTag();
			String id = model.getAttribute(XmlConst.ID_ATTR);
			if (tag.equals(XmlConst.ITEM_TAG)) {
				item_id_map.put(id, model);
				String id_split[] = id.split("#");
				int id_number = Integer.parseInt(id_split[1]);
				if (id_number > highest_id)
					highest_id = id_number;
			}
		}
		for (XmlObjectModel model: getChildren()) {
			String tag = model.getTag();
			String type = model.getAttribute(XmlConst.TYPE_ATTR);
			if (tag.equals(XmlConst.PROPERTY_TAG)) {
				properties_map.put(type, model);
			}
		}
	}
	
	public void createItem(String slot) {
		XmlObjectModel item = new XmlObjectModel(XmlConst.ITEM_TAG);
		String id = "Id#" + Integer.toString(highest_id + 1);
		item.addAttribute(XmlConst.ID_ATTR, id);
		item.addAttribute(XmlConst.NAME_ATTR, "Unnamed");
		item.addAttribute(XmlConst.SLOT_ATTR, slot);
		addChild(item);
		item_id_map.put(id, item);
		highest_id += 1;
	}
	
	public void deleteItem(String id) {
		int i = 0;
		for (XmlObjectModel child: getChildren()) {
			String child_id = child.getAttribute(XmlConst.ID_ATTR); 
			if (child_id.equals(id)) {
				removeChild(i);
				return;
			}
			i += 1;
		}
	}
	
	public void addProperty(XmlObjectModel property, String item_id) {
		XmlObjectModel item = getItem(item_id);
		int high_id = 0;
		for (XmlObjectModel child: item.getChildren()) {
			String property_id = child.getAttribute(XmlConst.ID_ATTR);
			String id_split[] = property_id.split("#");
			int id_number = Integer.parseInt(id_split[1]);
			if (id_number > high_id)
				high_id = id_number;
		}
		XmlObjectModel new_property = new XmlObjectModel(XmlConst.PROPERTY_TAG);
		new_property.addAttribute(XmlConst.NAME_ATTR, property.getAttribute(XmlConst.NAME_ATTR));
		new_property.addAttribute(XmlConst.ID_ATTR, "Id#" + Integer.toString(high_id + 1));
		for (XmlObjectModel option: property.getChildren()) {
			String option_value = option.getAttribute(XmlConst.VALUE_ATTR);
			for (XmlObjectModel entry: option.getChildren()) {
				String entry_value = entry.getAttribute(XmlConst.VALUE_ATTR);
				if (hasOption(option_value, entry_value)) {
					for (XmlObjectModel entry_data: entry.getChildren()) {
						new_property.addChild(entry_data);
					}
				}
			}
		}
		Map<String,String> option_map = parseOptionList(property);
		insertOptionStrings(option_map, new_property);
		item.addChild(new_property);
	}

	public void editProperty(XmlObjectModel property, String item_id, String property_id) {
		
	}
	
	private Boolean hasOption(String values, String value) {
		if (values == null)
			return false;
		for (String val: values.split(",")) {
			if (val.equals(value))
				return true;
		}
		return false;
	}
	
	private XmlObjectModel getItem(String id) {
		return item_id_map.get(id);
	}
	
	private XmlObjectModel insertOptionStrings(Map<String,String> option_map, XmlObjectModel property) {
		XmlObjectModel new_model = new XmlObjectModel(XmlConst.GENERATED_TAG);
		Set<Map.Entry<String,String>> entry_set = property.getAttributes().entrySet();
		for (Map.Entry<String, String> attribute: entry_set) {
			String attr = attribute.getKey();
			String value = attribute.getValue();
			for (Map.Entry<String, String> entry: option_map.entrySet()) {
				String dict_attr = entry.getKey();
				if (value.contains("[" + dict_attr + "]")) {
					String dict_value = entry.getValue();
					value = value.replace("[" + dict_attr + "]", dict_value);
				}
			}
			property.addAttribute(attr, value);
		}
		for (XmlObjectModel child: property.getChildren()) {
			insertOptionStrings(option_map, child);
		}
		return new_model;
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
