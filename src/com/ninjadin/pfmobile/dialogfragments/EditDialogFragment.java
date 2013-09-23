package com.ninjadin.pfmobile.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public abstract class EditDialogFragment extends DialogFragment {
	final public static String ID = "Id";
	
	protected abstract int getFragmentLayout();
	
	protected abstract void initializeViews();
	
	protected abstract String getOkText();
	
	protected abstract Intent returnData();
	
	Dialog dialog;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(getFragmentLayout(), null))
				.setPositiveButton(getOkText(), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = returnData();
						getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
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
	
	public void onResume() {
		super .onResume();
		initializeViews();
	}
}
