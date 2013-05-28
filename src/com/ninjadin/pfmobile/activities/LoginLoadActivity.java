package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ninjadin.pfmobile.R;

public class LoginLoadActivity extends Activity {
	public final static String CHARFILE_MESSAGE = "com.ninjadin.pfmobile.CHARFILE";
	public final static String INVFILE_MESSAGE = "com.ninjadin.pfmobile.INVFILE";
	private ArrayAdapter<String> adapter;
	
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
    	File[] files = this.getFilesDir().listFiles();
        Spinner charFilesSpinner = (Spinner) this.findViewById(R.id.chars_spinner);
        List<String> fileList = new ArrayList<String>();
        for (File file: files) {
        	fileList.add(file.getName());
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, fileList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        charFilesSpinner.setAdapter(adapter);
    }
    // Called when the user clicks the new_char button
    public void newChar(View view) {
    	// Get the new file name from the text box
    	Intent intent = new Intent(this, GeneratorActivity.class);
    	EditText editText = (EditText) findViewById(R.id.char_filename);
    	String newFilename = editText.getText().toString();
    	String invFilename = newFilename + "_inv.xml";
    	intent.putExtra(CHARFILE_MESSAGE, newFilename);
    	intent.putExtra(INVFILE_MESSAGE, invFilename);
    	// Create the new file from the blank template
    	File inventoryFile = new File(this.getFilesDir(), invFilename);
    	File newFile = new File(this.getFilesDir(), newFilename);
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
    	Spinner spinner = (Spinner) findViewById(R.id.chars_spinner);
    	String charFilename = spinner.getSelectedItem().toString();
    	String invFilename = charFilename + "_inv.xml";
    	Intent intent = new Intent(this, GeneratorActivity.class);
    	intent.putExtra(INVFILE_MESSAGE, invFilename);
       	intent.putExtra(CHARFILE_MESSAGE, charFilename);
        startActivity(intent);
    }
    
}
