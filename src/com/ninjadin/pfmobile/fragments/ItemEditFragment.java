package com.ninjadin.pfmobile.fragments;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.ItemEditor;

public class ItemEditFragment extends Fragment {
	ExpandableListView expList;
	String itemName;
	File inventoryFile;
	File tempFile;
	ItemEditor itemEditor;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basicexpandable, container, false);
		return view;
	}

	public void onResume() {
		super .onResume();
		Bundle args = this.getArguments();
		itemName = args.getString(XmlConst.NAME_ATTR);
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		inventoryFile = activity.inventoryFile;
		tempFile = activity.tempFile;
		try {
			itemEditor = new ItemEditor(itemName, inventoryFile);
			expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
			ExpandableListAdapter simpleExpAdapter = new SimpleExpandableListAdapter(
					activity,
					itemEditor.item.groupData,
					R.layout.titlerow_inventory,
					new String[] { XmlConst.NAME_ATTR },
					new int[] { R.id.title_text },
					itemEditor.item.itemData,
					R.layout.subrow_itemedit,
					new String[] { XmlConst.TYPE_ATTR, XmlConst.STACKTYPE_ATTR, XmlConst.SOURCE_ATTR, XmlConst.VALUE_ATTR },
					new int[] {R.id.item_type, R.id.item_stacktype, R.id.item_source, R.id.item_value } );
			expList.setAdapter(simpleExpAdapter);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPause() {
		super .onPause();
		try {
			itemEditor.saveChanges(tempFile);
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
				Button addButton = (Button)convertView.findViewById(R.id.filtertitle_add);
				if (addButton != null)
				addButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
			}
			TextView textView = (TextView)convertView.findViewById(R.id.filtertitle_text);
			if (textView != null)
				textView.setText(grpData.get(groupPosition).get(XmlConst.NAME_ATTR));
			return convertView;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.subrow_itemedit, null);
				EditText editText = (EditText)convertView.findViewById(R.id.item_value);
				ValueTextWatcher valueTextWatcher = new ValueTextWatcher(groupPosition, childPosition);
				editText.addTextChangedListener(valueTextWatcher);
			}
			return convertView;
		}
		
		private class ValueTextWatcher implements TextWatcher {
			private int groupPos;
			private int childPos;
			public ValueTextWatcher(int groupPosition, int childPosition) {
				groupPos = groupPosition;
				childPos = childPosition;
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				itemData.get(groupPos).get(childPos).put(XmlConst.VALUE_ATTR, arg0.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
		}
	}

}
