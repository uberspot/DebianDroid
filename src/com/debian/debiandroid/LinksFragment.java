package com.debian.debiandroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class LinksFragment extends ItemDetailFragment {
	
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
		
		EditText linkSearchInput = (EditText) rootView.findViewById(R.id.linksInputSearch);
		
		ListView listview = (ListView) rootView.findViewById(R.id.linkslistview);
		final ListArrayAdapter adapter = new ListArrayAdapter(getSherlockActivity(),
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
		
		// Verify it resolves
		List<ResolveInfo> activities = getSherlockActivity().getPackageManager()
												.queryIntentActivities(linkIntent, 0);  
		
		// Start an activity if it's intent safe
		if (activities.size() > 0) {
			startActivity(linkIntent); 
		}
	}
}
