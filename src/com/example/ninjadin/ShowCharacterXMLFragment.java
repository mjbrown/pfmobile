package com.example.ninjadin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowCharacterXMLFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.char_show_xml_fragment, container, false);
		return view;
	}
	
	@Override
	public void onResume() {
		super .onResume();
		String filename = ((CharGenActivity) getActivity()).masterCharFilename;
		if (filename != null) {
			TextView view = ((TextView) ((CharGenActivity) getActivity()).findViewById(R.id.char_xml));
			if (view != null)
				view.setText(XMLtoString(filename));
		}
	}

	public String XMLtoString(String filename) {
		try {
			FileInputStream inputStream = ((CharGenActivity) getActivity()).openFileInput(filename);
			FileChannel fc = inputStream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
