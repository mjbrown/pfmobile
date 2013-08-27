package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

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
	public final static String SPELLSFILE_MESSAGE = "com.ninjadin.pfmobile.SPELLSFILE";

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
    	String spellsFilename = newFilename + "_spells.xml";
    	intent.putExtra(CHARFILE_MESSAGE, newFilename);
    	intent.putExtra(INVFILE_MESSAGE, invFilename);
    	intent.putExtra(EFFECTFILE_MESSAGE, effectFilename);
    	intent.putExtra(SPELLSFILE_MESSAGE, spellsFilename);
    	// Create the new file from the blank template
    	File charFile = new File(this.getFilesDir(), newFilename);
    	File inventoryFile = new File(this.getFilesDir(), invFilename);
    	File effectFile = new File(this.getFilesDir(), effectFilename);
    	File spellsFile = new File(this.getFilesDir(), spellsFilename);
    	try {
    		copyFile(this.getResources().openRawResource(R.raw.template_charfile), charFile);
    		copyFile(this.getResources().openRawResource(R.raw.template_inventory), inventoryFile);
    		copyFile(this.getResources().openRawResource(R.raw.template_effects), effectFile);
    		copyFile(this.getResources().openRawResource(R.raw.template_prepared), spellsFile);
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
    	String spellsFilename = charFilename + "_spells.xml";
    	Intent intent = new Intent(this, GeneratorActivity.class);
    	intent.putExtra(INVFILE_MESSAGE, invFilename);
       	intent.putExtra(CHARFILE_MESSAGE, charFilename);
       	intent.putExtra(EFFECTFILE_MESSAGE, effectFilename);
       	intent.putExtra(SPELLSFILE_MESSAGE, spellsFilename);
        startActivity(intent);
    }
    
    public static void copyFile(InputStream source, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileOutputStream destination = null;

        try {
            destination = new FileOutputStream(destFile);
            Integer data = source.read();
            while (data != -1) {
            	destination.write(data);
            	data = source.read();
            }
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}
