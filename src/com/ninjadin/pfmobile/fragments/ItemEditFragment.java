package com.ninjadin.pfmobile.fragments;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.dialogfragments.CheckEditDialogFragment;
import com.ninjadin.pfmobile.dialogfragments.EditDialogFragment;
import com.ninjadin.pfmobile.dialogfragments.SpinnerEditDialogFragment;
import com.ninjadin.pfmobile.dialogfragments.TextEditDialogFragment;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class ItemEditFragment extends Fragment {
	public final static int OPTION_EDIT_CODE = 2;
	
	public final static String ITEM_ID = "Item Id";
	public final static String PROPERTY_ID = "Property Id";
	public final static String PROPERTY_NAME = "Property Name";
	public final static String OPTION_NAME = "Option Name";
	
	public interface ItemEditListener {
		public void itemAddProperty(XmlObjectModel property, String item_id, String property_id);
		public Map<String,String> getItemPropertyOptionMap(String item_id, String property_id);
	}
	
	ItemEditListener ieListener;
	@Override
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			ieListener = (ItemEditListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement ItemEditListener");
		}
	}
	
	Activity activity;
	ListView list_view;
	XmlObjectModel property;
	Map<String, XmlObjectModel> property_options = new HashMap<String, XmlObjectModel>();
	String item_id, property_id, property_name;
	
	public static ItemEditFragment newFragment(String item_id, String property_id, String property_name) {
		ItemEditFragment fragment = new ItemEditFragment();
		Bundle args = new Bundle();
		args.putString(ITEM_ID, item_id);
		args.putString(PROPERTY_ID, property_id);
		args.putString(PROPERTY_NAME, property_name);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = this.getArguments();
		item_id = args.getString(ITEM_ID);
		property_id = args.getString(PROPERTY_ID);
		property_name = args.getString(PROPERTY_NAME);
		activity = getActivity();
		InputStream item_data = getResources().openRawResource(R.raw.properties);
		XmlObjectModel model = new XmlObjectModel(item_data);
		for (XmlObjectModel child: model.getChildren()) {
			String type = child.getAttribute(XmlConst.NAME_ATTR); 
			if (type.equals(property_name)) {
				property = child;
				break;
			}
		}
		
		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		list_view = (ListView) view.findViewById(R.id.listView1);
		return view;
	}
	
	@Override 
	public void onResume() {
		super .onResume();
		dataSetChanged();
	}
	
	private void dataSetChanged() {
		Map<String,String> current_options = ieListener.getItemPropertyOptionMap(item_id, property_id);
		for (XmlObjectModel option: property.getChildren()) {
			String name = option.getAttribute(XmlConst.NAME_ATTR);
			String current_value = current_options.get(name);
			if (current_value != null) {
				option.setAttribute(XmlConst.VALUE_ATTR, current_value);
			}
			property_options.put(name, option);
		}
		List<Map<String,String>> list_data = new ExpListData(property).groupData;
		ItemEditAdapter adapter = new ItemEditAdapter(activity, 
						R.layout.titlerow_itemedit, R.id.option_name, list_data);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				XmlObjectModel option = property.getChildren().get(position);
				String type = option.getAttribute(XmlConst.TYPE_ATTR);
				if (type.equals(PropertyLists.spinner))
					spinnerEdit(option);
				else if (type.equals(PropertyLists.text))
					textEdit(option, false);
				else if (type.equals(PropertyLists.number))
					textEdit(option, true);
				else if (type.equals(PropertyLists.checkbox))
					checkedEdit(option);
			}
			
		});
	}
	
	private void checkedEdit(XmlObjectModel option) {
		String name = option.getAttribute(XmlConst.NAME_ATTR);
		String current = option.getAttribute(XmlConst.VALUE_ATTR);
		ArrayList<String> entries = new ArrayList<String>();
		for (XmlObjectModel entry: option.getChildren()) {
			String entry_value = entry.getAttribute(XmlConst.VALUE_ATTR);
			entries.add(entry_value);
		}
		DialogFragment dialog = CheckEditDialogFragment.newDialog(name, current, entries);
		dialog.setTargetFragment(this, OPTION_EDIT_CODE);
		dialog.show(getChildFragmentManager(), "CheckEditDialogFragment");
	}
	
	private void spinnerEdit(XmlObjectModel option) {
		String name = option.getAttribute(XmlConst.NAME_ATTR);
		String current = option.getAttribute(XmlConst.VALUE_ATTR);
		ArrayList<String> entries = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		for (XmlObjectModel entry: option.getChildren()) {
			String entry_value = entry.getAttribute(XmlConst.VALUE_ATTR);
			entries.add(entry_value);
			String entry_name = entry.getAttribute(XmlConst.NAME_ATTR);
			if (entry_name != null)
				names.add(entry_name);
		}
		if (names.size() < entries.size())
			names = null;
		DialogFragment dialog = SpinnerEditDialogFragment.newDialog(name, current, names, entries);
		dialog.setTargetFragment(this, OPTION_EDIT_CODE);
		dialog.show(getChildFragmentManager(), "SpinnerEditDialogFragment");
	}
	
	private void textEdit(XmlObjectModel option, Boolean is_number) {
		String name = option.getAttribute(XmlConst.NAME_ATTR);
		String current = option.getAttribute(XmlConst.VALUE_ATTR);
		DialogFragment dialog = TextEditDialogFragment.newDialog(name, current, is_number);
		dialog.setTargetFragment(this, OPTION_EDIT_CODE);
		dialog.show(getChildFragmentManager(), "TextEditDialogFragment");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OPTION_EDIT_CODE) {
			String option_name = data.getStringExtra(EditDialogFragment.ID);
			String option_value = data.getStringExtra(EditDialogFragment.RETURN_VALUE);
			XmlObjectModel option = property_options.get(option_name);
			option.setAttribute(XmlConst.VALUE_ATTR, option_value);
			ieListener.itemAddProperty(property, item_id, property_id);
			dataSetChanged();
		}
	}

	class ItemEditAdapter extends ArrayAdapter<Map<String,String>> {
		Context context;
		List<Map<String,String>> groupData;
		
		public ItemEditAdapter(Context context, int layout, int resource, List<Map<String, String>> objects) {
			super(context, layout, resource, objects);
			this.context = context;
			groupData = objects;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = super .getView(position, convertView, parent);
			TextView name = (TextView) convertView.findViewById(R.id.option_name);
			TextView value = (TextView) convertView.findViewById(R.id.option_value);
			String option_name = groupData.get(position).get(XmlConst.NAME_ATTR);
			String option_value = groupData.get(position).get(XmlConst.VALUE_ATTR);
			name.setText(option_name);
			value.setText(option_value);
			return convertView;
		}
	}
}
