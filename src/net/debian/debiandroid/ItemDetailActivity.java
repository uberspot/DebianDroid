
package net.debian.debiandroid;

import net.debian.debiandroid.contentfragments.Content;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ItemFragment}.
 */
public class ItemDetailActivity extends SherlockFragmentActivity {

    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            String extra = getIntent().getStringExtra(ItemFragment.ARG_ITEM_ID);
            arguments.putString(ItemFragment.ARG_ITEM_ID, extra);
            ItemFragment fragment = ItemFragment.getDetailFragment(extra);
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();

        }

        gestureDetector = new GestureDetectorCompat(this, new SwipeListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestureDetector.onTouchEvent(ev);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Forward the qrcode scan result to the corresponding CIFFragment
        if (resultCode == RESULT_OK) {
            ItemFragment fragment = (ItemFragment) getSupportFragmentManager().findFragmentById(
                    R.id.item_detail_container);
            if ((fragment != null) && fragment.isAdded() && ItemFragment.currentFragID.equals(Content.CIF)) {
                fragment.onActivityResult(requestCode, resultCode, intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (ItemFragment.isInListDisplayFrag) {
                    FragmentManager fm = getSupportFragmentManager();
                    if (fm != null) {
                        fm.popBackStack();
                    }
                } else {
                    NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        ItemFragment.currentFragID = "";
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        DDNotifyService.activityPaused();
    }

    @Override
    public void onResume() {
        super.onResume();
        DDNotifyService.activityResumed();
    }

    class SwipeListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (ItemFragment.isInListDisplayFrag) {
                return false;
            }
            try {
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if ((Math.abs(diffX) > SWIPE_THRESHOLD) && (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)) {
                        if (diffX > 0) {
                            // Swipe right
                            String fragmentID = ItemFragment.getPreviousFragmentId();
                            if (fragmentID == null) {
                                finish();
                            } else {
                                ItemFragment.moveToFragment(getSupportFragmentManager(), fragmentID, null,
                                        false);
                            }
                        } else {
                            // Swipe left
                            ItemFragment.moveToFragment(getSupportFragmentManager(),
                                    ItemFragment.getNextFragmentId(), null, true);
                        }
                        return true;
                    }
                } else {
                    if ((Math.abs(diffY) > SWIPE_THRESHOLD) && (Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)) {
                        if (diffY > 0) {
                            //Bottom swipe
                        } else {
                            //Top swipe
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }
}
