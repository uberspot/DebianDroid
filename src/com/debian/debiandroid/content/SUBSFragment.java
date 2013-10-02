package com.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.Set;

import com.debian.debiandroid.DExpandableAdapter;
import com.debian.debiandroid.ItemDetailFragment;
import com.debian.debiandroid.R;
import com.debian.debiandroid.apiLayer.BTS;
import com.debian.debiandroid.apiLayer.PTS;
import com.debian.debiandroid.utils.SearchCacher;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class SUBSFragment extends ItemDetailFragment {

	 private ArrayList<String> parentItems;
	 private ArrayList<Object> childItems;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.subs_fragment, container, false);
        
    	getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.subscriptions));
    	
    	setupSubsList((ExpandableListView) rootView.findViewById(R.id.subscriptionlist));
    	
        return rootView;
    }

	public void setupSubsList(ExpandableListView expandableList) {    	
    	expandableList.setDividerHeight(1);
    	expandableList.setClickable(true);
    	setSubscribedData();
    	
    	final DExpandableAdapter adapter = new DExpandableAdapter(parentItems, childItems);
    	adapter.setInflater((LayoutInflater) getSherlockActivity()
    						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    	expandableList.setAdapter(adapter);
    	registerForContextMenu(expandableList);
    	expandableList.setOnChildClickListener(new OnChildClickListener() {
    		
            public boolean onChildClick(ExpandableListView parent, View view,
                    int groupPosition, int childPosition, long id) {
            	String itemClicked = ((TextView)view).getText().toString();
                if(groupPosition==0) {
                	SearchCacher.setLastSearchByPckgName(itemClicked);
                	// Move to pts fragment
          		  	ItemDetailFragment fragment = ItemDetailFragment.getDetailFragment(
          				  Content.PTS);
	          		getActivity().getSupportFragmentManager().beginTransaction()
	              	.replace(R.id.item_detail_container, fragment)
	              	.commit();
                } else if(groupPosition==1) {
                	String[] items = itemClicked.split("\\|");
                	if(items.length>1) {
	                	SearchCacher.setLastBugSearch(items[0], items[1]);
	                	// Move to bts fragment
	          		  	ItemDetailFragment fragment = ItemDetailFragment.getDetailFragment(
	          				  Content.BTS);
		          		getActivity().getSupportFragmentManager().beginTransaction()
		              	.replace(R.id.item_detail_container, fragment)
		              	.commit();
	          		}
                }
                return true;
            }
        });
	}
	
	public void setSubscribedData() {
		parentItems = new ArrayList<String>();
		childItems = new ArrayList<Object>();
		
		Context context = getSherlockActivity().getApplicationContext();
		Set<String> ptsSubs = new PTS(context).getSubscriptions();
		Set<String> btsSubs = new BTS(context).getSubscriptions();
		
		parentItems.add(getString(R.string.subscribed_packages) + " (" + ptsSubs.size() + ")");
		parentItems.add(getString(R.string.subscribed_bugs) + " (" + btsSubs.size() + ")");
		
	    childItems.add(new ArrayList<String>(ptsSubs));
		childItems.add(new ArrayList<String>(btsSubs));
	}
}
