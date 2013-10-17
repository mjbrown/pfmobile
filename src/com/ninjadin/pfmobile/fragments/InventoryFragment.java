package com.ninjadin.pfmobile.fragments;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import com.ninjadin.pfmobile.dialogfragments.EditDialogFragment;
import com.ninjadin.pfmobile.dialogfragments.SpinnerEditDialogFragment;
import com.ninjadin.pfmobile.dialogfragments.TextEditDialogFragment;
import com.ninjadin.pfmobile.non_android.InventoryXmlObject;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;


public class InventoryFragment extends ExpListFragment {
	public final static int PROPERTY_ADD_CODE = 0;
	public final static int SLOT_SELECT_CODE = 1;
	public final static int ITEM_ADD_CODE = 2;
	
	public final static String ITEM_ID = "Item Id";
	public final static String PROPERTY_ID = "Property Id";
	
	public interface InventoryFragmentListener {
		public void inventoryCreateItem(String name, String slot);
		public void inventoryDeleteItem(String item_id);
		public void itemDeleteProperty(String item_id, String property_id);
		public void equipItem(String item_id, Boolean is_equipped);
		public Boolean itemIsEquipped(String item_id);
		public void itemAddProperty(XmlObjectModel property, String item_id, String property_id);
		public XmlObjectModel getXmlModel(int id);
	}
	
	InventoryFragmentListener invListener;
	Map<String,XmlObjectModel> properties_models = new HashMap<String,XmlObjectModel>();
	ArrayList<String> properties = new ArrayList<String>();
	Fragment this_fragment = this;
	
	@Override
	OnClickListener buildButtonClickListener() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<String> slot_names = new ArrayList<String>();
				for (String slot: PropertyLists.slotNames)
					slot_names.add(slot);
				DialogFragment dialog = SpinnerEditDialogFragment.newDialog("No Id", "Held", null, slot_names);
				dialog.setTargetFragment(this_fragment, SLOT_SELECT_CODE);
				dialog.show(getChildFragmentManager(), "SpinnerEditDialogFragment");
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
		slot_names.add("All");
		for (String slot: PropertyLists.slotNames)
			slot_names.add(slot);
		return slot_names;
	}

	@Override
	SimpleExpandableListAdapter buildExpListAdapter(String filter) {
		// TODO Auto-generated method stub
		ExpListData inventoryExpData = new ExpListData(invListener.getXmlModel(GeneratorActivity.INVENTORY_MODEL));
		if (filter != null)
			if (!filter.equals("All"))
				inventoryExpData.filterByAttribute(XmlConst.SLOT_ATTR, filter);
		SimpleExpandableListAdapter adapter = new InventoryExpListAdapter(activity,
				inventoryExpData.groupData,
				R.layout.titlerow_inventory,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.title_text },
				inventoryExpData.itemData,
				R.layout.subrow_inventory,
				new String[] {XmlConst.NAME_ATTR },
				new int[] { R.id.inv_sub_text });
		return adapter;
	}

	@Override
	int getFragmentLayout() {
		return R.layout.fragment_button_exp_list;
	}
	
	public void onResume() {
		super .onResume();
		InputStream item_data = getResources().openRawResource(R.raw.properties);
		XmlObjectModel model = new XmlObjectModel(item_data);
		for (XmlObjectModel child: model.getChildren()) {
			String type = child.getAttribute(XmlConst.NAME_ATTR); 
			properties_models.put(type, child);
			properties.add(type);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PROPERTY_ADD_CODE) {
			String item_id = data.getStringExtra(SpinnerEditDialogFragment.ID);
			String property_name = data.getStringExtra(SpinnerEditDialogFragment.RETURN_VALUE);
			XmlObjectModel property = properties_models.get(property_name);
			invListener.itemAddProperty(property, item_id, null);
			dataSetUpdate();
		} else if (requestCode == SLOT_SELECT_CODE) {
			String slot_name = data.getStringExtra(EditDialogFragment.RETURN_VALUE);
			DialogFragment dialog = TextEditDialogFragment.newDialog(slot_name, "Unnamed");
			dialog.setTargetFragment(this_fragment, ITEM_ADD_CODE);
			dialog.show(getChildFragmentManager(), "TextEditDialogFragment");
		} else if (requestCode == ITEM_ADD_CODE) {
			String slot_name = data.getStringExtra(EditDialogFragment.ID);
			String item_name = data.getStringExtra(EditDialogFragment.RETURN_VALUE);
			invListener.inventoryCreateItem(item_name, slot_name);
			dataSetUpdate();
		}
	}
	
	private class InventoryExpListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> groupData;
		List<List<Map<String,String>>> itemData;
		
		public InventoryExpListAdapter(Context context,
				List<Map<String, String>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<List<Map<String, String>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo, childData,
					childLayout, childFrom, childTo);
			mContext = context;
			this.groupData = groupData;
			this.itemData = childData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			convertView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
			String item_id = groupData.get(groupPosition).get(XmlConst.ID_ATTR);
			Button delete = (Button) convertView.findViewById(R.id.inventory_delete);
			if (delete != null)
				delete.setOnClickListener(new ExpListClickListener(groupPosition) {
					@Override
					public void onClick(View arg0) {
						String id = groupData.get(groupPosition).get(XmlConst.ID_ATTR);
						invListener.inventoryDeleteItem(id);
						dataSetUpdate();
					}
				});
			Button add_property = (Button) convertView.findViewById(R.id.inventory_enhance);
			if (add_property != null)
				add_property.setOnClickListener(new ExpListClickListener(groupPosition, this_fragment) {
					@Override
					public void onClick(View arg0) {
						String id = groupData.get(groupPosition).get(XmlConst.ID_ATTR);
						String current = groupData.get(groupPosition).get(XmlConst.VALUE_ATTR);
						DialogFragment dialog = SpinnerEditDialogFragment.newDialog(id, current, null, properties);
						dialog.setTargetFragment(fragment, PROPERTY_ADD_CODE);
						dialog.show(getChildFragmentManager(), "PropertyAddDialogFragment");
					}
				});
			Button equip_item = (Button) convertView.findViewById(R.id.inventory_equipitem);
			if (invListener.itemIsEquipped(item_id)) {
				equip_item.setText("Unequip");
				equip_item.setOnClickListener(new ExpListClickListener(groupPosition) {
					@Override
					public void onClick(View arg0) {
						String id = groupData.get(groupPosition).get(XmlConst.ID_ATTR);
						invListener.equipItem(id, false);
						dataSetUpdate();
					}
				});
			} else {
				equip_item.setText("Equip");
				equip_item.setOnClickListener(new ExpListClickListener(groupPosition) {
					@Override
					public void onClick(View arg0) {
						String id = groupData.get(groupPosition).get(XmlConst.ID_ATTR);
						invListener.equipItem(id, true);
						dataSetUpdate();
					}
				});
			}
			return convertView;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			convertView = super.getChildView(groupPosition, childPosition, isExpanded, convertView, parent);
			Button edit_property = (Button) convertView.findViewById(R.id.inv_sub_edit);
			edit_property.setOnClickListener(new ExpListClickListener(groupPosition, childPosition) {
				@Override
				public void onClick(View arg0) {
					String item_id = groupData.get(groupPosition).get(XmlConst.ID_ATTR);
					String property_id = itemData.get(groupPosition).get(childPosition).get(XmlConst.ID_ATTR);
					String property_name = itemData.get(groupPosition).get(childPosition).get(XmlConst.NAME_ATTR);
					ItemEditFragment frag = ItemEditFragment.newFragment(item_id, property_id, property_name);
					FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.fragment_container, frag);
					transaction.addToBackStack(null);
					transaction.commit();
				}
			});
			Button delete_property = (Button) convertView.findViewById(R.id.inv_sub_delete);
			delete_property.setOnClickListener(new ExpListClickListener(groupPosition, childPosition) {
				@Override
				public void onClick(View arg0) {
					String item_id = groupData.get(groupPosition).get(XmlConst.ID_ATTR);
					String property_id = itemData.get(groupPosition).get(childPosition).get(XmlConst.ID_ATTR);
					invListener.itemDeleteProperty(item_id, property_id);
					dataSetUpdate();
				}
			});
			return convertView;
		}

		private abstract class ExpListClickListener implements OnClickListener {
			public Integer groupPosition = null;
			public Integer childPosition = null;
			public Fragment fragment = null;
			
			public ExpListClickListener(int group) {
				super ();
				groupPosition = group;
			}
			
			public ExpListClickListener(int group, Fragment frag) {
				super ();
				groupPosition = group;
				fragment = frag;
			}
			
			public ExpListClickListener(int group, int child) {
				super ();
				groupPosition = group;
				childPosition = child;
			}
		}
	}
}
