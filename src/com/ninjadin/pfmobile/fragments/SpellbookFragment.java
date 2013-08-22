package com.ninjadin.pfmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;

public class SpellbookFragment extends Fragment {
	Spinner class_spinner;
	GeneratorActivity activity;
	String selected_class;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_spellbook, container, false);
		// Initialize selected in spinner
		if (savedInstanceState != null)
			selected_class = savedInstanceState.getString("class_spinner");
		activity = (GeneratorActivity) getActivity();
		class_spinner = (Spinner) view.findViewById(R.id.class_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
		int i = 0, position = 0;
		for (String caster_class: PropertyLists.spellbook_classes) {
			adapter.add(caster_class);
			if (selected_class != null)
				if (selected_class.equals(caster_class))
					position = i;
			i += 1;
		}
		for (String caster_class: PropertyLists.spelllist_classes) {
			adapter.add(caster_class);
			if (selected_class != null)
				if (selected_class.equals(caster_class))
					position = i;
			i += 1;
		}
		class_spinner.setAdapter(adapter);
		class_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//filter_actions(class_spinner.getSelectedItem().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		class_spinner.setSelection(position);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super .onSaveInstanceState(outState);
		if (class_spinner != null) {
			String class_name = class_spinner.getSelectedItem().toString();
			outState.putString("class_spinner", class_name);
		}
	}

}
