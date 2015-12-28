package slidenerd.vivz.bucketdrops.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Queue;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.extras.Util;


/**
 * Created by vivz on 26/10/15.
 */
public class BucketPickerView extends LinearLayout implements View.OnTouchListener {

    public static final int DELAY = 150;
    public static final int MESSAGE_CHECK_BTN_STILL_PRESSED = 1;
    public static final int DATE = 0;
    public static final int MONTH = 1;
    public static final int YEAR = 2;
    private SimpleDateFormat mFormatter = new SimpleDateFormat();
    private Calendar mCalendar;
    private TextView mTextMonth;
    private TextView mTextDate;
    private TextView mTextYear;
    private ImageButton mBtnIncDate;
    private ImageButton mBtnDecDate;
    private ImageButton mBtnIncMonth;
    private ImageButton mBtnDecMonth;
    private ImageButton mBtnIncYear;
    private ImageButton mBtnDecYear;
    private boolean mIncrement = false;
    private boolean mDecrement = false;
    private boolean mPressed = false;
    private int mQuantity;
    public final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CHECK_BTN_STILL_PRESSED:
                    if (mIncrement) {
                        increment(mQuantity);
                    }
                    if (mDecrement) {
                        decrement(mQuantity);
                    }
                    if (mIncrement
                            || mDecrement) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_BTN_STILL_PRESSED, DELAY);
                    }
                    break;
            }
            return true;
        }
    });

    public BucketPickerView(Context context) {
        super(context);
        init(context);
    }

    public BucketPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BucketPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BucketPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/raleway_thin.ttf");

        mCalendar = Calendar.getInstance();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.bucket_picker_view, this);
        mTextDate = (TextView) view.findViewById(R.id.tv_date);
        mTextMonth = (TextView) view.findViewById(R.id.tv_month);
        mTextYear = (TextView) view.findViewById(R.id.tv_year);

        mBtnIncDate = (ImageButton) view.findViewById(R.id.ib_increment_date);
        mBtnDecDate = (ImageButton) view.findViewById(R.id.ib_decrement_date);
        mBtnIncMonth = (ImageButton) view.findViewById(R.id.ib_increment_month);
        mBtnDecMonth = (ImageButton) view.findViewById(R.id.ib_decrement_month);
        mBtnIncYear = (ImageButton) view.findViewById(R.id.ib_increment_year);
        mBtnDecYear = (ImageButton) view.findViewById(R.id.ib_decrement_year);

        mBtnIncDate.setOnTouchListener(this);
        mBtnDecDate.setOnTouchListener(this);
        mBtnIncMonth.setOnTouchListener(this);
        mBtnDecMonth.setOnTouchListener(this);
        mBtnIncYear.setOnTouchListener(this);
        mBtnDecYear.setOnTouchListener(this);

        mTextDate.setTypeface(typeface);
        mTextMonth.setTypeface(typeface);
        mTextYear.setTypeface(typeface);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int date = mCalendar.get(Calendar.DATE);

        updateUi(date, month, year, 0, 0, 0);
    }

    private void updateUi(int date, int month, int year, int hour, int minute, int second) {
        mCalendar.set(Calendar.DATE, date);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.HOUR, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, second);
        try {
            String today = year + " " + month + " " + date + " " + hour + " " + minute + " " + second;
            mFormatter.applyLocalizedPattern("yyyy MM dd hh mm ss");
            mCalendar.setTime(mFormatter.parse(today));
            mTextYear.setText(year + "");
            mFormatter.applyLocalizedPattern("MMM");
            mTextMonth.setText(mFormatter.format(mCalendar.getTime()).toUpperCase());
            mTextDate.setText(date + "");
        } catch (ParseException e) {
            Log.d("VIVZ", "onRestoreInstanceState: " + e);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("day", mCalendar.get(Calendar.DATE));
        bundle.putInt("month", mCalendar.get(Calendar.MONTH));
        bundle.putInt("year", mCalendar.get(Calendar.YEAR));
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("instanceState");
            int year = bundle.getInt("year");
            int month = bundle.getInt("month") + 1;
            int date = bundle.getInt("day");
            updateUi(date, month, year, 0, 0, 0);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPressed = true;
                switch (id) {
                    case R.id.ib_increment_date:
                        mIncrement = true;
                        mQuantity = DATE;
                        increment(mQuantity);
                        toggleDrawable(mBtnIncDate, true);
                        break;
                    case R.id.ib_decrement_date:
                        mDecrement = true;
                        mQuantity = DATE;
                        decrement(mQuantity);
                        toggleDrawable(mBtnDecDate, false);
                        break;
                    case R.id.ib_increment_month:
                        mIncrement = true;
                        mQuantity = MONTH;
                        increment(mQuantity);
                        toggleDrawable(mBtnIncMonth, true);
                        break;
                    case R.id.ib_decrement_month:
                        mDecrement = true;
                        mQuantity = MONTH;
                        decrement(mQuantity);
                        toggleDrawable(mBtnDecMonth, false);
                        break;
                    case R.id.ib_increment_year:
                        mIncrement = true;
                        mQuantity = YEAR;
                        increment(mQuantity);
                        toggleDrawable(mBtnIncYear, true);
                        break;
                    case R.id.ib_decrement_year:
                        mDecrement = true;
                        mQuantity = YEAR;
                        decrement(mQuantity);
                        toggleDrawable(mBtnDecYear, false);
                        break;
                }

                mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_BTN_STILL_PRESSED, DELAY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPressed = false;
                toggleDrawable(mBtnIncDate, true);
                toggleDrawable(mBtnDecDate, false);
                toggleDrawable(mBtnIncMonth, true);
                toggleDrawable(mBtnDecMonth, false);
                toggleDrawable(mBtnIncYear, true);
                toggleDrawable(mBtnDecYear, false);
                mIncrement = false;
                mDecrement = false;
                break;
        }

        return true;
    }

    private void toggleDrawable(ImageButton button, boolean up) {
        if (up) {
            if (mPressed) {
                Util.setBackgroundDrawable(button, R.drawable.transparent_box_up);
                Util.setImageDrawable(button, R.drawable.ic_menu_up_colored);
            } else {
                Util.setBackgroundDrawable(button, R.drawable.purple_box_up);
                Util.setImageDrawable(button, R.drawable.ic_menu_up_transparent);
            }
        } else {
            if (mPressed) {
                Util.setBackgroundDrawable(button, R.drawable.transparent_box_down);
                Util.setImageDrawable(button, R.drawable.ic_menu_down_colored);
            } else {
                Util.setBackgroundDrawable(button, R.drawable.purple_box_down);
                Util.setImageDrawable(button, R.drawable.ic_menu_down_transparent);
            }
        }
    }

    private void increment(int quantity) {
        switch (quantity) {
            case DATE:
                mCalendar.add(Calendar.DATE, 1);
                break;
            case MONTH:
                mCalendar.add(Calendar.MONTH, 1);
                break;
            case YEAR:
                mCalendar.add(Calendar.YEAR, 1);
                break;
        }
        mTextDate.setText(mCalendar.get(Calendar.DATE) + "");
        mTextMonth.setText(mFormatter.format(mCalendar.getTime()).toUpperCase());
        mTextYear.setText(mCalendar.get(Calendar.YEAR) + "");
    }

    private void decrement(int quantity) {
        switch (quantity) {
            case DATE:
                mCalendar.add(Calendar.DATE, -1);
                break;
            case MONTH:
                mCalendar.add(Calendar.MONTH, -1);
                break;
            case YEAR:
                mCalendar.add(Calendar.YEAR, -1);
                break;
        }
        mTextDate.setText(mCalendar.get(Calendar.DATE) + "");
        mTextMonth.setText(mFormatter.format(mCalendar.getTime()).toUpperCase());
        mTextYear.setText(mCalendar.get(Calendar.YEAR) + "");
    }

    public long getTime() {
        return mCalendar.getTimeInMillis();
    }

}