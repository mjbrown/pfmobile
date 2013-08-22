package com.ninjadin.pfmobile.fragments;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
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

public class CharacterSheetFragment extends Fragment {
	ExpandableListView expList;
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
		charEdit = activity.characterEditor;
		groupData = PropertyLists.categoryData();
		itemData = PropertyLists.statisticData();
		expList = (ExpandableListView) activity.findViewById(R.id.expandableListView1);
		ExpandableListAdapter adapter = new CharacterSheetAdapter(
				activity,
				groupData,
				R.layout.titlerow_charactersheet,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { R.id.titlerow_text },
				itemData,
				R.layout.subrow_skills_scores,
				new String[] { XmlConst.NAME_ATTR},
				new int[] {R.id.score_name} );
		expList.setAdapter(adapter);
	}
	
	@Override
	public void onPause() {
		super .onPause();
		((GeneratorActivity) getActivity()).saveCharacterState();
	}
	
	private class CharacterSheetAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		
		public CharacterSheetAdapter(Context context,
				List<? extends Map<String, ?>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo, childData,
					childLayout, childFrom, childTo);
			mContext = context;
			// TODO Auto-generated constructor stub
		}
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			String category = groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.titlerow_charactersheet, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.titlerow_text);
			if (title != null)
				title.setText(category);
			TextView point_title = (TextView) convertView.findViewById(R.id.points_left);
			TextView points_left = (TextView) convertView.findViewById(R.id.number_points_left);
			if (points_left != null) {
				if (category.equals("Ability Scores")) { 
					points_left.setText(Integer.toString(charEdit.pointBuyRemaining) + " out of 20");
					points_left.setTextColor(Color.BLACK);
					point_title.setText("Points Remaining:");
				} else if (category.equals("Skills")) {
					String ranks_used = Integer.toString(charEdit.totalSkillRanks());
					String ranks_available = Integer.toString(manager.getValue("Skill Points"));
					points_left.setText(ranks_used + " / " + ranks_available);
					if (charEdit.totalSkillRanks() > manager.getValue("Skill Points"))
						points_left.setTextColor(Color.RED);
					else
						points_left.setTextColor(Color.BLACK);
					point_title.setText("Points Used:");
				} else if (category.equals("Points")) {
					int used = charEdit.favoredclass_points[0] + charEdit.favoredclass_points[1];
					String points_used = Integer.toString(used);
					int available = manager.getValue(PropertyLists.favored_points);
					String points_available = Integer.toString(available);
					points_left.setText(points_used + " / " + points_available);
					if (used > available)
						points_left.setTextColor(Color.RED);
					else
						points_left.setTextColor(Color.BLACK);
					point_title.setText("Favored Class:");
				} else {
					points_left.setText("");
					point_title.setText("");
				}
			}
			return convertView;
		}
		public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			String category = groupData.get(groupPosition).get(XmlConst.NAME_ATTR);
			String name = itemData.get(groupPosition).get(childPosition).get(XmlConst.NAME_ATTR);
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if ((category.equals("Ability Scores")) || (category.equals("Skills")) || (category.equals("Points"))) {
				convertView = inflater.inflate(R.layout.subrow_skills_scores, null);
				if (category.equals("Ability Scores")) {
					updateAbilityScoreSubrow(convertView, childPosition);
				} else if (category.equals("Skills")) {
					updateSkillSubrow(convertView, childPosition);
				} else if (category.equals("Points")) {
					updatePointSubrow(convertView, childPosition);
				}
			} else {
				convertView = inflater.inflate( R.layout.subrow_charsheet, null);
				TextView tx_modifier = (TextView) convertView.findViewById(R.id.score_modifier);
				tx_modifier.setText(Integer.toString(manager.getValue(name)));
			}
			TextView tx_name = (TextView) convertView.findViewById(R.id.score_name);
			if (tx_name != null)
				tx_name.setText(name);
			return convertView;
		}

		private void updatePointSubrow(View parent, int childPosition) {
			TextView base_score = (TextView) parent.findViewById(R.id.score);
			if (base_score != null) {
				base_score.setText(Integer.toString(charEdit.favoredclass_points[childPosition]));
			}
			String abilityName = PropertyLists.pointsNames[childPosition];
			TextView final_score = (TextView) parent.findViewById(R.id.final_score);
			if (final_score != null) {
				final_score.setText(Integer.toString(manager.getValue(abilityName)));
			}
			TextView tx_modifier = (TextView) parent.findViewById(R.id.score_modifier);
			tx_modifier.setText("");
			Button minus = (Button) parent.findViewById(R.id.minus);
			if (minus != null) {
				minus.setOnClickListener(pointListener);
				minus.setEnabled(charEdit.favoredclass_points[childPosition] > 0);
			}
			Button plus = (Button) parent.findViewById(R.id.plus);
			if (plus != null) {
				plus.setOnClickListener(pointListener);
				int available = manager.getValue(PropertyLists.favored_points);
				plus.setEnabled(available > (charEdit.favoredclass_points[0] + charEdit.favoredclass_points[1]));
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
				tx_modifier.setText(Integer.toString(modifier));
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
		private void updateSkillSubrow(View parent, int childPosition) {
			TextView tx_final = (TextView) parent.findViewById(R.id.final_score);
			if (tx_final != null)
				tx_final.setText("");
			TextView tx_final_label = (TextView) parent.findViewById(R.id.final_label);
			if (tx_final_label != null)
				tx_final_label.setText("");
			String ranks = Integer.toString(charEdit.skillRanks[childPosition]);
			String skill_name = PropertyLists.skillNames[childPosition];
			TextView tx_value = (TextView) parent.findViewById(R.id.score);
			if (tx_value != null)
				tx_value.setText(ranks);
			Button minus = (Button) parent.findViewById(R.id.minus);
			if (minus != null) {
				minus.setOnClickListener(skillListener);
				minus.setEnabled(charEdit.canSkillDown(childPosition));
			}
			Button plus = (Button) parent.findViewById(R.id.plus);
			if (plus != null) {
				plus.setOnClickListener(skillListener);
				plus.setEnabled(charEdit.canSkillUp(childPosition));
			}
			TextView tx_modifier = (TextView) parent.findViewById(R.id.score_modifier);
			Integer modifier = manager.getValue(skill_name);
			if (modifier < 0) {
				tx_modifier.setText(Integer.toString(modifier));
			} else {
				tx_modifier.setText("+" + Integer.toString(modifier));
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
				expList.invalidateViews();
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
					manager.newBonus(PropertyLists.skillNames[childPosition], "Ranks", "Natural", "1");
				} else {
					charEdit.skillRanks[childPosition] -= 1;
					manager.newBonus(PropertyLists.skillNames[childPosition], "Ranks", "Natural", "-1");
				}
				expList.invalidateViews();
			}
		};
		
		private OnClickListener pointListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				View parent = (View) v.getParent();
				int position = expList.getPositionForView(parent);
				int childPosition = ExpandableListView.getPackedPositionChild(expList.getAdapter().getItemId(position));
				if (v.getId() == R.id.plus) {
					charEdit.favoredclass_points[childPosition] += 1;
					manager.newBonus(PropertyLists.pointsNames[childPosition], "Ranks", "Natural", "1");
				} else {
					charEdit.favoredclass_points[childPosition] -= 1;
					manager.newBonus(PropertyLists.pointsNames[childPosition], "Ranks", "Natural", "-1");
				}
				expList.invalidateViews();
			}
		};
		

	}
	
	
}
