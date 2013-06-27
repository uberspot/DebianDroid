package com.debian.debiandroid;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidStorageUtils.StorageUtils;

public class BTSFragment extends ItemDetailFragment {
	
	private Spinner spinner;
	private Button searchButton;
	private String searchOptionSelected;
	private Context context;
	private EditText btsInput;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getSherlockActivity().getApplicationContext();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.bts_item_detail, container, false);	
        
        searchOptionSelected = StorageUtils.getInstance(context).getPreference("btsSearchOption");
        
        // Find the Views once in OnCreate to save time and not use findViewById later.
  		spinner = (Spinner) rootView.findViewById(R.id.btsSpinner);
  		setupSpinner();
  		
  		searchButton = (Button) rootView.findViewById(R.id.btsSearchButton);
  		searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                	//DO SEARCH USING APILAYER, DISPLAY RESULTS
            }
        });
  		
  		btsInput = (EditText) rootView.findViewById(R.id.btsInputSearch);
  		btsInput.setOnKeyListener(new View.OnKeyListener() {
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
	
	/** Initializes the spinner view and fills it with pts search choices */
	private void setupSpinner() {		
		String[] values = {"by number", "in package", "in pckgs maintained by", "submitted by", "with status", "with mail from"}; 
		
		spinner.setAdapter(new ArrayAdapter<String>(this.getActivity(), 
        				android.R.layout.simple_spinner_dropdown_item, values));
        
		spinner.setSelection(getSelectedOption(values));
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        	searchOptionSelected = (String) parent.getItemAtPosition(pos);
		        	//Save change in preferences with storageutils
		        	StorageUtils.getInstance(context).savePreference("btsSearchOption", searchOptionSelected);
		        } 
		        public void onNothingSelected(AdapterView<?> arg0) {} 
        });
	}
	
	/** Returns the position of the selected language in the given array
	 * @param values
	 * @return an int which is the position of the language in the values or 0 if it is not found
	 */
	private int getSelectedOption(String[] values) {
		int i=0;
		for(; i<values.length; i++){
				if(values[i].equalsIgnoreCase(searchOptionSelected))
					return i;
		}
		return 0;
	}
}
