package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionGroup extends StatisticGroup {
	
	protected Map<String,String> attributes;
	protected List<AttackGroup> attacks = new ArrayList<AttackGroup>();
	protected List<OnHitDamage> damages = new ArrayList<OnHitDamage>();
	protected List<OnHitCondition> conditions = new ArrayList<OnHitCondition>();
	protected List<XmlObjectModel> effects = new ArrayList<XmlObjectModel>();
	protected ActionGroup inherited = null;

	private Boolean visible;
	
	public ActionGroup(Map<String,String> attributes, ActionGroup inherit, StatisticGroup group_parent) {
		super (group_parent);
		inherited = inherit;
		this.attributes = attributes;
		visible = true;
	}
	
	public String getStringValue(String stat_name) {
		if (inherited != null)
			return super.getStringValue(stat_name) + " + " + inherited.getStringValue(stat_name);
		else
			return super.getStringValue(stat_name);
	}
	
	public void setVisibility(Boolean visible) {
		this.visible = visible;
	}
	
	public Boolean isVisible() {
		return visible;
	}
	
	public Integer getAttribute(String key) {
		Integer value = null;
		String attr = attributes.get(key);
		if (attr == null)
			value = inherited.getAttribute(key);
		else
			value = parent.evaluate(attr);
		return value;
	}
	
	public Map<String,String> getAttributes() {
		return attributes;
	}
	
	public void addEffect(XmlObjectModel model) {
		effects.add(model);
	}
	
	public List<XmlObjectModel> getEffects() {
		return effects;
	}
	
	public void addAttack(AttackGroup atk) {
		attacks.add(atk);
	}
	
	public List<AttackGroup> getAttacks() {
		List<AttackGroup> active_attacks = new ArrayList<AttackGroup>();
		for (AttackGroup atk: attacks) {
			if (atk.isActive())
				active_attacks.add(atk);
		}
		if (inherited != null) {
			for (AttackGroup atk: inherited.getAttacks())
				active_attacks.add(atk);
		}
		return active_attacks;
	}

	public void addOnHitDamage(OnHitDamage effect) {
		damages.add(effect);
	}
	
	public List<OnHitDamage> getOnHitDamages() {
		if (inherited != null) {
			List<OnHitDamage> concat_effects = new ArrayList<OnHitDamage>();
			for (OnHitDamage eff: inherited.getOnHitDamages()) 
				concat_effects.add(eff);
			for (OnHitDamage eff: damages)
				concat_effects.add(eff);
			return concat_effects;
		} else
			return damages;
	}
	
	public List<AttackGroup> getOnHitAttacks() {
		List<AttackGroup> onHitAttacks = new ArrayList<AttackGroup>();
		for (OnHitDamage dmg: getOnHitDamages()) {
			onHitAttacks.addAll(dmg.getAttacks());
		}
		return onHitAttacks;
	}

	public void addOnHitCondition(OnHitCondition condition) {
		conditions.add(condition);
	}
	
	public List<OnHitCondition> getOnHitConditions() {
		if (inherited != null) {
			List<OnHitCondition> concat_effects = new ArrayList<OnHitCondition>();
			for (OnHitCondition eff: inherited.getOnHitConditions()) 
				concat_effects.add(eff);
			for (OnHitCondition eff: conditions)
				concat_effects.add(eff);
			return concat_effects;
		} else
			return conditions;
	}
}
