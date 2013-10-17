package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.util.Log;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class ConditionList {
	Stack<String> last_conditional = new Stack<String>();
	Map<String,List<KeyValuePair>> bonus_conditions = new HashMap<String,List<KeyValuePair>>();
	public ConditionList() {
		init();
	}
	
	public ConditionList(ConditionList old_list) {
		init();
		for (String key: PropertyLists.keyNames) {
			List<KeyValuePair> kv_list = new ArrayList<KeyValuePair>();
			for (KeyValuePair condition: old_list.getConditionList(key))
				kv_list.add(condition);
			bonus_conditions.put(key, kv_list);
		}
	}
	
	private void init() {
		last_conditional.push("None.");
		for (String key: PropertyLists.keyNames) {
			List<KeyValuePair> name_list = new ArrayList<KeyValuePair>();
			bonus_conditions.put(key, name_list);
		}
	}
	
	public List<KeyValuePair> getConditionList(String key) {
		return bonus_conditions.get(key);
	}
	
	public boolean hasConditions() {
		return !last_conditional.peek().equals("None.");
	}
	
	public void startConditional(XmlObjectModel condition) {
		String key = condition.getAttribute(XmlConst.KEY_ATTR);
		String name = condition.getAttribute(XmlConst.NAME_ATTR);
		String type = condition.getAttribute(XmlConst.TYPE_ATTR);
		String value = condition.getAttribute(XmlConst.VALUE_ATTR);
		String comp = condition.getAttribute(XmlConst.LOGIC_ATTR);
		startConditional(key, name, type, value, comp);
	}
	
	public void startConditional(String key, String name, String type, String value, String logic) {
		if (key != null) {
			last_conditional.push(key);
			List<KeyValuePair> condition_list = bonus_conditions.get(key); 
			if (condition_list != null) {
				if (name != null) {
					condition_list.add(new KeyValuePair(name, null, logic));
				} else if ((type != null) && (value != null)) {
					condition_list.add(new KeyValuePair(type, value, logic));
				} else {
					Log.d("ReadXML", "Conditional with key, without name or type/value:" + key);
				}
			} else {
				Log.d("ReadXML2", "Conditional for non-existent key: " + key);
			}
		} else {
			last_conditional.push("Invalid conditional key!");
			Log.d("ReadXML3", "Conditional with no key!");
		}
	}

	public void endConditional() {
		String last = last_conditional.pop();
		List<KeyValuePair> last_list = bonus_conditions.get(last);
		if ((last_list != null) && (last_list.size() > 0))
			last_list.remove(last_list.size()-1);
	}

}
