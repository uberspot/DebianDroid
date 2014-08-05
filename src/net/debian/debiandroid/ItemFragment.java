
package net.debian.debiandroid;

import net.debian.debiandroid.content.BTSFragment;
import net.debian.debiandroid.content.CIFFragment;
import net.debian.debiandroid.content.Content;
import net.debian.debiandroid.content.DFTPFragment;
import net.debian.debiandroid.content.LinksFragment;
import net.debian.debiandroid.content.PTSFragment;
import net.debian.debiandroid.content.SUBSFragment;
import net.debian.debiandroid.content.UDDFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemFragment extends SherlockFragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static String currentFragID = "";
    public static final int SETTINGS_ID = Menu.FIRST + 1;
    public static boolean isInListDisplayFrag = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Save current loaded fragments id
            currentFragID = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        return rootView;
    }

    /** Returns the appropriate ItemDetailFragment implementation based on the given id
     * @param id a string containing the ContentMenu.Item describing the fragment to be returned
     * @return
     */
    public static ItemFragment getDetailFragment(String id) {
        Bundle arguments = new Bundle();
        arguments.putString(ItemFragment.ARG_ITEM_ID, id);
        ItemFragment fragment;
        if (id.equalsIgnoreCase(Content.BTS)) {
            fragment = new BTSFragment();
        } else if (id.equalsIgnoreCase(Content.PTS)) {
            fragment = new PTSFragment();
        } else if (id.equalsIgnoreCase(Content.UDD)) {
            fragment = new UDDFragment();
        } else if (id.equalsIgnoreCase(Content.DFTP)) {
            fragment = new DFTPFragment();
        } else if (id.equalsIgnoreCase(Content.CIF)) {
            fragment = new CIFFragment();
        } else if (id.equalsIgnoreCase(Content.SUBS)) {
            fragment = new SUBSFragment();
        } else if (id.equalsIgnoreCase(Content.LINKS)) {
            fragment = new LinksFragment();
        } else {
            fragment = new ItemFragment();
        }
        fragment.setArguments(arguments);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public static String getNextFragmentId() {
        if (currentFragID.equals("")) {
            return Content.PTS;
        }
        int position = Content.ITEMS.indexOf(new Content.ContentItem(currentFragID, ""));

        if ((position++ != -1) && (position < Content.ITEMS.size())) {
            return Content.ITEMS.get(position).id;
        }
        return currentFragID;
    }

    public static String getPreviousFragmentId() {
        if (currentFragID.equals("")) {
            return null;
        }
        int position = Content.ITEMS.indexOf(new Content.ContentItem(currentFragID, ""));
        // return to ItemListActivity and don't show fragments anymore
        if (position == 0) {
            return null;
        }
        if ((position-- != -1) && (position >= 0)) {
            return Content.ITEMS.get(position).id;
        }
        return currentFragID;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getSettingsMenuItem(menu);
    }

    public static void getSettingsMenuItem(Menu menu) {
        menu.add(0, SETTINGS_ID, Menu.CATEGORY_CONTAINER, "Settings").setIcon(R.drawable.settings)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public void hideSoftKeyboard(EditText input) {
        if ((getActivity().getCurrentFocus() != null) && (getActivity().getCurrentFocus() instanceof EditText)) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SETTINGS_ID:
                startActivity(new Intent(this.getSherlockActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void forwardToMailApp(Context context, String recipient, String subject, String body) {
        String uri = new StringBuilder("mailto:" + Uri.encode(recipient)).append("?subject=" + subject)
                .append("&body=" + body).toString();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));

        /* Send it off to the Activity-Chooser */
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public static void moveToFragment(FragmentManager fm, String fragID, Bundle arguments, boolean goesLeft) {
        if (!fragID.equals(currentFragID)) {
            ItemFragment fragment = getDetailFragment(fragID);
            if (arguments != null) {
                fragment.setArguments(arguments);
            }
            FragmentTransaction ft = fm.beginTransaction();
            if (goesLeft) {
                ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
                        R.anim.push_right_out);
            } else {
                ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in,
                        R.anim.push_left_out);
            }
            ft.replace(R.id.item_detail_container, fragment).commit();
        }
    }
}
