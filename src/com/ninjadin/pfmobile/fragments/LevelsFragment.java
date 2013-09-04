package com.ninjadin.pfmobile.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class LevelsFragment extends Fragment {
	ListView listView;
	LevelsAdapter levelsAdapter;
	List<XmlObjectModel> groupData;
	GeneratorActivity activity;
	
	public interface LevelsFragmentListener {
		public void characterLevelUp(LevelsFragment fragment);
		public void characterLevelDown(LevelsFragment fragment);
		public int currentCharacterLevel();
		public List<XmlObjectModel> characterChoiceList();
	}
	
	LevelsFragmentListener mListener;
	
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			mListener = (LevelsFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement LevelsFragmentListener");
		}
	}
	
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
			level_down.setEnabled(true);
			level_down.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					mListener.characterLevelDown(null);
					refreshViews();
					listView.invalidateViews();
				}
			});
		}
		Button level_up = (Button) activity.findViewById(R.id.levelUp);
		if (level_up != null) {
			level_up.setEnabled(true);
			level_up.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					mListener.characterLevelUp(null);
					refreshViews();
					listView.invalidateViews();
				}
			});
		}
	}
	
	private void refreshViews() {
		groupData = mListener.characterChoiceList();
		levelsAdapter = new LevelsAdapter(activity, R.layout.subrow_levels, R.id.group_text, groupData);
		listView.setAdapter(levelsAdapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int groupPosition, long id) {
				GeneratorActivity activity = ((GeneratorActivity) getActivity());
				String groupName = groupData.get(groupPosition).getAttribute(XmlConst.GRPNAME_ATTR);
				String choiceId = Integer.toString(groupPosition);
				String subGroup = groupData.get(groupPosition).getAttribute(XmlConst.SUBGRP);
				String specificNames = groupData.get(groupPosition).getAttribute(XmlConst.SPECIFIC_ATTR);
				activity.launchFilterSelect(view, groupName, subGroup, specificNames, choiceId);
			}
		});
		TextView level = (TextView) activity.findViewById(R.id.level_indicator);
		if (level != null) {
			level.setText("Level " + Integer.toString(mListener.currentCharacterLevel()));
		}
	}
	
	private List<Map<String,String>> getListMap(List<XmlObjectModel> objects) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		for (XmlObjectModel object: objects) {
			Map<String,String> map = new HashMap<String,String>();
			list.add(map);
			map.put(XmlConst.NAME_ATTR, object.getAttribute(XmlConst.NAME_ATTR));
		}
		return list;
	}
	
	class LevelsAdapter extends ArrayAdapter<Map<String,String>> {
		Context mContext;
		int resource;
		
		public LevelsAdapter(Context context, int rowLayoutResId, int textViewResourceId,
				List<XmlObjectModel> objects) {
			super(context, textViewResourceId, getListMap(objects));
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
				XmlObjectModel item = groupData.get(position);
				String groupName = item.getAttribute(XmlConst.GRPNAME_ATTR);
				String subGroupName = item.getAttribute(XmlConst.SUBGRP);
				String sourceName = item.getAttribute(XmlConst.SOURCE_ATTR);
				String selectionName = item.getAttribute(XmlConst.NAME_ATTR);
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
