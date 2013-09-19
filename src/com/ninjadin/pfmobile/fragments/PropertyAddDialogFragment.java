package com.ninjadin.pfmobile.fragments;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class PropertyAddDialogFragment extends DialogFragment {
	Dialog dialog;
	Spinner properties_spinner;
	Map<String,XmlObjectModel> properties_models = new HashMap<String,XmlObjectModel>();
	String item_id;
	
	public interface PropertyAddDialogListener {
		public void itemAddProperty(XmlObjectModel property, String item_id);
	}
	
	PropertyAddDialogListener mListener;
	
	public static PropertyAddDialogFragment newDialog(String id) {
		PropertyAddDialogFragment frag = new PropertyAddDialogFragment();
		Bundle args = new Bundle();
		args.putString("Item Id", id);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = this.getArguments();
		item_id = args.getString("Item Id");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_property_add, null))
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String selected = properties_spinner.getSelectedItem().toString();
						XmlObjectModel property_model = properties_models.get(selected);
						mListener.itemAddProperty(property_model, item_id);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
		dialog = builder.create();
		return dialog;
	}
	
	@Override
	public void onResume() {
		super .onResume();
		properties_spinner = (Spinner) dialog.findViewById(R.id.spinner_property_add);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
		InputStream item_data = getResources().openRawResource(R.raw.item_data);
		XmlObjectModel model = new XmlObjectModel(item_data);
		for (XmlObjectModel child: model.getChildren()) {
			String type = child.getAttribute(XmlConst.NAME_ATTR); 
			adapter.add(type);
			properties_models.put(type, child);
		}
		properties_spinner.setAdapter(adapter);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			mListener = (PropertyAddDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement PropertyAddDialogListener");
		}
	}
}
