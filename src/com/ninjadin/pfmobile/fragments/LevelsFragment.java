package com.ninjadin.pfmobile.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.XmlConst;

public class LevelsFragment extends Fragment {
	ListView listView;
	LevelsAdapter levelsAdapter;
	List<Map<String, String>> groupData;
	GeneratorActivity activity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_levels, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		activity = (GeneratorActivity) getActivity();
		listView = (ListView) activity.findViewById(R.id.listView1);
		refreshViews();
		Button level_down = (Button) activity.findViewById(R.id.levelDown);
		if (level_down != null) {
			if (activity.charData.charLevel == 0)
				level_down.setEnabled(false);
			else
				level_down.setEnabled(true);
			level_down.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					activity.charData.removeLevel();
					activity.refreshCharData();
					refreshViews();
					levelsAdapter.notifyDataSetChanged();
					listView.invalidateViews();
				}
			});
		}
		Button level_up = (Button) activity.findViewById(R.id.levelUp);
		if (level_up != null) {
			if (activity.charData.charLevel == 20)
				level_up.setEnabled(false);
			else
				level_up.setEnabled(true);
			level_up.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					InputStream charLevelData = activity.getResources().openRawResource(R.raw.base_levels);
					activity.charData.addLevel(charLevelData);
					try {
						charLevelData.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					activity.refreshCharData();
					refreshViews();
					levelsAdapter.notifyDataSetChanged();
					listView.invalidateViews();
				}
			});
		}
	}
	
	private void refreshViews() {
		groupData = reverse(activity.charData.levelData);
		levelsAdapter = new LevelsAdapter(activity, R.layout.subrow_levels, R.id.group_text, groupData);
		listView.setAdapter(levelsAdapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int groupPosition, long id) {
				GeneratorActivity activity = ((GeneratorActivity) getActivity());
				String groupName = groupData.get(groupPosition).get(XmlConst.GRPNAME_ATTR);
				String choiceId = groupData.get(groupPosition).get(XmlConst.NUM_ATTR);
				String subGroup = groupData.get(groupPosition).get(XmlConst.SUBGRP);
				String specificNames = groupData.get(groupPosition).get(XmlConst.SPECIFIC_ATTR);
				activity.launchFilterSelect(view, groupName, subGroup, specificNames, choiceId);
			}
		});
		TextView level = (TextView) activity.findViewById(R.id.level_indicator);
		if (level != null) {
			level.setText("Level " + Integer.toString(activity.charData.charLevel));
		}
	}
	
	private List<Map<String,String>> reverse(List<Map<String,String>> list) {
		List<Map<String,String>> reverseList = new ArrayList<Map<String,String>>();
		int size = list.size();
		for (int i = size-1; i > -1; i--) {
			Map<String,String> xfer = list.get(i);
			reverseList.add(xfer);
		}
		return reverseList;
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
					group.setTextColor(Color.BLUE);
				} else {
					group.setText("WTF!");
				}
				if (subGroupName != null) {
					subGroup.setText(subGroupName);
				} else {
					subGroup.setText("Any");
					subGroup.setTextColor(Color.GRAY);
				}
				if (sourceName != null) {
					source.setText(sourceName);
					source.setTextColor(Color.GRAY);
				} else {
					source.setText("WTF!");
				}
				if (selectionName != null) {
					name.setText(selectionName);
					name.setTextColor(Color.rgb(0, 0xAA, 0));
				} else {
					name.setText("Not selected.");
					name.setTextColor(Color.RED);
				}
			}
			return convertView;
		}
		public int getCount() {
			return groupData.size();
		}
	}
}
