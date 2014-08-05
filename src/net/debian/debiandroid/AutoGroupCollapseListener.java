
package net.debian.debiandroid;

import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class AutoGroupCollapseListener implements OnGroupExpandListener {

    public AutoGroupCollapseListener(ExpandableListView listView) {
        this.listView = listView;
    }

    private int previousGroup = -1;
    private ExpandableListView listView;

    @Override
    public void onGroupExpand(int groupPosition) {
        if ((groupPosition != previousGroup) && (listView != null)) {
            listView.collapseGroup(previousGroup);
        }
        previousGroup = groupPosition;
    }

}
