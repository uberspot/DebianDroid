
package net.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.Arrays;

import net.debian.debiandroid.ItemFragment;
import net.debian.debiandroid.ListDisplayFragment;
import net.debian.debiandroid.R;
import net.debian.debiandroid.apiLayer.BTS;
import net.debian.debiandroid.apiLayer.UDD;
import net.debian.debiandroid.utils.SearchCacher;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.uberspot.storageutils.Cacher;

public class UDDFragment extends ItemFragment {

    private String itemSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateUIReceiver,
                new IntentFilter(ListDisplayFragment.LIST_ACTION));
    }

    private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentFragID.equals(Content.UDD)) {
                if (ItemFragment.isInListDisplayFrag) {
                    SherlockFragmentActivity sa = getSherlockActivity();
                    if (sa != null) {
                        // Process the received action
                        String action = intent.getStringExtra(ListDisplayFragment.LIST_ACTION);
                        if (action.equals(ListDisplayFragment.REFRESH_ACTION)) {
                            // Pop listdisplayfragment from backstack
                            sa.getSupportFragmentManager().popBackStack();
                            itemSelected = intent.getStringExtra(ListDisplayFragment.LIST_TITLE_ID);
                            new SearchInfoTask().execute(true);
                        }
                        if (action.equals(ListDisplayFragment.ITEM_CLICK_ACTION)) {
                            itemSelected = intent.getStringExtra(ListDisplayFragment.LIST_TITLE_ID);
                            String itemClicked = intent.getStringExtra(ListDisplayFragment.ITEM_CLICKED);
                            if (itemSelected.equals(context.getString(R.string.rcbugs))) {
                                // Pop listdisplayfragment from backstack
                                sa.getSupportFragmentManager().popBackStack();
                                // get PckgName and BugNum from title and move to bts fragment to display it
                                String pckgName = UDD.getPckgNameFromRCBugTitle(itemClicked);
                                String bugNum = UDD.getBugNumFromRCBugTitle(itemClicked);
                                SearchCacher.setLastSearchByPckgName(pckgName);
                                SearchCacher.setLastBugSearch(BTS.BUGNUMBER, bugNum);
                                moveToFragment(sa.getSupportFragmentManager(), Content.BTS, null, true);
                            } else if (itemSelected.equals(context.getString(R.string.latest_uploads))) {
                                // Pop listdisplayfragment from backstack
                                sa.getSupportFragmentManager().popBackStack();
                                //get PckgName from title and move to pts fragment to display it
                                String pckgName = UDD.getPckgNameFromUploadsTitle(itemClicked);
                                SearchCacher.setLastSearchByPckgName(pckgName);
                                moveToFragment(sa.getSupportFragmentManager(), Content.PTS, null, true);
                            }
                        }
                    }
                }

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.udd_fragment, container, false);

        getSherlockActivity().getSupportActionBar().setTitle(R.string.udd);

        ArrayList<String> uddScripts = new ArrayList<String>(Arrays.asList(getString(R.string.rcbugs),
                getString(R.string.latest_uploads), getString(R.string.new_maintainers)));

        ListView listview = (ListView) rootView.findViewById(R.id.uddlistview);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
                R.layout.simple_list_child, new ArrayList<String>(uddScripts));

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                // start listdisplayfragment with the results as arguments
                itemSelected = item;
                new SearchInfoTask().execute();
            }
        });

        return rootView;
    }

    class SearchInfoTask extends AsyncTask<Boolean, Void, Void> {

        private ProgressDialog progressDialog;
        Bundle arguments;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getSherlockActivity(), getString(R.string.searching),
                    getString(R.string.searching_info_please_wait), true, false);
            arguments = new Bundle();
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            //If called with execute(true) disable the cache to always bring fresh results
            if ((params.length != 0) && params[0]) {
                Cacher.disableCache();
            }

            UDD udd = new UDD(getSherlockActivity());
            ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
            if (itemSelected.equals(getString(R.string.rcbugs))) {

                items = udd.getRCBugs();
                arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
                arguments.putString(ListDisplayFragment.LIST_HEADER_ID,
                        getString(R.string.rcbugs_withnum, items.get(0).size()));
                arguments.putString(ListDisplayFragment.LIST_TITLE_ID, getString(R.string.rcbugs));

            } else if (itemSelected.equals(getString(R.string.latest_uploads))) {

                items = udd.getLastUploads();
                arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
                arguments.putString(ListDisplayFragment.LIST_HEADER_ID,
                        getString(R.string.latest_uploads_withnum, items.get(0).size()));
                arguments.putString(ListDisplayFragment.LIST_TITLE_ID, getString(R.string.latest_uploads));

            } else if (itemSelected.equals(getString(R.string.new_maintainers))) {

                items = udd.getNewMaintainers();
                arguments.putSerializable(ListDisplayFragment.LIST_ITEMS_ID, items);
                arguments.putString(ListDisplayFragment.LIST_HEADER_ID,
                        getString(R.string.latest_new_maintainers, items.get(0).size()));
                arguments.putString(ListDisplayFragment.LIST_TITLE_ID, getString(R.string.new_maintainers));

            }

            if ((params.length != 0) && params[0]) {
                Cacher.enableCache();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (IllegalArgumentException e) {
                    return;
                }
            } else {
                return;
            }

            ListDisplayFragment.loadAndShow(getSherlockActivity().getSupportFragmentManager(), arguments);
        }
    }
}
