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

import slidenerd.vivz.bucketdrops.R;


public class DialogActions extends DialogFragment implements View.OnClickListener {

    private Bundle mArguments;
    private Button mBtnMarkCompleted;
    private ImageButton mBtnClose;
    private ActionListener mListener;
    private Activity mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArguments = getArguments();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_actions, container, false);
    }

    public void setDialogActionsListener(ActionListener listener) {
        mListener = listener;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnMarkCompleted = (Button) view.findViewById(R.id.btn_completed);
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_dialog_close);
        mBtnMarkCompleted.setOnClickListener(this);
        mBtnClose.setOnClickListener(this);
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
        //When the user marks an item as complete, get the item's id and notify the interested listeners so that they can further process this event
        int position = mArguments.getInt(ActionListener.POSITION);
        mListener.onClickComplete(position);
    }

    /**
     * Created by vivz on 14/07/15.
     */
    public interface ActionListener {
        String POSITION = "position";

        void onClickComplete(int position);
    }
}
