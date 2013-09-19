package com.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.debian.debiandroid.ItemDetailFragment;
import com.debian.debiandroid.R;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LinksFragment extends ItemDetailFragment {
	
	private EditText linkSearchInput;
	
	private final static HashMap<String, String> links = new HashMap<String, String>(){{
	     put("Debian.org", "http://debian.org");
	     put("Planet Debian", "http://planet.debian.org/");
	     put("Debian News (rss)", "http://www.debian.org/News/news");
	     put("Debian Security (rss)", "http://www.debian.org/security/dsa");
	}};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.links_item_detail, container,
				false);
		
		getSherlockActivity().getSupportActionBar().setTitle( getString(R.string.links) );
		
		linkSearchInput = (EditText) rootView.findViewById(R.id.linksInputSearch);
		
		ListView listview = (ListView) rootView.findViewById(R.id.linkslistview);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
				R.layout.listchild, new ArrayList<String>(links.keySet()));
		
		linkSearchInput.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        // When user changes the Text
		        adapter.getFilter().filter(cs);  
		    }
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		            int arg3) { }
		    @Override public void afterTextChanged(Editable arg0) { }
		});
		linkSearchInput.setOnEditorActionListener(new OnEditorActionListener() {
  		    @Override
  		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
  		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
  		        	hideSoftKeyboard(linkSearchInput);
  		            return true;
  		        }
  		        return false;
  		    }
  		});
		
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				openLinkInExternalApp(links.get(item));
			}
		});
		return rootView;
	}
	
	private void openLinkInExternalApp(String link) {
		Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
		
		// Verify the intent resolves
		List<ResolveInfo> activities = getSherlockActivity().getPackageManager()
												.queryIntentActivities(linkIntent, 0);  
		
		// Start an activity if it's intent safe
		if (activities.size() > 0) {
			startActivity(linkIntent); 
		}
	}
}
