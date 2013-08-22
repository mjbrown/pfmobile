package com.ninjadin.pfmobile.non_android;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

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
	Map<String, OnHitDamage> effects = new HashMap<String, OnHitDamage>();
	
	// A list of modifiers which can be turned on or off on a whim
	List<ActivatedCondition> modifiers = new ArrayList<ActivatedCondition>();
	
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
	
	public void readXMLBonuses(InputStream inStream) throws IOException, XmlPullParserException {
		ConditionList currentConditions = new ConditionList();
		Stack<StatisticGroup> lastObject = new Stack<StatisticGroup>();
		lastObject.push(master_stats);
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(inStream, null);
		String last_item_name = null;
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				String name = parser.getName();
				if (name != null) {
					if (name.equals(XmlConst.CONDITIONAL_TAG)) {
						currentConditions.endConditional();
					} else if (name.equals(XmlConst.ITEM_TAG)) {
						currentConditions.endConditional();
						last_item_name = null;
					}else if (name.equals(XmlConst.ACTION_TAG) || name.equals(XmlConst.ATTACK_TAG) || name.equals(XmlConst.ONHIT_TAG)) {
						lastObject.pop();
					}
				}
				continue;
			}
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			String tag = parser.getName();
			if (tag != null) {
				String key = parser.getAttributeValue(null, XmlConst.KEY_ATTR);
				String names = parser.getAttributeValue(null, XmlConst.NAME_ATTR);
				if ((last_item_name != null) && (names != null)) {
					names = names.replace("[Item Name]", last_item_name);
				}
				String types = parser.getAttributeValue(null, XmlConst.TYPE_ATTR);
				String values = parser.getAttributeValue(null, XmlConst.VALUE_ATTR);
				String uses = parser.getAttributeValue(null, XmlConst.USES_ATTR);
				String comparator = parser.getAttributeValue(null, XmlConst.COMPARE_ATTR);
				if (tag.equals(XmlConst.CONDITIONAL_TAG)) {
					String activate = parser.getAttributeValue(null, XmlConst.ACTIVATE_ATTR);
					if (activate != null)
						if (activate.equals(PropertyLists.manual)) {
							ActivatedCondition newBonus = new ActivatedCondition(key, names);
							modifiers.add(newBonus);
							if (currentConditions.hasConditions()) {
								newBonus.setConditions(currentConditions);
								conditional_bonuses.add(newBonus);
							}
						}
					currentConditions.startConditional(key, names, types, values, comparator);
				} else if (tag.equals(XmlConst.BONUS_TAG)) {
					String stackType = parser.getAttributeValue(null, XmlConst.STACKTYPE_ATTR);
					if ((types != null) && (values != null)) {
						String source = parser.getAttributeValue(null, XmlConst.SOURCE_ATTR);
						if (stackType == null)
							stackType = "Base";
						if (source == null)
							source = "Natural";
						StatisticGroup stat_group = lastObject.peek();
						for (String type: types.split(",")) {
							Bonus conditionalBonus = stat_group.addBonus(type, stackType, source, values);
							if (currentConditions.hasConditions()) {
								//Log.d("COND_BNS", "Adding conditional bonus: " + type + " " + values);
								conditionalBonus.setConditions(currentConditions);
								conditional_bonuses.add(conditionalBonus);
								updateConditionalBonus(conditionalBonus);
							}
						}
					} else {
						Log.d("ReadXML4", "Bonus missing type/value!");
					}
				} else if (tag.equals(XmlConst.CHOICE_TAG) || (tag.equals(XmlConst.CHOSEN_TAG))) {
					if (names != null) {
						activateCondition(PropertyLists.prerequisite, names);
					}
				} else if (tag.equals(XmlConst.CONDITION_TAG)) {
					if ((key != null) && (names != null)) {
						Map<String, Conditional> condition_map = conditions.get(key);
						if (condition_map != null) {
							for (String name: names.split(",")) {
								Log.d("ADD_COND", "Adding condition: " + key + " " + name);
								if (!currentConditions.hasConditions()) {
									conditions.get(key).put(name, new Conditional());
								} else {
									Conditional conditionalBonus = new Conditional();
									conditionalBonus.setConditions(currentConditions);
									conditions.get(key).put(name, conditionalBonus);
									conditional_bonuses.add(conditionalBonus);
								}
							}
						} else {
							Log.d("ReadXML5", "No condition map for key: " + key);
						}
					} else {
						Log.d("ReadXML5", "Condition without key/name!");
					}
				} else if (tag.equals(XmlConst.ACTION_TAG)) {
					String action_cost = parser.getAttributeValue(null, XmlConst.COST_ATTR);
					String parent = parser.getAttributeValue(null, XmlConst.PARENT_ATTR);
					String visible = parser.getAttributeValue(null, XmlConst.VISIBLE_ATTR);
					ActionGroup action = null;
					if (names != null)
						action = combatActions.get(names);
					if (action == null) {
						ActionGroup parent_action = combatActions.get(parent);
						action = new ActionGroup(action_cost, uses, parent_action, master_stats);
						if (visible != null) {
							if (visible.equals("No"))
								action.setVisibility(false);
						}
						if (currentConditions.hasConditions()) {
							action.setConditions(currentConditions);
							conditional_bonuses.add(action);
						}
						if (names != null)		// Named action means it is referenced elsewhere
							combatActions.put(names, action);
					}
					lastObject.push(action);
				} else if (tag.equals(XmlConst.ATTACK_TAG)) {
					String versus = parser.getAttributeValue(null, XmlConst.VERSUS_ATTR);
					String parent = parser.getAttributeValue(null, XmlConst.PARENT_ATTR);
					String target = parser.getAttributeValue(null, XmlConst.TARGET_ATTR);
					AttackGroup inheritedAttack = null;
					StatisticGroup parentGroup = null;
					if (parent != null) 
						inheritedAttack = attacks.get(parent);
					if (!(lastObject.peek() == master_stats)) {
						//Log.d("ATTACK", "Attack " + names + " has inherited " + parent);
						parentGroup = lastObject.peek();
					}
					AttackGroup newAttack = null;
					if (names != null)
						newAttack = attacks.get(names);
					if (newAttack == null) {
						newAttack = new AttackGroup(versus, target, uses, inheritedAttack, parentGroup);
						if (currentConditions.hasConditions()) {
							newAttack.setConditions(currentConditions);
							conditional_bonuses.add(newAttack);
						}
						if (names != null)		// Named attack means it is referenced elsewhere
							attacks.put(names, newAttack);
					}
					if (lastObject.peek() instanceof ActionGroup) {
						ActionGroup action_group = (ActionGroup) lastObject.peek();
						action_group.addAttack(newAttack);
					} else {
						//Log.d("ATTACK", "Parent of attack isn't action! " + names);
					}
					lastObject.push(newAttack);
				} else if (tag.equals(XmlConst.ONHIT_TAG)) {
					String parent = parser.getAttributeValue(null, XmlConst.PARENT_ATTR);
					OnHitDamage inheritedOnHit = null;
					StatisticGroup parentGroup = null;
					if (parent != null)
						inheritedOnHit = effects.get(parent);
					if (!(lastObject.peek() == master_stats))
						parentGroup = lastObject.peek();
					OnHitDamage newOnHit = null;
					if (names != null)
						newOnHit = effects.get(names);
					if (newOnHit == null) {
						newOnHit = new OnHitDamage(types, uses, inheritedOnHit, parentGroup);
						if (currentConditions.hasConditions()) {
							newOnHit.setConditions(currentConditions);
							conditional_bonuses.add(newOnHit);
						}
						if (names != null)
							effects.put(names, newOnHit);
					}
					if (lastObject.peek() instanceof AttackGroup) {
						AttackGroup parentAttack = (AttackGroup) lastObject.peek();
						parentAttack.addOnHitDamage(newOnHit);
					} else {
						//Log.d("ON_HIT", "Parent of OnHitEffect isn't an Attack! " + names);
					}
					lastObject.push(newOnHit);
				} else if (tag.equals(XmlConst.ITEM_TAG)) {
					currentConditions.startConditional(PropertyLists.equipment, names, types, values, comparator);
					last_item_name = names;
				} else if (tag.equals(XmlConst.APPLYCOND_TAG)) {
					String add = parser.getAttributeValue(null, XmlConst.ADD_ATTR);
					if ((last_item_name != null) && (add != null)) {
						add = add.replace("[Item Name]", last_item_name);
					}
					String remove = parser.getAttributeValue(null, XmlConst.REMOVE_ATTR);
					if ((last_item_name != null) && (remove != null)) {
						remove = remove.replace("[Item Name]", last_item_name);
					}
					OnHitCondition apply_cond = new OnHitCondition(key, add, remove); 
					if (currentConditions.hasConditions()) {
						apply_cond.setConditions(currentConditions);
						conditional_bonuses.add(apply_cond);
					}
					if (lastObject.peek() instanceof ActionGroup) {
						ActionGroup action_group = (ActionGroup) lastObject.peek();
						action_group.addOnHitCondition(apply_cond);
					} else {
						//Log.d("APPLYCOND", "Parent of applycond isn't action! " + names);
					}
				}
			}
		}
	}
	
	public void updateConditionalBonuses(int retries) {
		if (retries == 0)
			return;
		Boolean run_again = false;
		for (Conditional bonus: conditional_bonuses) {
			Boolean already_active = bonus.isActive();
			updateConditionalBonus(bonus);
			if (already_active != bonus.isActive())
				run_again = true;
		}
		if (run_again)
			updateConditionalBonuses(retries - 1);
		Log.d("RECUR_UPDATE", Integer.toString(retries));
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
		if (kv.value == null)
			for (String property: kv.key.split(",")) {
				if (hasProperty(key, property)) {
					if (kv.comparator != null)
						if (kv.comparator.equals("Not"))
							continue;
					return true;
				} else if (kv.comparator != null) {
					if (kv.comparator.equals("Not"))
						return true;
				}
			}
		else {
			String[] keys = kv.key.split(",");
			String[] values = kv.value.split(",");
			if (keys.length != values.length) {
				Log.d("hasCondition", "Key Value size mismatch " + kv.key + " " + kv.value);
				return false;
			}
			for (int i = 0; i < keys.length; i++) {
				if (getValue(keys[i]) >= evaluateValue(values[i]))
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
			Map<String,String> visible_action = new HashMap<String,String>();
			visible_action.put(XmlConst.NAME_ATTR, entry.getKey());
			visible_action.put(XmlConst.COST_ATTR, action.getCost());
			visible_action.put(XmlConst.USES_ATTR, action.getUses());
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

		List<OnHitDamage> damages = attack.getOnHitDamages();
		for (OnHitDamage dmg: damages) {
			for (int i = 0; i < length; i++) {
				String value = dmg.getStringValue(dmg_dice.get(i));
				int previous = damage.get(i);
				damage.add(i, master_stats.evaluate(value) + previous);
			}
		}
		int has_dice = 0;
		String output = "";
		for (int i = 0; i < length; i++) {
			int value = damage.get(i);
			if (value > 0) {
				if (has_dice > 0)
					output += "+";
				output += Integer.toString(value);
				if (i < length - 1)
					output += PropertyLists.die[i];
				has_dice += value;
			}
		}
		return output;
	}
	
	public List<AttackGroup> getAttacks(String action_name) {
		return combatActions.get(action_name).getAttacks();
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
	
}