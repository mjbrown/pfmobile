package com.example.ninjadin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class InfoFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.info_fragment, container, false);
		Spinner alignments_spinner = (Spinner) view.findViewById(R.id.alignments_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), 
				R.array.alignments_list, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		alignments_spinner.setAdapter(adapter);
		return view;
	}
	public void onResume() {
		super .onResume();
		updateInfo();
	}
	
	public void updateInfo() {
		CharGenActivity act = (CharGenActivity) getActivity();
		EditText txt = (EditText) act.findViewById(R.id.char_name);
		if (txt != null) {
			txt.setText(act.charData.info.characterName);
			((EditText) act.findViewById(R.id.deity_name)).setText(act.charData.info.deity, TextView.BufferType.EDITABLE);
			((Spinner) act.findViewById(R.id.alignments_spinner)).setSelection(act.charData.info.align);
			if (act.charData.info.gender.equals(CharacterInfo.MALE_VALUE)) {
				((RadioGroup) act.findViewById(R.id.gender_radio)).check(R.id.radio_male);
			} else if (act.charData.info.gender.equals(CharacterInfo.FEMALE_VALUE)) {
				((RadioGroup) act.findViewById(R.id.gender_radio)).check(R.id.radio_female);
			} else {
				((RadioGroup) act.findViewById(R.id.gender_radio)).check(R.id.radio_andro);
			}
			((TextView) act.findViewById(R.id.level_indicator)).setText("Level: " + 
					Integer.toString(act.charData.charLevel));
		}
		
	}
	public void onPause() {
		super .onPause();
		saveInfo();
	}
	
	public void saveInfo() {
		CharGenActivity act = (CharGenActivity) getActivity();
		EditText txt = (EditText) act.findViewById(R.id.char_name);
		if (txt != null) {
			act.charData.info.characterName = txt.getText().toString();
			act.charData.info.deity = ((EditText) act.findViewById(R.id.deity_name)).getText().toString();
			int gender = ((RadioGroup) act.findViewById(R.id.gender_radio)).getCheckedRadioButtonId();
			if (gender == R.id.radio_male) {
				act.charData.info.gender = CharacterInfo.MALE_VALUE;
			} else if (gender == R.id.radio_female) {
				act.charData.info.gender = CharacterInfo.FEMALE_VALUE;
			} else if (gender == R.id.radio_andro) {
				act.charData.info.gender = CharacterInfo.ANDRO_VALUE;
			}
			act.charData.info.align = ((Spinner) act.findViewById(R.id.alignments_spinner)).getSelectedItemPosition();
		}
		((CharGenActivity) getActivity()).saveCharacterState();
	}
}
