package slidenerd.vivz.bucketdrops.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import slidenerd.vivz.bucketdrops.R;

import static slidenerd.vivz.bucketdrops.extras.Constants.POSITION;


public class DialogMark extends DialogFragment {

    private Bundle mArguments;
    private Button mBtnMarkCompleted;
    private ImageButton mBtnClose;
    private MarkedListener mMarkedListener;

    private View.OnClickListener mBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_completed:
                    completeAction();
                    break;
            }
            dismiss();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArguments = getArguments();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mark, container, false);
    }

    public void setDialogActionsListener(MarkedListener listener) {
        mMarkedListener = listener;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnMarkCompleted = (Button) view.findViewById(R.id.btn_completed);
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_close);
        mBtnMarkCompleted.setOnClickListener(mBtnClickListener);
        mBtnClose.setOnClickListener(mBtnClickListener);
    }


    private void completeAction() {
        if (mMarkedListener == null) return;
        //When the user marks an row_drop as complete, get the row_drop's id and notify the interested listeners so that they can further process this event
        int position = mArguments.getInt(POSITION);
        mMarkedListener.onMarked(position);
    }

    /**
     * Created by vivz on 14/07/15.
     */
    public interface MarkedListener {

        void onMarked(int position);
    }
}
