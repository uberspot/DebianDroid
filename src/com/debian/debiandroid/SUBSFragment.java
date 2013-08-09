package com.debian.debiandroid;

import java.util.ArrayList;
import java.util.Set;

import com.debian.debiandroid.apiLayer.BTS;
import com.debian.debiandroid.apiLayer.PTS;
import com.debian.debiandroid.apiLayer.SearchCacher;
import com.debian.debiandroid.content.ContentMenu;

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
    	View rootView = inflater.inflate(R.layout.subs_item_detail, container, false);
        
    	getSherlockActivity().getSupportActionBar().setTitle("Favourites");
    	
    	ExpandableListView expandableList = (ExpandableListView) rootView.findViewById(R.id.subscriptionlist);
    	
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
                System.out.println("Child Clicked " + itemClicked + " " + groupPosition);
                if(groupPosition==0) {
                	SearchCacher.setLastSearchByPckgName(itemClicked);
                	// Move to pts fragment
          		  	ItemDetailFragment fragment = ItemDetailFragment.getDetailFragment(
          				  ContentMenu.ITEM.PTS.toString());
	          		getActivity().getSupportFragmentManager().beginTransaction()
	              	.replace(R.id.item_detail_container, fragment)
	              	.commit();
                } else if(groupPosition==1) {
                	SearchCacher.setLastSearchByBugNumber(itemClicked);
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
    	
    	
        return rootView;
    }
	
	public void setSubscribedData() {
		parentItems = new ArrayList<String>();
		childItems = new ArrayList<Object>();
		
		Context context = getSherlockActivity().getApplicationContext();
		Set<String> ptsSubs = new PTS(context).getSubscriptions();
		Set<String> btsSubs = new BTS(context).getSubscriptions();
		
		parentItems.add("Subscribed Packages (" + ptsSubs.size() + ")");
		parentItems.add("Subscribed Bugs (" + btsSubs.size() + ")");
		
		ArrayList<String> child = new ArrayList<String>();
		child.addAll(ptsSubs);
	    childItems.add(child);
	    
	    child = new ArrayList<String>();
	    child.addAll(btsSubs);
		childItems.add(child);
	}
}
