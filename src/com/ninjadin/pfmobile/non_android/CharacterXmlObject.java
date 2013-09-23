package com.ninjadin.pfmobile.non_android;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class CharacterXmlObject extends XmlObjectModel {
	XmlObjectModel levels;

	XmlObjectModel attribute_ranks;
	Map<String, XmlObjectModel> attributes = new HashMap<String, XmlObjectModel>();
	
	XmlObjectModel level_models;
	
	List<XmlObjectModel> choice_list;
	
	public CharacterXmlObject(File char_file, File temp_file, InputStream level_xml) {
		super (char_file, temp_file);
		initialize();
		level_models = new XmlObjectModel(level_xml);
	}
	
	private void initialize() {
		for (XmlObjectModel model: getChildren()) {
			if (model.getTag().equals(XmlConst.ATTRIBUTES_TAG)) {
				attribute_ranks = model;
			}
			if (model.getTag().equals(XmlConst.LEVELS_TAG)) {
				levels = model;
			}
		}
		for (XmlObjectModel model: attribute_ranks.getChildren()) {
			String skill = model.getAttribute(XmlConst.TYPE_ATTR);
			this.attributes.put(skill, model);
		}
		choice_list = new ArrayList<XmlObjectModel>();
		recursiveChoiceFind(this, "None.");
	}
	
	public void addChoice(XmlObjectModel selection, int position) {
		XmlObjectModel choice = choice_list.get(position);
		choice.clearChildren();
		String name = selection.getAttribute(XmlConst.NAME_ATTR);
		choice.setAttribute(XmlConst.NAME_ATTR, name);
		for (XmlObjectModel child: selection.getChildren())
			choice.addChild(child);
	}
	
	public List<XmlObjectModel> getChoiceList() {
		choice_list = new ArrayList<XmlObjectModel>();
		recursiveChoiceFind(this, "None.");
		return choice_list;
	}
	
	private void recursiveChoiceFind(XmlObjectModel model, String parent_name) {
		if (model.getTag().equals(XmlConst.CHOICE_TAG)) {
			model.setAttribute(XmlConst.SOURCE_ATTR, parent_name);
			model.setAttribute(XmlConst.NUM_ATTR, Integer.toString(choice_list.size()));
			choice_list.add(model);
			parent_name = model.getAttribute(XmlConst.NAME_ATTR);
		} else if (model.getTag().equals(XmlConst.LEVEL_TAG)) {
			parent_name = "Character Level " + model.getAttribute(XmlConst.NUM_ATTR);
		}
		for (XmlObjectModel child: model.getChildren()) {
			recursiveChoiceFind(child, parent_name);
		}
	}
	
	public void addLevel() {
		int level_number = levels.getChildren().size();
		if (level_number < 21) {
			XmlObjectModel level = level_models.getChildren().get(level_number);
			levels.addChild(level);
		}
	}
	
	public void removeLevel() {
		int level_number = levels.getChildren().size();
		if (level_number > 1) {
			levels.removeChild(level_number - 1);
		}
	}
	
	public int currentLevel() {
		return levels.getChildren().size() - 1;
	}
	
	private XmlObjectModel getRanksAttribute(String attribute_name) {
		XmlObjectModel attr = attributes.get(attribute_name);
		if (attr == null) {
			attr = new XmlObjectModel(XmlConst.BONUS_TAG);
			attr.setAttribute(XmlConst.TYPE_ATTR, attribute_name);
			attr.setAttribute(XmlConst.STACKTYPE_ATTR, PropertyLists.ranks);
			attr.setAttribute(XmlConst.VALUE_ATTR, "0");
			attribute_ranks.addChild(attr);
			attributes.put(attribute_name, attr);
		}
		return attr;
	}
	
	public void incrementRanksAttribute(String attribute_name) {
		XmlObjectModel attr = getRanksAttribute(attribute_name);
		int old_value = Integer.parseInt(attr.getAttribute(XmlConst.VALUE_ATTR));
		attr.setAttribute(XmlConst.VALUE_ATTR, Integer.toString(old_value + 1));
		updateAttributes();
	}
	
	public void decrementAttribute(String attribute_name) {
		XmlObjectModel attribute = getRanksAttribute(attribute_name);
		if (attribute != null) {
			int old_value = Integer.parseInt(attribute.getAttribute(XmlConst.VALUE_ATTR));
			attribute.setAttribute(XmlConst.VALUE_ATTR, Integer.toString(old_value - 1));
		}
		updateAttributes();
	}
	
	public String attributeRanks(String attribute_name) {
		XmlObjectModel attribute = attributes.get(attribute_name);
		if (attribute != null)
			return attribute.getAttribute(XmlConst.VALUE_ATTR);
		else
			return "0";
	}
	
	private void updateAttributes() {
		int point_buy = 0;
		for (String ability_score: PropertyLists.abilityScoreNames) {
			point_buy += pointBuyCost(Integer.parseInt(attributes.get(ability_score).getAttribute(XmlConst.VALUE_ATTR)));
		}
		XmlObjectModel point_buy_attr = getRanksAttribute(PropertyLists.point_buy_cost);
		point_buy_attr.setAttribute(XmlConst.VALUE_ATTR, Integer.toString(point_buy));
		
		int skills_used = 0;
		for (String skill_name: PropertyLists.skillNames) {
			skills_used += Integer.parseInt(getRanksAttribute(skill_name).getAttribute(XmlConst.VALUE_ATTR));
		}
		XmlObjectModel skill_points_used = getRanksAttribute(PropertyLists.skill_ranks_used);
		skill_points_used.setAttribute(XmlConst.VALUE_ATTR, Integer.toString(skills_used));
		int favored_points_used = 0;
		for (String point_name: PropertyLists.pointsNames) {
			favored_points_used += Integer.parseInt(getRanksAttribute(point_name).getAttribute(XmlConst.VALUE_ATTR));
		}
		XmlObjectModel favored_used = getRanksAttribute(PropertyLists.favored_points_used);
		favored_used.setAttribute(XmlConst.VALUE_ATTR, Integer.toString(favored_points_used));
	}
	
	private int pointBuyCost(int value) {
		int table[] = {-30,-25,-20,-16,-12,-9,-6,-4,-2,-1,0,1,2,3,5,7,10,13,17,21,26,31,37};
		if (value > table.length)
			return 999;
		return table[value];
	}
}
