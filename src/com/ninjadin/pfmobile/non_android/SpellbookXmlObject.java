package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class SpellbookXmlObject extends XmlObjectModel {

	XmlObjectModel spell_definitions = null;
	
	public SpellbookXmlObject(File spell_file, File temp_file, InputStream spell_streams) {
		super (spell_file, temp_file);
		spell_definitions = new XmlObjectModel(spell_streams);
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
		XmlObjectModel spell_definition = spell_definitions.findObject(XmlConst.SPELL_TAG, attributes);
		if (spell_definition != null) {
			String school_name = spell_definition.getAttribute(XmlConst.SCHOOL_ATTR);
			entry.addAttribute(XmlConst.SCHOOL_ATTR, school_name);
		}
		
		entry.addAttribute(XmlConst.SOURCE_ATTR, class_name);
		entry.addAttribute(XmlConst.NAME_ATTR, spell_name);
		
		entry.addChild(bonusObject(PropertyLists.spell_level, spell_level));
		entry.addChild(bonusObject(PropertyLists.caster_level, "[" + class_name + " Caster Level]"));
		entry.addChild(bonusObject(PropertyLists.spell_failure, "[" + class_name + " Spell Failure]"));
		entry.addChild(bonusObject(PropertyLists.save_dc, "10 + [" + castingStat.get(class_name) + " Modifier] + " + spell_level));
		if (isSpontaneous.get(class_name))
			entry.addChild(bonusObject(PropertyLists.uses, "[" + class_name + " Level " + spell_level + " Spells Per Day]"));
		addChild(entry);
	}
	
	private XmlObjectModel bonusObject(String type, String value) {
		XmlObjectModel bonus = new XmlObjectModel(XmlConst.BONUS_TAG);
		bonus.addAttribute(XmlConst.TYPE_ATTR, type);
		bonus.addAttribute(XmlConst.VALUE_ATTR, value);
		return bonus;
	}


}
