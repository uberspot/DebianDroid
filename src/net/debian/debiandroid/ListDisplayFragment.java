package net.debian.debiandroid;

import java.util.ArrayList;
import java.util.Arrays;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import net.debian.debiandroid.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * A fragment that displays some items in an expandable list.
 * The items along with the title and a header are pased to the fragment 
 * via Intent.
 * e.g. to call a new ListDisplayFragment
 * Bundle arguments = new Bundle();
 * arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, ArrayList<ArrayList<String>> items);
 * arguments.putString(ListDisplayFragment.LIST_HEADER_ID, "listHeader"));
 * arguments.putString(ListDisplayFragment.LIST_TITLE_ID, "listTitleForActionBar"); 
 * ItemDetailFragment fragment = new ListDisplayFragment();
 * fragment.setArguments(arguments);
 * getSherlockActivity().getSupportFragmentManager().beginTransaction()
 * 		.replace(R.id.item_detail_container, fragment).addToBackStack(null).commit();
 * 
 * and in the fragment that calls the list you add
 * LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
 * 				mUpdateUIReceiver, new IntentFilter(ListDisplayFragment.LIST_ACTION));
 * to the fragments onCreate method
 * and the implementation of the broadcast receiver should look like this
 * private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if(currentFragID.equals(Content.ID_OF_THE_FRAGMENT)) {
		    	// Pop listdisplayfragment from backstack
				if(ItemDetailFragment.isInListDisplayFrag) {
					getSherlockActivity().getSupportFragmentManager().popBackStack();
				}
				String action = intent.getStringExtra(ListDisplayFragment.LIST_ACTION);
				if(action.equals(ListDisplayFragment.REFRESH_ACTION)) {
					// Refresh the content to display and call listDisplayFragment again to display them
				}
			}
	    }
	};
 * 
 */
public class ListDisplayFragment extends ItemFragment {
	
	public static final int REFRESH_ID = Menu.FIRST+2;
	
	public static final String LIST_ITEMS_ID = "listItemsID",
							   LIST_HEADER_ID = "listHeaderID",
							   LIST_TITLE_ID = "listTitleID",
							   REFRESH_ACTION = "refresh",
							   ITEM_CLICKED = "item",
							   ITEM_CLICK_ACTION = "itemClick",
							   LIST_ACTION = "listAction";
	
	private ArrayList<String> parentItems; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInListDisplayFrag = true;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.list_display_fragment, container, false);
  		
    	setHasOptionsMenu(true);
    	
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
    	
    	ExpandableListView view = (ExpandableListView) rootView.findViewById(R.id.listdisplayexpListView); 
    	if(!listItems.isEmpty() && view !=null) {
    		setupList(view, listItems);
    	}
        return rootView;
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(0, REFRESH_ID, Menu.CATEGORY_ALTERNATIVE, R.string.refresh)
				.setIcon(R.drawable.refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    	 switch(item.getItemId()){
		    	 case REFRESH_ID:
		    		 Intent i = new Intent(LIST_ACTION);
		    		 i.putExtra(LIST_ACTION, REFRESH_ACTION);
		    		 i.putExtra(ListDisplayFragment.LIST_TITLE_ID, 
		    				 getSherlockActivity().getSupportActionBar().getTitle().toString());
		    		 LocalBroadcastManager.getInstance(getSherlockActivity()).sendBroadcast(i);
		    		 return true;
	        }
		return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onDestroy() {
		isInListDisplayFrag = false;
		super.onDestroy();
	}
	
	public void setupList(ExpandableListView expandableList, ArrayList<ArrayList<String>> listItems) {    	
    	expandableList.setDividerHeight(2);
    	expandableList.setClickable(true);
    	
    	parentItems = listItems.get(0);
    	ArrayList<Object> childItems = new ArrayList<Object>();
    	for(String detail : listItems.get(1)) {
    		childItems.add(new ArrayList<String>(Arrays.asList(detail)));
    	}
    	
    	final DExpandableAdapter adapter = new DExpandableAdapter(parentItems, childItems);
    	adapter.setInflater((LayoutInflater) getSherlockActivity()
    						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    	expandableList.setAdapter(adapter);
    	
    	registerForContextMenu(expandableList);
    	
    	expandableList.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View childView, int flatPos, long id) {
				if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    long packedPos = ((ExpandableListView) parent).getExpandableListPosition(flatPos);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                    
                    String itemClicked = parentItems.get(groupPosition);
                    
                    // If bug or package is selected forward to corresponding fragment via callbacks
                	if(itemClicked!=null && itemClicked.length()!=0) {
    	            	Intent i = new Intent(LIST_ACTION);
    		    		i.putExtra(LIST_ACTION, ITEM_CLICK_ACTION);
    		    		i.putExtra(ListDisplayFragment.LIST_TITLE_ID, 
   		    				 getSherlockActivity().getSupportActionBar().getTitle().toString());
    		    		i.putExtra(ITEM_CLICKED, itemClicked);
    		    		LocalBroadcastManager.getInstance(getSherlockActivity()).sendBroadcast(i);
    		    		return true;
    	    		}
                	
                    return true;
				}
				return false;
			}});
    	
	}

	public static void loadAndShow(FragmentManager fm, Bundle arguments) {
		ItemFragment fragment = new ListDisplayFragment();
		if(arguments != null)
			fragment.setArguments(arguments);

		fm.beginTransaction()
		  .replace(R.id.item_detail_container, fragment)
		  .addToBackStack(null).commit();
	}
}
