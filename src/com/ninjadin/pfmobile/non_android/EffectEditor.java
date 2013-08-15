package com.ninjadin.pfmobile.non_android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class EffectEditor {
	List<Map<String, String>> activatedConditions = new ArrayList<Map<String,String>>();
	String[] tag_names = new String[] { XmlConst.CONDITION_TAG };
	String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.KEY_ATTR,
			XmlConst.REMOVE_ATTR, XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR };
	
	public EffectEditor(File effectFile) {
		FileInputStream inStream;
		XmlExtractor effectExtractor = null;
		try {
			inStream = new FileInputStream(effectFile);
			effectExtractor = new XmlExtractor(inStream);
			effectExtractor.getData("effects", tag_names, tag_attrs, null, null);
			inStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		activatedConditions = effectExtractor.groupData;
	}
	
	public void activateEffect(Map<String,String> add_effect) {
		activatedConditions.add(add_effect);
	}
	
	public void deactivateEffect(String effect_name, String effect_key) {
		List<String> remove_more = new ArrayList<String>();
		List<Map<String,String>> new_list = new ArrayList<Map<String,String>>();
		for (Map<String,String> effect: activatedConditions) {
			String name = effect.get(XmlConst.NAME_ATTR);
			String key = effect.get(XmlConst.KEY_ATTR);
			if (name != null) {
				if (name.equals(effect_name) && effect_key.equals(key)) {
					String remove = effect.get(XmlConst.REMOVE_ATTR);
					if (remove != null)
						for (String rmv: remove.split(","))
							remove_more.add(rmv);
					continue;
				}
			}
			new_list.add(effect);
		}
		activatedConditions = new_list;
		for (String rmv: remove_more)
			deactivateEffect(rmv, effect_key);
	}
	
	public void deactivateEffect(Map<String,String> remove_effect) {
		String effect_name = remove_effect.get(XmlConst.NAME_ATTR);
		String effect_key = remove_effect.get(XmlConst.KEY_ATTR);
		deactivateEffect(effect_name, effect_key);
	}
	
	public void saveEffects(File effectFile) throws IOException {
		FileOutputStream outStream = new FileOutputStream(effectFile);
		OutputStreamWriter copyToSR = new OutputStreamWriter(outStream);
		BufferedWriter bufferedWriter = new BufferedWriter(copyToSR);
		
		bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<effects>\n");
		for (Map<String, String> entry: activatedConditions) {
			bufferedWriter.write("\t<" + XmlConst.CONDITION_TAG + " ");
			for (String attr: tag_attrs) {
				if (entry.get(attr) != null)
					bufferedWriter.write(attr + "=\"" + entry.get(attr) + "\" ");
			}
			bufferedWriter.write("/>\n");
		}
		bufferedWriter.write("</effects>");
		bufferedWriter.close();
		copyToSR.close();
		outStream.close();
		
	}
}
