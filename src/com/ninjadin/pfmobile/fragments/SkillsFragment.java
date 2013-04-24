package com.ninjadin.pfmobile.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.PropertyLists;

public class SkillsFragment extends Fragment {
	private ListView skillListView;
	private SkillListAdapter<String> listAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_skills, container, false);
		return view;
	}
	
	@Override
	public void onResume() {
		super .onResume();
		refreshSkills();
	}
	
	public void refreshSkills() {
		skillListView = (ListView) this.getActivity().findViewById(R.id.skills_list);
		Map<String, List<String>> skillList = getSkillRanks();
		listAdapter = new SkillListAdapter<String>(this.getActivity(), R.layout.row_skills, R.id.skillrow_text, skillList);
		if ((skillListView != null) && (listAdapter != null))
			skillListView.setAdapter(listAdapter);
	}
	
	private Map<String, List<String>> getSkillRanks() {
		Map<String, List<String>> skillMap = new HashMap<String, List<String>>();
		List<String> valueList = new ArrayList<String>();
		List<String> skillList = new ArrayList<String>();
		skillMap.put("NAME", skillList);
		skillMap.put("VALUE", valueList);
		skillList.addAll(Arrays.asList(PropertyLists.skillNames));
		GeneratorActivity activity = (GeneratorActivity) this.getActivity();
		for (int skill: activity.charData.skillRanks) {
			valueList.add(Integer.toString(skill));
		}
		return skillMap;
	}
	
	private class SkillListAdapter<String> extends ArrayAdapter<String> {
		List<String> valueList;
		List<String> skillList;
		Context mContext;
		
		public SkillListAdapter(Context context, int rowLayoutResId, int textViewResourceId, Map<String, List<String>> skillMap) {
			super(context, rowLayoutResId, textViewResourceId, skillMap.get("NAME"));
			valueList = skillMap.get("VALUE");
			skillList = skillMap.get("NAME");
			mContext = context;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_skills, null);
			}
			TextView skillName = (TextView) v.findViewById(R.id.skillrow_text);
			TextView skillValue = (TextView) v.findViewById(R.id.skillrow_value);
			if ((skillName != null) && (skillValue != null)) {
				skillName.setText(skillList.get(position).toString());
				skillValue.setText(valueList.get(position).toString());
			}
			Button increment = (Button) v.findViewById(R.id.skillrow_plus);
			Button decrement = (Button) v.findViewById(R.id.skillrow_minus);
			if ((increment != null) && (decrement != null)) {
				increment.setOnClickListener(incrementListener);
				decrement.setOnClickListener(decrementListener);
			}
			return v;
		}
		
		private OnClickListener incrementListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = skillListView.getPositionForView((View) v.getParent());
				GeneratorActivity activity = (GeneratorActivity) getActivity();
				activity.charData.skillRanks[position] += 1;
				refreshSkills();
			}
		};

		private OnClickListener decrementListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = skillListView.getPositionForView((View) v.getParent());
				GeneratorActivity activity = (GeneratorActivity) getActivity();
				activity.charData.skillRanks[position] -= 1;
				refreshSkills();
			}
		};
	}
}
