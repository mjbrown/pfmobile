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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class AttackFragment extends Fragment {
	ExpandableListView expListView;
	GeneratorActivity activity;
	List<Map<String,String>> filtered_action_list = null;
	List<Map<String,String>> full_action_list;
	List<List<Map<String,String>>> filtered_action_data = null;
	List<List<Map<String,String>>> full_action_data;
	Spinner action_type_spinner;
	String active_filter = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = (GeneratorActivity) getActivity();
		View view = inflater.inflate(R.layout.fragment_attack, container, false);
		expListView = (ExpandableListView) view.findViewById(R.id.action_expListView);
		action_type_spinner = (Spinner) view.findViewById(R.id.cost_spinner);
		List<String> action_types = new ArrayList<String>();
		action_types.add(PropertyLists.all);
		for (String slot: PropertyLists.actionCosts) {
			action_types.add(slot);
		}
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
		if (savedInstanceState != null) {
			active_filter = savedInstanceState.getString("filter");
		}
		if (active_filter != null) {
			int filter_pos = adapter.getPosition(active_filter);
			action_type_spinner.setSelection(filter_pos);
		} else {
			active_filter = PropertyLists.all;
		}
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		full_action_list = activity.dependencyManager.getActionList();
		full_action_data = activity.dependencyManager.getActionData();
		filter_actions(active_filter);
/*
		attackData = full_action_data;
		attackAdapter = new AttackAdapter(activity, 
				R.layout.subrow_attack, 
				R.id.name_text, 
				attackData);
		listView.setAdapter(attackAdapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int groupPosition, long id) {
				String action_name = attackData.get(groupPosition).get(XmlConst.NAME_ATTR);
				activity.performAction(action_name);
			}
		});
*/
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super .onSaveInstanceState(outState);
		if (action_type_spinner != null) {
			String filter = action_type_spinner.getSelectedItem().toString();
			outState.putString("filter", filter);
		}
	}

	private void filter_actions(String cost) {
		if (cost.equals(PropertyLists.all)) {
			filtered_action_list = full_action_list;
			filtered_action_data = full_action_data;
		} else {
			filtered_action_list = new ArrayList<Map<String,String>>();
			filtered_action_data = new ArrayList<List<Map<String,String>>>();
			for (int i = 0; i < full_action_list.size(); i++) {
				if (full_action_list.get(i).get(XmlConst.COST_ATTR).equals(cost)) {
					filtered_action_list.add(full_action_list.get(i));
					filtered_action_data.add(full_action_data.get(i));
				}
			}
		}
		ActionAdapter baseAdapt = new ActionAdapter(
				getActivity(),
				filtered_action_list,
				R.layout.titlerow_action,
				new String[] { XmlConst.NAME_ATTR },
				new int[] {R.id.action_title_text },
				filtered_action_data,
				R.layout.subrow_filterselect,
				new String[] { XmlConst.BONUS_TAG },
				new int[] {R.id.filterselect_text }
				);
		expListView.setAdapter(baseAdapt);
		expListView.invalidateViews();
	}
	
	class ActionAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> groupData;
		List<List<Map<String,String>>> itemData;
		
		public ActionAdapter(Context context, List<Map<String,String>> groupData, int title_layout_id, 
					String[] groupNames, int[] groupIds, List<List<Map<String,String>>> itemData, 
					int subrow_layout_id, String[] itemNames, int[] itemIds) {
			super(context, groupData, title_layout_id, groupNames, groupIds, itemData, subrow_layout_id, itemNames, itemIds);
			mContext = context;
			this.groupData = groupData;
			this.itemData = itemData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.titlerow_action, null);
			}
			TextView title_text = (TextView) convertView.findViewById(R.id.action_title_text);
			if (title_text != null) {
				String text = groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
				title_text.setText(text);
			}
			Button perform_button = (Button) convertView.findViewById(R.id.action_perform_button);
			if (perform_button != null)
				perform_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						int groupPos = expListView.getPositionForView((View) view.getParent());
						String action_name = groupData.get(groupPos).get(XmlConst.NAME_ATTR);
						((GeneratorActivity) getActivity()).performAction(action_name);
					}
				});
			return convertView;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.titlerow_charactersheet, null);
			}
			return convertView;
		}
	}
}
