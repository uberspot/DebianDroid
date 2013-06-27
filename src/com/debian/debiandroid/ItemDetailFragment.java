package com.debian.debiandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
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
    	if(id.equalsIgnoreCase(ContentMenu.ITEM.BTS.toString()))
    		return new BTSFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.PTS.toString()))
    		return new PTSFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.UDD.toString()))
    		return new UDDFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.CIF.toString()))
    		return new CIFFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.SETT.toString()))
    		return new SETTFragment();
    	else if(id.equalsIgnoreCase(ContentMenu.ITEM.SUBS.toString()))
    		return new SUBSFragment();
    	else
    		return new ItemDetailFragment();
    }
}
