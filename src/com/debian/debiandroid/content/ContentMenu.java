package com.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Helper class for providing sample content for user interfaces */
public class ContentMenu {

    /** An array of items. */
    public static List<MenuItem> ITEMS = new ArrayList<MenuItem>();

    /**  A map of items, by ID. */
    public static Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

    public static final String PTS="PTS", BTS="BTS", UDD="UDD", SUBS="SUBS", CIF="CIF", LINKS="LINKS";
    
    static {
        addItem(new MenuItem(PTS, "Package Tracking"));
        addItem(new MenuItem(BTS, "Bug Tracking"));
        addItem(new MenuItem(UDD, "UDD"));
        addItem(new MenuItem(SUBS, "Favourites"));
        addItem(new MenuItem(CIF, "Common Interest Finder"));
        addItem(new MenuItem(LINKS, "Useful Links"));
    }

    private static void addItem(MenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /** An item representing a piece of content. */
    public static class MenuItem {
        public String id;
        public String content;

        public MenuItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
