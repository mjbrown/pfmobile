package com.ninjadin.pfmobile.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.SpellbookEditor;
import com.ninjadin.pfmobile.non_android.XmlExtractor;

public class SpellListFragment extends Fragment {
	private final static String ERROR_MSG = "No Caster Levels found!";
	GeneratorActivity activity;
	String selected_class;
	Spinner spelllist_spinner;
	ExpandableListView expListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_spelllist, container, false);
		// Initialize selected in spinner
		if (savedInstanceState != null)
			selected_class = savedInstanceState.getString("class_spinner");
		activity = (GeneratorActivity) getActivity();
		spelllist_spinner = (Spinner) view.findViewById(R.id.spelllist_spinner);
		expListView = (ExpandableListView) view.findViewById(R.id.spelllist_expListView);
		return view;
	}

	public void onResume() {
		super .onResume();
		expListView.setOnChildClickListener(new OnChildClickListener () {
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				// TODO Auto-generated method stub
				TextView name_textview = (TextView) arg1.findViewById(R.id.filterselect_text);
				String spell_name = name_textview.getText().toString();
				String class_name = spelllist_spinner.getSelectedItem().toString();
				String spell_level = Integer.toString(arg2);
				activity.addSpell(class_name, spell_name, spell_level);
				activity.startMenu(spell_name + " added to spells known.");
				return false;
			}
			
		});
		ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
		List<String> casting_classes = activity.dependencyManager.castingClasses();
		if (casting_classes.size() == 0)
			spin_adapter.add(ERROR_MSG);
		for (String class_name: casting_classes) {
			spin_adapter.add(class_name);
		}
		spelllist_spinner.setAdapter(spin_adapter);
		spelllist_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				String class_name = spelllist_spinner.getSelectedItem().toString();
				if (!class_name.equals(ERROR_MSG))
					changeExpContent(class_name);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	private void changeExpContent(String class_name) {
		InputStream list_file = activity.getResources().openRawResource(R.raw.spell_lists);
		XmlExtractor spellbook;
		try {
			spellbook = SpellbookEditor.getSpellList(class_name, list_file);
			SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
					activity,
					spellbook.groupData,
					android.R.layout.simple_expandable_list_item_1,
					new String[] { XmlConst.VALUE_ATTR },
					new int[] { android.R.id.text1 },
					spellbook.itemData,
					R.layout.subrow_filterselect,
					new String[] { XmlConst.NAME_ATTR },
					new int[] { R.id.filterselect_text }
					);
			expListView.setAdapter(adapter);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
