package com.ninjadin.pfmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;

public class SpellListFragment extends Fragment {
	GeneratorActivity activity;
	String selected_class;
	Spinner spelllist_spinner;
	ExpandableListView expListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_spellbook, container, false);
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
	}
}
