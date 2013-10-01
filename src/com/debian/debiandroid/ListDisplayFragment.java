package com.debian.debiandroid;

import java.util.ArrayList;
import java.util.Arrays;

import com.debian.debiandroid.content.Content;
import com.debian.debiandroid.utils.SearchCacher;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

public class ListDisplayFragment extends ItemDetailFragment {

	public static final String LIST_ITEMS_ID = "listItemsID",
							   LIST_HEADER_ID = "listHeaderID",
							   LIST_TITLE_ID = "listTitleID";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInListDisplayFrag = true;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.list_display_item_detail, container, false);
  		
    	ArrayList<ArrayList<String>> listItems = new ArrayList<ArrayList<String>>();
    	Bundle args = getArguments();
    	if (args.containsKey(LIST_ITEMS_ID)) {
    		listItems = (ArrayList<ArrayList<String>>) args.getSerializable(LIST_ITEMS_ID);
        }
    	if (args.containsKey(LIST_TITLE_ID)) {
    		getSherlockActivity().getSupportActionBar().setTitle(args.getString(LIST_TITLE_ID));
        }
    	if (args.containsKey(LIST_HEADER_ID)) {
    		TextView header = (TextView) rootView.findViewById(R.id.listdisplaytextview);
    		header.setText(args.getString(LIST_HEADER_ID));
        }
    	
    	/*ListView listview = (ListView) rootView.findViewById(R.id.listdisplaylistview);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
				android.R.layout.simple_list_item_1, listItems);
		
		listview.setAdapter(adapter);*/
    	if(!listItems.isEmpty())
    		setupList((ExpandableListView) rootView.findViewById(R.id.listdisplayexpListView), listItems);
		
        return rootView;
    }
	
	@Override
	public void onDestroy() {
		isInListDisplayFrag = false;
		super.onDestroy();
	}
	
	public void setupList(ExpandableListView expandableList, ArrayList<ArrayList<String>> listItems) {    	
    	expandableList.setDividerHeight(2);
    	expandableList.setClickable(true);
    	
    	ArrayList<String> parentItems = listItems.get(0);
    	ArrayList<Object> childItems = new ArrayList<Object>();
    	for(String detail : listItems.get(1)) {
    		childItems.add(new ArrayList<String>(Arrays.asList(detail)));
    	}
    	
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
}
