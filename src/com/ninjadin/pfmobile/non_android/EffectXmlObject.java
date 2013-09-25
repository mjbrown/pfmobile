package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.util.List;

import com.ninjadin.pfmobile.data.XmlConst;

public class EffectXmlObject extends XmlObjectModel {
	
	public EffectXmlObject(File effectFile, File tempFile) {
		super(effectFile, tempFile);
	}
	
	public void addEffect(XmlObjectModel effect) {
		String action = effect.getAttribute(XmlConst.ACTION_TAG);
		String name = effect.getAttribute(XmlConst.NAME_ATTR);
		if (action != null) {
			if (action.equals("Add")) {
				XmlObjectModel new_effect = new XmlObjectModel(XmlConst.EFFECT_TAG);
				new_effect.setAttribute(XmlConst.ACTIVATE_ATTR, "Yes");
				new_effect.setAttribute(XmlConst.NAME_ATTR, name);
				for (XmlObjectModel child: effect.getChildren())
					new_effect.addChild(child);
				this.addChild(new_effect);
			} else if (action.equals("Remove")) {
				List<XmlObjectModel> child_list = getChildren();
				int size = child_list.size();
				for (int i = 0; i < size; i++) {
					XmlObjectModel child = child_list.get(i);
					if (child.getTag().equals(XmlConst.EFFECT_TAG)) {
						String effect_name = child.getAttribute(XmlConst.NAME_ATTR);
						if (effect_name.equals(name)) {
							removeChild(i--);
							size--;
						}
					}
				}
			}
		}
	}
	
	public void addCondition(String key, String name) {
		XmlObjectModel condition = new XmlObjectModel(XmlConst.CONDITION_TAG);
		condition.setAttribute(XmlConst.KEY_ATTR, key);
		condition.setAttribute(XmlConst.NAME_ATTR, name);
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
