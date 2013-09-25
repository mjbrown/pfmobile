package com.ninjadin.pfmobile.dialogfragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.ninjadin.pfmobile.R;

public class TextEditDialogFragment extends EditDialogFragment {
	EditText edit_text;
	String id;

	public static TextEditDialogFragment newDialog(String id, String def) {
		TextEditDialogFragment frag = new TextEditDialogFragment();
		Bundle args = new Bundle();
		args.putString(ID, id);
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
		return R.layout.dialog_edittext;
	}

	@Override
	protected void initializeViews() {
		Bundle args = this.getArguments();
		String def = args.getString(DEFAULT);
		id = args.getString(ID);
		edit_text = (EditText) dialog.findViewById(R.id.editText1);
		edit_text.setText(def);
		edit_text.setSelection(edit_text.getText().length());
		try { // FIXME: This is dumb.
			Integer.parseInt(def);
			edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
		} catch (NumberFormatException e) {
			
		}
	}
	
	@Override
	public void onResume() {
		super .onResume();
		edit_text.requestFocus();
	}

	@Override
	protected String getOkText() {
		return "Ok";
	}

	@Override
	protected Intent returnData() {
		Intent intent = new Intent();
		String text = edit_text.getText().toString();
		intent.putExtra(RETURN_VALUE, text);
		intent.putExtra(ID, id);
		return intent;
	}

}
