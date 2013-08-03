package com.debian.debiandroid;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class DExpandableAdapter extends BaseExpandableListAdapter {

	private ArrayList<Object> childtems;
	private LayoutInflater inflater;
	private ArrayList<String> parentItems, child;

	public DExpandableAdapter(ArrayList<String> parents, ArrayList<Object> children) {
		this.parentItems = parents;
		this.childtems = children;
	}

	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		child = (ArrayList<String>) childtems.get(groupPosition);

		TextView textView = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group, null);
		}

		textView = (TextView) convertView.findViewById(R.id.textView1);
		textView.setText(child.get(childPosition));

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child, null);
		}

		((TextView) convertView).setText(parentItems.get(groupPosition));

		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}