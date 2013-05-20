package com.ninjadin.pfmobile.fragments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
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
import com.ninjadin.pfmobile.data.ExpListData;
import com.ninjadin.pfmobile.data.XmlConst;
import com.ninjadin.pfmobile.non_android.XmlExtractor;

public class TemplateSelectFragment extends Fragment {
	ExpandableListView expList;
	String templateType;
	String itemName;
	List<Map<String, String>> groupData;
	List<List<Map<String,String>>> itemData;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_filterselect, container, false);
		return view;
	}
	
	public void onResume() {
		super .onResume();
		GeneratorActivity activity = (GeneratorActivity) getActivity();
		Bundle args = this.getArguments();
		templateType = args.getString("selection type");
		XmlExtractor data = null;
		if (templateType.equals(XmlConst.ENHANCE_TAG)) {
			itemName = args.getString(XmlConst.NAME_ATTR);
			InputStream inStream = (InputStream) getResources().openRawResource(R.raw.enchantments);
			try {
				data = ExpListData.initEnchantTemplates(inStream);
				inStream.close();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			InputStream templateStream = this.getResources().openRawResource(R.raw.equipment);
			try {
				data = ExpListData.initItemTemplates(templateStream);
				templateStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		groupData = data.groupData;
		itemData = data.itemData;
		expList = (ExpandableListView) activity.findViewById(R.id.filter_exp_listview);
		ExpandableListAdapter simpleExpAdapter = new TemplateSelectSimpleExpandableListAdapter(
				activity,
				groupData,
				R.layout.titlerow_filterselect,
				new String[] { XmlConst.NAME_ATTR },
				new int[] { android.R.id.text1 },
				itemData,
				R.layout.subrow_filterselect,
				new String[] { XmlConst.NAME_ATTR, XmlConst.SLOT_ATTR },
				new int[] {R.id.filterselect_text, R.id.filterselect_text2 } );
		expList.setAdapter(simpleExpAdapter);
	}
	class TemplateSelectSimpleExpandableListAdapter extends SimpleExpandableListAdapter {
		Context mContext;
		List<Map<String,String>> grpData;
		
		public TemplateSelectSimpleExpandableListAdapter(Context context, List<Map<String, String>> groupData,
				int groupLayout, String[] groupFrom, int[] groupTo, List<List<Map<String,String>>> childData, int childLayout,
				String[] childFrom, int[] childTo) {
			super (context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
			mContext = context;
			grpData = groupData;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.titlerow_filterselect, null);
				Button addButton = (Button)convertView.findViewById(R.id.filtertitle_add);
				if (addButton != null)
				addButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						int groupPos = expList.getPositionForView((View) view.getParent());
						String templateName = grpData.get(groupPos).get(XmlConst.NAME_ATTR);
						if (templateType.equals(XmlConst.ENHANCE_TAG)) {
							((GeneratorActivity) getActivity()).addTemplate(templateName, templateType, itemName);
						} else {
							((GeneratorActivity) getActivity()).addTemplate(templateName, templateType, itemName);
						}
					}
				});
			}
			TextView textView = (TextView)convertView.findViewById(R.id.filtertitle_text);
			if (textView != null)
				textView.setText(grpData.get(groupPosition).get(XmlConst.NAME_ATTR));
			return convertView;
		}
	}
}
