package slidenerd.vivz.bucketdrops.adapters;

/**
 * Created by vivz on 07/07/15.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.extras.Util;

public class Divider extends RecyclerView.ItemDecoration {
    public static final int LIST_VERTICAL = LinearLayoutManager.VERTICAL;
    //The drawable used to draw the divider
    private Drawable mDivider;
    //The current orientation of the RecyclerView's LayoutManager , horizontal or vertical
    private int mOrientation;

    @SuppressLint("NewApi")
    public Divider(Context context, int orientation) {
        if (Util.isLollipopOrMore()) {
            mDivider = context.getResources().getDrawable(R.drawable.divider, context.getTheme());
        } else {
            mDivider = context.getResources().getDrawable(R.drawable.divider);
        }
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != LIST_VERTICAL) {
            throw new IllegalArgumentException("Invalid Orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LIST_VERTICAL) {
            drawHorizontalDivider(c, parent);
        }
    }


    public void drawHorizontalDivider(Canvas c, RecyclerView parent) {
        // get the left padding of the RecyclerView
        final int left = parent.getPaddingLeft();
        //get the total width of the parent and subtract the right padding from it
        final int right = parent.getWidth() - parent.getPaddingRight();
        //get the number of children inside this RecyclerView [includes headers and footers if any]
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            //Get the current child inside the RecyclerView
            final View child = parent.getChildAt(i);
            //Draw dividers for all items that are not HEADER and FOOTER, all items usually have a ViewGroup as root in their layout, to ensure this works properly make sure either the header and footer don't use ViewGroup as their root or change the conditions here accordingly
            //Draw a divider below only if the current child is an instance of ViewGroup, in this app currently, the footer is a Button , hence it wont draw a divider below it
            if (child instanceof ViewGroup) {
                //the top of the divider
                final int top = child.getTop();
                //the bottom of the divider
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LIST_VERTICAL) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }
    }
}
