package com.ninjadin.pfmobile.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;

public class ItemEditDialogFragment extends DialogFragment {
	Dialog dialog;
	GeneratorActivity activity;
	public Spinner bonus_spinner, stack_spinner, source_spinner;
	public EditText valueEdit;
	
	public interface ItemEditDialogListener {
		public void onItemEditPositiveClick(ItemEditDialogFragment dialog);
		public void onItemEditNegativeClick(ItemEditDialogFragment dialog);
	}
	
	ItemEditDialogListener mListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_itemedit, null))
				.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mListener.onItemEditPositiveClick(ItemEditDialogFragment.this);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mListener.onItemEditNegativeClick(ItemEditDialogFragment.this);
					}
				});
		dialog = builder.create();
		return dialog;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			mListener = (ItemEditDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() 
					+ " must implement ItemEditDialogListener");
		}
	}
	
	@Override
	public void onResume() {
		super .onResume();
		activity = (GeneratorActivity)getActivity();
		bonus_spinner = (Spinner) dialog.findViewById(R.id.itemedit_bonustype);
		List<String> stat_list = PropertyLists.stat_list();
		ArrayAdapter<String> bonus_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, stat_list);
		bonus_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		if (bonus_spinner != null) {
			bonus_spinner.setAdapter(bonus_adapter);
			String bonusType = activity.itemEditor.getBonusType();
			for (int position = 0; position < stat_list.size(); position++) {
				if (stat_list.get(position).equals(bonusType)) {
					bonus_spinner.setSelection(position);
					break;
				}
			}
		}

		stack_spinner = (Spinner) dialog.findViewById(R.id.itemedit_stacktype);
		List<String> stack_types = new ArrayList<String>();
		for (String type: PropertyLists.stackableTypes)
			stack_types.add(type);
		for (String type: PropertyLists.notStackableTypes)
			stack_types.add(type);
		ArrayAdapter<String> stack_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, stack_types);
		stack_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		if (stack_spinner != null)  {
			stack_spinner.setAdapter(stack_adapter);
			String stackType = activity.itemEditor.getStackType();
			for (int position = 0; position < stack_types.size(); position++) {
				if (stack_types.get(position).equals(stackType)) {
					stack_spinner.setSelection(position);
					break;
				}
			}
		}

		source_spinner = (Spinner) dialog.findViewById(R.id.itemedit_sourcetype);
		ArrayAdapter<String> source_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, PropertyLists.bonusSources);
		source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		if (source_spinner != null) {
			source_spinner.setAdapter(source_adapter);
			String sourceType = activity.itemEditor.getSourceType();
			for (int position = 0; position < PropertyLists.bonusSources.length; position++) {
				if (PropertyLists.bonusSources.equals(sourceType)) {
					source_spinner.setSelection(position);
					break;
				}
			}
		}
		
		valueEdit = (EditText) dialog.findViewById(R.id.itemedit_value);
		if (valueEdit != null) {
			valueEdit.setText("");
			valueEdit.setHint(activity.itemEditor.getValue());
		}
	}
	
}
