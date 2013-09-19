package com.debian.debiandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.debian.debiandroid.content.BTSFragment;
import com.debian.debiandroid.content.CIFFragment;
import com.debian.debiandroid.content.Content;
import com.debian.debiandroid.content.LinksFragment;
import com.debian.debiandroid.content.PTSFragment;
import com.debian.debiandroid.content.SUBSFragment;
import com.debian.debiandroid.content.UDDFragment;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends SherlockFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static String currentFragmentID = "";
    public static final int SETTINGS_ID = Menu.FIRST+1;
    public static boolean isInListDisplayFrag = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Save current loaded fragments id
            currentFragmentID = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        return rootView;
    }
    
    /** Returns the appropriate ItemDetailFragment implementation based on the given id
     * @param id a string containing the ContentMenu.Item describing the fragment to be returned
     * @return 
     */
    public static ItemDetailFragment getDetailFragment(String id){
    	Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
        ItemDetailFragment fragment;
    	if(id.equalsIgnoreCase(Content.BTS))
    		fragment = new BTSFragment();
    	else if(id.equalsIgnoreCase(Content.PTS))
    		fragment = new PTSFragment();
    	else if(id.equalsIgnoreCase(Content.UDD))
    		fragment = new UDDFragment();
    	else if(id.equalsIgnoreCase(Content.CIF))
    		fragment = new CIFFragment();
    	else if(id.equalsIgnoreCase(Content.SUBS))
    		fragment = new SUBSFragment();
    	else if(id.equalsIgnoreCase(Content.LINKS))
    		fragment = new LinksFragment();
    	else
    		fragment = new ItemDetailFragment();
    	fragment.setArguments(arguments);
    	fragment.setHasOptionsMenu(true);
    	return fragment;
    }

    public static String getNextFragmentId(){
    	if(currentFragmentID.equals("")) {
    		return Content.PTS;
    	}
    	int position = Content.ITEMS.indexOf(new Content.ContentItem(currentFragmentID, ""));
    	
    	if(position++!=-1 && position<Content.ITEMS.size())
    		return Content.ITEMS.get(position).id;
    	return currentFragmentID;
    }
    
    public static String getPreviousFragmentId(){
    	if(currentFragmentID.equals("")) {
    		return null;
    	}
    	int position = Content.ITEMS.indexOf(new Content.ContentItem(currentFragmentID, ""));
    	// return to ItemListActivity and don't show fragments anymore
    	if(position==0) {
    		return null;
    	}
    	if(position--!=-1 && position>=0)
    		return Content.ITEMS.get(position).id;
    	return currentFragmentID;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	getSettingsMenuItem(menu);
    }
    
    public static void getSettingsMenuItem(Menu menu) {
		menu.add(0, SETTINGS_ID, Menu.CATEGORY_CONTAINER, "Settings")
				.setIcon(R.drawable.settings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}
    
    public void hideSoftKeyboard(EditText input){
        if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	 switch(item.getItemId()){
	    	 case SETTINGS_ID:
	    		 startActivity(new Intent(this.getSherlockActivity(), SettingsActivity.class));
	        	return true;
	        default:
	        	return super.onOptionsItemSelected(item);
        }
    }
    
    public static void forwardToMailApp(Context context, String recipient, String subject, String body) {
    	String uri = new StringBuilder("mailto:" + Uri.encode(recipient))
		.append("?subject=" + subject)
		.append("&body=" +  body)
		.toString();

		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
		
		/* Send it off to the Activity-Chooser */
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
