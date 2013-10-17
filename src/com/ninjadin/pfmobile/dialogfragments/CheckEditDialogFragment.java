package com.ninjadin.pfmobile.dialogfragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.non_android.InventoryXmlObject;

public class CheckEditDialogFragment extends EditDialogFragment {
	final protected static String LIST = "List";
	
	ListView list_view;
	String id, def;
	List<String> list;
	List<Boolean> checked = new ArrayList<Boolean>();
	
	public static CheckEditDialogFragment newDialog(String id, String def, ArrayList<String> entries) {
		CheckEditDialogFragment fragment = new CheckEditDialogFragment();
		Bundle args = new Bundle();
		args.putString(ID, id);
		args.putStringArrayList(LIST, entries);
		args.putString(DEFAULT, def);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	protected String getTitle() {
		return id;
	}
	@Override
	protected int getFragmentLayout() {
		return R.layout.fragment_listview;
	}

	@Override
	protected void initializeViews() {
		Bundle args = this.getArguments();
		id = args.getString(ID);
		def = args.getString(DEFAULT);
		list = args.getStringArrayList(LIST);
		for (int i = 0; i < list.size(); i++) {
			String value = list.get(i);
			checked.add(i, InventoryXmlObject.hasOption(def, value));
		}
		list_view = (ListView) dialog.findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new CheckEditAdapter(getActivity(), 
				R.layout.subrow_modifier, R.id.modifier_textView, list);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int arg2, long arg3) {
				CheckBox box = (CheckBox) view.findViewById(R.id.modifier_checkBox);
				checked.set(arg2, !box.isChecked());
				box.setChecked(checked.get(arg2));
			}
			
		});
	}

	@Override
	protected String getOkText() {
		// TODO Auto-generated method stub
		return "Ok";
	}

	@Override
	protected Intent returnData() {
		Intent intent = new Intent();
		intent.putExtra(ID, id);
		String return_value = "";
		for (int i = 0; i < list.size(); i++) {
			if (checked.get(i)) {
				if (return_value.equals("")) {
					return_value = list.get(i);
				} else {
					return_value += "," + list.get(i);
				}
			}
		}
		intent.putExtra(RETURN_VALUE, return_value);
		return intent;
	}
	
	private class CheckEditAdapter extends ArrayAdapter<String> {
		public CheckEditAdapter(Context context, int resource,
				int textViewResourceId, List<String> objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = super .getView(position, convertView, parent);
			CheckBox check_box = (CheckBox) convertView.findViewById(R.id.modifier_checkBox);
			check_box.setChecked(checked.get(position));
			return convertView;
		}
	}

}
