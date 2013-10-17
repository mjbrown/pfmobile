package com.ninjadin.pfmobile.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;

public class ActionFragment extends Fragment {
	ExpandableListView expListView;
	ExpListData expListData;
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
		Button modifiers = (Button) view.findViewById(R.id.button_modifiers);
		if (modifiers != null)
			modifiers.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					((GeneratorActivity) getActivity()).showModifierDialog();
					expListView.invalidateViews();
				}
			});
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		reloadActionData();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super .onSaveInstanceState(outState);
		if (action_type_spinner != null) {
			String filter = action_type_spinner.getSelectedItem().toString();
			outState.putString("filter", filter);
		}
	}
	public void reloadActionData() {
		expListData = activity.dependencyManager.getActionData();
		full_action_list = expListData.groupData;
		full_action_data = expListData.itemData;
		filter_actions(active_filter);
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
				R.layout.subrow_action,
				new String[] { PropertyLists.to_hit, PropertyLists.damage, PropertyLists.crit_range },
				new int[] {R.id.to_hit_value, R.id.damage_value, R.id.critical_value }
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
			convertView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
			TextView usage = (TextView) convertView.findViewById(R.id.action_uses_remaining);
			String str_uses = groupData.get(groupPosition).get(XmlConst.USES_ATTR);
			String str_used = groupData.get(groupPosition).get(XmlConst.USED_ATTR);
			String remaining = null;
			View uses_viewgroup = convertView.findViewById(R.id.action_uses_group);
			if ((str_uses != null) && (str_used != null)) {
				//Log.d("UsesUsed", str_uses + " " + str_used);
				Integer uses = activity.dependencyManager.getValue(str_uses);
				Integer used = activity.dependencyManager.getValue(str_used);
				remaining = Integer.toString(uses - used) + " of " + Integer.toString(uses);
				uses_viewgroup.setVisibility(View.VISIBLE);
			} else {
				uses_viewgroup.setVisibility(View.GONE);
			}
			usage.setText(remaining);
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
			convertView = super.getChildView(groupPosition, childPosition, isExpanded, convertView, parent);
			return convertView;
		}
	}
}
