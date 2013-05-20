package com.ninjadin.pfmobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.ninjadin.pfmobile.R;

public class ItemEditDialogFragment extends DialogFragment {

	public interface ItemEditDialogListener {
		public void onItemEditPositiveClick(DialogFragment dialog);
		public void onItemEditNegativeClick(DialogFragment dialog);
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
		return builder.create();
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
}
