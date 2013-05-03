package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.XmlConst;

public class InventoryFragment extends Fragment {
	ExpandableListView expList;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inventory, container, false);
		return view;
	}
	public void onResume() {
		super .onResume();
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		//Bundle args = this.getArguments();
		expList = (ExpandableListView) activity.findViewById(R.id.inventory_exp_listview);
		ExpandableListAdapter simpleExpAdapter = new InventoryExpandableListAdapter(
				activity,
				activity.inventoryManager.xmlData.groupData,
				R.layout.titlerow_inventory,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.title_text },
				activity.inventoryManager.xmlData.itemData,
				android.R.layout.simple_expandable_list_item_2,
				new String[] { XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR },
				new int[] {android.R.id.text1, android.R.id.text2 } );
		expList.setAdapter(simpleExpAdapter);
	}
	class InventoryExpandableListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> grpData;
		
		public InventoryExpandableListAdapter(Context context, List<Map<String, String>> groupData,
				int groupLayout, String[] groupFrom, int[] groupTo, List<List<Map<String,String>>> childData, int childLayout,
				String[] childFrom, int[] childTo) {
			super (context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
			mContext = context;
			grpData = groupData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.titlerow_inventory, null);
				Button enhanceButton = (Button)convertView.findViewById(R.id.inventory_enhance);
				if (enhanceButton != null)
				enhanceButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						int groupPos = expList.getPositionForView((View) view.getParent());
						String itemName = grpData.get(groupPos).get(XmlConst.NAME_ATTR);
						((GeneratorActivity) getActivity()).enchantFromTemplate(itemName);
					}
				});
				Button editButton = (Button)convertView.findViewById(R.id.inventory_edititem);
				if (editButton != null)
					editButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							int groupPos = expList.getPositionForView((View) view.getParent());
							String itemName = grpData.get(groupPos).get(XmlConst.NAME_ATTR);
							((GeneratorActivity) getActivity()).editItem(itemName);
						}
					});
			}
			TextView textView = (TextView)convertView.findViewById(R.id.title_text);
			if (textView != null)
				textView.setText(grpData.get(groupPosition).get(XmlConst.NAME_ATTR));
			return convertView;
		}
	}
}
