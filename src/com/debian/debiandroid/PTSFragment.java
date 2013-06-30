package com.debian.debiandroid;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class PTSFragment extends ItemDetailFragment {

	private ImageButton searchButton;
	private EditText ptsInput;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.pts_item_detail, container, false);
  		
    	searchButton = (ImageButton) rootView.findViewById(R.id.ptsSearchButton);
  		searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                	//DO SEARCH USING APILAYER, DISPLAY RESULTS
            }
        });
  		
  		ptsInput = (EditText) rootView.findViewById(R.id.ptsInputSearch);
  		ptsInput.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		        				(keyCode == KeyEvent.KEYCODE_ENTER)) {
		        	//DO SEARCH USING APILAYER, DISPLAY RESULTS
		          return true;
		        }
				return false;
			}
		});
    	
        return rootView;
    }
	
}
