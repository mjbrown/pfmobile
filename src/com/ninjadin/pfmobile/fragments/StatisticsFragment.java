package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.CharacterEditor;
import com.ninjadin.pfmobile.non_android.StatisticManager;

public class StatisticsFragment extends Fragment {
	ExpandableListView expList;
	CharacterSheetAdapter adapter;
	StatisticManager manager;
	CharacterEditor charEdit;
	List<Map<String,String>> groupData;
	List<List<Map<String,String>>> itemData;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basicexpandable, container, false);
		return view;
	}
	
	@Override
	public void onResume() {
		super .onResume();
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		manager = activity.dependencyManager;
		charEdit = activity.charData;
		groupData = PropertyLists.categoryData();
		itemData = PropertyLists.statisticData();
		expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
		ExpandableListAdapter adapter = new CharacterSheetAdapter(
				activity,
				groupData,
				R.layout.titlerow_abilityscores,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.titlerow_text },
				itemData,
				R.layout.subrow_abilityscores,
				new String[] { XmlConst.NAME_ATTR},
				new int[] {R.id.score_name} );
		expList.setAdapter(adapter);
	}
	private class CharacterSheetAdapter extends SimpleExpandableListAdapter {
		
		public CharacterSheetAdapter(Context context,
				List<? extends Map<String, ?>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo, childData,
					childLayout, childFrom, childTo);
			// TODO Auto-generated constructor stub
		}
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			String category = groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
			if (convertView == null) {
				convertView = View.inflate(getActivity(), R.layout.titlerow_abilityscores, null);
			}
			TextView title;
			TextView points_left = (TextView) convertView.findViewById(R.id.number_points_left);
			if (points_left != null) {
				if (category.equals("Ability Scores")) { 
					points_left.setText(Integer.toString(charEdit.pointBuyRemaining) + " / 20");
				} else if (category.equals("Skills")) {
					points_left.setText(Integer.toString(manager.getValue("Skill Points")) + " / 20");
				}
			}
			title = (TextView) convertView.findViewById(R.id.titlerow_text);
			if (title != null)
				title.setText(category);
			return convertView;
		}
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			String category = groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
			if ((category.equals("Ability Scores")) || (category.equals("Skills"))) {
				convertView = View.inflate(getActivity(), R.layout.subrow_abilityscores, null);
				String score_name = itemData.get(groupPosition).get(childPosition).get(XmlConst.NAME_ATTR);
				TextView tx_name = (TextView) convertView.findViewById(R.id.score_name);
				if (tx_name != null)
					tx_name.setText(score_name.substring(0, 3).toUpperCase());
				String final_score = Integer.toString(manager.getValue(score_name));
				TextView tx_final = (TextView) convertView.findViewById(R.id.final_score);
				if (tx_final != null)
					tx_final.setText(final_score);
				TextView tx_modifier = (TextView) convertView.findViewById(R.id.score_modifier);
				if (category.equals("Ability Scores")) {
					// Base ability score
					updateAbilityScoreSubrow(convertView, childPosition);
				} else if (category.equals("Skills")) {
					String score_value = Integer.toString(charEdit.skillRanks[childPosition]);
					TextView tx_value = (TextView) convertView.findViewById(R.id.score);
					if (tx_value != null)
						tx_value.setText(score_value);
					Button minus = (Button) convertView.findViewById(R.id.minus);
					if (minus != null) {
						minus.setOnClickListener(skillListener);
					}
					Button plus = (Button) convertView.findViewById(R.id.plus);
					if (plus != null) {
						plus.setOnClickListener(skillListener);
					}
				} else {
					tx_modifier.setText("");
				}
			} else {
				convertView = View.inflate(getActivity(), R.layout.row_statistics, null);
			}
			return convertView;
		}
	}
	private void updateAbilityScoreSubrow(View parent, int childPosition) {
		TextView base_score = (TextView) parent.findViewById(R.id.score);
		if (base_score != null) {
			base_score.setText(Integer.toString(charEdit.getAbilityScore(childPosition)));
		}
		String abilityName = PropertyLists.abilityScoreNames[childPosition];
		TextView final_score = (TextView) parent.findViewById(R.id.final_score);
		if (final_score != null) {
			final_score.setText(Integer.toString(manager.getValue(abilityName)));
		}
		TextView tx_modifier = (TextView) parent.findViewById(R.id.score_modifier);
		Integer modifier = manager.getValue(abilityName + " Modifier");
		if (modifier < 0) {
			tx_modifier.setText("-" + Integer.toString(modifier));
		} else {
			tx_modifier.setText("+" + Integer.toString(modifier));
		}
		Button minus = (Button) parent.findViewById(R.id.minus);
		if (minus != null) {
			minus.setOnClickListener(abilityScoreListener);
			minus.setEnabled(charEdit.canDecrementAbilityScore(childPosition));
		}
		Button plus = (Button) parent.findViewById(R.id.plus);
		if (plus != null) {
			plus.setOnClickListener(abilityScoreListener);
			plus.setEnabled(charEdit.canIncrementAbilityScore(childPosition));
		}
	}
	
	private void updateTitlesAndButtons() {
		View abilityScoreTitle = (View) expList.getChildAt(0);
		if (abilityScoreTitle != null) {
			TextView pts_left = (TextView) abilityScoreTitle.findViewById(R.id.number_points_left);
			if (pts_left != null) {
				pts_left.setText(Integer.toString(charEdit.pointBuyRemaining) + " / 20");
			}
		}
		for (int i = 0; i < PropertyLists.abilityScoreNames.length; i++) {
			View abilityScoreSubrow = (View) expList.getChildAt(i+1);
			if (abilityScoreSubrow != null) {
				Button plus = (Button) abilityScoreSubrow.findViewById(R.id.plus);
				if (plus != null) {
					plus.setEnabled(charEdit.canIncrementAbilityScore(i));
				}
				Button minus = (Button) abilityScoreSubrow.findViewById(R.id.minus);
				if (minus != null) {
					minus.setEnabled(charEdit.canDecrementAbilityScore(i));
				}
			}
		}
		
	}
	
	private OnClickListener abilityScoreListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			View parent = (View) v.getParent();
			int position = expList.getPositionForView(parent);
			int childPosition = ExpandableListView.getPackedPositionChild(expList.getAdapter().getItemId(position));
			if (v.getId() == R.id.minus) {
				charEdit.decrementAbilityScore(childPosition);
				manager.newBonus(PropertyLists.abilityScoreNames[childPosition], "Base", "Natural", "-1");
			} else {
				charEdit.incrementAbilityScore(childPosition);
				manager.newBonus(PropertyLists.abilityScoreNames[childPosition], "Base", "Natural", "1");
			}
			updateAbilityScoreSubrow(parent, childPosition);
			updateTitlesAndButtons();
		}
	};
	
	private OnClickListener skillListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			View parent = (View) v.getParent();
			int position = expList.getPositionForView(parent);
			int childPosition = ExpandableListView.getPackedPositionChild(expList.getAdapter().getItemId(position));
			if (v.getId() == R.id.plus) {
				charEdit.skillRanks[childPosition] += 1;
				manager.newBonus(PropertyLists.skillNames[childPosition], "Base", "Natural", "1");
			} else {
				charEdit.skillRanks[childPosition] -= 1;
				manager.newBonus(PropertyLists.skillNames[childPosition], "Base", "Natural", "-1");
			}
//			updateSkill(parent, childPosition);
			TextView ranks = (TextView) parent.findViewById(R.id.score);
			if (ranks != null) {
				ranks.setText(Integer.toString(charEdit.skillRanks[childPosition]));
			}
			TextView final_score = (TextView) parent.findViewById(R.id.final_score);
			if (final_score != null) {
				String skill_modifier = Integer.toString(manager.getValue(PropertyLists.skillNames[childPosition]));
				final_score.setText(skill_modifier);
			}
		}
	};
}
