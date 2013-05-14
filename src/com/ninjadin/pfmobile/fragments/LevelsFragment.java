package com.ninjadin.pfmobile.fragments;

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
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.XmlConst;

public class LevelsFragment extends Fragment {
	ListView listView;
	LevelsAdapter levelsAdapter;
	List<Map<String, String>> groupData;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_levels, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		groupData = activity.charData.levelData;
		listView = (ListView) activity.findViewById(R.id.listView1);
		levelsAdapter = new LevelsAdapter(activity, R.layout.subrow_levels, R.id.group_text, groupData);
		listView.setAdapter(levelsAdapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int groupPosition, long id) {
				GeneratorActivity activity = ((GeneratorActivity) getActivity());
				String groupName = groupData.get(groupPosition).get(XmlConst.GRPNAME_ATTR);
				String choiceId = groupData.get(groupPosition).get(XmlConst.NUM_ATTR);
				String subGroup = groupData.get(groupPosition).get(XmlConst.SUBGRP);
				activity.launchFilterSelect(view, groupName, subGroup, choiceId);
			}
		});
	}
	class LevelsAdapter extends ArrayAdapter<Map<String,String>> {
		Context mContext;
		int resource;
		
		public LevelsAdapter(Context context, int rowLayoutResId, int textViewResourceId,
				List<Map<String, String>> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			mContext = context;
			resource = rowLayoutResId;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(resource, null);
			}
			TextView group = (TextView) convertView.findViewById(R.id.group_text);
			TextView subGroup = (TextView) convertView.findViewById(R.id.subgroup_text);
			TextView source = (TextView) convertView.findViewById(R.id.source_text);
			TextView name = (TextView) convertView.findViewById(R.id.name_text);
			if ((group != null) && (subGroup != null) && (source != null) && (name != null)) {
				Map<String,String> item = groupData.get(position);
				String groupName = item.get(XmlConst.GRPNAME_ATTR);
				String subGroupName = item.get(XmlConst.SUBGRP);
				String sourceName = item.get(XmlConst.SOURCE_ATTR);
				String selectionName = item.get(XmlConst.NAME_ATTR);
				if (groupName != null) {
					group.setText(groupName);
				} else {
					group.setText("WTF!");
				}
				if (subGroupName != null) {
					subGroup.setText(subGroupName);
				} else {
					subGroup.setText("Any");
				}
				if (sourceName != null) {
					source.setText(sourceName);
				} else {
					source.setText("WTF!");
				}
				if (selectionName != null) {
					name.setText(selectionName);
				} else {
					name.setText("Not selected.");
				}
			}
			return convertView;
		}
		public int getCount() {
			return groupData.size();
		}
	}
}
