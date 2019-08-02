package org.apache.cordova.statusbar;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class StatusBarViewHelper {

    // For more information, see https://issuetracker.google.com/issues/36911528
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    //This solution was based off of
    //https://stackoverflow.com/questions/7417123/android-how-to-adjust-layout-in-full-screen-mode-when-softkeyboard-is-visible/19494006#answer-42261118

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Activity activity;

    static void assistActivity(Activity activity) {
        new StatusBarViewHelper(activity);
    }

    private StatusBarViewHelper(Activity a) {
        activity = a;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);

        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });

        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            frameLayoutParams.height = usableHeightNow;
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        boolean isFullscreen = ((uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == uiOptions);

        //If not fullscreen, then we have to take the status bar into consideration (represented by r.top)
        //r.bottom defines the keyboard, or navigation bar, or both.

        return isFullscreen ? r.bottom : r.bottom - r.top;
    }
}