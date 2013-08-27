package com.debian.debiandroid;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListDisplayFragment extends ItemDetailFragment {

	public static final String LIST_ITEMS_ID = "listItemsID",
							   LIST_HEADER_ID = "listHeaderID",
							   LIST_TITLE_ID = "listTitleID";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.list_display_item_detail, container, false);
  		
    	ArrayList<String> listItems = new ArrayList<String>();
    	if (getArguments().containsKey(LIST_ITEMS_ID)) {
    		listItems = getArguments().getStringArrayList(LIST_ITEMS_ID);
        }
    	if (getArguments().containsKey(LIST_TITLE_ID)) {
    		getSherlockActivity().getSupportActionBar().setTitle(getArguments().getString(LIST_TITLE_ID));
        }
    	if (getArguments().containsKey(LIST_HEADER_ID)) {
    		TextView header = (TextView) rootView.findViewById(R.id.listdisplaytextview);
    		header.setText(getArguments().getString(LIST_HEADER_ID));
        }
    	
    	ListView listview = (ListView) rootView.findViewById(R.id.listdisplaylistview);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
				android.R.layout.simple_list_item_1, new ArrayList<String>(listItems));
		
		listview.setAdapter(adapter);
		
        return rootView;
    }
}
