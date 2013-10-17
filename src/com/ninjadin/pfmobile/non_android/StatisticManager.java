package com.ninjadin.pfmobile.non_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.util.Log;

import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class StatisticManager {
	// A map of all CharacterStatistics
	private StatisticGroup master_stats = new StatisticGroup(null);
	// A list of all Conditionals (no unconditional bonuses)
	private List<Conditional> conditional_bonuses;
	
	// A map of equipment / environment conditions
	Map<String,Map<String,Conditional>> conditions = new HashMap<String,Map<String,Conditional>>();;
	
	// A map of NAMED actions available to the character
	Map<String, ActionGroup> combatActions = new HashMap<String, ActionGroup>();
	
	// A map of NAMED Attack Groups the character has available
	Map<String, AttackGroup> attacks = new HashMap<String, AttackGroup>();
	
	// A map of NAMED OnHitEffects
	Map<String, OnHitDamage> onhit_effects = new HashMap<String, OnHitDamage>();
	
	// A list of modifiers which can be turned on or off on a whim
	List<ActivatedCondition> modifiers = new ArrayList<ActivatedCondition>();
	
	// A list of spells available for memorization/casting
	List<SpellGroup> spells = new ArrayList<SpellGroup>();
	
	// A list of effects
	List<XmlObjectModel> effects = new ArrayList<XmlObjectModel>();
	Map<String, List<XmlObjectModel>> effect_select_map = new HashMap<String, List<XmlObjectModel>>();
	
	public StatisticManager() {
		for (String key: PropertyLists.keyNames) {
			Map<String,Conditional> condition_map = new HashMap<String,Conditional>();
			conditions.put(key, condition_map);
		}
		conditional_bonuses = new ArrayList<Conditional>();
	}
	
	public void newBonus(String statisticType, String stackType, String source, String value) {
		master_stats.addBonus(statisticType, stackType, source, value);
	}
	
	public int getValue(String statisticName) {
		return master_stats.getValue(statisticName);
	}
	
	public int evaluateValue(String value) {
		return master_stats.evaluate(value);
	}
	
	public boolean hasProperty(String key, String name) {
		Conditional property = conditions.get(key).get(name);
		if (property != null) {
			return property.isActive();
		} else {
			return false;
		}
	}
	
	public void readModel(XmlObjectModel model) {
		ConditionList currentConditions = new ConditionList();
		Stack<StatisticGroup> lastObject = new Stack<StatisticGroup>();
		lastObject.push(master_stats);
		recursiveReadModel(model, currentConditions, lastObject, null, null);
	}
	
	public void readPartialModel(XmlObjectModel model, String quit_tag, Map<String,String> quit_attr) {
		ConditionList currentConditions = new ConditionList();
		Stack<StatisticGroup> lastObject = new Stack<StatisticGroup>();
		lastObject.push(master_stats);
		recursiveReadModel(model, currentConditions, lastObject, quit_tag, quit_attr);
	}
	
	public Boolean recursiveReadModel(XmlObjectModel model, ConditionList currentConditions, 
			Stack<StatisticGroup> lastObject, String quit_tag, Map<String,String> quit_attr) {
		String tag = model.getTag();
		if (tag.equals(quit_tag)) {
			Boolean quit = true;
			for (Map.Entry<String, String> entry: quit_attr.entrySet()) {
				String quit_key = entry.getKey();
				String quit_value = entry.getValue();
				if (!model.getAttribute(quit_key).equals(quit_value)) {
					quit = false;
					break;
				}
			}
			if (quit)
				return true;
		}
		if (tag.equals(XmlConst.CONDITIONAL_TAG)) {
			String name = model.getAttribute(XmlConst.NAME_ATTR);
			String key = model.getAttribute(XmlConst.KEY_ATTR);
			if (model.getAttribute(XmlConst.ACTIVATE_ATTR) != null) {
				ActivatedCondition newBonus = new ActivatedCondition(key, name);
				modifiers.add(newBonus);
				if (currentConditions.hasConditions()) {
					newBonus.setConditions(currentConditions);
					conditional_bonuses.add(newBonus);
				}
			}
			currentConditions.startConditional(model);
		} else if (tag.equals(XmlConst.BONUS_TAG)) {
			String type = model.getAttribute(XmlConst.TYPE_ATTR);
			String value = model.getAttribute(XmlConst.VALUE_ATTR);
			String source = model.getAttribute(XmlConst.SOURCE_ATTR);
			String stack = model.getAttribute(XmlConst.STACKTYPE_ATTR);
			if (stack == null)
				stack = "Base";
			if (source == null)
				source = "Natural";
			StatisticGroup stat_group = lastObject.peek();
			for (String typ: type.split(",")) {
				Bonus conditionalBonus = stat_group.addBonus(typ, stack, source, value);
				if (currentConditions.hasConditions()) {
					conditionalBonus.setConditions(currentConditions);
					conditional_bonuses.add(conditionalBonus);
					updateConditionalBonus(conditionalBonus);
				}
			}
		} else if (tag.equals(XmlConst.CHOICE_TAG)) {
			String name = model.getAttribute(XmlConst.NAME_ATTR);
			if (name != null)
				activateCondition(PropertyLists.prerequisite, name);
		} else if (tag.equals(XmlConst.ACTION_TAG)) {
			String name = model.getAttribute(XmlConst.NAME_ATTR);
			String parent = model.getAttribute(XmlConst.PARENT_ATTR);
			String visible = model.getAttribute(XmlConst.VISIBLE_ATTR);
			ActionGroup action = null;
			if (name != null)
				action = combatActions.get(name);
			if (action == null) {
				ActionGroup parent_action = combatActions.get(parent);
				action = new ActionGroup(model.getAttributes(), parent_action, master_stats);
				if (visible != null) {
					if (visible.equals("No"))
						action.setVisibility(false);
				}
				if (currentConditions.hasConditions()) {
					action.setConditions(currentConditions);
					conditional_bonuses.add(action);
				}
				if (name != null)		// Named action means it is referenced elsewhere
					combatActions.put(name, action);
			}
			lastObject.push(action);
		} else if (tag.equals(XmlConst.ATTACK_TAG)) {
			String name = model.getAttribute(XmlConst.NAME_ATTR);
			String parent = model.getAttribute(XmlConst.PARENT_ATTR);
			AttackGroup inheritedAttack = null;
			StatisticGroup parentGroup = null;
			if (parent != null) 
				inheritedAttack = attacks.get(parent);
			if (!(lastObject.peek() == master_stats)) {
				//Log.d("ATTACK", "Attack " + names + " has inherited " + parent);
				parentGroup = lastObject.peek();
			}
			AttackGroup newAttack = null;
			if (name != null)
				newAttack = attacks.get(name);
			if (newAttack == null) {
				newAttack = new AttackGroup(model.getAttributes(), inheritedAttack, parentGroup);
				if (currentConditions.hasConditions()) {
					newAttack.setConditions(currentConditions);
					conditional_bonuses.add(newAttack);
				}
				if (name != null)		// Named attack means it is referenced elsewhere
					attacks.put(name, newAttack);
			}
			if (lastObject.peek() instanceof ActionGroup) {
				ActionGroup action_group = (ActionGroup) lastObject.peek();
				action_group.addAttack(newAttack);
			} else {
				//Log.d("ATTACK", "Parent of attack isn't action! " + names);
			}
			lastObject.push(newAttack);
		} else if (tag.equals(XmlConst.ONHIT_TAG)) {
			String name = model.getAttribute(XmlConst.NAME_ATTR);
			String parent = model.getAttribute(XmlConst.PARENT_ATTR);
			OnHitDamage inheritedOnHit = null;
			StatisticGroup parentGroup = null;
			if (parent != null)
				inheritedOnHit = onhit_effects.get(parent);
			if (!(lastObject.peek() == master_stats))
				parentGroup = lastObject.peek();
			OnHitDamage newOnHit = null;
			if (name != null)
				newOnHit = onhit_effects.get(name);
			if (newOnHit == null) {
				newOnHit = new OnHitDamage(model.getAttributes(), inheritedOnHit, parentGroup);
				if (currentConditions.hasConditions()) {
					newOnHit.setConditions(currentConditions);
					conditional_bonuses.add(newOnHit);
				}
				if (name != null)
					onhit_effects.put(name, newOnHit);
			}
			if (lastObject.peek() instanceof AttackGroup) {
				AttackGroup parentAttack = (AttackGroup) lastObject.peek();
				parentAttack.addOnHitDamage(newOnHit);
			}
			lastObject.push(newOnHit);
		} else if (tag.equals(XmlConst.ITEM_TAG)) {
			String active = model.getAttribute(InventoryXmlObject.EQUIPPED_ATTR);
			if (active.equals("False"))
				return false;
		} else if (tag.equals(XmlConst.SPELL_TAG)) {
			SpellGroup spell = new SpellGroup(model.getAttributes(), master_stats);
			spells.add(spell);
			if (currentConditions.hasConditions()) {
				spell.setConditions(currentConditions);
				conditional_bonuses.add(spell);
			}
			lastObject.push(spell);
		} else if (tag.equals(XmlConst.EFFECT_TAG)) {
			String activate = model.getAttribute(XmlConst.ACTIVATE_ATTR);
			String type = model.getAttribute(XmlConst.TYPE_ATTR);
			String select = model.getAttribute(XmlConst.SELECT_ATTR);
			if (type != null) {
				if (type.equals("Select")) {
					List<XmlObjectModel> select_list = effect_select_map.get(select);
					if (select_list == null) {
						select_list = new ArrayList<XmlObjectModel>();
						effect_select_map.put(select, select_list);
					}
					select_list.add(model);
				}
			}
			if (activate == null) {
				try {
					ActionGroup last = ((ActionGroup) lastObject.peek()); 
					last.addEffect(model);
				} catch (ClassCastException e) {
					
				}
				return false;
			} else if (activate.equals("False")) {
				return false;
			}
		} else if (tag.equals(XmlConst.CONDITION_TAG)) {
			String key = model.getAttribute(XmlConst.KEY_ATTR);
			String names = model.getAttribute(XmlConst.NAME_ATTR);
			Map<String,Conditional> condition_map = conditions.get(key);
			if (condition_map == null) {
				condition_map = new HashMap<String,Conditional>();
				conditions.put(key, condition_map);
			}
			for (String name: names.split(",")) {
				Conditional cond = new Conditional();
				condition_map.put(name, cond);
				//Log.d("ADD_COND", "Adding condition: " + key + " " + name);
				if (currentConditions.hasConditions()) {
					cond.setConditions(currentConditions);
					conditional_bonuses.add(cond);
				}
			}
		}
		for (XmlObjectModel child: model.getChildren()) {
			if (recursiveReadModel(child, currentConditions, lastObject, quit_tag, quit_attr))
				return true;
		}
		if (tag.equals(XmlConst.CONDITIONAL_TAG)) {
			currentConditions.endConditional();
		} else if (tag.equals(XmlConst.ACTION_TAG) || tag.equals(XmlConst.ATTACK_TAG) || tag.equals(XmlConst.ONHIT_TAG) || tag.equals(XmlConst.SPELL_TAG)) {
			lastObject.pop();
		}
		return false;
	}
	
	public void updateConditionalBonuses(int retries) {
		if (retries == 0)
			return;
		Log.d("RECUR_UPDATE", Integer.toString(retries));
		Boolean run_again = false;
		for (Conditional bonus: conditional_bonuses) {
			Boolean already_active = bonus.isActive();
			updateConditionalBonus(bonus);
			if (already_active ^ bonus.isActive()) {
				run_again = true;
			}
		}
		if (run_again)
			updateConditionalBonuses(retries - 1);
	}
	
	private void updateConditionalBonus(Conditional bonus) {
		if (checkConditions(bonus.getConditions())) {
			bonus.meetsConditions();
		} else {
			bonus.failsConditions();
			//Log.d("Condition", "Condition not met:" + bonus.getStringValue());
		}
	}
	
	private boolean checkConditions(ConditionList conditions) {
		for (String key: PropertyLists.keyNames) {
			if (conditions == null) // If no conditions are set, then conditions are met
				break;
			for (KeyValuePair kv: conditions.getConditionList(key)) {
				if (!hasCondition(key, kv)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean hasCondition(String key, KeyValuePair kv) {
		if (kv.value == null) {
			if (kv.logic == null) { // OR assumed
				for (String property: kv.key.split(",")) {
					if (hasProperty(key, property)) {
						return true;
					}
				}
				return false;
			} else if (kv.logic.equals("NAND")) {
				for (String property: kv.key.split(",")) {
					if (!hasProperty(key, property)) {
						return true;
					}
				}
				return false;
			} else if (kv.logic.equals("NOR")) {
				for (String property: kv.key.split(",")) {
					if (hasProperty(key, property)) {
						return false;
					}
				}
				return true;
			} else if (kv.logic.equals("AND")) {
				for (String property: kv.key.split(",")) {
					if (!hasProperty(key, property)) {
						return false;
					}
				}
				return true;
			}
		} else {
			String[] keys = kv.key.split(",");
			String[] values = kv.value.split(",");
			if (keys.length != values.length) {
				Log.d("hasCondition", "Key Value size mismatch " + kv.key + " " + kv.value);
				return false;
			}
			if (kv.logic == null) { // OR greater than or equal to assumed
				for (int i = 0; i < keys.length; i++) {
					if (getValue(keys[i]) >= evaluateValue(values[i]))
						return true;
				}
			} else if (kv.logic.equals("OR_EQ")) {
				for (int i = 0; i < keys.length; i++) {
					if (getValue(keys[i]) != evaluateValue(values[i]))
						return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public ExpListData getActionData() {
		ExpListData action_data = new ExpListData();
		List<Map<String,String>> atk_list = action_data.groupData;
		List<List<Map<String,String>>> atk_data = action_data.itemData;
		for (Map.Entry<String, ActionGroup> entry: combatActions.entrySet()) {
			ActionGroup action = entry.getValue();
			if ((action.isVisible() == false) || (action.isActive() == false))
				continue;
			Map<String,String> visible_action = action.getAttributes();
			visible_action.put(XmlConst.NAME_ATTR, entry.getKey());
			atk_list.add(visible_action);
			List<Map<String,String>> visible_attack = new ArrayList<Map<String,String>>();
			atk_data.add(visible_attack);
			for (AttackGroup attack: action.getAttacks()) {
				visible_attack.add(attackInfo(attack, 0));
				for (AttackGroup subattack: attack.getOnHitAttacks()) {
					visible_attack.add(attackInfo(subattack, 1));
					for (AttackGroup subsubattack: attack.getOnHitAttacks()) {
						visible_attack.add(attackInfo(subsubattack, 2));
					}
				}
			}
		}
		return action_data;
	}
	
	private Map<String,String> attackInfo(AttackGroup attack, int depth) {
		Map<String,String> attack_info = new HashMap<String,String>();
		attack_info.put("depth", Integer.toString(depth));
		attack_info.put(XmlConst.VERSUS_ATTR, attack.getVersus());
		attack_info.put(XmlConst.TARGET_ATTR, attack.getTarget());
		attack_info.put(PropertyLists.to_hit, 
				Integer.toString(master_stats.evaluate(attack.getStringValue(PropertyLists.to_hit))));
		// Assemble dice + damage string
		attack_info.put(PropertyLists.damage, getDamageString(attack));
		attack_info.put(PropertyLists.crit_range, getCriticalString(attack));
		return attack_info;
	}
	
	private String getCriticalString(ActionGroup attack) {
		int crit_range = master_stats.evaluate(attack.getStringValue(PropertyLists.crit_range));
		String crit_range_string = PropertyLists.criticalRangeStrings[crit_range];
		return crit_range_string;
	}
	
	private String getDamageString(ActionGroup attack) {
		List<String> dmg_dice = PropertyLists.damageDie();
		dmg_dice.add(PropertyLists.damage);
		int length = dmg_dice.size();
		List<Integer> damage = new ArrayList<Integer>();
		for (int i = 0; i < length; i++)
			damage.add(0);

		Integer weapon_damage = null;
		List<OnHitDamage> damages = attack.getOnHitDamages();
		for (OnHitDamage dmg: damages) {
			for (int i = 0; i < length; i++) {
				String value = dmg.getStringValue(dmg_dice.get(i));
				int previous = damage.get(i);
				damage.add(i, master_stats.evaluate(value) + previous);
			}
			String medium_size_weapon_damage = dmg.getStringValue(PropertyLists.medium_size_weapon_damage);
			if (medium_size_weapon_damage != null) {
				weapon_damage = master_stats.evaluate(medium_size_weapon_damage);
			}
		}
		int has_dice = 0;
		String output = null;
		if (weapon_damage != null) {
			int size = master_stats.getValue(PropertyLists.character_size);
			if (size == 5)
				output = PropertyLists.medium_damage[weapon_damage];
			else if (size == 4)
				output = PropertyLists.small_damage[weapon_damage];
			else if (size == 6)
				output = PropertyLists.large_damage[weapon_damage];
			else
				output = PropertyLists.medium_damage[weapon_damage];
		} else
			output = "";
		for (int i = 0; i < length; i++) {
			int value = damage.get(i);
			if (value > 0) {
				output += "+" + Integer.toString(value);
				if (i < length - 1)
					output += PropertyLists.die[i];
				has_dice += value;
			}
		}
		return output;
	}
	
	public List<XmlObjectModel> getEffects(String action_name) {
		return combatActions.get(action_name).getEffects();
	}
	
	public List<AttackGroup> getAttacks(String action_name) {
		return combatActions.get(action_name).getAttacks();
	}
	
	public List<SpellGroup> getSpells() {
		List<SpellGroup> available_spells = new ArrayList<SpellGroup>();
		for (SpellGroup spell: spells) {
			if (spell.isActive())
				available_spells.add(spell);
		}
		return available_spells;
	}

	public ExpListData getActivatableConditions() {
		Map<String,Map<String,String>> key_list = new HashMap<String,Map<String,String>>();
		for (ActivatedCondition cond: modifiers) {
			if (!(cond.isActive()))
				continue;
			String key = cond.getKey();
			Map<String,String> names_map = key_list.get(key);
			if (names_map == null) {
				names_map = new HashMap<String,String>();
				key_list.put(key, names_map);
			}
			names_map.put(cond.getName(), XmlConst.NAME_ATTR);
		}
		ExpListData expData = new ExpListData();
		for (Map.Entry<String, Map<String,String>> key_entry: key_list.entrySet()) {
			Map<String,String> key_map = new HashMap<String,String>(); 
			expData.groupData.add(key_map);
			key_map.put(XmlConst.NAME_ATTR, key_entry.getKey());
			List<Map<String,String>> names_list = new ArrayList<Map<String,String>>();
			for (Map.Entry<String, String> names_entry: key_entry.getValue().entrySet()) {
				Map<String,String> name_map = new HashMap<String,String>();
				names_list.add(name_map);
				name_map.put(XmlConst.NAME_ATTR, names_entry.getKey());
			}
			expData.itemData.add(names_list);
		}
		return expData;
	}
	
	public Conditional activateCondition(String key, String name) {
		Map<String,Conditional> condition_map = conditions.get(key);
		Conditional property = condition_map.get(name);
		if (property == null) {
			property = new Conditional();
			condition_map.put(name, property);
//			updateConditionalBonuses(1);
		}
		return property;
	}
	
	public void deactivateCondition(String key, String name) {
		Map<String,Conditional> condition_map = conditions.get(key);
		Conditional property = condition_map.get(name);
		if (property != null) {
			condition_map.remove(name);
//			updateConditionalBonuses(1);
		}
	}
	
	public List<String> castingClasses() {
		List<String> names = new ArrayList<String>();
		for (String name: PropertyLists.intelligenceCasters)
			if (master_stats.getValue(name + " Spell Levels") > 0)
				names.add(name);
		for (String name: PropertyLists.wisdomCasters)
			if (master_stats.getValue(name + " Spell Levels") > 0)
				names.add(name);
		for (String name: PropertyLists.charismaCasters)
			if (master_stats.getValue(name + " Spell Levels") > 0)
				names.add(name);
		return names;
	}
	
	public ExpListData getSelectEffects() {
		return new ExpListData(effect_select_map);
	}
	
	public void selectEffect(String map, String name) {
		for (XmlObjectModel effect: effect_select_map.get(map)){
			String effect_name = effect.getAttribute(XmlConst.NAME_ATTR);
			if (effect_name.equals(name)) {
				effect.setAttribute(XmlConst.ACTIVATE_ATTR, "True");
			} else {
				effect.setAttribute(XmlConst.ACTIVATE_ATTR, "False");
			}
		}
	}
}
