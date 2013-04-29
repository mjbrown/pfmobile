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
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.XmlConst;

public class EquipmentFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basicexpandable, container, false);
		return view;
	}
	public void onResume() {
		super .onResume();
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		//Bundle args = this.getArguments();
		ExpandableListView expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
		ExpandableListAdapter simpleExpAdapter = new SimpleExpandableListAdapter(
				activity,
				activity.inventoryManager.equipmentSlots,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { android.R.id.text1 },
				activity.inventoryManager.equipmentItems,
				android.R.layout.simple_expandable_list_item_2,
				new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR },
				new int[] {android.R.id.text1, android.R.id.text2 } );
		expList.setAdapter(simpleExpAdapter);
		expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				GeneratorActivity activity = ((GeneratorActivity) getActivity());
				String slotName = activity.inventoryManager.equipmentItems.get(groupPosition).get(childPosition).get(XmlConst.SLOT_ATTR);
				String itemName = activity.inventoryManager.equipmentItems.get(groupPosition).get(childPosition).get(XmlConst.NAME_ATTR);
				activity.equipItem(slotName, itemName);
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

}
