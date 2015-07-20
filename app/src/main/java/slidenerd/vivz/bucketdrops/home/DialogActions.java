package slidenerd.vivz.bucketdrops.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.extras.Util;


public class DialogActions extends DialogFragment implements View.OnClickListener {

    private Bundle mArguments;
    private TextView mTextCompleted;
    private Button mBtnMarkCompleted;
    private ImageButton mBtnClose;
    private Actions mListener;
    private Activity parent;

    private long getDropId() {
        return mArguments.getLong(Actions.DROP_POSITION);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArguments = getArguments();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_actions, container, false);
    }

    public void setDialogActionsListener(Actions listener) {
        mListener = listener;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextCompleted = (TextView) view.findViewById(R.id.btn_completed);
        mBtnMarkCompleted = (Button) view.findViewById(R.id.btn_completed);
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_dialog_close);
        mBtnMarkCompleted.setOnClickListener(this);
        mBtnClose.setOnClickListener(this);
        mTextCompleted.setTypeface(Util.loadRalewayRegular(parent));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_completed:
                completeAction();
                break;
        }
        dismiss();
    }

    private void completeAction() {
        if (mListener == null) return;
        mListener.onClickComplete(getDropId());
    }

    /**
     * Created by vivz on 14/07/15.
     */
    public static interface Actions {
        String DROP_POSITION = "drop_position";

        void onClickComplete(long dropId);

    }
}
