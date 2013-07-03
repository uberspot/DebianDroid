package com.debian.debiandroid.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Helper class for providing sample content for user interfaces */
public class ContentMenu {

    /** An array of sample items. */
    public static List<MenuItem> ITEMS = new ArrayList<MenuItem>();

    /**  A map of sample items, by ID. */
    public static Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

    public enum ITEM {PTS, BTS, UDD, SUBS, CIF};
    
    static {
        addItem(new MenuItem(ITEM.PTS.toString(), "Package Tracking"));
        addItem(new MenuItem(ITEM.BTS.toString(), "Bug Tracking"));
        addItem(new MenuItem(ITEM.UDD.toString(), "UDD"));
        addItem(new MenuItem(ITEM.SUBS.toString(), "Favourites"));
        addItem(new MenuItem(ITEM.CIF.toString(), "Common Interest Finder"));
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
