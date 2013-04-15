package com.example.ninjadin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CharGenMenuFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.char_gen_menu, container, false);
	}
	
	@Override
	public void onResume() {
		super .onResume();
		Bundle args = this.getArguments();
		String message = args.getString("message");
		if (message != null) {
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		}
	}
}
