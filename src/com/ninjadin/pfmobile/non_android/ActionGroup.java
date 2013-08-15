package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ActionGroup extends StatisticGroup {
	protected List<AttackGroup> attacks = new ArrayList<AttackGroup>();
	protected List<OnHitDamage> damages = new ArrayList<OnHitDamage>();
	protected List<OnHitCondition> conditions = new ArrayList<OnHitCondition>();
	protected ActionGroup inherited = null;

	private String cost;
	private String uses;
	private Boolean visible;
	
	public ActionGroup(String cost, String uses, ActionGroup inherit, StatisticGroup group_parent) {
		super (group_parent);
		inherited = inherit;
		this.cost = cost;
		this.uses = uses;
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
	
	public String getCost() {
		if ((inherited != null) && (cost == null))
			return inherited.getCost();
		else
			return cost;
	}
	
	public String getUses() {
		if ((inherited != null) && (uses == null))
			return inherited.getUses();
		else
			return uses;
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
