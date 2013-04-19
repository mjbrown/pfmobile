package com.ninjadin.pfmobile.activities;

import java.io.File;
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

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.fragments.InventoryMenuFragment;
import com.ninjadin.pfmobile.fragments.TemplateSelectFragment;
import com.ninjadin.pfmobile.non_android.InventoryManager;

public class InventoryActivity extends FragmentActivity {
	File inventoryFile;
	public InventoryManager inventoryManager;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			InventoryMenuFragment firstFragment = new InventoryMenuFragment();
			firstFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
			
		}
		// Show the Up button in the action bar.
		setupActionBar();
	}

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
		try {
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
	}
	
	public void refreshInventoryData() throws FileNotFoundException, XmlPullParserException, IOException {
		Intent intent = getIntent();
		String inventoryFilename = intent.getStringExtra(LoginLoadActivity.EXTRA_MESSAGE);
		InputStream templateStream = this.getResources().openRawResource(R.raw.equipment);
		inventoryFile = new File(this.getFilesDir(), inventoryFilename);
		inventoryManager = new InventoryManager(inventoryFile, templateStream);
		templateStream.close();
	}
	
	public void addFromTemplate(View view) {
		TemplateSelectFragment newFragment = new TemplateSelectFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void addTemplate(String templateName) {
		File tempFile = new File(this.getFilesDir(), "temp_file.xml");
		InputStream templateFileStream = this.getResources().openRawResource(R.raw.equipment);
		try {
			inventoryManager.addFromTemplate(templateFileStream, templateName, tempFile);
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
		returnToMenu(null);
	}
	
	public void returnToMenu(View view) {
		InventoryMenuFragment newFragment = new InventoryMenuFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void createEmpty(View view) {
		
	}
}
