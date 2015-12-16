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

import java.util.GregorianCalendar;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.adapters.OnAddListener;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.extras.Util;
import slidenerd.vivz.bucketdrops.widgets.CustomDatePicker;


public class DialogAdd extends DialogFragment
        implements TextView.OnEditorActionListener {

    private Activity mContext;
    //Title of the dialog
    private TextView mTitle;
    //The close button for this dialog
    private ImageButton mBtnClose;
    //The area where the user can type his/her goal
    private EditText mInputWhat;
    //The control with which user can select the date for his/her goal by which they feel they wanna accomplish their goal
    private CustomDatePicker mInputWhen;
    //The button clicking which the goal and date will be added to the database
    private Button mBtnAdd;
    //The object which will be notified when the user hits the "Add Drop" button
    private OnAddListener mListener;

    private View.OnClickListener mBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add:
                    addAction();
                    break;
            }
            dismiss();
        }
    };

    public void setOnAddListener(OnAddListener listener) {
        mListener = listener;
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
        mTitle = (TextView) view.findViewById(R.id.text_dialog_title);
        //The close button for this dialog
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_dialog_close);
        //The area where the user can type his/her goal
        mInputWhat = (EditText) view.findViewById(R.id.input_task);
        //The control with which user can select the date for his/her goal by which they feel they wanna accomplish their goal
        mInputWhen = (CustomDatePicker) view.findViewById(R.id.input_time);
        //The button clicking which the goal and date will be added to the database
        mBtnAdd = (Button) view.findViewById(R.id.btn_add);
        //monitor the user clicking buttons such as DONE on the virtual keyboard
        mInputWhat.setOnEditorActionListener(this);
        mBtnClose.setOnClickListener(mBtnClickListener);
        mBtnAdd.setOnClickListener(mBtnClickListener);
        //load custom fonts wherever appropriate
        initCustomFont();
    }


    private void initCustomFont() {
        mTitle.setTypeface(Util.loadRalewayRegular(mContext));
        mInputWhat.setTypeface(Util.loadRalewayThin(mContext));
        mBtnAdd.setTypeface(Util.loadRalewayThin(mContext));
    }

    private void addAction() {
        if (mListener != null) {
            //Load the taskname, convert the user entered date to a specific value of 0 hours 0 minutes and 0 seconds, 12 am precisely on the day they want things to be done
            String taskName = mInputWhat.getText().toString();

            int dayOfMonth = mInputWhen.getDay();
            int month = mInputWhen.getMonth();
            int year = mInputWhen.getYear();
            GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, dayOfMonth, 0, 0, 0);
            long currentTime = System.currentTimeMillis();
            long when = gregorianCalendar.getTimeInMillis();
            if (when < currentTime) {
                Toast.makeText(getActivity(), R.string.message_today, Toast.LENGTH_LONG).show();
            } else {
                Drop drop = new Drop(taskName, System.currentTimeMillis(), when, false);
                mListener.onAdd(drop);
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
