package com.ninjadin.pfmobile.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.StatisticManager;
import com.ninjadin.pfmobile.non_android.XmlExtractor;

public class ChoiceSelectFragment extends Fragment {
	ExpandableListView expList;
	int choiceId;
	String groupName;
	String subGroupName;
	XmlExtractor choices;
	StatisticManager manager;
	
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
			choiceId = Integer.parseInt(args.getString("choiceId"));
			InputStream dataFile = activity.getResources().openRawResource(args.getInt("rawDataInt"));
			String[] tags = new String[] { XmlConst.SELECTION_TAG };
			String[] tag_attrs = new String[] {XmlConst.NAME_ATTR };
			String[] subtags = new String[] {XmlConst.BONUS_TAG, XmlConst.PROFICIENCY_TAG, 
					XmlConst.CHOICE_TAG};
			String[] subtag_attrs = new String[] { XmlConst.GRPNAME_ATTR, XmlConst.NAME_ATTR,
					XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR };
			manager = activity.dependencyManager;
			choices = new XmlExtractor(dataFile, manager);
			choices.findTagAttr(XmlConst.BONUSGRP, XmlConst.GRPNAME_ATTR, groupName);
			if (subGroupName != null) {
				if (!subGroupName.equals("Any")) {
					choices.findTagAttr(XmlConst.SUBGRP, XmlConst.GRPNAME_ATTR, subGroupName);
					choices.getPrereqMetData(XmlConst.SUBGRP, tags, tag_attrs, subtags, subtag_attrs);
				} else {
					choices.getPrereqMetData(XmlConst.BONUSGRP, tags, tag_attrs, subtags, subtag_attrs);
				}
			} else {
				choices.getPrereqMetData(XmlConst.BONUSGRP, tags, tag_attrs, subtags, subtag_attrs);
			}
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
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					String selectionName = choices.groupData.get(groupPos).get(XmlConst.NAME_ATTR);
					((GeneratorActivity) getActivity()).addSelection(choiceId, groupName, subGroupName, selectionName);
				}
			});
			TextView textView = (TextView)convertView.findViewById(R.id.filtertitle_text);
			if (textView != null)
				textView.setText(grpData.get(groupPosition).get(XmlConst.NAME_ATTR));
			return convertView;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//			if (convertView == null) {
				convertView = View.inflate(getActivity(), R.layout.subrow_filterselect, null);
//			}
			TextView text = (TextView) convertView.findViewById(R.id.filterselect_text);
			TextView text2 = (TextView) convertView.findViewById(R.id.filterselect_text2);
			String tag = itmData.get(groupPosition).get(childPosition).get("tag");
			String left_text = null, right_text = null;
			if (tag.equals(XmlConst.PROFICIENCY_TAG)) {
				left_text = "Proficiency";
				right_text = itmData.get(groupPosition).get(childPosition).get(XmlConst.TYPE_ATTR);
			} else if (tag.equals(XmlConst.CHOICE_TAG) || tag.equals(XmlConst.CHOSEN_TAG)) {
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
