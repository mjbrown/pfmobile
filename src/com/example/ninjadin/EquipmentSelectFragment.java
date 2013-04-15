package com.example.ninjadin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class EquipmentSelectFragment extends Fragment {
	FilterSelect equipFilter;
	ExpandableListView expList;
	int choiceId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
		View view = inflater.inflate(R.layout.filter_select_fragment, container, false);
		return view;
	}
}
