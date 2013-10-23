package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.XmlConst;

public class EffectXmlObject extends XmlObjectModel {
	final private static String ADD = "Add";
	final private static String REMOVE = "Remove";
	
	final private static String FINALLY_TAG = "finally";
	final private static String EXPEND_TAG = "expend";
	final private static String TEMPEXPEND_TAG = "tempExpend";
	
	final public static String DURATION_ATTR = "duration";
	final public static String ELAPSED_ATTR = "elapsed";
	
	final private static String ONGOING = "Ongoing";
	final private static String PERMANENT = "Permanent";
	final private static String DURATION = "Duration";
	final private static String EXPENDED = "Expended";
	final private static String SELECT = "Select";
	
	XmlObjectModel expended_effects;
	
	public EffectXmlObject(File effectFile, File tempFile) {
		super(effectFile, tempFile);
		for (XmlObjectModel child: getChildren()) {
			String name = child.getAttribute(XmlConst.NAME_ATTR);
			if (name.equals(EXPENDED)) {
				expended_effects = child;
			}
		}
	}
	
	public void addEffect(XmlObjectModel effect, StatisticManager manager) {
		String type = effect.getAttribute(XmlConst.TYPE_ATTR);
		String name = effect.getAttribute(XmlConst.NAME_ATTR);
		if (type != null) {
			if (type.equals(ADD)) {
				//removeEffect(name, manager);
				XmlObjectModel new_effect = new XmlObjectModel(effect);
				new_effect.setAttribute(XmlConst.ACTIVATE_ATTR, "Yes");
				addChild(new_effect);
			} else if (type.equals(REMOVE)) {
				removeEffect(name, manager);
			} else if (type.equals(DURATION)) {
				XmlObjectModel new_effect = new XmlObjectModel(effect);
				new_effect.setAttribute(XmlConst.ACTIVATE_ATTR, "Yes");
				int duration = 0;
				for (XmlObjectModel child: effect.getChildren()) {
					String child_type = child.getAttribute(XmlConst.TYPE_ATTR);
					if (child_type != null)
						if (child_type.equals(DURATION))
							duration += manager.evaluateValue(child.getAttribute(XmlConst.VALUE_ATTR));
				}
				new_effect.setAttribute(DURATION_ATTR, Integer.toString(duration));
				new_effect.setAttribute(ELAPSED_ATTR, "0");
				addChild(new_effect);
				elapseRound(new_effect, manager);
			} else if (type.equals(SELECT)) {
				
			}
		}
	}
	
	public void elapseRound(XmlObjectModel effect, StatisticManager manager) {
		int duration = Integer.parseInt(effect.getAttribute(DURATION_ATTR));
		int elapsed = Integer.parseInt(effect.getAttribute(ELAPSED_ATTR));
		elapsed += 1;
		if (duration < elapsed) {
			removeEffect(effect.getAttribute(XmlConst.NAME_ATTR), manager);
			return;
		}
		effect.setAttribute(ELAPSED_ATTR, Integer.toString(elapsed));
		List<XmlObjectModel> children = effect.getChildren();
		int size = children.size();
		for (int i = 0; i < size; i++) {
			XmlObjectModel child = children.get(i);
			if (child.getTag().equals(EXPEND_TAG)) {
				XmlObjectModel expend = new XmlObjectModel(child);
				expend.changeTag(XmlConst.BONUS_TAG);
				expended_effects.addChild(expend);
			} else if (child.getTag().equals(TEMPEXPEND_TAG)) {
				XmlObjectModel local = new XmlObjectModel(child);
				local.changeTag(XmlConst.BONUS_TAG);
				effect.addChild(local);
			}
		}
	}
	
	public void removeEffect(String effect_name, StatisticManager manager) {
		List<XmlObjectModel> effect_children = getChildren();
		int size = effect_children.size();
		for (int i = 0; i < size; i++) {
			XmlObjectModel child = effect_children.get(i);
			if (child.getTag().equals(XmlConst.EFFECT_TAG)) {
				String name = child.getAttribute(XmlConst.NAME_ATTR);
				if (effect_name.equals(name)) {
					executeDestructor(child, manager);
					removeChild(i);
					size -= 1;
				}
			}
		}
	}
	
	public void executeDestructor(XmlObjectModel effect, StatisticManager manager) {
		for (XmlObjectModel child: effect.getChildren()) {
			if (child.getTag().equals(FINALLY_TAG)) {
				for (XmlObjectModel finally_effect: child.getChildren()) {
					addEffect(finally_effect, manager);
				}
			}
		}
	}
	
	public void addCondition(String key, String name) {
		XmlObjectModel condition = new XmlObjectModel(XmlConst.CONDITION_TAG);
		condition.setAttribute(XmlConst.KEY_ATTR, key);
		condition.setAttribute(XmlConst.NAME_ATTR, name);
		expended_effects.addChild(condition);
	}
	
	public void expendSpell(String used) {
		XmlObjectModel bonus = new XmlObjectModel(XmlConst.BONUS_TAG);
		bonus.setAttribute(XmlConst.TYPE_ATTR, used);
		bonus.setAttribute(XmlConst.VALUE_ATTR, "1");
		expended_effects.addChild(bonus);
	}
	
	public void removeCondition(String key, String name) {
		List<XmlObjectModel> children = expended_effects.getChildren();
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
