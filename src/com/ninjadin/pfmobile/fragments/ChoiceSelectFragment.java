package com.ninjadin.pfmobile.fragments;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.StatisticManager;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class ChoiceSelectFragment extends Fragment {
	ExpandableListView expList;
	int choiceId;
	String groupName;
	String subGroupName;
	String specificNames;
	ExpListData choices;
	StatisticManager manager;
	List<XmlObjectModel> selection_list = new ArrayList<XmlObjectModel>();
	
	public interface ChoiceSelectFragmentListener {
		public XmlObjectModel getXmlModel(int enum_model);
		public void insertCharacterSelection(XmlObjectModel selection, int choice_number);
	}
	
	ChoiceSelectFragmentListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			mListener = (ChoiceSelectFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() 
					+ " must implement ChoiceSelectFragmentListener");
		}

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basicexpandable, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		try {
			Bundle args = this.getArguments();
			groupName = args.getString(XmlConst.GRPNAME_ATTR);
			subGroupName = args.getString(XmlConst.SUBGRP);
			specificNames = args.getString(XmlConst.SPECIFIC_ATTR);
			choiceId = Integer.parseInt(args.getString("choiceId"));

			InputStream dataFile = activity.getResources().openRawResource(args.getInt("rawDataInt"));
			XmlObjectModel model = new XmlObjectModel(dataFile);
			Map<String,String> attributes = new HashMap<String,String>();
			attributes.put(XmlConst.GRPNAME_ATTR, groupName);
			XmlObjectModel group = model.findObject(XmlConst.BONUSGRP, attributes);
			XmlObjectModel subGroup = group;
			if (subGroupName != null) {
				attributes.remove(XmlConst.GRPNAME_ATTR);
				attributes.put(XmlConst.GRPNAME_ATTR, subGroupName);
				subGroup = group.findObject(XmlConst.SUBGRP, attributes);
			}
			
			manager = new StatisticManager();
			manager.readModel(mListener.getXmlModel(GeneratorActivity.DEPENDENCIES_MODEL));
			Map<String,String> quit_attr = new HashMap<String,String>();
			quit_attr.put(XmlConst.NUM_ATTR, Integer.toString(choiceId));
			manager.readPartialModel(mListener.getXmlModel(GeneratorActivity.CHARACTER_MODEL), XmlConst.CHOICE_TAG, quit_attr);
			
			if (specificNames != null)
				filterSpecifics(specificNames, subGroup);
			else
				filterPrerequisites(manager, subGroup);

			choices = new ExpListData(selection_list);
			expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
			ExpandableListAdapter baseAdapt = new FilterSelectSimpleExpandableListAdapter(
					activity, 
					choices.groupData, 
					R.layout.titlerow_filterselect,
					new String[] { XmlConst.NAME_ATTR }, 
					new int[] { R.id.filtertitle_text },
					choices.itemData, 
					R.layout.subrow_filterselect, 
					new String[] { XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR  }, 
					new int[] { R.id.filterselect_text, R.id.filterselect_text2 }	);
			expList.setAdapter(baseAdapt);
			expList.setOnItemClickListener(new ExpandableListView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int groupPosition, long id) {
				}
			});
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void filterPrerequisites(StatisticManager manager, XmlObjectModel model) {
		for (XmlObjectModel child: model.getChildren()) {
			String tag = child.getTag();
			if (tag.equals(XmlConst.SELECTION_TAG)) {
				selection_list.add(child);
			} else if (tag.equals(XmlConst.PREREQ_TAG)) {
				String key = child.getAttribute(XmlConst.KEY_ATTR);
				String type = child.getAttribute(XmlConst.TYPE_ATTR);
				String value = child.getAttribute(XmlConst.VALUE_ATTR);
				
				if (key != null)
					if (manager.hasProperty(key, child.getAttribute(XmlConst.NAME_ATTR))) {
						filterPrerequisites(manager, child);
						continue;
					}
				if (type != null) {
					int req_value = Integer.parseInt(value);
					int val = manager.getValue(type);
					//Log.d("PrereqTypeValue", Integer.toString(val) + " >= " + Integer.toString(req_value));
					String logic = child.getAttribute(XmlConst.LOGIC_ATTR);
					if (logic != null) {
						if (logic.equals(PropertyLists.equals)) {
							if (val == req_value) {
								filterPrerequisites(manager,child);
								continue;
							}
						}
					} else {
						if (val >= req_value) {
							filterPrerequisites(manager, child);
							continue;
						}
					}
				}
			} else {
				filterPrerequisites(manager, child);
			}
		}
	}
	
	private void filterSpecifics(String specifics, XmlObjectModel model) {
		for (XmlObjectModel child: model.getChildren()) {
			String tag = child.getTag();
			if (tag.equals(XmlConst.SELECTION_TAG)) {
				String name = child.getAttribute(XmlConst.NAME_ATTR);
				for (String selection_name: specifics.split(",")) {
					if (name.equals(selection_name))
						selection_list.add(child);
				}
			} else {
				filterSpecifics(specifics, child);
			}
		}
	}
	
	class FilterSelectSimpleExpandableListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> grpData;
		List<List<Map<String,String>>> itmData;
		
		public FilterSelectSimpleExpandableListAdapter(Context context, List<Map<String, String>> groupData,
				int groupLayout, String[] groupFrom, int[] groupTo, List<List<Map<String,String>>> childData, int childLayout,
				String[] childFrom, int[] childTo) {
			super (context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
			mContext = context;
			grpData = groupData;
			itmData = childData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.titlerow_filterselect, null);
//			}
			Button addButton = (Button)convertView.findViewById(R.id.filtertitle_add);
			if (addButton != null)
			addButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					int groupPos = expList.getPositionForView((View) view.getParent());
					mListener.insertCharacterSelection(selection_list.get(groupPos), choiceId);
				}
			});
			TextView textView = (TextView)convertView.findViewById(R.id.filtertitle_text);
			if (textView != null)
				textView.setText(grpData.get(groupPosition).get(XmlConst.NAME_ATTR));
			return convertView;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			convertView = View.inflate(getActivity(), R.layout.subrow_filterselect, null);
			TextView text = (TextView) convertView.findViewById(R.id.filterselect_text);
			TextView text2 = (TextView) convertView.findViewById(R.id.filterselect_text2);
			String tag = itmData.get(groupPosition).get(childPosition).get("tag");
			String left_text = null, right_text = null;
			if (tag.equals(XmlConst.CHOICE_TAG)) {
				left_text = itmData.get(groupPosition).get(childPosition).get(XmlConst.GRPNAME_ATTR);
				String subgroup = itmData.get(groupPosition).get(childPosition).get(XmlConst.SUBGRP);
				if (subgroup != null)
					right_text = subgroup;
				else
					right_text = "Any";
			} else if (tag.equals(XmlConst.BONUS_TAG)) {
				left_text = itmData.get(groupPosition).get(childPosition).get(XmlConst.TYPE_ATTR);
				right_text = Integer.toString(manager.evaluateValue(itmData.get(groupPosition).get(childPosition).get(XmlConst.VALUE_ATTR)));
			}
			if ((text != null) && (text2 != null) && (right_text != null) && (left_text != null)) {
				text.setText(left_text);
				text2.setText(right_text);
			}
			return convertView;
		}
	}
}
