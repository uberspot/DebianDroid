
package net.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.debian.debiandroid.R;
import android.content.Context;

/** Helper class for providing content for user interfaces */
public class Content {

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
}
