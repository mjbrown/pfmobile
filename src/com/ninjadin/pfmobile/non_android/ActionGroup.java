package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.List;

import com.ninjadin.pfmobile.data.PropertyLists;

public class ActionGroup extends StatisticGroup {
	protected List<AttackGroup> attacks = new ArrayList<AttackGroup>();
	private ActionGroup inherited = null;

	private String action_cost;
	
	protected ActionGroup(StatisticGroup group_parent) {
		super (group_parent);
	}
	
	public ActionGroup(ActionGroup inherit, StatisticGroup group_parent) {
		super (group_parent);
		inherited = inherit;
	}
	
	public ActionGroup(String cost, StatisticGroup group_parent) {
		super (group_parent);
		addStatNames(PropertyLists.attackProperties);
		action_cost = cost;
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
		if (inherited != null) {
			List<AttackGroup> concat_attacks = new ArrayList<AttackGroup>(inherited.getAttacks());
			concat_attacks.addAll(attacks);
			return concat_attacks;
		} else
			return attacks;
	}

}
