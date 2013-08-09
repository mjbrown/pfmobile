package com.ninjadin.pfmobile.non_android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ninjadin.pfmobile.data.XmlConst;

public class EffectEditor {
	Map<String, String> activatedConditions = new HashMap<String,String>();
	
	final static String NOT_UNIQUE = "Not Unique";
	
	public EffectEditor(File effectFile) {
		String[] tag_names = new String[] { XmlConst.CONDITION_TAG };
		String[] tag_attrs = new String[] { XmlConst.NAME_ATTR, XmlConst.KEY_ATTR,
				XmlConst.UNIQUE_ATTR, XmlConst.TYPE_ATTR };
		FileInputStream inStream;
		XmlExtractor activatedEffects = null;
		try {
			inStream = new FileInputStream(effectFile);
			activatedEffects = new XmlExtractor(inStream);
			activatedEffects.getData("effects", tag_names, tag_attrs, null, null);
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
		for (Map<String,String> condition: activatedEffects.groupData) {
			String unique = condition.get(XmlConst.UNIQUE_ATTR);
			if (unique != null) {
				activatedConditions.put(condition.get(XmlConst.NAME_ATTR), unique);
			} else {
				activatedConditions.put(condition.get(XmlConst.NAME_ATTR), NOT_UNIQUE);
			}
		}
	}
	
	public void activateEffect(String name, String unique) {
		if (unique != null) {
			deactivateEffect(unique);
			activatedConditions.put(unique, name);
		}
		activatedConditions.put(name, NOT_UNIQUE);
	}
	
	public void deactivateEffect(String name) {
		String value = activatedConditions.get(name);
		if (value != null)
			if (!(value.equals(NOT_UNIQUE)))
				activatedConditions.remove(value);
		activatedConditions.remove(name);
	}
	
	public void saveEffects(File effectFile) throws IOException {
		FileOutputStream outStream = new FileOutputStream(effectFile);
		OutputStreamWriter copyToSR = new OutputStreamWriter(outStream);
		BufferedWriter bufferedWriter = new BufferedWriter(copyToSR);
		
		String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<effects>\n";
		bufferedWriter.write(header);
		
		for (Map.Entry<String, String> entry: activatedConditions.entrySet()) {
			bufferedWriter.write("\t<condition key=\"Activated\" name=\"");
			bufferedWriter.write(entry.getKey());
			if (entry.getValue() != NOT_UNIQUE) {
				bufferedWriter.write("\" unique=\"");
				bufferedWriter.write(entry.getValue());
			}
			bufferedWriter.write("\" />\n");
		}
		String footer = "</effects>";
		bufferedWriter.write(footer);
		bufferedWriter.close();
		copyToSR.close();
		outStream.close();
		
	}
}
