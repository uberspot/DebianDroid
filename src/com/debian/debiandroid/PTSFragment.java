package com.debian.debiandroid;

import java.util.Arrays;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.debian.debiandroid.apiLayer.PTS;
import com.debian.debiandroid.apiLayer.SearchCacher;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class PTSFragment extends ItemDetailFragment {

	private ImageButton searchButton;
	private EditText ptsInput;
	private PTS pts;
	private TextView ptsPckgName, ptsPckgLatestVersion, 
					ptsPckgMaintainerInfo, ptsPckgBugCount, 
					ptsPckgUplNames, ptsPckgBinNames;
	
	/** ID for the (un)subscribe menu item. It starts from +2 
	 * because the settings icon is in the +1 position */
	public static final int SUBSCRIPTION_ID = Menu.FIRST+2;
	public static final int REFRESH_ID = Menu.FIRST+3;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pts = new PTS(getSherlockActivity().getApplicationContext());
        if(SearchCacher.hasLastSearch()) {
        	new SearchPackageInfoTask().execute();
        }
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.pts_item_detail, container, false);
  		
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.search_packages));
    	
    	searchButton = (ImageButton) rootView.findViewById(R.id.ptsSearchButton);
    	ptsInput = (EditText) rootView.findViewById(R.id.ptsInputSearch);
    	ptsPckgName = (TextView) rootView.findViewById(R.id.ptsPckgName);
    	ptsPckgLatestVersion = (TextView) rootView.findViewById(R.id.ptsPckgLatestVersion);
    	ptsPckgMaintainerInfo = (TextView) rootView.findViewById(R.id.ptsPckgMaintainerInfo);
    	ptsPckgBugCount = (TextView) rootView.findViewById(R.id.ptsPckgBugCount);
    	ptsPckgUplNames = (TextView) rootView.findViewById(R.id.ptsPckgUplNames);
    	ptsPckgBinNames = (TextView) rootView.findViewById(R.id.ptsPckgBinNames);
    	
  		searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SearchCacher.setLastSearchByPckgName(ptsInput.getText().toString().trim());
            	new SearchPackageInfoTask().execute();
            }
        });
  		
  		ptsInput.setOnEditorActionListener(new OnEditorActionListener() {
  		    @Override
  		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
  		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
  		        	SearchCacher.setLastSearchByPckgName(ptsInput.getText().toString().trim());
  		        	new SearchPackageInfoTask().execute();
  		            return true;
  		        }
  		        return false;
  		    }
  		});
    	
        return rootView;
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//Add subscription icon
		MenuItem subMenuItem = menu.add(0, SUBSCRIPTION_ID, Menu.CATEGORY_SECONDARY, "(Un)Subscribe");
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		if(pts.isSubscribedTo("testpckgname")) {
			subMenuItem.setIcon(R.drawable.subscribed);
			subMenuItem.setTitle("Unsubscribe");
		} else {
			subMenuItem.setIcon(R.drawable.unsubscribed);
			subMenuItem.setTitle("Subscribe");
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
			    		if(pts.isSubscribedTo("testpckgname")) {
			    			item.setIcon(R.drawable.unsubscribed);
			    			item.setTitle(getString(R.string.subscribe));
			    			pts.removeSubscriptionTo("testpckgname");
			    		} else {
			    			item.setIcon(R.drawable.subscribed);
			    			item.setTitle(getString(R.string.unsubscribe));
			    			pts.addSubscriptionTo("testpckgname");
			    		}
			    		return true;
		    	 case REFRESH_ID:
		    		 	new SearchPackageInfoTask().execute();
			    		return true;
	        }
		return super.onOptionsItemSelected(item);
    }
	
	class SearchPackageInfoTask extends AsyncTask<Void, Void, Void> {
		private String[] pckgInfo;
		private ProgressDialog progressDialog;
		protected void onPreExecute(){ 
		   super.onPreExecute();
		   progressDialog = ProgressDialog.show(getSherlockActivity(), 
				   "Searching", "Searching info about " + SearchCacher.getLastPckgName() 
				   + ". Please wait...", true, false);  
		}
		
		protected Void doInBackground(Void... params) {
			pckgInfo = new String[6];
			pckgInfo[0] = SearchCacher.getLastPckgName(); //Last Package Name
			pckgInfo[1] = pts.getLatestVersion(pckgInfo[0]);
			pckgInfo[2] = pts.getMaintainerName(pckgInfo[0]) + "\n <" + pts.getMaintainerEmail(pckgInfo[0])+ ">";
			pckgInfo[3] = pts.getBugCounts(pckgInfo[0]);
			pckgInfo[4] = Arrays.toString(pts.getUploaderNames(pckgInfo[0]));
			pckgInfo[5] = Arrays.toString(pts.getBinaryNames(pckgInfo[0]));
			return null;
		}  
		protected void onPostExecute (Void result) {
			progressDialog.dismiss();
			ptsInput.setText(pckgInfo[0]);
			ptsPckgName.setText("Package: \n  "+ pckgInfo[0]);
			ptsPckgLatestVersion.setText("Latest version: \n  " + pckgInfo[1]);
			ptsPckgMaintainerInfo.setText("Maintainer: \n  " + pckgInfo[2]);
			ptsPckgBugCount.setText("Bug Count: \n  " + pckgInfo[3]);
			ptsPckgUplNames.setText("Uploaders: \n" + pckgInfo[4]);
	    	ptsPckgBinNames.setText("Binary Names: \n" + pckgInfo[5]);
		}
    }
}
