package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.List;

import com.ninjadin.pfmobile.data.PropertyLists;

public class ActionGroup extends StatisticGroup {
	protected List<AttackGroup> attacks = new ArrayList<AttackGroup>();
	protected List<OnHitDamage> damages = new ArrayList<OnHitDamage>();
	protected List<OnHitCondition> conditions = new ArrayList<OnHitCondition>();
	protected ActionGroup inherited_action = null;

	private String action_cost;
	private String action_uses;
	private Boolean visible;
	
	public ActionGroup(String cost, String uses, ActionGroup inherit, StatisticGroup group_parent) {
		super (group_parent);
		inherited_action = inherit;
		action_cost = cost;
		action_uses = uses;
		visible = true;
	}
	
	public String getStringValue(String stat_name) {
		if (inherited_action != null)
			return super.getStringValue(stat_name) + " + " + inherited_action.getStringValue(stat_name);
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
		if ((inherited_action != null) && (action_cost == null))
			return inherited_action.getCost();
		else
			return action_cost;
	}
	
	public String getUses() {
		if ((inherited_action != null) && (action_uses == null))
			return inherited_action.getUses();
		else
			return action_uses;
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
		if (inherited_action != null) {
			for (AttackGroup atk: inherited_action.getAttacks())
				active_attacks.add(atk);
		}
		return active_attacks;
	}

	public void addOnHitDamage(OnHitDamage effect) {
		damages.add(effect);
	}
	
	public List<OnHitDamage> getOnHitDamages() {
		if (inherited_action != null) {
			List<OnHitDamage> concat_effects = new ArrayList<OnHitDamage>();
			for (OnHitDamage eff: inherited_action.getOnHitDamages()) 
				concat_effects.add(eff);
			for (OnHitDamage eff: damages)
				concat_effects.add(eff);
			return concat_effects;
		} else
			return damages;
	}

	public void addOnHitCondition(OnHitCondition condition) {
		conditions.add(condition);
	}
	
	public List<OnHitCondition> getOnHitConditions() {
		if (inherited_action != null) {
			List<OnHitCondition> concat_effects = new ArrayList<OnHitCondition>();
			for (OnHitCondition eff: inherited_action.getOnHitConditions()) 
				concat_effects.add(eff);
			for (OnHitCondition eff: conditions)
				concat_effects.add(eff);
			return concat_effects;
		} else
			return conditions;
	}
}
