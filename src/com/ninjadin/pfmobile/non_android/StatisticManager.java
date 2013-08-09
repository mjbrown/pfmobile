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

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class StatisticManager {
	// A map of all CharacterStatistics
	private StatisticGroup master_stats = new StatisticGroup(null);
	// A list of all Conditional Bonuses (no unconditional bonuses)
	private List<ConditionalBonus> conditional_bonuses;
	
	// A map of equipment / environment conditions
	Map<String,Map<String,ConditionalBonus>> conditions = new HashMap<String,Map<String,ConditionalBonus>>();;
	
	// A map of NAMED actions available to the character
	Map<String, ActionGroup> combatActions = new HashMap<String, ActionGroup>();
	
	// A map of NAMED Attack Groups the character has available
	Map<String, AttackGroup> attacks = new HashMap<String, AttackGroup>();
	
	// A map of NAMED OnHitEffects
	Map<String, OnHitDamage> effects = new HashMap<String, OnHitDamage>();
	
	public StatisticManager() {
		for (String key: PropertyLists.keyNames) {
			Map<String,ConditionalBonus> condition_map = new HashMap<String,ConditionalBonus>();
			conditions.put(key, condition_map);
		}
		conditional_bonuses = new ArrayList<ConditionalBonus>();
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
		ConditionalBonus property = conditions.get(key).get(name);
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
					if ((name.equals(XmlConst.CONDITIONAL_TAG)) || (name.equals(XmlConst.ACTIVATEDEFFECT_TAG))) {
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
				if (tag.equals(XmlConst.CONDITIONAL_TAG)) {
					currentConditions.startConditional(key, names, types, values);
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
							ConditionalBonus conditionalBonus = stat_group.addBonus(type, stackType, source, values);
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
						conditions.get(PropertyLists.prerequisite).put(names, new ConditionalBonus());
						updateConditionalBonuses();
					}
				} else if (tag.equals(XmlConst.CONDITION_TAG)) {
					if ((key != null) && (names != null)) {
						Map<String, ConditionalBonus> condition_map = conditions.get(key);
						if (condition_map != null) {
							for (String name: names.split(",")) {
								Log.d("ADD_COND", "Adding condition: " + key + " " + name);
								if (!currentConditions.hasConditions()) {
									conditions.get(key).put(name, new ConditionalBonus());
								} else {
									ConditionalBonus conditionalBonus = new ConditionalBonus();
									conditionalBonus.setConditions(currentConditions);
									conditions.get(key).put(key, conditionalBonus);
									conditional_bonuses.add(conditionalBonus);
								}
								// Update other conditional bonuses after a new one is added
								updateConditionalBonuses();
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
					}
					if (currentConditions.hasConditions()) {
						action.setConditions(currentConditions);
						conditional_bonuses.add(action);
					}
					if (names != null)		// Named action means it is referenced elsewhere
						combatActions.put(names, action);
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
					if (newAttack == null)
						newAttack = new AttackGroup(versus, target, uses, inheritedAttack, parentGroup);
					if (currentConditions.hasConditions()) {
						newAttack.setConditions(currentConditions);
						conditional_bonuses.add(newAttack);
					}
					if (names != null)		// Named attack means it is referenced elsewhere
						attacks.put(names, newAttack);
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
					if (newOnHit == null)
						newOnHit = new OnHitDamage(types, uses, inheritedOnHit, parentGroup);
					if (lastObject.peek() instanceof AttackGroup) {
						AttackGroup parentAttack = (AttackGroup) lastObject.peek();
						parentAttack.addOnHitDamage(newOnHit);
					} else {
						//Log.d("ON_HIT", "Parent of OnHitEffect isn't an Attack! " + names);
					}
					if (currentConditions.hasConditions()) {
						newOnHit.setConditions(currentConditions);
						conditional_bonuses.add(newOnHit);
					}
					if (names != null)
						effects.put(names, newOnHit);
					lastObject.push(newOnHit);
				} else if (tag.equals(XmlConst.ACTIVATEDEFFECT_TAG)) {
					currentConditions.startConditional(PropertyLists.activated, names, types, values);
				} else if (tag.equals(XmlConst.ITEM_TAG)) {
					currentConditions.startConditional(PropertyLists.activated, names, types, values);
					last_item_name = names;
				} else if (tag.equals(XmlConst.APPLYCOND_TAG)) {
					String unique = parser.getAttributeValue(null, XmlConst.UNIQUE_ATTR);
					OnHitCondition apply_cond = new OnHitCondition(key, names, unique); 
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
	
	private void updateConditionalBonuses() {
		for (ConditionalBonus bonus: conditional_bonuses) {
			updateConditionalBonus(bonus);
		}
	}
	
	private void updateConditionalBonus(ConditionalBonus bonus) {
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
				if (hasProperty(key, property))
					return true;
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
	
	public List<Map<String,String>> getActionList() {
		List<Map<String,String>> atk_list = new ArrayList<Map<String,String>>();
		for (Map.Entry<String, ActionGroup> entry: combatActions.entrySet()) {
			ActionGroup action = entry.getValue();
			if (action.isVisible() == false)
				continue;
			Map<String,String> visible_action = new HashMap<String,String>();
			visible_action.put(XmlConst.NAME_ATTR, entry.getKey());
			visible_action.put(XmlConst.COST_ATTR, action.getCost());
			visible_action.put(XmlConst.USES_ATTR, action.getUses());
			atk_list.add(visible_action);
		}
		return atk_list;
	}
	
	public List<List<Map<String,String>>> getActionData() {
		List<List<Map<String,String>>> atk_data = new ArrayList<List<Map<String,String>>>();
		for (Map.Entry<String, ActionGroup> entry: combatActions.entrySet()) {
			ActionGroup action = entry.getValue();
			if (action.isVisible() == false)
				continue;
			List<Map<String,String>> visible_action = new ArrayList<Map<String,String>>();
			atk_data.add(visible_action);
		}
		return atk_data;
	}
	
	public List<Map<String,String>> getActionData(String action_name) {
		List<Map<String,String>> atk_data = new ArrayList<Map<String,String>>();
		return atk_data;
	}
	
	public List<AttackGroup> getAttacks(String action_name) {
		return combatActions.get(action_name).getAttacks();
	}

}