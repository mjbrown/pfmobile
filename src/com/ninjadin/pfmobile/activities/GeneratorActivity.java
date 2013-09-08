package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.fragments.ActionFragment;
import com.ninjadin.pfmobile.fragments.CharacterSheetFragment;
import com.ninjadin.pfmobile.fragments.CharacterSheetFragment.CharacterSheetFragmentListener;
import com.ninjadin.pfmobile.fragments.ChoiceSelectFragment;
import com.ninjadin.pfmobile.fragments.ChoiceSelectFragment.ChoiceSelectFragmentListener;
import com.ninjadin.pfmobile.fragments.GeneratorMenuFragment;
import com.ninjadin.pfmobile.fragments.InventoryFragment;
import com.ninjadin.pfmobile.fragments.ItemEditDialogFragment;
import com.ninjadin.pfmobile.fragments.ItemEditDialogFragment.ItemEditDialogListener;
import com.ninjadin.pfmobile.fragments.LevelsFragment;
import com.ninjadin.pfmobile.fragments.LevelsFragment.LevelsFragmentListener;
import com.ninjadin.pfmobile.fragments.ModifierDialogFragment;
import com.ninjadin.pfmobile.fragments.ModifierDialogFragment.ModifierDialogListener;
import com.ninjadin.pfmobile.fragments.ShowXMLFragment;
import com.ninjadin.pfmobile.fragments.SpellListFragment.SpellListFragmentListener;
import com.ninjadin.pfmobile.fragments.SpellbookFragment;
import com.ninjadin.pfmobile.non_android.CharacterXmlObject;
import com.ninjadin.pfmobile.non_android.EffectXmlObject;
import com.ninjadin.pfmobile.non_android.InventoryEditor;
import com.ninjadin.pfmobile.non_android.SpellbookXmlObject;
import com.ninjadin.pfmobile.non_android.StatisticManager;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class GeneratorActivity extends FragmentActivity implements 
	ItemEditDialogListener, ModifierDialogListener, LevelsFragmentListener, 
	CharacterSheetFragmentListener, ChoiceSelectFragmentListener, SpellListFragmentListener {
	public StatisticManager dependencyManager;
	public InventoryEditor inventoryEditor;
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
	XmlObjectModel dependencies, inventory;
	final static public int CHARACTER_MODEL = 0;
	final static public int DEPENDENCIES_MODEL = 1;
	
	public Boolean dirtyFiles = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		refreshCharData();
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
		getMenuInflater().inflate(R.menu.char_fragments_list, menu);
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
		character = new CharacterXmlObject(charFile, tempFile, this.getResources().openRawResource(R.raw.base_levels));
		effectFile = new File(context.getFilesDir(), effectFilename);
		effects = new EffectXmlObject(effectFile, tempFile);
		inventoryFile = new File(context.getFilesDir(), inventoryFilename);
		inventory = new XmlObjectModel(inventoryFile, tempFile);
		spellsFile = new File(context.getFilesDir(), spellsFilename);
		spells = new SpellbookXmlObject(spellsFile, tempFile, getResources().openRawResource(R.raw.spells));
		dependencies = new XmlObjectModel(getResources().openRawResource(R.raw.dependencies));;
		refreshManager();
		
		inventoryEditor = new InventoryEditor(inventoryFile);
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
		saveCharacterState();
		refreshCharData();
		CharacterSheetFragment newFragment = new CharacterSheetFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchLevels(View view) {
		refreshCharData();
		LevelsFragment newFragment = new LevelsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchAttacks(View view) {
		refreshCharData();
		ActionFragment newFragment = new ActionFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchInventory(View view) {
		InventoryFragment newFragment = new InventoryFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void launchFilterSelect(View view, String groupName, String subGroup, String specificNames, String choiceId) {
		refreshCharData();
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
		} else if (groupName.equals("Equipment")) {
			passedData.putInt("rawDataInt", R.raw.equipment);
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
		GeneratorMenuFragment firstFragment = new GeneratorMenuFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);
		transaction.commit();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	public void launchSpellbook(View view) {
		refreshCharData();
		SpellbookFragment firstFragment = new SpellbookFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void launchCharXML(View view) {
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
		Bundle passedData = new Bundle();
		passedData.putString("filename", spellsFilename);
		ShowXMLFragment newFragment = new ShowXMLFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
/*	
	public void equipItem(String slotName, String itemName) {
		if (!(slotName.equals(PropertyLists.inventory))) {
			effectEditor.deactivateEffect(slotName, PropertyLists.equipment);
			Map<String,String> slot_activate = new HashMap<String,String>();
			slot_activate.put(XmlConst.NAME_ATTR, slotName);
			slot_activate.put(XmlConst.REMOVE_ATTR, itemName);
			slot_activate.put(XmlConst.KEY_ATTR, PropertyLists.equipment);
			effectEditor.activateEffect(slot_activate);
		}

		Map<String,String> item_activate = new HashMap<String,String>();
		item_activate.put(XmlConst.NAME_ATTR, itemName);
		item_activate.put(XmlConst.KEY_ATTR, PropertyLists.equipment);
		effectEditor.activateEffect(item_activate);

		try {
			effectEditor.saveEffects(effectFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dirtyFiles = true;
		startMenu(itemName + " equipped to " + slotName + ".");
	}
*/
	public void performAction(String action_name) {
		List<XmlObjectModel> effect_list = dependencyManager.getEffects(action_name);
		for (XmlObjectModel effect: effect_list) {
			effects.addEffect(effect);
		}
		startMenu(action_name + " action taken.");
	}
	
	public void activateCondition(String key, String name) {
		effects.addCondition(key, name);
		dependencyManager.activateCondition(key, name);
	}
	
	public void deactivateCondition(String key, String name) {
		effects.removeCondition(key, name);
		dependencyManager.deactivateCondition(key, name);
	}
	
	public void showItemEditDialog(int groupPosition, int childPosition) {
		DialogFragment dialog = new ItemEditDialogFragment();
		dialog.show(getSupportFragmentManager(), "ItemEditDialogFragment");
	}
	
	public void showModifierDialog() {
		DialogFragment dialog = new ModifierDialogFragment();
		dialog.show(getSupportFragmentManager(), "ModifierDialogFragment");
	}
	
	@Override
	public void onItemEditPositiveClick(ItemEditDialogFragment dialog) {

	}
	
	@Override
	public void onItemEditNegativeClick(ItemEditDialogFragment dialog) {
		
	}
	
	@Override
	public void onModifierPosClick(ModifierDialogFragment dialog) {
		ActionFragment fragment = (ActionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		refreshCharData();
		fragment.reloadActionData();
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
		if (enum_model == CHARACTER_MODEL)
			return character;
		if (enum_model == DEPENDENCIES_MODEL)
			return dependencies;
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
}
