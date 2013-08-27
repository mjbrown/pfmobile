package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.XmlConst;

public class SpellbookEditor {
	Map<String, XmlExtractor> spell_lists = new HashMap<String, XmlExtractor>();
	File static_lists;
	File dynamic_books;
	
	public SpellbookEditor(File lists, File books) throws FileNotFoundException {
		static_lists = lists;
		dynamic_books = books;
	}
	
	private XmlExtractor getSpellList(String class_name) throws XmlPullParserException, IOException {
		FileInputStream stc_stream = new FileInputStream(static_lists);
		XmlExtractor book_data = new XmlExtractor(stc_stream);
		if (book_data.findTagAttr(XmlConst.SPELLLIST_TAG, XmlConst.SOURCE_ATTR, class_name)) {
			
		} else {
			stc_stream.close();
			stc_stream = new FileInputStream(dynamic_books);
			book_data = new XmlExtractor(stc_stream);
			book_data.findTagAttr(XmlConst.SPELLLIST_TAG, XmlConst.SOURCE_ATTR, class_name);
		}
		String tags[] = { XmlConst.SPELLLEVEL_TAG };
		String tag_attrs[] = { XmlConst.VALUE_ATTR };
		String subtags[] = { XmlConst.ENTRY_TAG };
		String subtag_attrs[] = {XmlConst.NAME_ATTR };
		return book_data;
	}
}
