package com.ninjadin.pfmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.InventoryActivity;
import com.ninjadin.pfmobile.non_android.XmlConst;

public class InventoryMenuFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inventory, container, false);
		return view;
	}
	public void onResume() {
		super .onResume();
		InventoryActivity inventoryActivity = (InventoryActivity) getActivity();
		//Bundle args = this.getArguments();
		ExpandableListView expList = (ExpandableListView) inventoryActivity.findViewById(R.id.inventory_exp_listview);
		ExpandableListAdapter simpleExpAdapter = new SimpleExpandableListAdapter(
				inventoryActivity,
				inventoryActivity.inventoryManager.xmlData.groupData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { android.R.id.text1 },
				inventoryActivity.inventoryManager.xmlData.itemData,
				android.R.layout.simple_expandable_list_item_2,
				new String[] { XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR },
				new int[] {android.R.id.text1, android.R.id.text2 } );
		expList.setAdapter(simpleExpAdapter);
	}

}
