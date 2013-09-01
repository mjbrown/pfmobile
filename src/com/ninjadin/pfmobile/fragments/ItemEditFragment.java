package com.ninjadin.pfmobile.fragments;

import java.io.IOException;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.ItemEditor;

public class ItemEditFragment extends Fragment {
	ExpandableListView expList;
	String itemName;
	GeneratorActivity activity;
	Spinner slot_spinner;
	EditText name_edit;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_itemedit, container, false);
		return view;
	}

	public void onResume() {
		super .onResume();
		Bundle args = this.getArguments();
		itemName = args.getString(XmlConst.NAME_ATTR);
		activity = (GeneratorActivity) getActivity();
		try {
			activity.itemEditor = new ItemEditor(itemName, activity.inventoryFile);
			expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
			ExpandableListAdapter simpleExpAdapter = new ItemEditExpandableListAdapter(
					activity,
					activity.itemEditor.item.groupData,
					R.layout.titlerow_filterselect,
					new String[] { XmlConst.NAME_ATTR },
					new int[] { R.id.title_text },
					activity.itemEditor.item.itemData,
					R.layout.subrow_itemedit,
					new String[] { XmlConst.TYPE_ATTR, XmlConst.VALUE_ATTR },
					new int[] {R.id.item_type, R.id.item_value } );
			expList.setAdapter(simpleExpAdapter);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expList.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				// TODO Auto-generated method stub
				activity.showItemEditDialog(arg2, arg3);
				return false;
			}
			
		});
		
		slot_spinner = (Spinner) activity.findViewById(R.id.itemedit_reslot);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, PropertyLists.slotNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		slot_spinner.setAdapter(adapter);
		for (int position = 0; position < PropertyLists.slotNames.length; position++) {
			if (PropertyLists.slotNames[position].equals(activity.itemEditor.slot)) {
				slot_spinner.setSelection(position);
				break;
			}
		}
		name_edit = (EditText) activity.findViewById(R.id.itemedit_rename);
		name_edit.setText(itemName);
	}
	
	@Override
	public void onPause() {
		super .onPause();
		try {
			activity.itemEditor.rename(name_edit.getText().toString());
			activity.itemEditor.reslot(slot_spinner.getSelectedItem().toString());
			activity.itemEditor.saveToInventory(activity.tempFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ItemEditExpandableListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> grpData;
		List<List<Map<String,String>>> itemData;
		
		public ItemEditExpandableListAdapter(Context context, List<Map<String, String>> groupData,
				int groupLayout, String[] groupFrom, int[] groupTo, List<List<Map<String,String>>> childData, int childLayout,
				String[] childFrom, int[] childTo) {
			super (context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
			mContext = context;
			grpData = groupData;
			itemData = childData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.titlerow_filterselect, null);
			}
			Button addButton = (Button)convertView.findViewById(R.id.filtertitle_add);
			if (addButton != null)
				addButton.setText("Delete");
			addButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
				}
			});
			TextView textView = (TextView)convertView.findViewById(R.id.filtertitle_text);
			if (textView != null)
				textView.setText(grpData.get(groupPosition).get(XmlConst.NAME_ATTR));
			return convertView;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				
				convertView = View.inflate(mContext, R.layout.subrow_itemedit, null);
			}
			TextView value = (TextView) convertView.findViewById(R.id.item_value);
			value.setText(itemData.get(groupPosition).get(childPosition).get(XmlConst.VALUE_ATTR));
			TextView textView = (TextView) convertView.findViewById(R.id.item_type);
			if (textView != null) {
				textView.setText(itemData.get(groupPosition).get(childPosition).get(XmlConst.TYPE_ATTR));
			}
			return convertView;
		}
	}
}
