package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.util.List;

import com.ninjadin.pfmobile.data.XmlConst;

public class EffectXmlObject extends XmlObjectModel {
	
	public EffectXmlObject(File effectFile, File tempFile) {
		super(effectFile, tempFile);
	}
	
	public void addEffect(XmlObjectModel effect) {
		effect.addAttribute(XmlConst.ACTIVATE_ATTR, "Yes");
		this.addChild(effect);
	}
	
	public void addCondition(String key, String name) {
		XmlObjectModel condition = new XmlObjectModel(XmlConst.CONDITION_TAG);
		condition.addAttribute(XmlConst.KEY_ATTR, key);
		condition.addAttribute(XmlConst.NAME_ATTR, name);
		this.addChild(condition);
	}
	
	public void removeCondition(String key, String name) {
		List<XmlObjectModel> children = this.getChildren();
		int size = children.size();
		for (int i = size-1; i >= 0; i--) {
			XmlObjectModel effect = children.get(i);
			if (effect.getTag().equals(XmlConst.CONDITION_TAG)) {
				if (effect.getAttribute(XmlConst.KEY_ATTR).equals(key) &&
						effect.getAttribute(XmlConst.NAME_ATTR).equals(name)) {
					this.removeChild(i);
				}
			}
		}
	}
}
