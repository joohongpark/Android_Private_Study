/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.profq.call_block_proto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwipeDismissListViewTouchListener implements View.OnTouchListener { // view의 터치리스너 추가
    // Cached ViewConfiguration and system-wide constant values
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed properties
    private ListView mListView;
    private Callbacks mCallbacks;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private List<PendingDismissData> mPendingDismisses = new ArrayList<PendingDismissData>();
    private int mDismissAnimationRefCount = 0;
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private int mDownPosition;
    private View mDownView;
    private View mButtonView;
    private boolean mPaused;
    private int mButtonWidth;
    private float mX;
    private float t_x;

    /**
     * The callback interface used by {@link SwipeDismissListViewTouchListener} to inform its client
     * about a successful dismissal of one or more list item positions.
     */
    public interface Callbacks {
        /**
         * Called to determine whether the given position can be dismissed.
         */
        boolean canDismiss(int position);

        /**
         * Called when the user has indicated they she would like to dismiss one or more list item
         * positions.
         *
         * @param listView               The originating {@link ListView}.
         * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
         *                               order for convenience.
         */
        void onDismiss(ListView listView, int[] reverseSortedPositions);

        /**
         * 테스트 콜백함수
         */
        void button_click(int position, int button_position);
    }

    /**
     * Constructs a new swipe-to-dismiss touch listener for the given list view.
     *
     * @param listView  The list view whose items should be dismissable.
     * @param callbacks The callback to trigger when the user has indicated that she would like to
     *                  dismiss one or more list items.
     */
    public SwipeDismissListViewTouchListener(ListView listView, Callbacks callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        mSlop = vc.getScaledTouchSlop(); // 드래그인지 스크롤인지 확인
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = listView.getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mListView = listView;
        mCallbacks = callbacks;
    }

    /**
     * Enables or disables (pauses or resumes) watching for swipe-to-dismiss gestures.
     *
     * @param enabled Whether or not to watch for gestures.
     */
    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }

    /**
     * Returns an {@link AbsListView.OnScrollListener} to be added to the {@link
     * ListView} using {@link ListView#setOnScrollListener(AbsListView.OnScrollListener)}.
     * If a scroll listener is already assigned, the caller should still pass scroll changes through
     * to this listener. This will ensure that this {@link SwipeDismissListViewTouchListener} is
     * paused during list view scrolling.</p>
     *
     * @see SwipeDismissListViewTouchListener
     */
    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mViewWidth < 2) {
            mViewWidth = mListView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: { // 터치 감지, 리스트뷰 요소 감지
                Log.d("status","ACTION_DOWN");
                if (mPaused) { // 사용 안함
                    return false;
                }

                // TODO: ensure this is a finger, and set a flag

                // Find the child view that was touched (perform a hit test)
                Rect rect = new Rect();
                int childCount = mListView.getChildCount();
                int[] listViewCoords = new int[2];
                mListView.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];

                View child;
                for (int i = 0; i < childCount; i++) {
                    child = (View) mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        View btn;
                        mDownView = (View) ((ViewGroup)child).getChildAt(0);
                        mButtonView = (View) ((ViewGroup)child).getChildAt(1);
                        int[] ButtonViewCoords = new int[2];
                        mButtonView.getLocationOnScreen(ButtonViewCoords);
                        int btn_x = (int) motionEvent.getRawX() - ButtonViewCoords[0];
                        int btn_y = (int) motionEvent.getRawY() - ButtonViewCoords[1];
                        Log.d("btn_x, btn_y",btn_x+", "+btn_y);
                        for(int index=0; index<((ViewGroup)mButtonView).getChildCount(); ++index) {
                            btn = ((ViewGroup)mButtonView).getChildAt(index);
                            btn.getHitRect(rect);
                            if(rect.contains(btn_x, btn_y)) {
                                mCallbacks.button_click(mListView.getPositionForView(mDownView), index );
                            }
                        }
                        break;
                    }
                }
                if (mDownView != null) {
                    mDownX = motionEvent.getRawX();
                    mDownY = motionEvent.getRawY();
                    mDownPosition = mListView.getPositionForView(mDownView);
                    Log.d("mDownPosition","mDownPosition : " + mDownPosition);
                    if (mCallbacks.canDismiss(mDownPosition)) {
                        mVelocityTracker = VelocityTracker.obtain();
                        mVelocityTracker.addMovement(motionEvent);
                    } else {
                        mDownView = null;
                    }
                }
                if(mButtonView != null) {
                    mButtonWidth = mButtonView.getWidth();
                    mButtonView.requestLayout();
                    mX = mDownView.getTranslationX(); // 오류 발생
                }
                return false;
            }

            case MotionEvent.ACTION_CANCEL: { // 목록에서 지울때?
                Log.d("status","ACTION_CANCEL");
                if (mVelocityTracker == null) {
                    break;
                }
                if (mDownView != null && mSwiping) {
                    // cancel
                    mDownView.animate()
                            .translationX(330)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mDownView = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_UP: { // 손가락을 뗄 때
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - mDownX; // 손가락을 스와이프 한 거리

                Log.d("deltaX_ACTION_UP", ""+deltaX);
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity(); // x 속도
                float absVelocityX = Math.abs(velocityX); // x 속도 절대값
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity()); // y 속도 절대값
                boolean dismiss = false;
                //boolean dismissRight = false;
                boolean switchRight = false;

                /*
                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) { // 스와이프한 거리가 뷰의 반을 넘어가면
                    // 목록에서 지움
                    //dismiss = true;
                    dismissRight = deltaX > 0; // 스와이프를 왼쪽으로 하면 true
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                        && absVelocityY < absVelocityX && mSwiping) {
                    // dismiss only if flinging in the same direction as dragging (손가락 팅기는거)
                    //dismiss = (velocityX < 0) == (deltaX < 0);
                    dismissRight = mVelocityTracker.getXVelocity() > 0;
                }

                */

                if (    (mMinFlingVelocity <= absVelocityX) &&
                        (absVelocityX <= mMaxFlingVelocity) &&
                        (absVelocityY < absVelocityX) &&
                         mSwiping) {
                    // dismiss only if flinging in the same direction as dragging (가속에 따라 손가락 팅기는거 판)
                    //dismiss = (velocityX < 0) == (deltaX < 0);
                    switchRight = velocityX < 0;
                }


                if(true) {
                    // 안지움
                    if(mButtonWidth > Math.abs(mDownView.getTranslationX())) { // 안보이게
                        mDownView.animate()
                                .translationX(0)
                                .alpha(1)
                                .setDuration(mAnimationTime)
                                .setListener(null);
                        mButtonView.animate()
                                .translationX(0)
                                .alpha(1)
                                .setDuration(mAnimationTime)
                                .setListener(null);
                    } else { // 보이게
                        mDownView.animate()
                                .translationX(-mButtonWidth)
                                .alpha(1)
                                .setDuration(mAnimationTime)
                                .setListener(null);
                        mButtonView.animate()
                                .translationX(-mButtonWidth)
                                .alpha(1)
                                .setDuration(mAnimationTime)
                                .setListener(null);
                    }
                }

                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mDownView = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: { // 스와이프 하는 도중
                if (mVelocityTracker == null || mPaused) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                float deltaY = motionEvent.getRawY() - mDownY;
                if ( (Math.abs(deltaX) > mSlop) && Math.abs(deltaY) < Math.abs(deltaX) / 2) { // 스와이프인지 스크롤인지
                    mSwiping = true;
                    mSwipingSlop = (deltaX > 0 ? mSlop : -mSlop);
                    mListView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex()
                                    << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mListView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                } else {
                    mSwiping = false;
                }
                t_x = (deltaX - mSwipingSlop) + mX;
                //if (mSwiping && (Math.abs(mSwipingSlop) <= Math.abs(deltaX + mX))) { // 왼쪽으로 스와이프
                if (mSwiping && (t_x <= 0)) { // 왼쪽으로 스와이프
                    //Log.d("x",""+t_x);
                    //t_x = (deltaX - mSwipingSlop) + mX;
                    mDownView.setTranslationX(t_x);
                    if( (mButtonWidth > Math.abs(t_x))) {
                        mButtonView.setTranslationX(t_x);
                    } else {
                        mButtonView.setTranslationX(-mButtonWidth);
                    }
                    // 스와이프 할 때 색상 흐려지게
                    //mDownView.setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public View view;

        public PendingDismissData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingDismissData other) {
            // Sort by descending position
            return other.position - position;
        }
    }

    public void dismiss(int pos) {
        // dismiss
        final View downView = mListView.getChildAt(pos); // mDownView gets null'd before animation ends
        final int downPosition = pos;
        ++mDismissAnimationRefCount;
        downView.animate()
                .translationX(-mViewWidth)
                .alpha(0)
                .setDuration(mAnimationTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        performDismiss(downView, downPosition);
                    }
                });
    }

    private void performDismiss(final View dismissView, final int dismissPosition) {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more performant.

        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();
        Log.d("test","performDismiss : " + 0);

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("end","end");
                --mDismissAnimationRefCount;
                if (mDismissAnimationRefCount == 0) {
                    // No active animations, process all pending dismisses.
                    // Sort by descending position
                    Collections.sort(mPendingDismisses);

                    int[] dismissPositions = new int[mPendingDismisses.size()];
                    for (int i = mPendingDismisses.size() - 1; i >= 0; i--) {
                        dismissPositions[i] = mPendingDismisses.get(i).position;
                    }
                    mCallbacks.onDismiss(mListView, dismissPositions);

                    // Reset mDownPosition to avoid MotionEvent.ACTION_UP trying to start a dismiss
                    // animation with a stale position
                    mDownPosition = ListView.INVALID_POSITION;

                    ViewGroup.LayoutParams lp;
                    for (PendingDismissData pendingDismiss : mPendingDismisses) {
                        // Reset view presentation
                        Log.d("pendingDismiss","" + pendingDismiss);
                        pendingDismiss.view.setAlpha(1f);
                        pendingDismiss.view.setTranslationX(0);
                        lp = pendingDismiss.view.getLayoutParams();
                        lp.height = originalHeight;
                        pendingDismiss.view.setLayoutParams(lp);
                    }
                    // Send a cancel event
                    long time = SystemClock.uptimeMillis();
                    MotionEvent cancelEvent = MotionEvent.obtain(time, time,
                            MotionEvent.ACTION_CANCEL, 0, 0, 0);
                    mListView.dispatchTouchEvent(cancelEvent);

                    mPendingDismisses.clear();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                Log.d("height",""+valueAnimator.getAnimatedValue());
                dismissView.setLayoutParams(lp);
            }
        });

        mPendingDismisses.add(new PendingDismissData(dismissPosition, dismissView));
        animator.start();
    }

}