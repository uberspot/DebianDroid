
package net.debian.debiandroid.contentfragments;

import java.util.ArrayList;
import java.util.Arrays;

import net.debian.debiandroid.ItemFragment;
import net.debian.debiandroid.R;
import net.debian.debiandroid.apiLayer.BTS;
import net.debian.debiandroid.apiLayer.PTS;
import net.debian.debiandroid.utils.SearchCacher;
import net.debian.debiandroid.utils.UIUtils;
import net.debian.debiandroid.view.SearchBarView;
import net.debian.debiandroid.view.SearchBarView.OnSearchActionListener;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.uberspot.storageutils.Cacher;

public class PTSFragment extends ItemFragment {

    private SearchBarView ptsSearchBar;
    private PTS pts;
    private ListView ptsMadisonList;
    private TextView ptsPckgInfo, emptyTextView;
    private ListView ptsPckgList;

    private ArrayList<String> ptsMadisonInfo;

    /** ID for the (un)subscribe menu item. It starts from +2
     * because the settings icon is in the +1 position */
    public static final int SUBSCRIPTION_ID = Menu.FIRST + 2;
    public static final int REFRESH_ID = Menu.FIRST + 3;
    public static final int NEW_EMAIL_ID = Menu.FIRST + 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pts = new PTS(getSherlockActivity().getApplicationContext());
        if (SearchCacher.hasLastPckgSearch()) {
            new SearchPackageInfoTask().execute();
        }

        ptsMadisonInfo = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pts_fragment, container, false);

        setHasOptionsMenu(true);
        getSherlockActivity().getSupportActionBar().setTitle(R.string.search_packages);

        ptsPckgList = (ListView) rootView.findViewById(R.id.ptsPckgList);

        ptsMadisonList = (ListView) rootView.findViewById(R.id.ptsMadisonList);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.pts_exp_list_header, ptsMadisonList, false);
        ptsMadisonList.addHeaderView(header, null, false);

        ptsSearchBar = (SearchBarView) rootView.findViewById(R.id.ptsSearchBarView);
        ptsSearchBar.setHintAndType(R.string.pts_search_hint, InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        ptsPckgInfo = (TextView) rootView.findViewById(R.id.ptsPckgInfo);
        emptyTextView = (TextView) rootView.findViewById(R.id.ptsEmptyTextView);

        ptsSearchBar.setOnSearchActionListener(new OnSearchActionListener() {

            @Override
            public void onSearchAction(String searchInput) {
                if ((searchInput != null) && !searchInput.equals("")) {
                    searchPckg(searchInput);
                }
            }});

        return rootView;
    }

    public void setupMadisonInfoList() {
        ptsMadisonList.setDividerHeight(1);
        ptsMadisonList.setClickable(false);

        ptsMadisonList.setAdapter(new ArrayAdapter<String>(getSherlockActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, ptsMadisonInfo));

        registerForContextMenu(ptsMadisonList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Add subscription icon
        MenuItem subMenuItem = menu.add(0, SUBSCRIPTION_ID, Menu.CATEGORY_SECONDARY, "");
        subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        setSubscriptionIcon(subMenuItem, SearchCacher.getLastPckgName());

        menu.add(0, REFRESH_ID, Menu.CATEGORY_ALTERNATIVE, R.string.refresh)
                .setIcon(R.drawable.refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, NEW_EMAIL_ID, Menu.CATEGORY_SECONDARY, R.string.submit_new_bug_report)
                .setIcon(R.drawable.new_email).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        setSubscriptionIcon(menu.findItem(SUBSCRIPTION_ID), SearchCacher.getLastPckgName());
        super.onPrepareOptionsMenu(menu);
    }

    public void setSubscriptionIcon(MenuItem subMenuItem, String pckgName) {
        if ((pckgName != null) && pts.isSubscribedTo(pckgName)) {
            subMenuItem.setIcon(R.drawable.subscribed);
            subMenuItem.setTitle(R.string.unsubscribe);
        } else {
            subMenuItem.setIcon(R.drawable.unsubscribed);
            subMenuItem.setTitle(R.string.subscribe);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SUBSCRIPTION_ID:
                String pckgName = SearchCacher.getLastPckgName();
                if (pckgName != null) {
                    if (pts.isSubscribedTo(pckgName)) {
                        item.setIcon(R.drawable.unsubscribed);
                        item.setTitle(R.string.subscribe);
                        pts.removeSubscriptionTo(pckgName);
                    } else {
                        item.setIcon(R.drawable.subscribed);
                        item.setTitle(R.string.unsubscribe);
                        pts.addSubscriptionTo(pckgName);
                    }
                }
                return true;
            case REFRESH_ID:
                if (SearchCacher.hasLastPckgSearch()) {
                    new SearchPackageInfoTask().execute(true);
                }
                return true;
            case NEW_EMAIL_ID:
                forwardToMailApp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void forwardToMailApp() {
        if (SearchCacher.hasLastPckgSearch()) {
            String pckgName = SearchCacher.getLastPckgName();

            UIUtils.forwardToMailApp(getSherlockActivity(), BTS.NEWBUGREPORTMAIL,
                    BTS.getNewBugReportSubject(pckgName),
                    BTS.getNewBugReportBody(pckgName, SearchCacher.getLastPckgVersion()));
        }
    }

    public void searchPckg(String input) {
        boolean searchPckgNamesFirst = PreferenceManager.getDefaultSharedPreferences(
                getSherlockActivity().getApplicationContext()).getBoolean("searchSimilarPckgs", true);
        if (searchPckgNamesFirst) {
            new SearchPckgNamesTask().execute(input);
        } else {
            SearchCacher.setLastSearchByPckgName(input);
            new SearchPackageInfoTask().execute();
        }
    }

    class SearchPackageInfoTask extends AsyncTask<Boolean, Integer, Void> {

        private String pckgName, pckgVersion, pckgBugCount, pckgMaintName, pckgMaintMail, pckgUploaders,
                pckgBinNames;
        private final static int pckgInfoCount = 7;

        private ProgressDialog progressDialog;
        private String progressMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ptsPckgList != null) {
                ptsPckgList.setVisibility(View.GONE);
            }
            progressMessage = getString(R.string.searching_info_about,
                    SearchCacher.getLastPckgName());
            progressDialog = ProgressDialog.show(getSherlockActivity(), getString(R.string.searching),
                    progressMessage, true, false);
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            //If called with execute(true) set the cache to always bring fresh results
            if ((params.length != 0) && params[0]) {
                Cacher.disableCache();
            }
            pckgName = SearchCacher.getLastPckgName(); //Last Package Name

            if (pckgName != null) {

                pckgVersion = pts.getLatestVersion(pckgName);
                publishProgress(2);
                SearchCacher.setLastPckgVersion(pckgVersion);

                //set Maintainer Info
                pckgMaintMail = pts.getMaintainerEmail(pckgName);
                pckgMaintName = pts.getMaintainerName(pckgName);
                publishProgress(3);

                pckgBugCount = pts.getBugCounts(pckgName);
                publishProgress(4);

                if (!pckgVersion.equals("") && !pckgBugCount.equals("")) {
                    //Set Uploader Names
                    pckgUploaders = Arrays.toString(pts.getUploaderNames(pckgName))
                                    .replaceAll("^\\[|\\]$", "");
                    publishProgress(5);
                    //Set Binary Names
                    pckgBinNames = Arrays.toString(pts.getBinaryNames(pckgName))
                                    .replaceAll("^\\[|\\]$", "");
                    publishProgress(6);
                    ptsMadisonInfo = pts.getMadisonInfo(pckgName);
                    publishProgress(7);
                }
            }

            if ((params.length != 0) && params[0]) {
                Cacher.enableCache();
            }
            return null;
        }

        @Override
        @SuppressLint("NewApi")
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
            if (pckgName != null) {
                ptsSearchBar.getInputEditText().setText(pckgName);

                if (!pckgVersion.equals("") && !pckgBugCount.equals("")) {
                    ptsPckgInfo.setText(getString(R.string.pckg_info_format, pckgName, pckgVersion,
                            pckgMaintName, pckgMaintMail, pckgBugCount, pckgUploaders, pckgBinNames));
                    ptsPckgInfo.setMovementMethod(LinkMovementMethod.getInstance());
                    emptyTextView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                    ptsMadisonInfo = new ArrayList<String>();
                    ptsPckgInfo.setText("");
                }
                setupMadisonInfoList();
                ptsMadisonList.setVisibility(View.VISIBLE);
            }
            UIUtils.hideSoftKeyboard(getActivity(), ptsSearchBar.getInputEditText());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getSherlockActivity().invalidateOptionsMenu();
            }
        }

        @Override
        public void onProgressUpdate(Integer... args) {
            progressDialog.setMessage(progressMessage
                    + getString(R.string.info_retrieved, args[0] + "/" + pckgInfoCount));
        }
    }

    class SearchPckgNamesTask extends AsyncTask<String, Integer, Void> {

        private ArrayList<String> pckgNames = null;

        private ProgressDialog progressDialog;
        private String progressMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ptsMadisonList != null) {
                ptsMadisonList.setVisibility(View.GONE);
            }
            progressMessage = getString(R.string.searching_info_please_wait);
            progressDialog = ProgressDialog.show(getSherlockActivity(), getString(R.string.searching),
                    progressMessage, true, false);
        }

        @Override
        protected Void doInBackground(String... params) {
            if ((params.length != 0) && (params[0] != null)) {
                pckgNames = pts.getSimilarPckgNames(params[0]);
            }
            return null;
        }

        @Override
        @SuppressLint("NewApi")
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
            if (pckgNames != null) {
                if (pckgNames.size() == 0) {
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(),
                            R.layout.simple_list_child, pckgNames);

                    ptsPckgList.setAdapter(adapter);
                    ptsPckgList.setVisibility(View.VISIBLE);

                    ptsPckgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                            final String item = (String) parent.getItemAtPosition(position);
                            // search pckg info for selected pckg name
                            SearchCacher.setLastSearchByPckgName(item);
                            new SearchPackageInfoTask().execute();
                        }
                    });
                }
            }
            UIUtils.hideSoftKeyboard(getActivity(), ptsSearchBar.getInputEditText());
        }
    }
}
