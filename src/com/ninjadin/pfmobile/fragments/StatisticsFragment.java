package com.ninjadin.pfmobile.fragments;

import com.example.ninjadin.R;
import com.ninjadin.pfmobile.activities.CharGenActivity;
import com.ninjadin.pfmobile.non_android.DependencyManager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticsFragment extends Fragment {
	private ListView statisticsListView;
	private StatisticsListAdapter listAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_statistics, container, false);
		return view;
	}
	
	@Override
	public void onResume() {
		super .onResume();
		refreshStatistics();
	}

	public void refreshStatistics() {
		CharGenActivity activity = (CharGenActivity) getActivity();
		statisticsListView = (ListView) activity.findViewById(R.id.statistics_list);
		DependencyManager depends = activity.dependencyManager;
		listAdapter = new StatisticsListAdapter(activity, R.layout.row_statistics, R.id.statisticsrow_value, depends);
		if ((statisticsListView != null) && (listAdapter != null))
			statisticsListView.setAdapter(listAdapter);
	}
	
	private class StatisticsListAdapter extends ArrayAdapter<String> {
		DependencyManager depends;
		Context mContext;
		public StatisticsListAdapter(Context context, int rowLayoutResId, int textViewResourceId, DependencyManager dep) {
			super(context, rowLayoutResId, textViewResourceId, dep.masterList);
			depends = dep;
			mContext = context;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_statistics, null);
			}
			TextView statisticName = (TextView) v.findViewById(R.id.statisticsrow_text);
			TextView statisticValue = (TextView) v.findViewById(R.id.statisticsrow_value);
			if ((statisticName != null) && (statisticValue != null)) {
				int val = depends.getValue(depends.masterList.get(position));
				statisticName.setText(depends.masterList.get(position).toString());
				statisticValue.setText(Integer.toString(val));
			}
			return v;
		}
	}
}
