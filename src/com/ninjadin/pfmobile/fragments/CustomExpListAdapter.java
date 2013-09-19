package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleExpandableListAdapter;

public class CustomExpListAdapter extends SimpleExpandableListAdapter {
	Context mContext;
	List<Map<String,String>> groupData;
	List<List<Map<String,String>>> itemData;
	
	public CustomExpListAdapter(Context context, List<Map<String,String>> groupData,
			int expandedGroupLayout, String[] groupFrom, int[] groupTo, List<List<Map<String,String>>> childData,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, expandedGroupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
		mContext = context;
		this.groupData = groupData;
		this.itemData = childData;
	}
}
