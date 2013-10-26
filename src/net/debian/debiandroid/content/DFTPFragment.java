package net.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.Arrays;

import net.debian.debiandroid.ItemDetailFragment;
import net.debian.debiandroid.ListDisplayFragment;
import net.debian.debiandroid.apiLayer.DFTP;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidStorageUtils.Cacher;

import net.debian.debiandroid.R;

public class DFTPFragment extends ItemDetailFragment {
	
	private String itemSelected;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
        		mUpdateUIReceiver, new IntentFilter(ListDisplayFragment.LIST_ACTION));
    }
	
	private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if(currentFragID.equals(Content.DFTP)) {
		    	// Pop listdisplayfragment from backstack
				if(ItemDetailFragment.isInListDisplayFrag) {
					getSherlockActivity().getSupportFragmentManager().popBackStack();
				}
				String action = intent.getStringExtra(ListDisplayFragment.LIST_ACTION);
				if(action.equals(ListDisplayFragment.REFRESH_ACTION)) {
					itemSelected = intent.getStringExtra(ListDisplayFragment.LIST_TITLE_ID);
					new SearchInfoTask().execute(true);
				}
	    	}
	    }
	};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.dftp_fragment, container, false);
  		
    	getSherlockActivity().getSupportActionBar().setTitle(R.string.dftp);
    	
    	ArrayList<String> dftpScripts = new ArrayList<String>(Arrays.asList(getString(R.string.new_packages), 
    			getString(R.string.removed_packages), getString(R.string.deferred_packages)));
    	    	
    	ListView listview = (ListView) rootView.findViewById(R.id.dftpListView);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
				R.layout.simple_list_child, new ArrayList<String>(dftpScripts));
		
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				itemSelected = (String) parent.getItemAtPosition(position);
				// start listdisplayfragment with the dftp results as arguments
				new SearchInfoTask().execute();
			}
		});
		
        return rootView;
    }
	
	class SearchInfoTask extends AsyncTask<Boolean, Void, Void> {
		private ProgressDialog progressDialog;
		Bundle arguments;
		
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog
					.show(getSherlockActivity(), getString(R.string.searching),
							getString(R.string.searching_info_please_wait),
							true, false);
			arguments = new Bundle();
		}

		protected Void doInBackground(Boolean... params) {
			//If called with execute(true) disable the cache to always bring fresh results
			if(params.length!=0 && params[0]) {
				Cacher.disableCache();
			}
			
			DFTP dftp = new DFTP(getSherlockActivity());
			ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
			if (itemSelected.equals(getString(R.string.new_packages))) {
				
				items = dftp.getNewPackages();
				arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
				arguments.putString(ListDisplayFragment.LIST_HEADER_ID, 
						getString(R.string.new_packages_withnum, items.get(0).size()));
				arguments.putString(ListDisplayFragment.LIST_TITLE_ID, getString(R.string.new_packages));
				
			} else if (itemSelected.equals(getString(R.string.removed_packages))) {
				
				items = dftp.getRemovedPackages();
				arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
				arguments.putString(ListDisplayFragment.LIST_HEADER_ID, 
						getString(R.string.removed_packages_withnum, items.get(0).size()));
				arguments.putString(ListDisplayFragment.LIST_TITLE_ID, getString(R.string.removed_packages));
			
			} else if (itemSelected.equals(getString(R.string.deferred_packages))) {
			
				items = dftp.getDeferredPackages();
				arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
				arguments.putString(ListDisplayFragment.LIST_HEADER_ID, 
						getString(R.string.deferred_packages_withnum, items.get(0).size()));
				arguments.putString(ListDisplayFragment.LIST_TITLE_ID, getString(R.string.deferred_packages));
			
			}
			
			if(params.length!=0 && params[0]) {
				Cacher.enableCache();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (progressDialog != null && progressDialog.isShowing()) {
				try {
					progressDialog.dismiss();
				} catch (IllegalArgumentException e) {
					return;
				}
			} else {
				return;
			}
			ItemDetailFragment fragment = new ListDisplayFragment();
			
			if(arguments != null)
				fragment.setArguments(arguments);

			getSherlockActivity().getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.item_detail_container, fragment)
					.addToBackStack(null).commit();
		}
	}
}
