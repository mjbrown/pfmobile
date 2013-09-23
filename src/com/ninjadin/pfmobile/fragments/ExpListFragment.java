package com.ninjadin.pfmobile.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;

import com.ninjadin.pfmobile.R;

public abstract class ExpListFragment extends Fragment {
	Activity activity;
	
	Button button;

	Spinner filter_spinner;
	ArrayAdapter<String> spinner_adapter;

	ExpandableListView exp_list;
	SimpleExpandableListAdapter exp_list_adapter;
	
//	public interface ExpListFragmentListener {
//		public void reloadXmlObjects();
//	}
	
	abstract int getFragmentLayout();
	
	abstract OnClickListener buildButtonClickListener();
	
	abstract ArrayAdapter<String> buildSpinnerAdapter();
	
	abstract SimpleExpandableListAdapter buildExpListAdapter(String filter);

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(getFragmentLayout(), container, false);
		
		activity = getActivity();
		
		//Initialise button
		button = (Button) view.findViewById(R.id.button_add);
		if (button != null)
			button.setOnClickListener(buildButtonClickListener());
		
		//Initialise spinner
		filter_spinner = (Spinner) view.findViewById(R.id.spinner_filter);
		spinner_adapter = buildSpinnerAdapter();
		if (spinner_adapter != null) {
			filter_spinner.setAdapter(spinner_adapter);
			if (savedInstanceState != null) {
				Integer position = savedInstanceState.getInt("Spinner Position");
				if (position != null)
					filter_spinner.setSelection(position);
			}
			filter_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					dataSetUpdate();
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
		exp_list = (ExpandableListView) view.findViewById(R.id.expList);
		dataSetUpdate();
		//Initialise Expandable list view
		return view;
	}
	
	public void onSaveInstanceState(Bundle outState) {
		super .onSaveInstanceState(outState);
		if (spinner_adapter != null) {
			int position = filter_spinner.getSelectedItemPosition();
			outState.putInt("Spinner Position", position);
		}
	}
	
	public void dataSetUpdate() {
		if (spinner_adapter != null) {
			String filter = filter_spinner.getSelectedItem().toString();
			exp_list_adapter = buildExpListAdapter(filter);
		} else {
			exp_list_adapter = buildExpListAdapter(null);
		}
		exp_list.setAdapter(exp_list_adapter);
		exp_list.invalidateViews();
	}
}
