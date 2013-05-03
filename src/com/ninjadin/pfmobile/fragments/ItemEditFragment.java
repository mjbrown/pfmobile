package com.ninjadin.pfmobile.fragments;

import java.io.File;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

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
import com.ninjadin.pfmobile.non_android.ItemEditor;

public class ItemEditFragment extends Fragment {
	ExpandableListView expList;
	String itemName;
	File inventoryFile;
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
}
