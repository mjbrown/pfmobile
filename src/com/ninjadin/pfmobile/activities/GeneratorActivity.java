package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

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
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.fragments.ActionFragment;
import com.ninjadin.pfmobile.fragments.CharacterSheetFragment;
import com.ninjadin.pfmobile.fragments.ChoiceSelectFragment;
import com.ninjadin.pfmobile.fragments.GeneratorMenuFragment;
import com.ninjadin.pfmobile.fragments.InventoryFragment;
import com.ninjadin.pfmobile.fragments.ItemEditDialogFragment;
import com.ninjadin.pfmobile.fragments.ItemEditDialogFragment.ItemEditDialogListener;
import com.ninjadin.pfmobile.fragments.ItemEditFragment;
import com.ninjadin.pfmobile.fragments.LevelsFragment;
import com.ninjadin.pfmobile.fragments.ModifierDialogFragment;
import com.ninjadin.pfmobile.fragments.ModifierDialogFragment.ModifierDialogListener;
import com.ninjadin.pfmobile.fragments.ShowXMLFragment;
import com.ninjadin.pfmobile.fragments.SpellbookFragment;
import com.ninjadin.pfmobile.fragments.TemplateSelectFragment;
import com.ninjadin.pfmobile.non_android.AttackGroup;
import com.ninjadin.pfmobile.non_android.CharacterEditor;
import com.ninjadin.pfmobile.non_android.EffectEditor;
import com.ninjadin.pfmobile.non_android.InventoryEditor;
import com.ninjadin.pfmobile.non_android.ItemEditor;
import com.ninjadin.pfmobile.non_android.OnHitCondition;
import com.ninjadin.pfmobile.non_android.StatisticManager;

public class GeneratorActivity extends FragmentActivity implements ItemEditDialogListener, ModifierDialogListener {
	public StatisticManager dependencyManager;
	public CharacterEditor characterEditor;
	public InventoryEditor inventoryEditor;
	public ItemEditor itemEditor;
	public EffectEditor effectEditor;
	public String masterCharFilename;
	public String inventoryFilename;
	public String effectFilename;
	public String tempFilename;
	public File charFile;
	public File inventoryFile;
	public File effectFile;
	public File tempFile;
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
		tempFilename = masterCharFilename.concat(".temp");
		charFile = new File(context.getFilesDir(), masterCharFilename);
		tempFile = new File(context.getFilesDir(), tempFilename);
		effectFile = new File(context.getFilesDir(), effectFilename);
		inventoryFile = new File(context.getFilesDir(), inventoryFilename);
		dependencyManager = new StatisticManager();
		try {
			characterEditor = new CharacterEditor(charFile, tempFile);
			inventoryEditor = new InventoryEditor(inventoryFile);
			effectEditor = new EffectEditor(effectFile);

			InputStream inStream = (InputStream) getResources().openRawResource(R.raw.dependencies);
			dependencyManager.readXMLBonuses(inStream);
			inStream.close();
			inStream = new FileInputStream(effectFile);
			dependencyManager.readXMLBonuses(inStream);
			inStream.close();
			inStream = new FileInputStream(inventoryFile);
			dependencyManager.readXMLBonuses(inStream);
			inStream.close();
			inStream = new FileInputStream(charFile);
			dependencyManager.readXMLBonuses(inStream);
			inStream.close();
			dependencyManager.updateConditionalBonuses(10);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPause() {
		super .onPause();
		saveCharacterState();
	}
	public void saveCharacterState() {
		try {
			File newTemp = new File(this.getCacheDir(), "temp");
			characterEditor.writeCharacterData(newTemp);
			newTemp.delete();
			effectEditor.saveEffects(effectFile);
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
	
	public void addSelection(int choiceId, String groupName, String subGroup, String specificNames, String selectionName) {
		InputStream dataFile;
		if (groupName.equals("Race")) {
			dataFile = getResources().openRawResource(R.raw.races);
		} else if (groupName.equals("Class Level")) {
			dataFile = getResources().openRawResource(R.raw.classes);
		} else if (groupName.equals("Feat")) {
			dataFile = getResources().openRawResource(R.raw.feats);
		} else if (groupName.equals("Equipment")) {
			dataFile = getResources().openRawResource(R.raw.equipment);
		} else {
			dataFile = getResources().openRawResource(R.raw.other);
		}
		try {
			characterEditor.insertChoice(dataFile, choiceId, groupName, subGroup, specificNames, selectionName);
			dataFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dirtyFiles = true;
		startMenu(selectionName + " selected.");
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
	
	public void addFromTemplate(View view) {
		Bundle passedData = new Bundle();
		passedData.putString("selection type", XmlConst.ITEM_TAG);
		TemplateSelectFragment newFragment = new TemplateSelectFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void enchantFromTemplate(String itemName) {
		Bundle passedData = new Bundle();
		passedData.putString("selection type", XmlConst.ENHANCE_TAG);
		passedData.putString(XmlConst.NAME_ATTR, itemName);
		TemplateSelectFragment newFragment = new TemplateSelectFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void editItem(String itemName) {
		Bundle passedData = new Bundle();
		passedData.putString(XmlConst.NAME_ATTR, itemName);
		ItemEditFragment newFragment = new ItemEditFragment();
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void addTemplate(String templateName, String templateType, String itemName) {
		File tempFile = new File(this.getFilesDir(), "temp_file.xml");
		InputStream templateFileStream;
		try {
			if (templateType.equals(XmlConst.ENHANCE_TAG)) {
				templateFileStream = this.getResources().openRawResource(R.raw.enchantments);
				inventoryEditor.enchantFromTemplate(templateFileStream, templateName, itemName, tempFile);
			} else {
				templateFileStream = this.getResources().openRawResource(R.raw.equipment);
				inventoryEditor.addFromTemplate(templateFileStream, templateName, tempFile);
			}
			templateFileStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dirtyFiles = true;
		startMenu(templateName + " added.");
	}
	
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
	
	public void performAction(String action_name) {
		List<AttackGroup> attacks = dependencyManager.getAttacks(action_name);
		for (AttackGroup attack: attacks) {
			if (attack.getTarget().equals("Self")) {
				List<OnHitCondition> conds = attack.getOnHitConditions();
				for (OnHitCondition cond: conds) {
					for (Map<String,String> add: cond.getAddedConditions())
						effectEditor.activateEffect(add);
					for (Map<String,String> remove: cond.getRemovedConditions())
						effectEditor.deactivateEffect(remove);
				}
			}
		}
		try {
			effectEditor.saveEffects(effectFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dirtyFiles = true;
		startMenu(action_name + " action taken.");
	}
	
	public void activateEffect(String key, String name) {
		Map<String,String> map = new HashMap<String,String>();
		map.put(XmlConst.KEY_ATTR, key);
		map.put(XmlConst.NAME_ATTR, name);
		effectEditor.activateEffect(map);
		dependencyManager.activateCondition(key, name);
		try {
			effectEditor.saveEffects(effectFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dirtyFiles = true;
	}
	
	public void deactivateEffect(String key, String name) {
		Map<String,String> map = new HashMap<String,String>();
		map.put(XmlConst.KEY_ATTR, key);
		map.put(XmlConst.NAME_ATTR, name);
		effectEditor.deactivateEffect(map);
		dependencyManager.deactivateCondition(key, name);
		try {
			effectEditor.saveEffects(effectFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dirtyFiles = true;
	}
	
	public void showItemEditDialog(int groupPosition, int childPosition) {
		DialogFragment dialog = new ItemEditDialogFragment();
		itemEditor.setPosition(groupPosition, childPosition);
		dialog.show(getSupportFragmentManager(), "ItemEditDialogFragment");
	}
	
	public void showModifierDialog() {
		DialogFragment dialog = new ModifierDialogFragment();
		dialog.show(getSupportFragmentManager(), "ModifierDialogFragment");
	}
	
	@Override
	public void onItemEditPositiveClick(ItemEditDialogFragment dialog) {
		itemEditor.setBonusType(dialog.bonus_spinner.getSelectedItem().toString());
		itemEditor.setStackType(dialog.stack_spinner.getSelectedItem().toString());
		itemEditor.setSourceType(dialog.source_spinner.getSelectedItem().toString());
		itemEditor.setValue(dialog.valueEdit.getText().toString());
		try {
			itemEditor.saveToInventory(tempFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ExpandableListView expList = (ExpandableListView) findViewById(R.id.expandableListView1);
		expList.invalidateViews();
		dirtyFiles = true;
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
}
