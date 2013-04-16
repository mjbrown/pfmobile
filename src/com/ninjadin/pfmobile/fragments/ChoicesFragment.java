package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import com.example.ninjadin.R;
import com.ninjadin.pfmobile.activities.CharGenActivity;
import com.ninjadin.pfmobile.non_android.GlobalConstants;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class ChoicesFragment extends Fragment {
	List<Map<String, String>> groupData;
	List<List<Map<String, String>>> listData;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basicexpandable, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		CharGenActivity activity = (CharGenActivity) getActivity();
		Bundle args = this.getArguments();
		String choiceType = args.getString("choiceType");
		if (choiceType.equals("Levels")) {
			groupData = activity.charData.levels.groupData;
			listData = activity.charData.levels.itemData;
		} else if (choiceType.equals("Equipment")) {
			groupData = activity.charData.equipment.groupData;
			listData = activity.charData.equipment.itemData;
		}
		ExpandableListView expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
		ExpandableListAdapter baseAdapt = new SimpleExpandableListAdapter(
				activity, 
				groupData, 
				android.R.layout.simple_expandable_list_item_1,
				new String[] { GlobalConstants.NUM_ATTR }, 
				new int[] { android.R.id.text1 }, 
				listData, 
				android.R.layout.simple_expandable_list_item_2, 
				new String[] { GlobalConstants.GRPNAME_ATTR, GlobalConstants.SUBGRP_ATTR  }, 
				new int[] { android.R.id.text1, android.R.id.text2 }	);
		expList.setAdapter(baseAdapt);
		expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
				CharGenActivity activity = ((CharGenActivity) getActivity());
				String groupName = listData.get(groupPosition).get(childPosition).get(GlobalConstants.GRPNAME_ATTR);
				String choiceId = listData.get(groupPosition).get(childPosition).get(GlobalConstants.NUM_ATTR);
				String subGroup = listData.get(groupPosition).get(childPosition).get(GlobalConstants.SUBGRP_ATTR);
				activity.launchFilterSelect(view, groupName, subGroup, choiceId);
				return false;
			}
		});
	}
	
}
