package com.debian.debiandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UDDFragment extends ItemDetailFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.udd_item_detail, container, false);
  		
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.udd));
    	
        return rootView;
    }
	
}
