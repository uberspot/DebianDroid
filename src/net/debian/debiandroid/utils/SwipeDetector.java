package net.debian.debiandroid.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;


public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {

    protected static final int SWIPE_THRESHOLD = 80;
    protected static final int SWIPE_VELOCITY_THRESHOLD = 80;

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        try {
            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if ((Math.abs(diffX) > SWIPE_THRESHOLD) && (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)) {
                    if (diffX > 0) {
                        // Swipe right
                        return onSwipeRight();
                    } else {
                        // Swipe left
                        return onSwipeLeft();
                    }
                }
            } else {
                if ((Math.abs(diffY) > SWIPE_THRESHOLD) && (Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)) {
                    if (diffY > 0) {
                        //Bottom swipe
                        return onSwipeDown();
                    } else {
                        //Top swipe
                        return onSwipeUp();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean onSwipeRight() { return false; }
    public boolean onSwipeLeft() { return false; }
    public boolean onSwipeDown() { return false; }
    public boolean onSwipeUp() { return false; }

}