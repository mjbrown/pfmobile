package com.ninjadin.pfmobile.dialogfragments;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.XmlConst;

public class ModifierDialogFragment extends DialogFragment {
	Dialog dialog;
	GeneratorActivity activity;
	ExpListData expListData;
	
	public interface ModifierDialogListener {
		public void onModifierPosClick(ModifierDialogFragment dialog);
		public void onModifierNegClick(ModifierDialogFragment dialog);
	}
	
	ModifierDialogListener mListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_modifier, null))
		.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onModifierPosClick(ModifierDialogFragment.this);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				mListener.onItemEditNegativeClick(ItemEditDialogFragment.this);
			}
		});
		dialog = builder.create();
		return dialog;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			mListener = (ModifierDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement ModifierDialogListener");
		}
	}
	
	@Override
	public void onResume() {
		super .onResume();
		activity = (GeneratorActivity) getActivity();
		expListData = activity.dependencyManager.getActivatableConditions();
		ExpandableListView expListView = (ExpandableListView) dialog.findViewById(R.id.modifier_expListView);
		SimpleExpandableListAdapter adapter = new ModifierExpListAdapter (
				activity,
				expListData.groupData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { android.R.id.text1 },
				expListData.itemData,
				R.layout.subrow_modifier,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.modifier_textView }
				);
		expListView.setAdapter(adapter);
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View view,
					int groupPosition, int childPosition, long id) {
				String key = expListData.groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
				String name = expListData.itemData.get(groupPosition).get(childPosition).get(XmlConst.NAME_ATTR);
				CheckBox box = (CheckBox) view.findViewById(R.id.modifier_checkBox);
				if (box.isChecked())
					activity.deactivateCondition(key, name);
				else
					activity.activateCondition(key, name);
				box.setChecked(!box.isChecked()); // Toggle
				return false;
			} });
	}
	
	class ModifierExpListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> groupData;
		List<List<Map<String,String>>> itemData;
		
		public ModifierExpListAdapter(Context context,
				List<Map<String, String>> groupData,
				int expandedGroupLayout, String[] groupFrom, int[] groupTo,
				List<List<Map<String, String>>> childData,
				int childLayout, String[] childFrom,
				int[] childTo) {
			super(context, groupData, expandedGroupLayout, groupFrom,
					groupTo, childData, childLayout, childFrom, childTo);
			// TODO Auto-generated constructor stub
			mContext = context;
			itemData = childData;
			this.groupData = groupData;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, 
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.subrow_modifier, null);
			}
			String key = groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
			String name = itemData.get(groupPosition).get(childPosition).get(XmlConst.NAME_ATTR);
			TextView property_text = (TextView) convertView.findViewById(R.id.modifier_textView);
			if (property_text != null) {
				property_text.setText(name);
			}
			GeneratorActivity activity = (GeneratorActivity) getActivity();
			CheckBox box = (CheckBox) convertView.findViewById(R.id.modifier_checkBox);
			box.setChecked(activity.dependencyManager.masterHasProperty(key, name));
			return convertView;
		}
	}
}
