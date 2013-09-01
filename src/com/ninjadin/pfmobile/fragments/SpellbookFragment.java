package com.ninjadin.pfmobile.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.SpellGroup;

public class SpellbookFragment extends Fragment {
	Button add_spell;
	Spinner class_spinner;
	GeneratorActivity activity;
	ExpandableListView expListView;
	String selected_class;
	List<SpellGroup> master_list;
	Map<String, String> spell_sources = new HashMap<String,String>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_spellbook, container, false);
		// Initialize selected in spinner
		if (savedInstanceState != null)
			selected_class = savedInstanceState.getString("class_spinner");
		activity = (GeneratorActivity) getActivity();
		class_spinner = (Spinner) view.findViewById(R.id.class_spinner);
		expListView = (ExpandableListView) view.findViewById(R.id.spellBook_expListView);
		add_spell = (Button) view.findViewById(R.id.button_add_spell);
		add_spell.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SpellListFragment newFragment = new SpellListFragment();
				FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
			
		});
		return view;
	}
	
	@Override
	public void onResume() {
		super .onResume();
		master_list = activity.dependencyManager.getSpells();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
		int i = 0, position = 0;
		for (String src: sourceList()) {
			adapter.add(src);
			if (selected_class != null)
				if (selected_class.equals(src))
					position = i;
			i += 1;
		}
		if (adapter.getCount() == 0)
			adapter.add("None Available.");
		class_spinner.setAdapter(adapter);
		class_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				refreshExpListAdapter();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		class_spinner.setSelection(position);
		refreshExpListAdapter();
	}

	private List<String> sourceList() {
		List<String> sourceList = new ArrayList<String>();
		Map<String,Boolean> sourceMap = new HashMap<String, Boolean>();
		for (SpellGroup spell: master_list) {
			sourceMap.put(spell.getSource(), true);
		}
		for (Map.Entry<String, Boolean> entry: sourceMap.entrySet()) {
			sourceList.add(entry.getKey());
		}
		return sourceList;
	}
	
	private void refreshExpListAdapter() {
		ExpListData expData = getLevelList(class_spinner.getSelectedItem().toString());
		SpellsExpListAdapter expAdapter = new SpellsExpListAdapter (
				activity,
				expData.groupData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { android.R.id.text1 },
				expData.itemData,
				R.layout.subrow_spell,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.name_text });
		expListView.setAdapter(expAdapter);
	}
	
	private ExpListData getLevelList(String src) {
		ExpListData levelList = new ExpListData();
		int max_level = -1;
		for (SpellGroup spell: master_list) {
			if (src.equals(spell.getSource())) {
				int level = spell.getValue(PropertyLists.spell_level);
				while (max_level < level) {
					max_level += 1;
					Map<String,String> levelString = new HashMap<String,String>();
					levelString.put(XmlConst.NAME_ATTR, "Spell Level " + Integer.toString(max_level));
					levelList.groupData.add(levelString);
					List<Map<String,String>> levelSpells = new ArrayList<Map<String,String>>();
					levelList.itemData.add(levelSpells);
				}
				Map<String,String> spell_details = new HashMap<String,String>();
				levelList.itemData.get(level).add(spell_details);
				spell_details.put(XmlConst.NAME_ATTR, spell.getName());
				spell_details.put(XmlConst.SCHOOL_ATTR, spell.getSchool());
				spell_details.put(PropertyLists.save_dc, Integer.toString(spell.getValue(PropertyLists.save_dc)));
				spell_details.put(PropertyLists.spell_failure, Integer.toString(spell.getValue(PropertyLists.spell_failure)));
				spell_details.put(PropertyLists.uses, Integer.toString(spell.getValue(PropertyLists.uses)));
				spell_details.put(PropertyLists.caster_level, Integer.toString(spell.getValue(PropertyLists.caster_level)));
			}
		}
		return levelList;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super .onSaveInstanceState(outState);
		if (class_spinner != null) {
			String class_name = class_spinner.getSelectedItem().toString();
			outState.putString("class_spinner", class_name);
		}
	}

	private class SpellsExpListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> groupData;
		List<List<Map<String,String>>> itemData;
		
		public SpellsExpListAdapter(Context context, List<Map<String,String>> groupData,
				int expandedGroupLayout, String[] groupFrom, int[] groupTo, List<List<Map<String,String>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, expandedGroupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
			mContext = context;
			this.groupData = groupData;
			this.itemData = childData;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.subrow_spell, null);
			}
			Map<String,String> child_map = itemData.get(groupPosition).get(childPosition);
			String name = child_map.get(XmlConst.NAME_ATTR);
			((TextView) convertView.findViewById(R.id.name_text)).setText(name);
			String save_dc = child_map.get(PropertyLists.save_dc);
			((TextView) convertView.findViewById(R.id.savedc_text)).setText(save_dc);
			String spell_failure = child_map.get(PropertyLists.spell_failure);
			((TextView) convertView.findViewById(R.id.spell_fail_text)).setText(spell_failure);
			String uses = child_map.get(PropertyLists.uses);
			((TextView) convertView.findViewById(R.id.uses_text)).setText(uses);
			String caster_level = child_map.get(PropertyLists.caster_level);
			((TextView) convertView.findViewById(R.id.caster_text)).setText(caster_level);
			String school = child_map.get(XmlConst.SCHOOL_ATTR);
			if (school != null)
				((TextView) convertView.findViewById(R.id.school_text)).setText(school);
			return convertView;
		}
	}
}
