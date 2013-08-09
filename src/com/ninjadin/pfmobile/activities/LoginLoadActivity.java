package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.ninjadin.pfmobile.R;

public class LoginLoadActivity extends Activity {
	public final static String CHARFILE_MESSAGE = "com.ninjadin.pfmobile.CHARFILE";
	public final static String INVFILE_MESSAGE = "com.ninjadin.pfmobile.INVFILE";
	public final static String EFFECTFILE_MESSAGE = "com.ninjadin.pfmobile.EFFECTFILE";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_char);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open_char, menu);
        return true;
    }
    
    public void onResume() {
    	super .onResume();
// Populate the Spinner with directory contents
    }
    // Called when the user clicks the new_char button
    public void newChar(View view) {
    	// Get the new file name from the text box
    	Intent intent = new Intent(this, GeneratorActivity.class);
    	String newFilename = "Char0";
    	String invFilename = newFilename + "_inv.xml";
    	String effectFilename = newFilename + "_effects.xml";
    	intent.putExtra(CHARFILE_MESSAGE, newFilename);
    	intent.putExtra(INVFILE_MESSAGE, invFilename);
    	intent.putExtra(EFFECTFILE_MESSAGE, effectFilename);
    	// Create the new file from the blank template
    	File newFile = new File(this.getFilesDir(), newFilename);
    	File inventoryFile = new File(this.getFilesDir(), invFilename);
    	File effectFile = new File(this.getFilesDir(), effectFilename);
    	try {
			FileOutputStream out = new FileOutputStream(newFile);
			InputStream in = getResources().openRawResource(R.raw.template);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			FileOutputStream oStream = new FileOutputStream(inventoryFile);
			oStream.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			oStream.write("<inventory>\n<template name=\"Unarmored\">\n<item name=\"Unarmored\" slot=\"Armor\" >\n<enhancement name=\"Base Item\">\n<bonus type=\"Equipment Cost\" value=\"0\" />\n<bonus type=\"Encumbrance\" value=\"0\" />\"\n<condition name=\"Unarmored\" />\n</enhancement>\n</item>\n</template>\n</inventory>\n".getBytes());
    		oStream.close();
    		oStream = new FileOutputStream(effectFile);
			oStream.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			oStream.write("<effects>\n</effects>\n".getBytes());
			oStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	// Start the Character Generation Activity
    	startActivity(intent);
    }
    
    // Called when the user clicks the open_char button
    public void openChar(View view) {
    	String charFilename = "Char0";
    	String invFilename = charFilename + "_inv.xml";
    	String effectFilename = charFilename + "_effects.xml";
    	Intent intent = new Intent(this, GeneratorActivity.class);
    	intent.putExtra(INVFILE_MESSAGE, invFilename);
       	intent.putExtra(CHARFILE_MESSAGE, charFilename);
       	intent.putExtra(EFFECTFILE_MESSAGE, effectFilename);
        startActivity(intent);
    }
    
}
