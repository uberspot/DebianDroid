package com.debian.debiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.debian.debiandroid.content.ContentMenu;

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

    public ContentMenu.MenuItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ContentMenu.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.content);
        } 
        return rootView;
    }
    
    /** Returns the appropriate ItemDetailFragment implementation based on the given id
     * @param id a string containing the ContentMenu.Item describing the fragment to be returned
     * @return 
     */
    public static ItemDetailFragment getDetailFragment(String id){
    	currentFragmentID = id;
    	Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
        ItemDetailFragment fragment;
    	if(id.equalsIgnoreCase(ContentMenu.ITEM.BTS.toString()))
    		fragment = new BTSFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.PTS.toString()))
    		fragment = new PTSFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.UDD.toString()))
    		fragment = new UDDFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.CIF.toString()))
    		fragment = new CIFFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.SUBS.toString()))
    		fragment = new SUBSFragment();
    	else
    		fragment = new ItemDetailFragment();
    	fragment.setArguments(arguments);
    	fragment.setHasOptionsMenu(true);
    	return fragment;
    }

    public static String getNextFragmentId(){
    	int position = getPositionOfItem(currentFragmentID);
    	if(position++!=-1 && position<ContentMenu.ITEM.values().length)
    		return ContentMenu.ITEM.values()[position].toString();
    	return currentFragmentID;
    }
    
    public static String getPreviousFragmentId(){
    	int position = getPositionOfItem(currentFragmentID);
    	if(position--!=-1 && position>=0)
    		return ContentMenu.ITEM.values()[position].toString();
    	return currentFragmentID;
    }
    
    private static int getPositionOfItem(String id){
    	ContentMenu.ITEM[] items = ContentMenu.ITEM.values();
    	for(int i=0;i<items.length; i++) {
    		if(items[i].toString().equalsIgnoreCase(id)) {
    			return i;
    		}
    	} 
    	return -1;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	getSettingsMenuItem(menu);
    }
    
    public static void getSettingsMenuItem(Menu menu) {
		MenuItem item = menu.add(0, SETTINGS_ID, 0, "Settings");
		item.setIcon(android.R.drawable.ic_menu_preferences);
		
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
}
