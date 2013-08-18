package com.debian.debiandroid;

import java.util.ArrayList;
import java.util.HashMap;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.debian.debiandroid.apiLayer.BTS;
import com.debian.debiandroid.apiLayer.SearchCacher;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView.OnEditorActionListener;
import androidStorageUtils.StorageUtils;

public class BTSFragment extends ItemDetailFragment {
	
	private Spinner spinner;
	private ImageButton searchButton;
	private String searchOptionSelected;
	private EditText btsInput;
	private ExpandableListView bugList;
	
	private BTS bts;
	private Context context;
	
	private ArrayList<String> bugListParentItems;
	private ArrayList<Object> bugListChildItems;
	
	private String[] spinnerValues; 
		
	/** ID for the (un)subscribe menu item. It starts from +2 
	 * because the settings icon is in the +1 position */
	public static final int SUBSCRIPTION_ID = Menu.FIRST+2;
	public static final int REFRESH_ID = Menu.FIRST+3;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getSherlockActivity().getApplicationContext();
        bts = new BTS(context);
        if(SearchCacher.hasAnyLastSearch()) {
        	new SearchBugInfoTask().execute();
        }
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.bts_item_detail, container, false);	
        
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.select_bugs));
    	
        searchOptionSelected = StorageUtils.getInstance(context).getPreference("btsSearchOption");
        bugList = (ExpandableListView) rootView.findViewById(R.id.btsList);
        
        // Find the Views once in OnCreate to save time and not use findViewById later.
  		spinner = (Spinner) rootView.findViewById(R.id.btsSpinner);
  		spinnerValues = new String[]{ getString(R.string.by_number), 
  				getString(R.string.in_package), 
  				getString(R.string.in_pckgs_maint_by), 
  				getString(R.string.submitted_by), 
  				getString(R.string.with_status) }; 
  		setupSpinner();
  		
  		searchButton = (ImageButton) rootView.findViewById(R.id.btsSearchButton);
  		searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            		SearchCacher.setLastBugSearch(
            				optionSelectedToBTSParam(searchOptionSelected), 
            				btsInput.getText().toString());      	
                	new SearchBugInfoTask().execute();
            }
        });
  		
  		btsInput = (EditText) rootView.findViewById(R.id.btsInputSearch);
  		btsInput.setOnEditorActionListener(new OnEditorActionListener() {
  		    @Override
  		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
  		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
  		        	SearchCacher.setLastBugSearch(
  		        			optionSelectedToBTSParam(searchOptionSelected), 
  		        			btsInput.getText().toString());
                	new SearchBugInfoTask().execute();
  		            return true;
  		        }
  		        return false;
  		    }
  		});
  		
        return rootView;
    }
	
	/** Initializes the spinner view and fills it with pts search choices */
	private void setupSpinner() {
		spinner.setAdapter(new ArrayAdapter<String>(this.getActivity(), 
        				android.R.layout.simple_spinner_item, spinnerValues));
        
		spinner.setSelection(getSelectedOption(spinnerValues, searchOptionSelected));
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        	searchOptionSelected = (String) parent.getItemAtPosition(pos);
		        	//Save change in preferences with storageutils
		        	StorageUtils.getInstance(context).savePreference("btsSearchOption", searchOptionSelected);
		        } 
		        public void onNothingSelected(AdapterView<?> arg0) {} 
        });
	}
	
	private String optionSelectedToBTSParam(String option) {
		if(searchOptionSelected.equals(getString(R.string.in_package)))
			return BTS.PACKAGE;
		else if(searchOptionSelected.equals(getString(R.string.in_pckgs_maint_by)))
			return BTS.MAINT;
		else if(searchOptionSelected.equals(getString(R.string.submitted_by)))
			return BTS.SUBMITTER;
		else if(searchOptionSelected.equals(getString(R.string.with_status)))
			return BTS.STATUS;
		else if(searchOptionSelected.equals(getString(R.string.by_number)))
			return BTS.BUGNUMBER;
		return "";
	}
	
	private String BTSParamToSpinnerOption(String param) {
		if(param.equals(BTS.PACKAGE))
			return getString(R.string.in_package);
		else if(param.equals(BTS.MAINT))
			return getString(R.string.in_pckgs_maint_by);
		else if(param.equals(BTS.SUBMITTER))
			return getString(R.string.submitted_by);
		else if(param.equals(BTS.STATUS))
			return getString(R.string.with_status);
		else if(param.equals(BTS.BUGNUMBER))
			return getString(R.string.by_number);
		return "";
	}
	
	public void setupBugsList() {    	
		bugList.setDividerHeight(1);
		bugList.setClickable(true);
    	
    	final DExpandableAdapter adapter = new DExpandableAdapter(bugListParentItems, bugListChildItems);
    	adapter.setInflater((LayoutInflater) getSherlockActivity()
    						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    	bugList.setAdapter(adapter);
    	registerForContextMenu(bugList);
    	
    	bugList.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View view,
                    int groupPosition, int childPosition, long id) {
            	//String itemClicked = ((TextView)view).getText().toString();
                
                return true;
            }
        });
	}
	
	public void setBugData() {
		bugListParentItems = new ArrayList<String>();
		bugListChildItems = new ArrayList<Object>();
				
		ArrayList<String> bugNums = new ArrayList<String>();
		//Do search and fill bug results table
		if(SearchCacher.hasLastBugsSearch()) {
			if(SearchCacher.getLastBugSearchOption().equals(BTS.BUGNUMBER))
				bugNums.add(SearchCacher.getLastBugSearchValue());
			else
				bugNums = bts.getBugs(new String[]{SearchCacher.getLastBugSearchOption()}, 
						new String[]{SearchCacher.getLastBugSearchValue()});
        } else if(SearchCacher.hasLastPckgSearch()) {
        	bugNums = bts.getBugs(new String[]{BTS.PACKAGE}, new String[]{SearchCacher.getLastPckgName()});
        }
		for(int i=0; i<bugNums.size(); i++) {
			try {
				//build array with mail log
				ArrayList<HashMap<String,String>> mailLog = bts.getBugLog(Integer.parseInt(bugNums.get(i).trim()));
				int mailLogSize = mailLog.size();
				ArrayList<String> log = new ArrayList<String>(mailLogSize);
				
				// Shows bugs in the format [bugNumber](mails sent for that bugnum) Subject of first mail
				bugListParentItems.add("["+bugNums.get(i)+"]("+mailLogSize+")" 
								+ ((mailLogSize>0)?mailLog.get(0).get("subject"):"") );
				
				for(HashMap<String,String> mail: mailLog) {
					StringBuilder m = new StringBuilder();
					m.append("Date: ").append(mail.get("date")).append("\n")
					//.append("Subject: ").append(mail.get("subject")).append("\n")
					.append("From: ").append(mail.get("from")).append("\n")
					//.append("CC: ").append(mail.get("cc"))
					.append("\n-------------\nBody: ").append(mail.get("body"));
					
					log.add(m.toString());
				}
			    bugListChildItems.add(log);
			 } catch(NumberFormatException e) { e.printStackTrace(); }
		}
	}
	
	/** Returns the position of the selected language in the given array
	 * @param values
	 * @return an int which is the position of the language in the values or 0 if it is not found
	 */
	private int getSelectedOption(String[] values, String selection) {
		for(int i=0; i<values.length; i++){
				if(values[i].equalsIgnoreCase(selection))
					return i;
		}
		return 0;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//Add subscription icon
		MenuItem subMenuItem = menu.add(0, SUBSCRIPTION_ID, Menu.CATEGORY_SECONDARY, "(Un)Subscribe");
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		setSubscriptionIcon(subMenuItem);
		
		menu.add(0, REFRESH_ID, Menu.CATEGORY_ALTERNATIVE, getString(R.string.refresh))
				.setIcon(R.drawable.refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		setSubscriptionIcon(menu.findItem(SUBSCRIPTION_ID));
		super.onPrepareOptionsMenu(menu);
	}
	
	public void setSubscriptionIcon(MenuItem subMenuItem) {
		String subscription = 
		 		SearchCacher.getLastBugSearchOption() + "|" + SearchCacher.getLastBugSearchValue();
		if(SearchCacher.hasLastBugsSearch() && bts.isSubscribedTo(subscription)) {
			subMenuItem.setIcon(R.drawable.subscribed);
			subMenuItem.setTitle(getString(R.string.unsubscribe));
		} else {
			subMenuItem.setIcon(R.drawable.unsubscribed);
			subMenuItem.setTitle(getString(R.string.subscribe));
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    	 switch(item.getItemId()){
		    	 case SUBSCRIPTION_ID:
		    		 	String subscription = 
		    		 		SearchCacher.getLastBugSearchOption() + "|" + SearchCacher.getLastBugSearchValue();
			    		if(bts.isSubscribedTo(subscription)) {
			    			item.setIcon(R.drawable.unsubscribed);
			    			item.setTitle(getString(R.string.subscribe));
			    			bts.removeSubscriptionTo(subscription);
			    		} else {
			    			item.setIcon(R.drawable.subscribed);
			    			item.setTitle(getString(R.string.unsubscribe));
			    			bts.addSubscriptionTo(subscription);
			    		}
			    		return true;
		    	 case REFRESH_ID:
		    		 	if(SearchCacher.hasAnyLastSearch()) {
		    		 		new SearchBugInfoTask().execute();
		    		 	}
			    		return true;
	        }
		return super.onOptionsItemSelected(item);
    }
	
	class SearchBugInfoTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;
		protected void onPreExecute(){ 
		   super.onPreExecute();
		   progressDialog = ProgressDialog.show(getSherlockActivity(), 
				   getString(R.string.searching), getString(R.string.searching_info) + ". " + getString(R.string.please_wait) + "...", true, false);  
		}
		
		protected Void doInBackground(Void... params) {		
			// search and set bug data
			setBugData();
			return null;
		}  
		@SuppressLint("NewApi")
		protected void onPostExecute (Void result) {
			progressDialog.dismiss();
			
	    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    		getSherlockActivity().invalidateOptionsMenu();
			}
	    	btsInput.setText(SearchCacher.getLastBugSearchValue());
	    	spinner.setSelection(getSelectedOption(spinnerValues, 
	    			BTSParamToSpinnerOption(SearchCacher.getLastBugSearchOption())));
	    	setupBugsList();
	    }
    }
}
