package com.debian.debiandroid;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.debian.debiandroid.apiLayer.BTS;
import com.debian.debiandroid.apiLayer.SearchCacher;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import androidStorageUtils.StorageUtils;

public class BTSFragment extends ItemDetailFragment {
	
	private Spinner spinner;
	private ImageButton searchButton;
	private String searchOptionSelected;
	private EditText btsInput;
	
	private BTS bts;
	private Context context;
		
	/** ID for the (un)subscribe menu item. It starts from +2 
	 * because the settings icon is in the +1 position */
	public static final int SUBSCRIPTION_ID = Menu.FIRST+2;
	public static final int REFRESH_ID = Menu.FIRST+3;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getSherlockActivity().getApplicationContext();
        bts = new BTS(context);
        if(SearchCacher.hasLastSearch()) {
        	//TODO fill all UI elements with values from last search
        }
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.bts_item_detail, container, false);	
        
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.select_bugs));
    	
        searchOptionSelected = StorageUtils.getInstance(context).getPreference("btsSearchOption");
        
        // Find the Views once in OnCreate to save time and not use findViewById later.
  		spinner = (Spinner) rootView.findViewById(R.id.btsSpinner);
  		setupSpinner();
  		
  		searchButton = (ImageButton) rootView.findViewById(R.id.btsSearchButton);
  		searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                	//DO SEARCH USING APILAYER, DISPLAY RESULTS
            }
        });
  		
  		btsInput = (EditText) rootView.findViewById(R.id.btsInputSearch);
  		btsInput.setOnEditorActionListener(new OnEditorActionListener() {
  		    @Override
  		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
  		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
		String[] values = { getString(R.string.by_number), 
							getString(R.string.in_package), 
							getString(R.string.in_pckgs_maint_by), 
							getString(R.string.submitted_by), 
							getString(R.string.with_status), 
							getString(R.string.with_mail_from) }; 
		
		spinner.setAdapter(new ArrayAdapter<String>(this.getActivity(), 
        				android.R.layout.simple_spinner_item, values));
        
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
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//Add subscription icon
		MenuItem subMenuItem = menu.add(0, SUBSCRIPTION_ID, Menu.CATEGORY_SECONDARY, "(Un)Subscribe");
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		if(bts.isSubscribedTo("testBugNumber")) {
			subMenuItem.setIcon(R.drawable.subscribed);
			subMenuItem.setTitle(getString(R.string.unsubscribe));
		} else {
			subMenuItem.setIcon(R.drawable.unsubscribed);
			subMenuItem.setTitle(getString(R.string.subscribe));
		}
		
		menu.add(0, REFRESH_ID, Menu.CATEGORY_ALTERNATIVE, getString(R.string.refresh))
				.setIcon(R.drawable.refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    	 switch(item.getItemId()){
		    	 case SUBSCRIPTION_ID:
			    		if(bts.isSubscribedTo("testBugNumber")) {
			    			item.setIcon(R.drawable.unsubscribed);
			    			item.setTitle(getString(R.string.subscribe));
			    			bts.removeSubscriptionTo("testBugNumber");
			    		} else {
			    			item.setIcon(R.drawable.subscribed);
			    			item.setTitle(getString(R.string.unsubscribe));
			    			bts.addSubscriptionTo("testBugNumber");
			    		}
			    		return true;
		    	 case REFRESH_ID:
			    		// Do a refresh if there was a search
			    		return true;
	        }
		return super.onOptionsItemSelected(item);
    }
}
