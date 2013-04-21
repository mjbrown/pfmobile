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
import com.ninjadin.pfmobile.non_android.XmlConst;
import com.ninjadin.pfmobile.non_android.TwoDimXmlExtractor;

public class ChoiceSelectFragment extends Fragment {
	ExpandableListView expList;
	int choiceId;
	String groupName;
	String subGroupName;
	TwoDimXmlExtractor choices;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_filterselect, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super .onResume();
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		try {
			Bundle args = this.getArguments();
			groupName = args.getString(XmlConst.GRPNAME_ATTR);
			subGroupName = args.getString(XmlConst.SUBGRP_ATTR);
			choiceId = Integer.parseInt(args.getString("choiceId"));
			InputStream dataFile = activity.getResources().openRawResource(args.getInt("rawDataInt"));
			String[] tags = new String[] { XmlConst.SELECTION_TAG };
			String[] tag_attrs = new String[] {XmlConst.NAME_ATTR };
			String[] subtags = new String[] {XmlConst.BONUS_TAG, XmlConst.PROFICIENCY_TAG, 
					XmlConst.CHOICE_TAG};
			String[] subtag_attrs = new String[] { XmlConst.BONUSGRP_TAG, XmlConst.NAME_ATTR,
					XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR };
			choices = new TwoDimXmlExtractor(dataFile);
			choices.findTagAttr(XmlConst.BONUSGRP_TAG, XmlConst.GRPNAME_ATTR, groupName);
			if (subGroupName != null) {
				if (!subGroupName.equals("Any")) {
					choices.findTagAttr(XmlConst.SUBGRP_ATTR, XmlConst.GRPNAME_ATTR, subGroupName);
					choices.getData(XmlConst.SUBGRP_ATTR, tags, tag_attrs, subtags, subtag_attrs);
				} else {
					choices.getData(XmlConst.BONUSGRP_TAG, tags, tag_attrs, subtags, subtag_attrs);
				}
			} else {
				choices.getData(XmlConst.BONUSGRP_TAG, tags, tag_attrs, subtags, subtag_attrs);
			}
			expList = (ExpandableListView) activity.findViewById(R.id.filter_exp_listview);
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
		
		public FilterSelectSimpleExpandableListAdapter(Context context, List<Map<String, String>> groupData,
				int groupLayout, String[] groupFrom, int[] groupTo, List<List<Map<String,String>>> childData, int childLayout,
				String[] childFrom, int[] childTo) {
			super (context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
			mContext = context;
			grpData = groupData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.titlerow_filterselect, null);
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
			}
			TextView textView = (TextView)convertView.findViewById(R.id.filtertitle_text);
			if (textView != null)
				textView.setText(grpData.get(groupPosition).get(XmlConst.NAME_ATTR));
			return convertView;
		}
	}
}
