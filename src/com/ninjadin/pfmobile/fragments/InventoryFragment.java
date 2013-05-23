package com.ninjadin.pfmobile.fragments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.ninjadin.pfmobile.non_android.XmlExtractor;

public class InventoryFragment extends Fragment {
	ExpandableListView expList;
	Spinner slot_spinner;
	InventoryExpandableListAdapter expAdapt;
	XmlExtractor full_inventory;
	List<Map<String,String>> groupData;
	List<List<Map<String,String>>> itemData;
	final static String all = "All";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inventory, container, false);
		return view;
	}
	public void onResume() {
		super .onResume();
		Bundle args = this.getArguments();
		String slotName = null;
		if (args != null)
			slotName = args.getString(XmlConst.SLOT_ATTR);
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		try {
			InputStream inventoryStream = new FileInputStream(activity.inventoryFile);
			full_inventory = ExpListData.initInventory(inventoryStream);
			inventoryStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		groupData = full_inventory.groupData;
		itemData = full_inventory.itemData;
		expList = (ExpandableListView) activity.findViewById(R.id.inventory_exp_listview);
		slot_spinner = (Spinner) activity.findViewById(R.id.slot_spinner);
		List<String> slot_names = new ArrayList<String>();
		slot_names.add(all);
		for (String slot: PropertyLists.slotNames)
			slot_names.add(slot);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, slot_names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		slot_spinner.setAdapter(adapter);
		for (int position = 0; position < PropertyLists.slotNames.length; position++) {
			if (PropertyLists.slotNames[position].equals(slotName)) {
				slot_spinner.setSelection(position);
				break;
			}
		}
		slot_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				filter_inventory(slot_spinner.getSelectedItem().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		if (slotName != null) {
			filter_inventory(slotName);
		} else {
			filter_inventory(all);
		}
	}
	
	private void filter_inventory(String slot) {
		if (slot.equals(all)) {
			groupData = full_inventory.groupData;
			itemData = full_inventory.itemData;
		} else {
			groupData = new ArrayList<Map<String,String>>();
			itemData = new ArrayList<List<Map<String,String>>>();
			for (int item = 0; item < full_inventory.groupData.size(); item++) {
				String item_slot = full_inventory.groupData.get(item).get(XmlConst.SLOT_ATTR);
				if (slot.equals(item_slot)) {
					groupData.add(full_inventory.groupData.get(item));
					itemData.add(full_inventory.itemData.get(item));
				}
			}
		}
		expAdapt = new InventoryExpandableListAdapter(
				getActivity(),
				groupData,
				R.layout.titlerow_inventory,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.title_text },
				itemData,
				android.R.layout.simple_expandable_list_item_2,
				new String[] { XmlConst.NAME_ATTR },
				new int[] {android.R.id.text1 } );
		expList.setAdapter(expAdapt);
		expList.invalidateViews();
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
			}
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
			Button equip_button = (Button) convertView.findViewById(R.id.inventory_equipitem);
			if (equip_button != null) {
				equip_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						int groupPos = expList.getPositionForView((View) view.getParent());
						String itemName = grpData.get(groupPos).get(XmlConst.NAME_ATTR);
						String slotName = grpData.get(groupPos).get(XmlConst.SLOT_ATTR);
						((GeneratorActivity) getActivity()).equipItem(slotName, itemName);
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
