package com.ninjadin.pfmobile.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class ExpListData {
	public List<Map<String,String>> groupData = new ArrayList<Map<String,String>>();
	public List<List<Map<String,String>>> itemData = new ArrayList<List<Map<String,String>>>();
	
	public ExpListData() {
		
	}
	
	public ExpListData(Map<String,List<XmlObjectModel>> map_models) {
		for (Map.Entry<String, List<XmlObjectModel>> entry: map_models.entrySet()) {
			Map<String,String> group = new HashMap<String,String>();
			group.put(XmlConst.NAME_ATTR, entry.getKey());
			groupData.add(group);
//			Log.d("Constructor", entry.getKey());
			List<Map<String,String>> child_list = new ArrayList<Map<String,String>>();
			itemData.add(child_list);
			for (XmlObjectModel child: entry.getValue()) {
				child_list.add(child.getAttributes());
			}
		}
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
	
	public ExpListData(XmlObjectModel model) {
		for (XmlObjectModel child: model.getChildren()) {
			groupData.add(child.getAttributes());
			List<Map<String,String>> child_data = new ArrayList<Map<String,String>>();
			itemData.add(child_data);
			for (XmlObjectModel childs_child: child.getChildren()) {
				child_data.add(childs_child.getAttributes());
			}
		}
	}
	
	public void filterByAttribute(String attr, String value) {
		List<Map<String,String>> newGroupData = new ArrayList<Map<String,String>>();
		List<List<Map<String,String>>> newItemData = new ArrayList<List<Map<String,String>>>();
		for (int i = 0; i < groupData.size(); i++) {
			String groupAttr = groupData.get(i).get(attr);
			if (groupAttr.equals(value)) {
				newGroupData.add(groupData.get(i));
				newItemData.add(itemData.get(i));
			}
		}
		groupData = newGroupData;
		itemData = newItemData;
	}
}
