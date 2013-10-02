package net.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.Arrays;

import net.debian.debiandroid.ItemDetailFragment;
import net.debian.debiandroid.ListDisplayFragment;
import net.debian.debiandroid.apiLayer.UDD;

import com.debian.debiandroid.R;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UDDFragment extends ItemDetailFragment {
	
	private UDD udd;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.udd_fragment, container, false);
  		
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.udd));
    	
    	ArrayList<String> uddScripts = new ArrayList<String>(Arrays.asList(getString(R.string.rcbugs), 
    			getString(R.string.latest_uploads), getString(R.string.new_maintainers)));
    	udd = new UDD(getSherlockActivity());
    	
    	ListView listview = (ListView) rootView.findViewById(R.id.uddlistview);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
				R.layout.simple_list_child, new ArrayList<String>(uddScripts));
		
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				// start listdisplayfragment with the results as arguments
				new SearchInfoTask().execute(item);
			}
		});
		
        return rootView;
    }
	
	class SearchInfoTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog progressDialog;
		String title="", header=""; 
		ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
		
		protected void onPreExecute() {
			   super.onPreExecute();
			   progressDialog = ProgressDialog.show(getSherlockActivity(), 
					   getString(R.string.searching), getString(R.string.searching_info) + ". " + getString(R.string.please_wait) + "...", true, false);  
			}
			
			protected Void doInBackground(String... params) {
				if( params[0].equals(getString(R.string.rcbugs)) ) {
					items = udd.getRCBugs();
					header = getString(R.string.rcbugs) + " (" + items.get(0).size() + ")";
					title = getString(R.string.rcbugs);
				} else if( params[0].equals(getString(R.string.latest_uploads)) ) {
					items = udd.getLastUploads();
					header = items.get(0).size() + " " + getString(R.string.latest_uploads);
					title = getString(R.string.latest_uploads);
				} else if( params[0].equals(getString(R.string.new_maintainers)) ) {
					items = udd.getNewMaintainers();
					header = getString(R.string.latest) + " " + items.get(0).size() + " " + getString(R.string.new_maintainers) ;
					title = getString(R.string.new_maintainers);
				}
				return null;
			}
			
			protected void onPostExecute (Void result) {
				progressDialog.dismiss();
				ItemDetailFragment fragment = new ListDisplayFragment();
				Bundle arguments = new Bundle();
				arguments.putString(ListDisplayFragment.LIST_HEADER_ID, header);
				arguments.putString(ListDisplayFragment.LIST_TITLE_ID, title);
				arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
				
		        fragment.setArguments(arguments);
				
		    	getSherlockActivity().getSupportFragmentManager().beginTransaction()
		    	.replace(R.id.item_detail_container, fragment).addToBackStack(null).commit();
			}
	}
}
