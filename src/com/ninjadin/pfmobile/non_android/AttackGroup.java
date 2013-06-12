package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.List;

public class AttackGroup extends StatisticGroup {
	private String versus = null;
	protected List<OnHitEffect> effects = new ArrayList<OnHitEffect>();
	private AttackGroup inherited = null;

	public AttackGroup(AttackGroup inherit, StatisticGroup group_parent) {
		super (group_parent);
		inherited = inherit;
	}
	
	public AttackGroup(String ver, StatisticGroup group_parent) {
		super (group_parent);
		versus = ver;
	}
	
	public String getVersus() {
		if (inherited != null)
			return inherited.getVersus();
		else
			return versus;
	}
	
	public void addEffect(OnHitEffect effect) {
		effects.add(effect);
	}
	
	public List<OnHitEffect> getEffects() {
		if (inherited != null) {
			List<OnHitEffect> concat_effects = new ArrayList<OnHitEffect>(inherited.getEffects());
			concat_effects.addAll(effects);
			return concat_effects;
		} else
			return effects;
	}
	
	public int getValue(String stat_name) {
		if (inherited != null)
			return super.getValue(stat_name) + inherited.getValue(stat_name) + getValue(stat_name);
		else
			return super.getValue(stat_name) + getValue(stat_name);
	}

}
