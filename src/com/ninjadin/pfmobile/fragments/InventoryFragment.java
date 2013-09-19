package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleExpandableListAdapter;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;


public class InventoryFragment extends ExpListFragment {

	public interface InventoryFragmentListener {
		public void inventoryCreateItem(String slot);
		public void inventoryDeleteItem(String item_id);
		public XmlObjectModel getXmlModel(int id);
		public void activateCondition(String key, String name);
		public void deactivateCondition(String key, String name);
		public Boolean isConditionActive(String key, String name);
	}
	
	InventoryFragmentListener invListener;
	
	@Override
	OnClickListener buildButtonClickListener() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Launch ItemEdit Fragment Here
				invListener.inventoryCreateItem(filter_spinner.getSelectedItem().toString());
				dataSetUpdate();
			}
		};
		return listener;
	}

	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			invListener = (InventoryFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement InventoryFragmentListener!");
		}
	}
	
	@Override
	ArrayAdapter<String> buildSpinnerAdapter() {
		ArrayAdapter<String> slot_names = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
		for (String slot: PropertyLists.slotNames)
			slot_names.add(slot);
		return slot_names;
	}

	@Override
	SimpleExpandableListAdapter buildExpListAdapter(String filter) {
		// TODO Auto-generated method stub
		ExpListData inventoryExpData = new ExpListData(invListener.getXmlModel(GeneratorActivity.INVENTORY_MODEL));
		if (filter != null)
			inventoryExpData.filterByAttribute(XmlConst.SLOT_ATTR, filter);
		SimpleExpandableListAdapter adapter = new InventoryExpListAdapter(activity,
				inventoryExpData.groupData,
				R.layout.titlerow_inventory,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.title_text },
				inventoryExpData.itemData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] {XmlConst.ID_ATTR },
				new int[] { android.R.id.text1 });
		return adapter;
	}

	@Override
	int getFragmentLayout() {
		return R.layout.fragment_button_exp_list;
	}
	
	private class InventoryExpListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> groupData;
//		List<List<Map<String,String>>> itemData;
		
		public InventoryExpListAdapter(Context context,
				List<Map<String, String>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo, childData,
					childLayout, childFrom, childTo);
			mContext = context;
			this.groupData = groupData;
//			this.itemData = childData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			convertView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
			String item_name = groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
			Button delete = (Button) convertView.findViewById(R.id.inventory_delete);
			if (delete != null)
				delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						int group_pos = exp_list.getPositionForView((View)arg0.getParent());
						String id = groupData.get(group_pos).get(XmlConst.ID_ATTR);
						invListener.inventoryDeleteItem(id);
						dataSetUpdate();
					}
				});
			Button add_property = (Button) convertView.findViewById(R.id.inventory_enhance);
			if (add_property != null)
				add_property.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						int group_pos = exp_list.getPositionForView((View)arg0.getParent());
						String id = groupData.get(group_pos).get(XmlConst.ID_ATTR);
						Log.d("addProp: ", id);
						DialogFragment dialog = PropertyAddDialogFragment.newDialog(id);
						dialog.show(getChildFragmentManager(), "PropertyAddDialogFragment");
					}
				});
			Button equip_item = (Button) convertView.findViewById(R.id.inventory_equipitem);
			if (invListener.isConditionActive(PropertyLists.equipment, item_name)) {
				equip_item.setText("Unequip");
				equip_item.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						int group_pos = exp_list.getPositionForView((View)arg0.getParent());
						String name = groupData.get(group_pos).get(XmlConst.NAME_ATTR);
						invListener.deactivateCondition(PropertyLists.equipment, name);
						dataSetUpdate();
					}
				});
			} else {
				equip_item.setText("Equip");
				equip_item.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						int group_pos = exp_list.getPositionForView((View)arg0.getParent());
						String name = groupData.get(group_pos).get(XmlConst.NAME_ATTR);
						invListener.activateCondition(PropertyLists.equipment, name);
						dataSetUpdate();
					}
				});
			}
			return convertView;
		}
	}
}
