package slidenerd.vivz.bucketdrops.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.adapters.OnAddDropListener;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.extras.Util;
import slidenerd.vivz.bucketdrops.widgets.CustomDatePicker;


public class DialogAdd extends DialogFragment implements View.OnClickListener, TextView.OnEditorActionListener {

    private Activity mContext;
    //Title of the dialog
    private TextView mTextTitle;
    //The close button for this dialog
    private ImageButton mBtnClose;
    //The area where the user can type his/her goal
    private EditText mInputWhat;
    //The control with which user can select the date for his/her goal by which they feel they wanna accomplish their goal
    private CustomDatePicker mInputWhen;
    //The button clicking which the goal and date will be added to the database
    private Button mBtnAddDrop;
    //The object which will be notified when the user hits the "Add Drop" button
    private OnAddDropListener mOnAddDropListener;

    public void setAddDropListener(OnAddDropListener OnAddDropListener) {
        mOnAddDropListener = OnAddDropListener;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        //init the date picker
        initDatePicker(view);
        //monitor the user clicking buttons such as DONE on the virtual keyboard
        mInputWhat.setOnEditorActionListener(this);
        mBtnClose.setOnClickListener(this);
        mBtnAddDrop.setOnClickListener(this);
        //load custom fonts wherever appropriate
        initCustomFont();
    }

    private void initViews(View view) {
        mTextTitle = (TextView) view.findViewById(R.id.text_dialog_title);
        //The close button for this dialog
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_dialog_close);
        //The area where the user can type his/her goal
        mInputWhat = (EditText) view.findViewById(R.id.input_task);
        //The control with which user can select the date for his/her goal by which they feel they wanna accomplish their goal
        mInputWhen = (CustomDatePicker) view.findViewById(R.id.input_time);
        //The button clicking which the goal and date will be added to the database
        mBtnAddDrop = (Button) view.findViewById(R.id.btn_add_drop);
    }


    private void initDatePicker(View view) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void initCustomFont() {
        mTextTitle.setTypeface(Util.loadRalewayRegular(mContext));
        mInputWhat.setTypeface(Util.loadRalewayThin(mContext));
        mBtnAddDrop.setTypeface(Util.loadRalewayThin(mContext));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_drop:
                addAction();
                break;
        }
        dismiss();
    }

    private void addAction() {
        if (mOnAddDropListener != null) {
            //Load the taskname, convert the user entered date to a specific value of 0 hours 0 minutes and 0 seconds, 12 am precisely on the day they want things to be done
            String taskName = mInputWhat.getText().toString();
            int dayOfMonth = 0;
            int month = 0;
            int year = 0;
            GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, dayOfMonth, 0, 0, 0);
            long currentTime = System.currentTimeMillis();
            long when = gregorianCalendar.getTimeInMillis();
            if (when < currentTime) {
                Toast.makeText(getActivity(), "Right Today? Are You Serious!", Toast.LENGTH_LONG).show();
            } else {
                Drop drop = new Drop(taskName, System.currentTimeMillis(), when, false);
                mOnAddDropListener.onClickAddDrop(drop);
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            //Hide the keyboard when the user presses done on it
            mInputWhen.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mInputWhat.getWindowToken(), 0);
            return true;
        }
        return false;
    }
}
