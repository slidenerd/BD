package slidenerd.vivz.bucketdrops.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.adapters.AdapterDrops;
import slidenerd.vivz.bucketdrops.adapters.AddListener;
import slidenerd.vivz.bucketdrops.adapters.Divider;
import slidenerd.vivz.bucketdrops.adapters.OnAddListener;
import slidenerd.vivz.bucketdrops.adapters.SimpleTouchCallback;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.extras.Util;
import slidenerd.vivz.bucketdrops.widgets.BucketRecyclerView;

import static slidenerd.vivz.bucketdrops.extras.Constants.COMPLETED;
import static slidenerd.vivz.bucketdrops.extras.Constants.POSITION;
import static slidenerd.vivz.bucketdrops.extras.Constants.SHOW_COMPLETE;
import static slidenerd.vivz.bucketdrops.extras.Constants.SHOW_INCOMPLETE;
import static slidenerd.vivz.bucketdrops.extras.Constants.SORT_ASCENDING_DATE;
import static slidenerd.vivz.bucketdrops.extras.Constants.SORT_DESCENDING_DATE;
import static slidenerd.vivz.bucketdrops.extras.Constants.WHEN;

public class ActivityMain extends AppCompatActivity {
    public static final String TAG_DIALOG = "dialog_add";
    private Realm mRealm;

    private RealmResults<Drop> mResults;
    private BucketRecyclerView mRecycler;
    private Button mBtnAdd;
    //The View to be displayed when the RecyclerView is empty.
    private View mEmptyView;
    private Toolbar mToolbar;
    private AdapterDrops mAdapter;
    private ImageView mBackground;
    private OnAddListener mOnAddListener = new OnAddListener() {
        @Override
        public void onAdd(Drop drop) {
            mAdapter.add(drop);
        }
    };
    //When the add item button is clicked, show a dialog that lets the person add a new item
    private View.OnClickListener mBtnAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogAdd();
        }
    };
    private AddListener mAddListener = new AddListener() {
        @Override
        public void add() {
            showDialogAdd();
        }
    };
    private DialogActions.MarkedListener mMarkedListener = new DialogActions.MarkedListener() {
        @Override
        public void onMarked(int position) {
            //Mark an item as complete in our database when the user clicks "Mark as Complete"
            mAdapter.markComplete(position);
        }
    };
    private AdapterDrops.MarkListener mMarkListener = new AdapterDrops.MarkListener() {
        @Override
        public void onMark(int position) {
            //Launch the DialogActions which are shown when the user clicks on some item from our RecyclerView
            Bundle arguments = new Bundle();
            arguments.putInt(POSITION, position);
            DialogActions dialog = new DialogActions();
            dialog.setArguments(arguments);
            dialog.setDialogActionsListener(mMarkedListener);
            dialog.show(getSupportFragmentManager(), "dialog_actions");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRealm = Realm.getDefaultInstance();
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        initBackgroundImage();
        initRecycler();
        if (savedInstanceState == null) {
            Util.notifyUpcomingDrops(this);
        }
    }

    private void initRecycler() {
        mRecycler = (BucketRecyclerView) findViewById(R.id.recycler_tasks);
        mEmptyView = findViewById(R.id.recycler_empty_view);
        //Set an Empty View to be displayed when the RecyclerView has no items
        mRecycler.setEmptyView(mEmptyView);

        //hide the toolbar when the bucket is empty and show it when it has atleast one item in it
        mRecycler.setToolbar(mToolbar);

        //Add a divider to our RecyclerView
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mBtnAdd = (Button) findViewById(R.id.btn_add);

        mBtnAdd.setOnClickListener(mBtnAddListener);
        mResults = mRealm.where(Drop.class).findAllSortedAsync(WHEN);
        mAdapter = new AdapterDrops(this, mRealm, mResults);
        mResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.updateResults(mResults);
                mResults.removeChangeListener(this);
            }
        });
        //Let our Activity handle the event when the footer is clicked from our RecyclerView
        mAdapter.setAddListener(mAddListener);
        //Let our Activity handle the event when the Add Drop button is clicked from the empty view
        mAdapter.setMarkListener(mMarkListener);

        //Handler the swipe from our RecyclerView
        ItemTouchHelper.Callback callback =
                new SimpleTouchCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecycler);
        mRecycler.setAdapter(mAdapter);
    }

    private void initBackgroundImage() {
        //Convert our background image to a specific size that suits our device's screen size
        //THIS HAPPENS ON THE UI THREAD WHICH IS A POSSIBLE AREA FOR IMPROVEMENT
        mBackground = (ImageView) findViewById(R.id.img_background);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Bitmap bitmapModified = Util.getScaledVersion(this, displayMetrics.widthPixels, displayMetrics.heightPixels);
        mBackground.setImageBitmap(bitmapModified);
    }

    private void showDialogAdd() {
        DialogAdd dialog = new DialogAdd();
        dialog.setOnAddListener(mOnAddListener);
        dialog.show(getSupportFragmentManager(), TAG_DIALOG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean handled = true;
        switch (id) {
            case R.id.action_add:
                showDialogAdd();
                break;
            default:
                handled = false;
                break;
        }
        int sortOption = SORT_ASCENDING_DATE;
        if (id == R.id.action_show_completed) {
            sortOption = SHOW_COMPLETE;
            mResults = mRealm.where(Drop.class).equalTo(COMPLETED, true).findAllAsync();
        } else if (id == R.id.action_show_uncompleted) {
            sortOption = SHOW_INCOMPLETE;
            mResults = mRealm.where(Drop.class).equalTo(COMPLETED, false).findAllAsync();
        } else if (id == R.id.action_sort_ascending_date) {
            sortOption = SORT_ASCENDING_DATE;
            mResults = mRealm.where(Drop.class).findAllSortedAsync(WHEN, Sort.ASCENDING);
        } else if (id == R.id.action_sort_descending_date) {
            sortOption = SORT_DESCENDING_DATE;
            mResults = mRealm.where(Drop.class).findAllSortedAsync(WHEN, Sort.DESCENDING);
        } else {
            mResults = mRealm.where(Drop.class).findAllAsync();
        }
        BucketDropsApp.storeSortOption(sortOption);
        mResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.updateResults(mResults);
                mResults.removeChangeListener(this);
            }
        });
        return handled;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
