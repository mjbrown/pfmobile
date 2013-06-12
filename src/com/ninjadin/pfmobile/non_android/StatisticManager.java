package com.ninjadin.pfmobile.non_android;

import java.io.File;
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
	private StatisticGroup master_stats = new StatisticGroup();
	// A list of all CharacterStatistics names
	public List<String> stat_list;
	// A list of all Conditional Bonuses (no unconditional bonuses)
	private List<ConditionalBonus> conditional_bonuses;
	
	// A map of equipment / environment conditions
	Map<String,Map<String,ConditionalBonus>> conditions = new HashMap<String,Map<String,ConditionalBonus>>();;
	
	// A map of NAMEDactions available to the character
	Map<String, ActionGroup> combatActions = new HashMap<String, ActionGroup>();
	
	// A map of NAMED Attack Groups the character has available
	Map<String, AttackGroup> attacks = new HashMap<String, AttackGroup>();
	
	// A map of NAMED OnHitEffects
	Map<String, OnHitEffect> effects = new HashMap<String, OnHitEffect>();
	
	public StatisticManager() {
		for (String key: PropertyLists.keyNames) {
			Map<String,ConditionalBonus> condition_map = new HashMap<String,ConditionalBonus>();
			conditions.put(key, condition_map);
		}
		conditional_bonuses = new ArrayList<ConditionalBonus>();
		stat_list = new ArrayList<String>();
		master_stats.addStatNames(PropertyLists.abilityScoreNames);
		master_stats.addStatNames(PropertyLists.abilityModifierNames);
		master_stats.addStatNames(PropertyLists.basicStatsNames);
		master_stats.addStatNames(PropertyLists.otherStatisticNames);
		master_stats.addStatNames(PropertyLists.reductionNames);
		master_stats.addStatNames(PropertyLists.speedNames);
		master_stats.addStatNames(PropertyLists.casterLevelNames);
		master_stats.addStatNames(PropertyLists.skillNames);
		master_stats.addStatNames(PropertyLists.classLevelNames);
		master_stats.addStatNames(PropertyLists.equipRelatedNames);
		master_stats.addStatNames(PropertyLists.attackProperties);
		stat_list = master_stats.getKeyList();
		AttackGroup basicAttack = new AttackGroup("Armor Class", master_stats);
		attacks.put("New Attack", basicAttack);
	}
	
	public void newBonus(String statisticType, String stackType, String source, String value) {
		StatisticInstance bonusRecipient = master_stats.getStatistic(statisticType);
		if (bonusRecipient != null) { // Not a valid target
			ConditionalBonus newBonus = new ConditionalBonus(stackType, source, value);
			// Add the bonus to the statistic
			bonusRecipient.addBonus(newBonus);
			for (String stat: stat_list) {
				master_stats.getStatistic(stat).isDirty = true;
			}
		}
		return;
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
	
	public void readXMLBonuses(InputStream inStream, File inventory) throws IOException, XmlPullParserException {
		ConditionList currentConditions = new ConditionList();
		Stack<StatisticGroup> lastObject = new Stack<StatisticGroup>();
		lastObject.push(master_stats);
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(inStream, null);
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				String name = parser.getName();
				if (name != null) {
					if (name.equals(XmlConst.CONDITIONAL_TAG)) {
						currentConditions.endConditional();
					} else if (name.equals(XmlConst.ACTION_TAG) || name.equals(XmlConst.ATTACK_TAG)) {
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
				String types = parser.getAttributeValue(null, XmlConst.TYPE_ATTR);
				String values = parser.getAttributeValue(null, XmlConst.VALUE_ATTR);
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
								Log.d("COND_BNS", "Adding conditional bonus: " + type + " " + values);
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
					ActionGroup newAction = new ActionGroup(action_cost, master_stats);
					if (currentConditions.hasConditions()) {
						newAction.setConditions(currentConditions);
						conditional_bonuses.add(newAction);
					}
					if (names != null)		// Named action means it is referenced elsewhere
						combatActions.put(names, newAction);
					lastObject.push(newAction);
				} else if (tag.equals(XmlConst.ATTACK_TAG)) {
					String versus = parser.getAttributeValue(null, XmlConst.VERSUS_ATTR);
					String parent = parser.getAttributeValue(null, XmlConst.PARENT_ATTR);
					AttackGroup newAttack, inheritedAttack = null;
					if (parent != null) 
						inheritedAttack = attacks.get(parent);
					if (inheritedAttack != null)
						newAttack = new AttackGroup(inheritedAttack, lastObject.peek());
					else
						newAttack = new AttackGroup(versus, lastObject.peek());
					if (currentConditions.hasConditions()) {
						newAttack.setConditions(currentConditions);
						conditional_bonuses.add(newAttack);
					}
					if (names != null)		// Named attack means it is referenced elsewhere
						attacks.put(names, newAttack);
					lastObject.push(newAttack);
				} else if (tag.equals(XmlConst.ONHIT_TAG)) {
					String parent = parser.getAttributeValue(null, XmlConst.PARENT_ATTR);
					OnHitEffect newOnHit, inheritedOnHit = null;
					if (parent != null)
						inheritedOnHit = effects.get(parent);
					if (inheritedOnHit != null)
						newOnHit = new OnHitEffect(inheritedOnHit, lastObject.peek());
					else
						newOnHit = new OnHitEffect(types, lastObject.peek());
					AttackGroup parentAttack = (AttackGroup) lastObject.peek();
					if (lastObject.peek() instanceof AttackGroup) {
						parentAttack.addEffect(newOnHit);
					} else {
						Log.d("ON_HIT", "Parent of OnHitEffect isn't an Attack!");
					}
					if (currentConditions.hasConditions()) {
						newOnHit.setConditions(currentConditions);
						conditional_bonuses.add(newOnHit);
					}
					if (names != null)
						effects.put(names, newOnHit);
					lastObject.push(newOnHit);
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
			Log.d("Condition", "Condition not met:" + bonus.getStringValue());
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
}