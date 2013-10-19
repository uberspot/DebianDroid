package net.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.debian.debiandroid.ItemDetailFragment;

import net.debian.debiandroid.R;

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
	
	/** HashMap with <Description, Link>. The Description is later on searchable with the
	 * ArrayAdapter filter that's why tags like "rss" or "social" are added after a - character. */
	private final static HashMap<String, String> links = new HashMap<String, String>(){
		private static final long serialVersionUID = 5237952374216701176L;
	{
	     put("Debian.org", "http://debian.org");
	     put("Planet Debian - social", "http://planet.debian.org/");
	     put("Debian News - rss", "http://www.debian.org/News/news");
	     put("Debian Security - rss", "http://www.debian.org/security/dsa");
	     put("Debian Twitter - social", "https://twitter.com/debian");
	     put("Debian Google+ - social","https://plus.google.com/111711190057359692089/posts");
	     put("Debian Identi.ca - social","https://identi.ca/debian");
	     put("Debian irc channel list - social","https://wiki.debian.org/IRC/");
	     put("Debian mailing lists","https://lists.debian.org/");
	     put("Debian on Reddit - social","http://www.reddit.com/r/debian");
	}};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.links_fragment, container,
				false);
		
		getSherlockActivity().getSupportActionBar().setTitle( R.string.links );
		
		linkSearchInput = (EditText) rootView.findViewById(R.id.linksInputSearch);
		
		ListView listview = (ListView) rootView.findViewById(R.id.linkslistview);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
				R.layout.simple_list_child, new ArrayList<String>(links.keySet()));
		
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
