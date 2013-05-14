package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.ninjadin.pfmobile.fragments.ChoiceSelectFragment;
import com.ninjadin.pfmobile.fragments.EquipmentFragment;
import com.ninjadin.pfmobile.fragments.GeneratorMenuFragment;
import com.ninjadin.pfmobile.fragments.InfoFragment;
import com.ninjadin.pfmobile.fragments.InventoryFragment;
import com.ninjadin.pfmobile.fragments.ItemEditFragment;
import com.ninjadin.pfmobile.fragments.LevelsFragment;
import com.ninjadin.pfmobile.fragments.ShowXMLFragment;
import com.ninjadin.pfmobile.fragments.StatisticsFragment;
import com.ninjadin.pfmobile.fragments.TemplateSelectFragment;
import com.ninjadin.pfmobile.non_android.CharacterEditor;
import com.ninjadin.pfmobile.non_android.InventoryEditor;
import com.ninjadin.pfmobile.non_android.StatisticManager;

public class GeneratorActivity extends FragmentActivity {
	public CharacterEditor charData;
	public StatisticManager dependencyManager;
	public InventoryEditor inventoryManager;
	public ExpListData expListData;
	public String masterCharFilename;
	public String inventoryFilename;
	public String tempFilename;
	public File charFile;
	public File inventoryFile;
	public File tempFile;
	
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
		refreshInventoryData();
	}
	
	public void refreshCharData() {
		Intent intent = getIntent();
		masterCharFilename = intent.getStringExtra(LoginLoadActivity.CHARFILE_MESSAGE);
		inventoryFilename = intent.getStringExtra(LoginLoadActivity.INVFILE_MESSAGE);
		tempFilename = masterCharFilename.concat(".temp");
		charFile = new File(this.getFilesDir(), masterCharFilename);
		tempFile = new File(this.getFilesDir(), tempFilename);
		inventoryFile = new File(this.getFilesDir(), inventoryFilename);
		dependencyManager = new StatisticManager();
		expListData = new ExpListData();
		try {
			charData = new CharacterEditor(charFile, tempFile);
			InputStream inStream = new FileInputStream(charFile);
			dependencyManager.readXMLBonuses(inStream, inventoryFile);
			inStream.close();
			inStream = (InputStream) getResources().openRawResource(R.raw.dependencies);
			dependencyManager.readXMLBonuses(inStream, inventoryFile);
			inStream.close();
			inStream = (InputStream) getResources().openRawResource(R.raw.enchantments);
			expListData.initEnchantTemplates(inStream);
			inStream.close();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshInventoryData() {
		Intent intent = getIntent();
		InputStream templateStream = this.getResources().openRawResource(R.raw.equipment);
		try {
			inventoryManager = new InventoryEditor(inventoryFile, templateStream);
			templateStream.close();
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
	}
	
	@Override
	public void onPause() {
		super .onPause();
		saveCharacterState();
	}
	public void saveCharacterState() {
		try {
			File newTemp = new File(this.getCacheDir(), "temp");
			charData.writeCharacterData(newTemp);
			newTemp.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onDestroy() {
		super .onDestroy();
	}
	
	public void launchCharInfo(View view) {
		InfoFragment newFragment = new InfoFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchStatistics(View view) {
		saveCharacterState();
		refreshCharData();
		StatisticsFragment newFragment = new StatisticsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchLevels(View view) {
		try {
			InputStream character = new FileInputStream(charFile);
			expListData.initLevels(character);
			character.close();
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
		LevelsFragment newFragment = new LevelsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchEquipment(View view) {
		EquipmentFragment newFragment = new EquipmentFragment();
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

	public void levelChange(View view) {
		if (view.getId() == R.id.levelDown) {
			try {
				charData.removeLevel();
				charData.charLevel -= 1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (view.getId() == R.id.levelUp) {
			InputStream charLevelData = getResources().openRawResource(R.raw.base_levels);
			try {
				charData.addLevel(charLevelData);
				charData.charLevel += 1;
				charLevelData.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		((InfoFragment) getSupportFragmentManager().findFragmentById(
				R.id.fragment_container)).updateInfo();
	}
	
	public void launchFilterSelect(View view, String groupName, String subGroup, String choiceId) {
		ChoiceSelectFragment newFragment = new ChoiceSelectFragment();
		Bundle passedData = new Bundle();
		passedData.putString("groupName", groupName);
		passedData.putString("subGroup", subGroup);
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
	
	public void addSelection(int choiceId, String groupName, String subGroup, String selectionName) {
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
			charData.insertChoice(dataFile, choiceId, groupName, subGroup, selectionName);
			dataFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				inventoryManager.enchantFromTemplate(templateFileStream, templateName, itemName, tempFile);
			} else {
				templateFileStream = this.getResources().openRawResource(R.raw.equipment);
				inventoryManager.addFromTemplate(templateFileStream, templateName, tempFile);
			}
			templateFileStream.close();
			refreshInventoryData();
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
		startMenu(templateName + " added.");
	}
	
	public void equipItem(String slotName, String itemName) {
		try {
			if (charData != null)
				charData.equipItem(slotName, itemName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startMenu(itemName + " equipped to " + slotName + ".");
	}
	
}
