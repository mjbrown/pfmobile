package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.dialogfragments.ModifierDialogFragment;
import com.ninjadin.pfmobile.dialogfragments.ModifierDialogFragment.ModifierDialogListener;
import com.ninjadin.pfmobile.dialogfragments.SpellListEditDialogFragment.SpellListFragmentListener;
import com.ninjadin.pfmobile.fragments.ActionFragment;
import com.ninjadin.pfmobile.fragments.CharacterSheetFragment;
import com.ninjadin.pfmobile.fragments.CharacterSheetFragment.CharacterSheetFragmentListener;
import com.ninjadin.pfmobile.fragments.ChoiceSelectFragment;
import com.ninjadin.pfmobile.fragments.ChoiceSelectFragment.ChoiceSelectFragmentListener;
import com.ninjadin.pfmobile.fragments.EffectSelectFragment;
import com.ninjadin.pfmobile.fragments.EffectSelectFragment.EffectSelectFragmentListener;
import com.ninjadin.pfmobile.fragments.EffectsFragment;
import com.ninjadin.pfmobile.fragments.EffectsFragment.EffectsFragmentListener;
import com.ninjadin.pfmobile.fragments.GeneratorMenuFragment;
import com.ninjadin.pfmobile.fragments.InventoryFragment;
import com.ninjadin.pfmobile.fragments.InventoryFragment.InventoryFragmentListener;
import com.ninjadin.pfmobile.fragments.ItemEditFragment.ItemEditListener;
import com.ninjadin.pfmobile.fragments.LevelsFragment;
import com.ninjadin.pfmobile.fragments.LevelsFragment.LevelsFragmentListener;
import com.ninjadin.pfmobile.fragments.ShowXMLFragment;
import com.ninjadin.pfmobile.fragments.SpellbookFragment;
import com.ninjadin.pfmobile.fragments.SpellbookFragment.SpellbookFragmentListener;
import com.ninjadin.pfmobile.non_android.CharacterXmlObject;
import com.ninjadin.pfmobile.non_android.EffectXmlObject;
import com.ninjadin.pfmobile.non_android.InventoryXmlObject;
import com.ninjadin.pfmobile.non_android.SpellGroup;
import com.ninjadin.pfmobile.non_android.SpellbookXmlObject;
import com.ninjadin.pfmobile.non_android.StatisticManager;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class GeneratorActivity extends FragmentActivity implements 
	ModifierDialogListener, LevelsFragmentListener, 
	CharacterSheetFragmentListener, ChoiceSelectFragmentListener, SpellListFragmentListener,
	InventoryFragmentListener, SpellbookFragmentListener, ItemEditListener, EffectsFragmentListener, 
	EffectSelectFragmentListener {
	public StatisticManager dependencyManager;
	public String masterCharFilename;
	public String inventoryFilename;
	public String effectFilename;
	public String spellsFilename;
	public String tempFilename;
	public File charFile;
	public File inventoryFile;
	public File effectFile;
	public File spellsFile;
	public File tempFile;
	CharacterXmlObject character;
	SpellbookXmlObject spells;
	EffectXmlObject effects;
	InventoryXmlObject inventory;
	XmlObjectModel dependencies;
	final static public int CHARACTER_MODEL = 0;
	final static public int DEPENDENCIES_MODEL = 1;
	final static public int INVENTORY_MODEL = 2;
	final static public int EFFECTS_MODEL = 3;
	
	public Boolean dirtyFiles = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		refreshCharData();
		setContentView(R.layout.activity_fragment_container);
		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			GeneratorMenuFragment firstFragment = new GeneratorMenuFragment();
			firstFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
			
		}
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.main_menu:
			startMenu("Main Menu");
			return true;
		case R.id.character_menu:
			startMenu("Character Menu");
			return true;
		case R.id.modifiers:
			showModifierDialog();
			return true;
		}
			
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super .onResume();
		refreshCharData();
	}
	
	public void refreshCharData() {
		if (dirtyFiles == false)
			return;
		dirtyFiles = false;
		final GeneratorActivity context = this;
		Intent intent = context.getIntent();
		masterCharFilename = intent.getStringExtra(LoginLoadActivity.CHARFILE_MESSAGE);
		inventoryFilename = intent.getStringExtra(LoginLoadActivity.INVFILE_MESSAGE);
		effectFilename = intent.getStringExtra(LoginLoadActivity.EFFECTFILE_MESSAGE);
		spellsFilename = intent.getStringExtra(LoginLoadActivity.SPELLSFILE_MESSAGE);
		tempFilename = masterCharFilename.concat(".temp");
		charFile = new File(context.getFilesDir(), masterCharFilename);
		tempFile = new File(context.getFilesDir(), tempFilename);
		character = new CharacterXmlObject(charFile, tempFile, 
				this.getResources().openRawResource(R.raw.base_levels));
		effectFile = new File(context.getFilesDir(), effectFilename);
		effects = new EffectXmlObject(effectFile, tempFile);
		inventoryFile = new File(context.getFilesDir(), inventoryFilename);
		inventory = new InventoryXmlObject(inventoryFile, tempFile );
		spellsFile = new File(context.getFilesDir(), spellsFilename);
		spells = new SpellbookXmlObject(spellsFile, tempFile);
		dependencies = new XmlObjectModel(getResources().openRawResource(R.raw.dependencies));;
		refreshManager();
		
	}
	
	private void refreshManager() {
		dependencyManager = new StatisticManager();
		dependencyManager.readModel(dependencies);
		dependencyManager.readModel(character);
		dependencyManager.readModel(effects);
		dependencyManager.readModel(inventory);
		dependencyManager.readModel(spells);
		dependencyManager.updateConditionalBonuses(10);
	}
	
	@Override
	public void onPause() {
		super .onPause();
		saveCharacterState();
	}
	public void saveCharacterState() {
		try {
			character.saveChanges();
			spells.saveChanges();
			effects.saveChanges();
			inventory.saveChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onDestroy() {
		super .onDestroy();
	}
	
	public void launchStatistics(View view) {
		refreshManager();
		CharacterSheetFragment newFragment = new CharacterSheetFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchLevels(View view) {
		refreshManager();
		LevelsFragment newFragment = new LevelsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchAttacks(View view) {
		refreshManager();
		ActionFragment newFragment = new ActionFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchInventory(View view) {
		refreshManager();
		InventoryFragment newFragment = new InventoryFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchEffectSelect(View view) {
		refreshManager();
		EffectSelectFragment newFragment = new EffectSelectFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void launchFilterSelect(View view, String groupName, String subGroup, String specificNames, String choiceId) {
		refreshManager();
		ChoiceSelectFragment newFragment = new ChoiceSelectFragment();
		Bundle passedData = new Bundle();
		passedData.putString(XmlConst.GRPNAME_ATTR, groupName);
		passedData.putString(XmlConst.SUBGRP, subGroup);
		passedData.putString(XmlConst.SPECIFIC_ATTR, specificNames);
		passedData.putString("charFileName", charFile.getName());
		passedData.putString("choiceId", choiceId);
		if (groupName.equals("Race")) {
			passedData.putInt("rawDataInt", R.raw.races);
		} else if (groupName.equals("Class Level")) {
			passedData.putInt("rawDataInt", R.raw.classes);
		} else if (groupName.equals("Feat")) {
			passedData.putInt("rawDataInt", R.raw.feats);
		} else {
			passedData.putInt("rawDataInt", R.raw.other);
		}
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void startMenu(String message) {
		refreshManager();
		GeneratorMenuFragment firstFragment = new GeneratorMenuFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);
		transaction.commit();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	public void launchSpellbook(View view) {
		refreshManager();
		SpellbookFragment firstFragment = new SpellbookFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchEffects(View view) {
		refreshManager();
		EffectsFragment fragment = new EffectsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void launchCharXML(View view) {
		saveCharacterState();
		Bundle passedData = new Bundle();
		passedData.putString("filename", masterCharFilename);
		ShowXMLFragment newFragment = new ShowXMLFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void launchInventoryXML(View view) {
		saveCharacterState();
		Bundle passedData = new Bundle();
		passedData.putString("filename", inventoryFilename);
		ShowXMLFragment newFragment = new ShowXMLFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void launchEffectXML(View view) {
		saveCharacterState();
		Bundle passedData = new Bundle();
		passedData.putString("filename", effectFilename);
		ShowXMLFragment newFragment = new ShowXMLFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchSpellbookXML(View view) {
		saveCharacterState();
		Bundle passedData = new Bundle();
		passedData.putString("filename", spellsFilename);
		ShowXMLFragment newFragment = new ShowXMLFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void performAction(String action_name) {
		List<XmlObjectModel> effect_list = dependencyManager.getEffects(action_name);
		for (XmlObjectModel effect: effect_list) {
			effects.addEffect(effect, dependencyManager);
		}
		refreshManager();
		startMenu(action_name + " action taken.");
	}
	
	public void activateCondition(String key, String name) {
		effects.addCondition(key, name);
		//dependencyManager.activateCondition(key, name);
	}
	
	public void deactivateCondition(String key, String name) {
		effects.removeCondition(key, name);
		//dependencyManager.deactivateCondition(key, name);
	}
	
	public Boolean isConditionActive(String key, String name) {
		return dependencyManager.masterHasProperty(key, name);
	}
	
	public void showModifierDialog() {
		DialogFragment dialog = new ModifierDialogFragment();
		dialog.show(getSupportFragmentManager(), "ModifierDialogFragment");
	}
	
	@Override
	public void onModifierPosClick(ModifierDialogFragment dialog) {
		refreshManager();
	}
	
	@Override
	public void onModifierNegClick(ModifierDialogFragment dialog) {
		
	}
	
	@Override
	public void characterLevelUp(LevelsFragment fragment) {
		character.addLevel();
		refreshManager();
	}
	
	@Override
	public void characterLevelDown(LevelsFragment fragment) {
		character.removeLevel();
		refreshManager();
	}
	
	@Override
	public int currentCharacterLevel() {
		return character.currentLevel();
	}
	
	@Override
	public String characterAttributeRanks(String skill_name) {
		return character.attributeRanks(skill_name);
	}
	
	@Override
	public void characterAttributeIncrement(String skill_name) {
		character.incrementRanksAttribute(skill_name);
	}
	
	@Override
	public void characterAttributeDecrement(String skill_name) {
		character.decrementAttribute(skill_name);
	}
	
	@Override
	public List<XmlObjectModel> characterChoiceList() {
		return character.getChoiceList();
	}
	
	@Override
	public XmlObjectModel getXmlModel(int enum_model) {
		if (character == null)
			refreshCharData();
		if (enum_model == CHARACTER_MODEL)
			return character;
		if (enum_model == DEPENDENCIES_MODEL)
			return dependencies;
		if (enum_model == INVENTORY_MODEL)
			return inventory;
		if (enum_model == EFFECTS_MODEL)
			return effects;
		return null;
	}
	
	@Override
	public void insertCharacterSelection(XmlObjectModel selection, int choice_number) {
		character.addChoice(selection, choice_number);
		refreshManager();
		startMenu(selection.getAttribute(XmlConst.NAME_ATTR) + " selected.");
	}
	
	@Override
	public void spellbookAddSpell(String class_name, String spell_name, String spell_level) {
		spells.createEntry(class_name, spell_name, spell_level);
		refreshManager();
	}
	
	@Override
	public Boolean itemIsEquipped(String item_id) {
		return inventory.isEquipped(item_id);
	}
	
	@Override
	public void equipItem(String item_id, Boolean is_equipped) {
		inventory.equipItem(item_id, is_equipped);
		refreshManager();
	}
	
	@Override
	public void inventoryCreateItem(String name, String slot) {
		inventory.createItem(name, slot);
		refreshManager();
	}
	
	@Override
	public void inventoryDeleteItem(String id) {
		inventory.deleteById(id);
		refreshManager();
	}

	@Override
	public void itemAddProperty(XmlObjectModel property, String item_id, String property_id) {
		inventory.setProperty(property, item_id, property_id);
		refreshManager();
	}
	
	@Override
	public void itemDeleteProperty(String item_id, String property_id) {
		inventory.deleteProperty(item_id, property_id);
		refreshManager();
	}
	
	@Override
	public Map<String,String> getItemPropertyOptionMap(String item_id, String property_id) {
		if (inventory == null)
			refreshCharData();
		return inventory.getItemPropertyOptionMap(item_id, property_id);
	}
	
	@Override
	public List<String> getSpellcastingClassList() {
		return dependencyManager.castingClasses();
	}
	
	@Override
	public List<SpellGroup> getSpellsAvailable() {
		refreshManager();
		return dependencyManager.getSpells();
	}
	
	@Override
	public void removeEffect(String effect_name) {
		effects.removeEffect(effect_name, dependencyManager);
	}

	@Override
	public ExpListData getSelectEffects() {
		return dependencyManager.getSelectEffects();
	}

	@Override
	public void selectEffect(String select, String name) {
		dependencyManager.selectEffect(select, name);
	}
	
	@Override
	public void castSpell(SpellGroup spell) {
		effects.expendSpell(spell.getAttribute(XmlConst.USED_ATTR));
		refreshManager();
		startMenu(spell.getAttribute(XmlConst.NAME_ATTR) + " used.");
	}
	
	@Override
	public void memorizeSpell(String id, Integer number) {
		spells.setMemorized(id, number);
		refreshManager();
		startMenu("Spells memorized.");
	}
	
	@Override
	public void deleteSpell(String id) {
		spells.deleteById(id);
		refreshManager();
		startMenu("Spell Deleted.");
	}
}
