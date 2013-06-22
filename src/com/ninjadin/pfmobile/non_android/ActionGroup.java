package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.List;

import com.ninjadin.pfmobile.data.PropertyLists;

public class ActionGroup extends StatisticGroup {
	protected List<AttackGroup> attacks = new ArrayList<AttackGroup>();
	protected List<OnHitEffect> effects = new ArrayList<OnHitEffect>();
	protected ActionGroup inherited = null;

	private String action_cost;
	
	public ActionGroup(String cost, ActionGroup inherit, StatisticGroup group_parent) {
		super (group_parent);
		inherited = inherit;
		addStatNames(PropertyLists.attackProperties);
		addStatNames(PropertyLists.damageProperties);
		action_cost = cost;
	}
	
	public String getStringValue(String stat_name) {
		if (inherited != null)
			return super.getStringValue(stat_name) + " + " + inherited.getStringValue(stat_name);
		else
			return super.getStringValue(stat_name);
	}
	
	public String getCost() {
		if (inherited != null)
			return inherited.getCost();
		else
			return action_cost;
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

	public void addEffect(OnHitEffect effect) {
		effects.add(effect);
	}
	
	public List<OnHitEffect> getEffects() {
		if (inherited != null) {
			List<OnHitEffect> concat_effects = new ArrayList<OnHitEffect>();
			for (OnHitEffect eff: inherited.getEffects()) 
				concat_effects.add(eff);
			for (OnHitEffect eff: effects)
				concat_effects.add(eff);
			return concat_effects;
		} else
			return effects;
	}

}
