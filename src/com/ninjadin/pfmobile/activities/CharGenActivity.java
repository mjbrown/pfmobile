package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import com.example.ninjadin.R;
import com.ninjadin.pfmobile.fragments.CharGenMenuFragment;
import com.ninjadin.pfmobile.fragments.ChoiceSelectFragment;
import com.ninjadin.pfmobile.fragments.ChoicesFragment;
import com.ninjadin.pfmobile.fragments.InfoFragment;
import com.ninjadin.pfmobile.fragments.PointBuyFragment;
import com.ninjadin.pfmobile.fragments.ShowCharacterXMLFragment;
import com.ninjadin.pfmobile.fragments.SkillsFragment;
import com.ninjadin.pfmobile.fragments.StatisticsFragment;
import com.ninjadin.pfmobile.non_android.CharacterData;
import com.ninjadin.pfmobile.non_android.DependencyManager;

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

public class CharGenActivity extends FragmentActivity {
	public CharacterData charData;
	public DependencyManager dependencyManager;
	public String masterCharFilename;
	public String tempFilename;
	public File charFile;
	public File tempFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_char_fragments_list);
		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			CharGenMenuFragment firstFragment = new CharGenMenuFragment();
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
	}
	
	public void refreshCharData() {
		Intent intent = getIntent();
		masterCharFilename = intent.getStringExtra(OpenCharActivity.EXTRA_MESSAGE);
		tempFilename = masterCharFilename.concat(".temp");
		charFile = new File(this.getFilesDir(), masterCharFilename);
		tempFile = new File(this.getFilesDir(), tempFilename);
		dependencyManager = new DependencyManager();
		try {
			charData = new CharacterData(charFile, tempFile);
			InputStream inStream = new FileInputStream(charFile);
			dependencyManager.readXMLBonuses(inStream);
			inStream.close();
			inStream = (InputStream) getResources().openRawResource(R.raw.dependencies);
			dependencyManager.readXMLBonuses(inStream);
			inStream.close();
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
	
	public void launchStats(View view) {
		PointBuyFragment newFragment = new PointBuyFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchLevels(View view) {
		refreshCharData();
		ChoicesFragment newFragment = new ChoicesFragment();
		Bundle passedData = new Bundle();
		passedData.putString("choiceType", "Levels");
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void launchEquipment(View view) {
		refreshCharData();
		ChoicesFragment newFragment = new ChoicesFragment();
		Bundle passedData = new Bundle();
		passedData.putString("choiceType", "Equipment");
		newFragment.setArguments(passedData);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void launchSkills(View view) {
		SkillsFragment newFragment = new SkillsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void levelChange(View view) {
//		((CharacterInfoFragment) getSupportFragmentManager().findFragmentById(
//				R.id.fragment_container)).saveInfo();
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
		CharGenMenuFragment firstFragment = new CharGenMenuFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);
		transaction.commit();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	public void launchCharXML(View view) {
		ShowCharacterXMLFragment newFragment = new ShowCharacterXMLFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
		
	}

	public void statChange(View view) {
		((PointBuyFragment) getSupportFragmentManager().findFragmentById(
				R.id.fragment_container)).statChange(view);
	}
}