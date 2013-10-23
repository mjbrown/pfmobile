package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class SpellbookXmlObject extends XmlObjectModel {
	final private String GROUP_TAG = "group";
	final private String MEMORIZED = "Memorized";
	final private String EXPENDED = "Expended";
	
	XmlObjectModel memorized = null, expended = null;
	
	public SpellbookXmlObject(File spell_file, File temp_file) {
		super (spell_file, temp_file);
		memorized = findObject(GROUP_TAG, XmlConst.NAME_ATTR, MEMORIZED);
		expended = findObject(GROUP_TAG, XmlConst.NAME_ATTR, EXPENDED);
		initializeIdMap(XmlConst.SPELL_TAG);
	}
	
	private final static Map<String, Boolean> isSpontaneous;
	static {
		Map<String, Boolean> spontaneous = new HashMap<String,Boolean>();
		for (String memorize: PropertyLists.intelligenceCasters)
			spontaneous.put(memorize, false);
		for (String memorize: PropertyLists.wisdomCasters)
			spontaneous.put(memorize, false);
		for (String spont: PropertyLists.charismaCasters)
			spontaneous.put(spont, true);
		isSpontaneous = Collections.unmodifiableMap(spontaneous);
	}
	
	private final static Map<String,String> castingStat;
	static {
		Map<String,String> statMap = new HashMap<String,String>();
		for (String memorize: PropertyLists.intelligenceCasters)
			statMap.put(memorize, "Intelligence");
		for (String memorize: PropertyLists.wisdomCasters)
			statMap.put(memorize, "Wisdom");
		for (String spont: PropertyLists.charismaCasters)
			statMap.put(spont, "Charisma");
		castingStat = Collections.unmodifiableMap(statMap);
	}
	
	public void createEntry(String class_name, String spell_name, String spell_level) {
		XmlObjectModel entry = new XmlObjectModel(XmlConst.SPELL_TAG);

		Map<String,String> attributes = new HashMap<String,String>();
		attributes.put(XmlConst.NAME_ATTR, spell_name);
		String id = getUniqueId();
		entry.setAttribute(XmlConst.ID_ATTR, id);
		entry.setAttribute(XmlConst.SOURCE_ATTR, class_name);
		entry.setAttribute(XmlConst.NAME_ATTR, spell_name);
		if (isSpontaneous.get(class_name)) {
			entry.setAttribute(XmlConst.USES_ATTR, spontaneousUses(class_name, spell_level));
			entry.setAttribute(XmlConst.USED_ATTR, spontaneousUsed(class_name, spell_level));
		} else {
			entry.setAttribute(XmlConst.USES_ATTR, "Spell " + id + " Uses");
			entry.setAttribute(XmlConst.USED_ATTR, "Spell " + id + " Used");
			setMemorized(id, 0);
		}
		entry.addChild(bonusObject(PropertyLists.spell_level, spell_level));
		entry.addChild(bonusObject(PropertyLists.caster_level, "[" + class_name + " Caster Level]"));
		entry.addChild(bonusObject(PropertyLists.spell_failure, "[" + class_name + " Spell Failure]"));
		entry.addChild(bonusObject(PropertyLists.save_dc, "10 + [" + castingStat.get(class_name) + " Modifier] + " + spell_level));
		addChild(entry);
	}
	
	private String spontaneousUses(String class_name, String spell_level) {
		return class_name + " Level " + spell_level + " Spells Per Day";
	}
	
	private String spontaneousUsed(String class_name, String spell_level) {
		return class_name + " Level " + spell_level + " Spells Per Day Used";
	}
	
	private XmlObjectModel bonusObject(String type, String value) {
		XmlObjectModel bonus = new XmlObjectModel(XmlConst.BONUS_TAG);
		bonus.setAttribute(XmlConst.TYPE_ATTR, type);
		bonus.setAttribute(XmlConst.VALUE_ATTR, value);
		return bonus;
	}

	public void setMemorized(String id, Integer value) {
		memorized.deleteById(id);
		XmlObjectModel bonus = bonusObject("Spell " + id + " Uses", Integer.toString(value));
		bonus.setAttribute(XmlConst.ID_ATTR, id);
		memorized.addChild(bonus);
	}
	
	@Override
	public void deleteById(String id) {
		super .deleteById(id);
		memorized.deleteById(id);
	}
}
