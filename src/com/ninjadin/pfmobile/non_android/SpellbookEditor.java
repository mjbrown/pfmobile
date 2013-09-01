package com.ninjadin.pfmobile.non_android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class SpellbookEditor {
	
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
	
	static public String createEntry(String class_name, String spell_name, String spell_level, InputStream spellStream) throws XmlPullParserException, IOException {
		String school_name = null;
		try {
			XmlExtractor spell_definition = new XmlExtractor(spellStream);
			spell_definition.findTagAttr(XmlConst.SPELL_TAG, XmlConst.NAME_ATTR, spell_name);
			school_name = spell_definition.getAttribute(XmlConst.SCHOOL_ATTR);
		} catch (IndexOutOfBoundsException e) {
			school_name = null;
		}
		String entry = "\t<" + XmlConst.SPELL_TAG + " " + XmlConst.SOURCE_ATTR + "=\"" + class_name + "\" " + XmlConst.NAME_ATTR + "=\"" + spell_name + "\" ";
		if (school_name != null)
			entry +=  XmlConst.SCHOOL_ATTR + "=\"" + school_name + "\""; 
		entry += " >";
		entry += bonus(PropertyLists.spell_level, spell_level);
		entry += bonus(PropertyLists.caster_level, "[" + class_name + " Caster Level]");
		entry += bonus(PropertyLists.spell_failure, "[" + class_name + " Spell Failure]");
		if (isSpontaneous.get(class_name))
			entry += bonus(PropertyLists.uses, "[" + class_name + " Level " + spell_level + " Spells Per Day]");
		entry += bonus(PropertyLists.save_dc, "10 + [" + castingStat.get(class_name) + " Modifier] + " + spell_level);
		entry += "\n\t</" + XmlConst.SPELL_TAG + ">\n";
		spellStream.close();
		return entry;
	}
	
	static private String bonus(String type, String value) {
		return "\n\t\t<bonus type=\"" + type + "\" value=\"" + value + "\" />";
	}
	
	static public XmlExtractor getSpellList(String class_name, InputStream listStream) throws XmlPullParserException, IOException {
		XmlExtractor book_data = new XmlExtractor(listStream);
		book_data.findTagAttr(XmlConst.SPELLLIST_TAG, XmlConst.SOURCE_ATTR, class_name);
		String tags[] = { XmlConst.SPELLLEVEL_TAG };
		String tag_attrs[] = { XmlConst.VALUE_ATTR };
		String subtags[] = { XmlConst.ENTRY_TAG };
		String subtag_attrs[] = {XmlConst.NAME_ATTR };
		book_data.getData(XmlConst.SPELLLIST_TAG, tags, tag_attrs, subtags, subtag_attrs);
		return book_data;
	}
}
