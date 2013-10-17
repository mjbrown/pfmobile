package com.ninjadin.pfmobile.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ninjadin.pfmobile.R;
import com.ninjadin.pfmobile.activities.GeneratorActivity;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.EffectXmlObject;
import com.ninjadin.pfmobile.non_android.XmlObjectModel;

public class EffectsFragment extends Fragment {
	ListView list_view;
	public interface EffectsFragmentListener {
		public XmlObjectModel getXmlModel(int id);
		public void removeEffect(String name);
	}
	
	EffectsFragmentListener effectsListener;
	
	public void onAttach(Activity activity) {
		super .onAttach(activity);
		try {
			effectsListener = (EffectsFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement EffectsFragmentListener!");
		}
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		
		list_view = (ListView) view.findViewById(R.id.listView1);
		return view;
	}
	
	@Override 
	public void onResume() {
		super .onResume();
		dataSetChanged();
	}
	
	private void dataSetChanged() {
		XmlObjectModel effects = effectsListener.getXmlModel(GeneratorActivity.EFFECTS_MODEL);
		List<Map<String,String>> effect_list = new ArrayList<Map<String,String>>();
		for (XmlObjectModel effect: effects.getChildren()) {
			if (effect.getTag().equals(XmlConst.EFFECT_TAG)) {
				effect_list.add(effect.getAttributes());
			}
		}
		EffectsAdapter adapter = new EffectsAdapter(getActivity(), R.layout.titlerow_effects, R.id.effect_name, effect_list);
		list_view.setAdapter(adapter);
	}
	
	class EffectsAdapter extends ArrayAdapter<Map<String,String>> {
		Context context;
		List<Map<String,String>> groupData;
		
		public EffectsAdapter(Context context, int layout, int resource, List<Map<String, String>> objects) {
			super(context, layout, resource, objects);
			this.context = context;
			groupData = objects;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = super .getView(position, convertView, parent);

			TextView name = (TextView) convertView.findViewById(R.id.effect_name);
			LinearLayout extra = (LinearLayout) convertView.findViewById(R.id.effect_duration_block);
			TextView source = (TextView) convertView.findViewById(R.id.effect_source);
			TextView duration = (TextView) convertView.findViewById(R.id.effect_duration);
			TextView elapsed = (TextView) convertView.findViewById(R.id.effect_elapsed);
			String effect_name = groupData.get(position).get(XmlConst.NAME_ATTR);
			String effect_source = groupData.get(position).get(XmlConst.SOURCE_ATTR);
			String effect_duration = groupData.get(position).get(EffectXmlObject.DURATION_ATTR);
			String effect_elapsed = groupData.get(position).get(EffectXmlObject.ELAPSED_ATTR);
			name.setText(effect_name);
			if (effect_source != null)
				source.setText(effect_source);
			if (effect_duration != null) {
				duration.setText(effect_duration);
				elapsed.setText(effect_elapsed);
				extra.setVisibility(View.VISIBLE);
			} else
				extra.setVisibility(View.GONE);

			Button remove = (Button) convertView.findViewById(R.id.effect_remove);
			remove.setOnClickListener(new ListButtonClickListener(effect_name) {

				@Override
				public void onClick(View v) {
					effectsListener.removeEffect(name);
					dataSetChanged();
				}
				
			});
			return convertView;
		}
		
		private abstract class ListButtonClickListener implements OnClickListener {
			String name;
			public ListButtonClickListener(String name) {
				super ();
				this.name = name;
			}
		}
	}

}
