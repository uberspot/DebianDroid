package com.debian.debiandroid;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ListArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public ListArrayAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
    }

    @Override
    public long getItemId(int position) {
      return mIdMap.get(getItem(position));
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }
  }