package com.debian.debiandroid;

import java.util.ArrayList;
import java.util.Arrays;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.debian.debiandroid.apiLayer.BTS;
import com.debian.debiandroid.apiLayer.PTS;
import com.debian.debiandroid.apiLayer.SearchCacher;
import com.debian.debiandroid.content.ContentMenu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView.OnEditorActionListener;
import androidStorageUtils.Cacher;

public class PTSFragment extends ItemDetailFragment {

	private ImageButton searchButton;
	private EditText ptsInput;
	private PTS pts;
	private ExpandableListView bugList;
	private TextView ptsPckgName, ptsPckgLatestVersion, 
					ptsPckgMaintainerInfo, ptsPckgBugCount, 
					ptsPckgUplNames, ptsPckgBinNames;
	
	private ArrayList<String> bugListParentItems;
	private ArrayList<Object> bugListChildItems;
	
	/** ID for the (un)subscribe menu item. It starts from +2 
	 * because the settings icon is in the +1 position */
	public static final int SUBSCRIPTION_ID = Menu.FIRST+2;
	public static final int REFRESH_ID = Menu.FIRST+3;
	public static final int NEW_EMAIL_ID = Menu.FIRST+4;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pts = new PTS(getSherlockActivity().getApplicationContext());
        if(SearchCacher.hasLastPckgSearch()) {
        	new SearchPackageInfoTask().execute();
        }
        
        bugListParentItems = new ArrayList<String>();
		bugListChildItems = new ArrayList<Object>();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.pts_item_detail, container, false);

    	bugList = (ExpandableListView) rootView.findViewById(R.id.ptsBugsList);
    	ViewGroup header = (ViewGroup)inflater.inflate(R.layout.pts_item_header, bugList, false);
    	bugList.addHeaderView(header, null, false);
    	
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
            	String input = ptsInput.getText().toString().trim();
            	if(input!=null && !input.trim().equals("")) {
	            	SearchCacher.setLastSearchByPckgName(input);
	            	new SearchPackageInfoTask().execute();
            	}
            }
        });
  		
  		ptsInput.setOnEditorActionListener(new OnEditorActionListener() {
  		    @Override
  		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        String input = ptsInput.getText().toString().trim();
  		        if (actionId == EditorInfo.IME_ACTION_SEARCH && input!=null && !input.trim().equals("")) {
  		        	SearchCacher.setLastSearchByPckgName(ptsInput.getText().toString().trim());
  		        	new SearchPackageInfoTask().execute();
  		            return true;
  		        }
  		        return false;
  		    }
  		});
    	
        return rootView;
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
            	String itemClicked = ((TextView)view).getText().toString().trim();
            	if(!"".equals(itemClicked)) {
	                //save search by bug num
	                SearchCacher.setLastBugSearch(BTS.BUGNUMBER, itemClicked);
	                // Move to bts fragment
	      		  	ItemDetailFragment fragment = ItemDetailFragment.getDetailFragment(
	      				  ContentMenu.ITEM.BTS.toString());
	          		getActivity().getSupportFragmentManager().beginTransaction()
	              	.replace(R.id.item_detail_container, fragment)
	              	.commit();
            	}
                return true;
            }
        });
	}
	
	public void setBugData(String pkgName) {
		bugListParentItems = new ArrayList<String>();
		bugListChildItems = new ArrayList<Object>();
		
		Context context = getSherlockActivity().getApplicationContext();
		BTS bts = new BTS(context);
		
		ArrayList<String> bugs = bts.getBugs(new String[]{BTS.PACKAGE}, new String[]{pkgName});
		bugListParentItems.add(getString(R.string.all_bugs) + " (" + bugs.size() + ")");
	    bugListChildItems.add(bugs);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//Add subscription icon
		MenuItem subMenuItem = menu.add(0, SUBSCRIPTION_ID, Menu.CATEGORY_SECONDARY, "(Un)Subscribe");
		subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	 	setSubscriptionIcon(subMenuItem, SearchCacher.getLastPckgName());
		
		menu.add(0, REFRESH_ID, Menu.CATEGORY_ALTERNATIVE, getString(R.string.refresh))
				.setIcon(R.drawable.refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, NEW_EMAIL_ID, Menu.CATEGORY_SECONDARY, getString(R.string.submit_new_bug_report))
				.setIcon(R.drawable.new_email)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		setSubscriptionIcon(menu.findItem(SUBSCRIPTION_ID), SearchCacher.getLastPckgName());
		super.onPrepareOptionsMenu(menu);
	}

	public void setSubscriptionIcon(MenuItem subMenuItem, String pckgName) {
		if(pckgName!=null && pts.isSubscribedTo(pckgName)) {
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
		    		 	String pckgName = SearchCacher.getLastPckgName();
		    		 	if(pckgName!=null) {
				    		if(pts.isSubscribedTo(pckgName)) {
				    			item.setIcon(R.drawable.unsubscribed);
				    			item.setTitle(getString(R.string.subscribe));
				    			pts.removeSubscriptionTo(pckgName);
				    		} else {
				    			item.setIcon(R.drawable.subscribed);
				    			item.setTitle(getString(R.string.unsubscribe));
				    			pts.addSubscriptionTo(pckgName);
				    		}
				    	}
			    		return true;
		    	 case REFRESH_ID:
			    		 if(SearchCacher.hasLastPckgSearch()) {
			    		 	new SearchPackageInfoTask().execute(true);
			    		 }
			    		return true;
		    	 case NEW_EMAIL_ID:
		    		 	forwardToMailApp();
		    		 	return true;
	        }
		return super.onOptionsItemSelected(item);
    }
	
	private void forwardToMailApp() {
		if(SearchCacher.hasLastPckgSearch()) {
			String pckgName = SearchCacher.getLastPckgName();
			
			forwardToMailApp(getSherlockActivity(), BTS.NEWBUGREPORTMAIL, BTS.getNewBugReportSubject(pckgName),
					BTS.getNewBugReportBody(pckgName, SearchCacher.getLastPckgVersion() ));
		 }
	}

	class SearchPackageInfoTask extends AsyncTask<Boolean, Integer, Void> {
		private String[] pckgInfo;
		private ProgressDialog progressDialog;
		private String progressMessage =  getString(R.string.searching_info_about) + " " + SearchCacher.getLastPckgName() 
				   + ". " + getString(R.string.please_wait) + "...";
		
		protected void onPreExecute(){ 
		   super.onPreExecute();
		   progressDialog = ProgressDialog.show(getSherlockActivity(), getString(R.string.searching),
				   progressMessage, true, false);  
		}
		
		protected Void doInBackground(Boolean... params) {
			//If called with execute(true) set the cache to always bring fresh results
			if(params.length!=0 && params[0]) {
				Cacher.disableCache();
			}
			
			pckgInfo = new String[6];
			pckgInfo[0] = SearchCacher.getLastPckgName(); //Last Package Name
			if(pckgInfo[0]!=null) {
				pckgInfo[1] = pts.getLatestVersion(pckgInfo[0]);
				publishProgress(2);
				SearchCacher.setLastPckgVersion(pckgInfo[1]);
				String maintEmail =  pts.getMaintainerEmail(pckgInfo[0]).trim();
				pckgInfo[2] = pts.getMaintainerName(pckgInfo[0]) + "\n" + getString(R.string.packages_overview) +":\n  " + 
				"http://qa.debian.org/developer.php?login="+ maintEmail +
								"\n "+getString(R.string.mail) + ":\n  " + maintEmail;
				publishProgress(3);
				pckgInfo[3] = pts.getBugCounts(pckgInfo[0]);
				publishProgress(4);
				if(!pckgInfo[1].trim().equals("") && !pckgInfo[3].trim().equals("") ) {
					pckgInfo[4] = Arrays.toString(pts.getUploaderNames(pckgInfo[0]));
					publishProgress(5);
					pckgInfo[5] = Arrays.toString(pts.getBinaryNames(pckgInfo[0]));
					publishProgress(6);
					setBugData(pckgInfo[0]);
				}
			}
			
			if(params.length!=0 && params[0]) {
				Cacher.enableCache();
			}
			return null;
		}  
		@SuppressLint("NewApi")
		protected void onPostExecute (Void result) {
			progressDialog.dismiss();
			if(pckgInfo[0]!=null) {
				ptsInput.setText(pckgInfo[0]);
				 
				if(!pckgInfo[1].trim().equals("") && !pckgInfo[3].trim().equals("") ) {
					ptsPckgName.setText(getString(R.string.pckg) + ": \n  "+ pckgInfo[0]);
					ptsPckgLatestVersion.setText(getString(R.string.latest_version) + ":\n  " + pckgInfo[1]);
					ptsPckgMaintainerInfo.setText(getString(R.string.maintainer) + ":\n  " + pckgInfo[2]);
		    		ptsPckgMaintainerInfo.setMovementMethod(LinkMovementMethod.getInstance());
					ptsPckgBugCount.setText(getString(R.string.bug_count) + ":\n  " + pckgInfo[3]);
					ptsPckgUplNames.setText(getString(R.string.uploaders) + ":\n  " + pckgInfo[4]);
			    	ptsPckgBinNames.setText(getString(R.string.binary_names) + ":\n  " + pckgInfo[5]);
		    	} else {
		    		System.out.println(pckgInfo[1]+","+pckgInfo[3]+","+(!pckgInfo[1].trim().equals("") && !pckgInfo[3].trim().equals(""))+"\n"+getString(R.string.no_info_found_for) + " " + pckgInfo[0]);
		    		ptsPckgName.setText(getString(R.string.no_info_found_for) + " " + pckgInfo[0]);
		    		ptsPckgLatestVersion.setText("");
		    		ptsPckgMaintainerInfo.setText("");
		    		ptsPckgBugCount.setText("");
		    		ptsPckgUplNames.setText("");
		    		ptsPckgBinNames.setText("");
		    		bugListParentItems = new ArrayList<String>();
		    		bugListChildItems = new ArrayList<Object>();
		    	}
				setupBugsList();
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    		getSherlockActivity().invalidateOptionsMenu();
			}
	    }
		@Override
	    public void onProgressUpdate(Integer... args){
            progressDialog.setMessage(progressMessage + " " + args[0] + "/" + pckgInfo.length + " info retrieved!");
        }
    }
}
