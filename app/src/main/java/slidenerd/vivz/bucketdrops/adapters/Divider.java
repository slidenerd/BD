package slidenerd.vivz.bucketdrops.adapters;

/**
 * Created by vivz on 07/07/15.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.extras.Util;

public class Divider extends RecyclerView.ItemDecoration {

    public static final int LIST_HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int LIST_VERTICAL = LinearLayoutManager.VERTICAL;
    private Drawable mDivider;
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
        if (orientation != LIST_HORIZONTAL && orientation != LIST_VERTICAL) {
            throw new IllegalArgumentException("Invalid Orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LIST_VERTICAL) {
            drawVerticalDivider(c, parent);
        } else {
            drawHorizontalDivider(c, parent);
        }
    }

    public void drawVerticalDivider(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            //Draw dividers for all items that are not HEADER and FOOTER, all items usually have a ViewGroup as root in their layout, to ensure this works properly make sure either the header and footer don't use ViewGroup as their root or change the conditions here accordingly
            //Draw a divider below only if the current child is an instance of ViewGroup, in this app currently, the footer is a Button , hence it wont draw a divider below it
            if (child instanceof ViewGroup) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    public void drawHorizontalDivider(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin +
                    Math.round(ViewCompat.getTranslationX(child));
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LIST_VERTICAL) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
