package com.ninjadin.pfmobile.fragments;

import com.example.ninjadin.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CharGenMenuFragment extends Fragment {
	String message;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_mainmenu, container, false);
	}
	
	@Override
	public void onResume() {
		super .onResume();
	}
}
