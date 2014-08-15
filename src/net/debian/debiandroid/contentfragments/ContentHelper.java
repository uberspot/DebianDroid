
package net.debian.debiandroid.contentfragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.debian.debiandroid.ItemFragment;
import net.debian.debiandroid.R;
import android.content.Context;
import android.os.Bundle;

/** Helper class for providing content for user interfaces */
public class ContentHelper {

    /** An array of items to display. */
    public static List<ContentItem> ITEMS = new ArrayList<ContentItem>();

    /**  A map of items, by ID. */
    public static Map<String, ContentItem> ITEM_MAP = new HashMap<String, ContentItem>();

    public static final String PTS = "PTS", BTS = "BTS", UDD = "UDD", SUBS = "SUBS", CIF = "CIF",
            LINKS = "LINKS", DFTP = "DFTP";

    private static void addItem(ContentItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static void clearItems() {
        try {
            ITEMS.clear();
            ITEM_MAP.clear();
        } catch (UnsupportedOperationException e) {
        }
    }

    /** An item representing a piece of content. */
    public static class ContentItem {

        public String id;
        public String content;

        public ContentItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            ContentItem a = ((ContentItem) o);
            if ((o == null) || !o.getClass().equals(getClass()) || ((id == null) && (a.id != null))) {
                return false;
            }
            return (a.id.equalsIgnoreCase(id));
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return ((64 * 3) + ((id == null) ? 0 : id.hashCode())) * 64;
        }
    }

    public static void initializeItems(Context context) {
        clearItems();
        addItem(new ContentItem(PTS, context.getString(R.string.search_packages)));
        addItem(new ContentItem(BTS, context.getString(R.string.search_bugs)));
        addItem(new ContentItem(UDD, context.getString(R.string.udd)));
        addItem(new ContentItem(DFTP, context.getString(R.string.dftp)));
        addItem(new ContentItem(SUBS, context.getString(R.string.subscriptions)));
        addItem(new ContentItem(CIF, context.getString(R.string.find_common_interests)));
        addItem(new ContentItem(LINKS, context.getString(R.string.links)));
    }

    public static String getNextFragmentId() {
        if (ItemFragment.currentFragID.equals("")) {
            return PTS;
        }
        int position = ITEMS.indexOf(new ContentItem(ItemFragment.currentFragID, ""));
    
        if ((position++ != -1) && (position < ITEMS.size())) {
            return ITEMS.get(position).id;
        }
        return ItemFragment.currentFragID;
    }

    public static String getPreviousFragmentId() {
        if (ItemFragment.currentFragID.equals("")) {
            return null;
        }
        int position = ITEMS.indexOf(new ContentItem(ItemFragment.currentFragID, ""));
        // return to ItemListActivity and don't show fragments anymore
        if (position == 0) {
            return null;
        }
        if ((position-- != -1) && (position >= 0)) {
            return ITEMS.get(position).id;
        }
        return ItemFragment.currentFragID;
    }

    /** Returns the appropriate ItemDetailFragment implementation based on the given id
     * @param id a string containing the ContentMenu.Item describing the fragment to be returned
     * @return
     */
    public static ItemFragment getDetailFragment(String id) {
        Bundle arguments = new Bundle();
        arguments.putString(ItemFragment.ARG_ITEM_ID, id);
        ItemFragment fragment;
        if (id.equalsIgnoreCase(BTS)) {
            fragment = new BTSFragment();
        } else if (id.equalsIgnoreCase(PTS)) {
            fragment = new PTSFragment();
        } else if (id.equalsIgnoreCase(UDD)) {
            fragment = new UDDFragment();
        } else if (id.equalsIgnoreCase(DFTP)) {
            fragment = new DFTPFragment();
        } else if (id.equalsIgnoreCase(CIF)) {
            fragment = new CIFFragment();
        } else if (id.equalsIgnoreCase(SUBS)) {
            fragment = new SUBSFragment();
        } else if (id.equalsIgnoreCase(LINKS)) {
            fragment = new LinksFragment();
        } else {
            fragment = new ItemFragment();
        }
        fragment.setArguments(arguments);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }
}
