package com.ninjadin.pfmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.non_android.CharacterData;

public class PointBuyFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_abilityscores, container, false);
		return view;
	}
	public void onResume() {
		super .onResume();
		updateStatsFragment();
	}
	public void onPause() {
		super .onPause();
		((GeneratorActivity) getActivity()).saveCharacterState();
	}
	public void statChange(View view) {
		int viewId = view.getId();
		CharacterData charData = ((GeneratorActivity) getActivity()).charData;
		if (viewId == R.id.str_plus) {
			charData.incrementAbilityScore(0);
		} else if (viewId == R.id.dex_plus) {
			charData.incrementAbilityScore(1);
		} else if (viewId == R.id.con_plus) {
			charData.incrementAbilityScore(2);
		} else if (viewId == R.id.int_plus) {
			charData.incrementAbilityScore(3);
		} else if (viewId == R.id.wis_plus) {
			charData.incrementAbilityScore(4);
		} else if (viewId == R.id.cha_plus) {
			charData.incrementAbilityScore(5);
		} else if (viewId == R.id.str_minus) {
			charData.decrementAbilityScore(0);
		} else if (viewId == R.id.dex_minus) {
			charData.decrementAbilityScore(1);
		} else if (viewId == R.id.con_minus) {
			charData.decrementAbilityScore(2);
		} else if (viewId == R.id.int_minus) {
			charData.decrementAbilityScore(3);
		} else if (viewId == R.id.wis_minus) {
			charData.decrementAbilityScore(4);
		} else if (viewId == R.id.cha_minus) {
			charData.decrementAbilityScore(5);
		}
		updateStatsFragment();
	}
	
	public void updateStatsFragment() {
		GeneratorActivity act = (GeneratorActivity) getActivity();
		TextView txt = (TextView) act.findViewById(R.id.num_str);
		if (txt != null) {
			txt.setText(Integer.toString(act.charData.getAbilityScore(0)));
			((TextView) act.findViewById(R.id.str_mod)).setText(act.charData.abilityScoreModifier(0));
			((Button) act.findViewById(R.id.str_minus)).setEnabled(act.charData.canDecrementAbilityScore(0));
			((Button) act.findViewById(R.id.str_plus)).setEnabled(act.charData.canIncrementAbilityScore(0));
			
			((TextView) act.findViewById(R.id.num_dex)).setText(Integer.toString(act.charData.getAbilityScore(1)));
			((TextView) act.findViewById(R.id.dex_mod)).setText(act.charData.abilityScoreModifier(1));
			((Button) act.findViewById(R.id.dex_minus)).setEnabled(act.charData.canDecrementAbilityScore(1));
			((Button) act.findViewById(R.id.dex_plus)).setEnabled(act.charData.canIncrementAbilityScore(1));
			
			((TextView) act.findViewById(R.id.num_con)).setText(Integer.toString(act.charData.getAbilityScore(2)));
			((TextView) act.findViewById(R.id.con_mod)).setText(act.charData.abilityScoreModifier(2));
			((Button) act.findViewById(R.id.con_minus)).setEnabled(act.charData.canDecrementAbilityScore(2));
			((Button) act.findViewById(R.id.con_plus)).setEnabled(act.charData.canIncrementAbilityScore(2));
			
			((TextView) act.findViewById(R.id.num_int)).setText(Integer.toString(act.charData.getAbilityScore(3)));
			((TextView) act.findViewById(R.id.int_mod)).setText(act.charData.abilityScoreModifier(3));
			((Button) act.findViewById(R.id.int_minus)).setEnabled(act.charData.canDecrementAbilityScore(3));
			((Button) act.findViewById(R.id.int_plus)).setEnabled(act.charData.canIncrementAbilityScore(3));
			
			((TextView) act.findViewById(R.id.num_wis)).setText(Integer.toString(act.charData.getAbilityScore(4)));
			((TextView) act.findViewById(R.id.wis_mod)).setText(act.charData.abilityScoreModifier(4));
			((Button) act.findViewById(R.id.wis_minus)).setEnabled(act.charData.canDecrementAbilityScore(4));
			((Button) act.findViewById(R.id.wis_plus)).setEnabled(act.charData.canIncrementAbilityScore(4));
			
			((TextView) act.findViewById(R.id.num_cha)).setText(Integer.toString(act.charData.getAbilityScore(5)));
			((TextView) act.findViewById(R.id.cha_mod)).setText(act.charData.abilityScoreModifier(5));
			((Button) act.findViewById(R.id.cha_minus)).setEnabled(act.charData.canDecrementAbilityScore(5));
			((Button) act.findViewById(R.id.cha_plus)).setEnabled(act.charData.canIncrementAbilityScore(5));
			
			((TextView) act.findViewById(R.id.points)).setText(Integer.toString(act.charData.pointBuyRemaining));
		}
	}
}
