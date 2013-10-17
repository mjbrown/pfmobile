package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.XmlConst;

public class EffectSelectFragment extends ExpListFragment {
	ExpListData expData;
	
	public interface EffectSelectFragmentListener {
		public ExpListData getSelectEffects();
		public void selectEffect(String select, String name);
	}
	
	EffectSelectFragmentListener effListener;
	
	@Override
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			effListener = (EffectSelectFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement EffectSelectFragmentListener");
		}
	}

	@Override
	int getFragmentLayout() {
		return R.layout.fragment_button_exp_list;
	}

	@Override
	OnClickListener buildButtonClickListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	ArrayAdapter<String> buildSpinnerAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	SimpleExpandableListAdapter buildExpListAdapter(String filter) {
		expData = effListener.getSelectEffects();
		SimpleExpandableListAdapter adapter = new EffectSelectAdapter(
				activity,
				expData.groupData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { android.R.id.text1 },
				expData.itemData,
				R.layout.subrow_modifier,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.modifier_textView }
				);
		return adapter;
	}
	
	@Override
	public void onResume() {
		super .onResume();
		exp_list.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View view,
					int groupPosition, int childPosition, long id) {
				String key = expData.groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
				String name = expData.itemData.get(groupPosition).get(childPosition).get(XmlConst.NAME_ATTR);
				effListener.selectEffect(key, name);
				CheckBox box = (CheckBox) view.findViewById(R.id.modifier_checkBox);
				box.setChecked(true); // Toggle
				dataSetUpdate();
				return false;
			} });
	}
	
	private class EffectSelectAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> groupData;
		List<List<Map<String,String>>> itemData;
		
		public EffectSelectAdapter(Context context,
				List<Map<String, String>> groupData,
				int expandedGroupLayout, String[] groupFrom, int[] groupTo,
				List<List<Map<String, String>>> childData,
				int childLayout, String[] childFrom,
				int[] childTo) {
			super(context, groupData, expandedGroupLayout, groupFrom,
					groupTo, childData, childLayout, childFrom, childTo);
			// TODO Auto-generated constructor stub
			mContext = context;
			itemData = childData;
			this.groupData = groupData;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = super .getChildView(groupPosition, childPosition, isExpanded, convertView, parent);
			CheckBox box = (CheckBox) convertView.findViewById(R.id.modifier_checkBox);
			String active = itemData.get(groupPosition).get(childPosition).get(XmlConst.ACTIVATE_ATTR);
			if (active != null) {
				box.setChecked(active.equals("True"));
			} else {
				box.setChecked(false);
			}
			return convertView;
		}
	}
}
