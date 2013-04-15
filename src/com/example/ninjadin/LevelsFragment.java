package com.example.ninjadin;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class LevelsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.level_fragment, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		CharGenActivity activity = (CharGenActivity) getActivity();
		List<Map<String, String>> groupData = activity.charData.levelsNames;
		List<List<Map<String, String>>> listData = activity.charData.levelsLists;
		ExpandableListView expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
		ExpandableListAdapter baseAdapt = new SimpleExpandableListAdapter(
				activity, 
				groupData, 
				android.R.layout.simple_expandable_list_item_1,
				new String[] { "NAME" }, 
				new int[] { android.R.id.text1 }, 
				listData, 
				android.R.layout.simple_expandable_list_item_2, 
				new String[] { "SEL", "DESCRIPTION"  }, 
				new int[] { android.R.id.text1, android.R.id.text2 }	);
		expList.setAdapter(baseAdapt);
		expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
				CharGenActivity activity = ((CharGenActivity) getActivity());
				String groupName = activity.charData.levelsLists.get(groupPosition).get(childPosition).get("GROUP");
				String choiceId = activity.charData.levelsLists.get(groupPosition).get(childPosition).get("ID");
				String subGroup = activity.charData.levelsLists.get(groupPosition).get(childPosition).get("SUBGROUP");
				activity.launchFilterSelect(view, groupName, subGroup, choiceId);
				return false;
			}
		});
	}
	
}
