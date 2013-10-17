package com.ninjadin.pfmobile.dialogfragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ninjadin.pfmobile.R;

public class SpinnerEditDialogFragment extends EditDialogFragment {
	final protected static String NAMES = "Names";
	final protected static String VALUES = "Values";
	
	Spinner spinner;
	String id, def;
	List<String> names, values;
	
	public static SpinnerEditDialogFragment newDialog(String id, String def, ArrayList<String> array_names, ArrayList<String> array_values) {
		SpinnerEditDialogFragment frag = new SpinnerEditDialogFragment();
		Bundle args = new Bundle();
		args.putString(ID, id);
		if (array_names != null)
			args.putStringArrayList(NAMES, array_names);
		else
			args.putStringArrayList(NAMES, array_values);
		args.putStringArrayList(VALUES, array_values);
		args.putString(DEFAULT, def);
		frag.setArguments(args);
		return frag;
	}
	@Override
	protected String getTitle() {
		return id;
	}
	
	@Override
	protected int getFragmentLayout() {
		return R.layout.dialog_spinneredit;
	}

	@Override
	protected void initializeViews() {
		Bundle args = this.getArguments();
		id = args.getString(ID);
		names = args.getStringArrayList(NAMES);
		values = args.getStringArrayList(VALUES);
		def = args.getString(DEFAULT);
		spinner = (Spinner) dialog.findViewById(R.id.spinner_property_add);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
		int i = 0, def_int = 0;
		for (String property: names) {
			adapter.add(property);
			if (property.equals(def))
				def_int = i;
			i++;
		}
		spinner.setAdapter(adapter);
		spinner.setSelection(def_int);
	}

	@Override
	protected Intent returnData() {
		String selected = spinner.getSelectedItem().toString();
		String value = null;
		for (int i = 0; i < values.size(); i++) {
			if (names.get(i).equals(selected)) {
				value = values.get(i);
				break;
			}
		}
		Intent intent = new Intent();
		intent.putExtra(ID, id);
		intent.putExtra(RETURN_VALUE, value);
		return intent;
	}

	@Override
	protected String getOkText() {
		return "Ok";
	}

}
