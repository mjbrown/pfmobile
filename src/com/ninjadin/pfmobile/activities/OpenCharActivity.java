package com.ninjadin.pfmobile.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.ninjadin.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class OpenCharActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.ninjadin.MESSAGE";

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
    	
    	Intent intent = new Intent(this, CharGenActivity.class);
    	EditText editText = (EditText) findViewById(R.id.char_filename);
    	// Get the new file name from the text box
    	String newFilename = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, newFilename);
    	// Create the new file from the blank template
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
    	Intent intent = new Intent(this, CharGenActivity.class);
    	Spinner spinner = (Spinner) findViewById(R.id.chars_spinner);
    	String openFilename = spinner.getSelectedItem().toString();
       	intent.putExtra(EXTRA_MESSAGE, openFilename);
        startActivity(intent);
    }
}