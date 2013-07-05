package com.ninjadin.pfmobile.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class AttackFragment extends Fragment {
	ListView listView;
	GeneratorActivity activity;
	List<Map<String,String>> attackData;
	List<Map<String,String>> full_action_data;
	AttackAdapter attackAdapter;
	Spinner action_type_spinner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_attack, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		activity = (GeneratorActivity) getActivity();
		listView = (ListView) activity.findViewById(R.id.listView1);
		full_action_data = activity.dependencyManager.getActionData();
		attackData = full_action_data;
		attackAdapter = new AttackAdapter(activity, 
				R.layout.subrow_attack, 
				R.id.name_text, 
				attackData);
		listView.setAdapter(attackAdapter);
		action_type_spinner = (Spinner) activity.findViewById(R.id.cost_spinner);
		List<String> action_types = new ArrayList<String>();
		action_types.add(PropertyLists.all);
		for (String slot: PropertyLists.actionCosts)
			action_types.add(slot);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, action_types);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		action_type_spinner.setAdapter(adapter);
		action_type_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				filter_actions(action_type_spinner.getSelectedItem().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void filter_actions(String cost) {
		if (cost.equals(PropertyLists.all)) {
			attackData = full_action_data;
		} else {
			attackData = new ArrayList<Map<String,String>>();
			for (Map<String,String> action: full_action_data) {
				if (action.get(XmlConst.TYPE_ATTR).equals(cost)) {
					attackData.add(action);
				}
			}
		}
		attackAdapter = new AttackAdapter(	activity, 
											R.layout.subrow_attack, 
											R.id.name_text, 
											attackData);
		listView.setAdapter(attackAdapter);
		listView.invalidateViews();
	}
	
	class AttackAdapter extends ArrayAdapter<Map<String,String>> {
		Context mContext;
		int resource;
		List<Map<String,String>> groupData;
		
		public AttackAdapter(Context context, int rowLayoutResId, int textViewResourceId, List<Map<String,String>> objects) {
			super (context, textViewResourceId, objects);
			mContext = context;
			resource = rowLayoutResId;
			groupData = objects;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(resource, null);
			}
			TextView name = (TextView) convertView.findViewById(R.id.name_text);
			TextView tohit = (TextView) convertView.findViewById(R.id.tohit_text);
			TextView damage = (TextView) convertView.findViewById(R.id.damage_text);
			TextView critical = (TextView) convertView.findViewById(R.id.critical_text);
			TextView damage_type = (TextView) convertView.findViewById(R.id.damagetype_text);
			Map<String,String> entry = groupData.get(position);
			if (name != null) {
				name.setText(entry.get(XmlConst.NAME_ATTR));
			}
			if (tohit != null) {
				tohit.setText(entry.get(PropertyLists.to_hit));
			}
			if (damage != null) {
				damage.setText(entry.get(PropertyLists.damage));
			}
			if (critical != null) {
				critical.setText(entry.get(PropertyLists.crit_range) + "/" + entry.get(PropertyLists.crit_multiplier));
			}
			if (damage_type != null) {
				damage_type.setText(entry.get(XmlConst.SOURCE_ATTR));
			}
			return convertView;
		}
	}
}
