package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class AttackFragment extends Fragment {
	ListView listView;
	GeneratorActivity activity;
	List<Map<String,String>> attackData;
	AttackAdapter attackAdapter;
	
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
		attackData = activity.dependencyManager.getActionData();
		attackAdapter = new AttackAdapter(activity, 
				R.layout.subrow_attack, 
				R.id.name_text, 
				attackData);
		listView.setAdapter(attackAdapter);
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
