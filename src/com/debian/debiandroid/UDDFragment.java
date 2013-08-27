package com.debian.debiandroid;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UDDFragment extends ItemDetailFragment {
	
	private static final ArrayList<String> uddScripts = 
			new ArrayList<String>(Arrays.asList("rcbugs", "latest uploads", "new maintainers"));
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.udd_item_detail, container, false);
  		
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.udd));
    	
    	ListView listview = (ListView) rootView.findViewById(R.id.uddlistview);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
				R.layout.listchild, new ArrayList<String>(uddScripts));
		
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				// start listdisplayfragment with the udd results as ArrayList<String> arguments
			}
		});
		
        return rootView;
    }
}
