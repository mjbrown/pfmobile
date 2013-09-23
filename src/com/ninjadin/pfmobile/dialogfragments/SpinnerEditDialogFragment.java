package com.ninjadin.pfmobile.dialogfragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ninjadin.pfmobile.R;

public class SpinnerEditDialogFragment extends EditDialogFragment {
	final protected static String LIST = "List";
	final public static String SELECTED = "Selected";
	
	Spinner spinner;
	String id;
	List<String> list;
	
	public static SpinnerEditDialogFragment newDialog(String id, ArrayList<String> array_list) {
		SpinnerEditDialogFragment frag = new SpinnerEditDialogFragment();
		Bundle args = new Bundle();
		args.putString(ID, id);
		args.putStringArrayList(LIST, array_list);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	protected int getFragmentLayout() {
		return R.layout.dialog_property_add;
	}

	@Override
	protected void initializeViews() {
		Bundle args = this.getArguments();
		id = args.getString(ID);
		list = args.getStringArrayList(LIST);
		spinner = (Spinner) dialog.findViewById(R.id.spinner_property_add);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
		for (String property: list) {
			adapter.add(property);
		}
		spinner.setAdapter(adapter);
	}

	@Override
	protected Intent returnData() {
		String selected = spinner.getSelectedItem().toString();
		Intent intent = new Intent();
		intent.putExtra(ID, id);
		intent.putExtra(SELECTED, selected);
		return intent;
	}

	@Override
	protected String getOkText() {
		return "Ok";
	}

}
