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
import slidenerd.vivz.bucketdrops.adapters.Divider;
import slidenerd.vivz.bucketdrops.adapters.AdapterDrops;
import slidenerd.vivz.bucketdrops.adapters.OnAddDropListener;
import slidenerd.vivz.bucketdrops.adapters.SimpleItemTouchHelperCallback;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.extras.Util;
import slidenerd.vivz.bucketdrops.widgets.BucketRecyclerView;

import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_COMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_INCOMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_ASCENDING_DATE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DEFAULT;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DESCENDING_DATE;

public class ActivityMain extends AppCompatActivity implements
        OnAddDropListener,
        AdapterDrops.FooterClickListener,
        AdapterDrops.MarkListener,
        DialogActions.ActionListener {

    private Realm mRealm;

    private RealmResults<Drop> mResults;
    //The RecyclerView that displays all our items
    private BucketRecyclerView mRecycler;
    private Button mBtnAdd;
    //The View to be displayed when the RecyclerView is empty.
    private View mEmptyView;
    private Toolbar mToolbar;
    private AdapterDrops mAdapter;
    private ImageView mBackground;
    //When the add item button is clicked, show a dialog that lets the person add a new item
    private View.OnClickListener mOnClickAddDropListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogAdd();
        }
    };

    private void showDialogAdd() {
        DialogAdd dialog = new DialogAdd();
        dialog.setAddDropListener(this);
        dialog.show(getSupportFragmentManager(), "dialog_add");
    }

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
        mBtnAdd = (Button) findViewById(R.id.btn_add_drop);
        mEmptyView = findViewById(R.id.recycler_empty_view);
        mBtnAdd.setOnClickListener(mOnClickAddDropListener);
        mResults = mRealm.where(Drop.class).findAllSortedAsync("when");
        mAdapter = new AdapterDrops(this, mRealm, mResults);
        mResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.updateResults(mResults);
                mResults.removeChangeListener(this);
            }
        });
        //Let our Activity handle the event when the footer is clicked from our RecyclerView
        mAdapter.setOnFooterClickListener(this);
        //Let our Activity handle the event when the Add Drop button is clicked from the empty view
        mAdapter.setDropClickListener(this);
        //Set an Empty View to be displayed when the RecyclerView has no items
        mRecycler.setEmptyView(mEmptyView);

        //hide the toolbar when the bucket is empty and show it when it has atleast one item in it
        mRecycler.setToolbar(mToolbar);

        //Add a divider to our RecyclerView
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        //Handler the swipe from our RecyclerView
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
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
        final RealmResults<Drop> results;
        int sortOption = SORT_DEFAULT;
        if (id == R.id.action_show_completed) {
            sortOption = SHOW_COMPLETE;
            results = mRealm.where(Drop.class).equalTo("completed", true).findAllAsync();
        } else if (id == R.id.action_show_uncompleted) {
            sortOption = SHOW_INCOMPLETE;
            results = mRealm.where(Drop.class).equalTo("completed", false).findAllAsync();
        } else if (id == R.id.action_sort_ascending_date) {
            sortOption = SORT_DESCENDING_DATE;
            results = mRealm.where(Drop.class).findAllSortedAsync("when", Sort.ASCENDING);
        } else if (id == R.id.action_sort_descending_date) {
            sortOption = SORT_ASCENDING_DATE;
            results = mRealm.where(Drop.class).findAllSortedAsync("when", Sort.DESCENDING);
        } else {
            results = mRealm.where(Drop.class).findAllAsync();
        }
        BucketDropsApp.storeSortOption(sortOption);
        final int finalSortOption = sortOption;
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.updateResults(results);
                results.removeChangeListener(this);
            }
        });

        return handled;
    }

    @Override
    public void onClickAddDrop(Drop drop) {
        mAdapter.add(drop);
    }

    @Override
    public void onClickFooter() {
        showDialogAdd();
    }

    @Override
    public void onMark(int position) {
        //Launch the DialogActions which are shown when the user clicks on some item from our RecyclerView
        Bundle arguments = new Bundle();
        arguments.putInt(DialogActions.ActionListener.POSITION, position);
        DialogActions dialog = new DialogActions();
        dialog.setArguments(arguments);
        dialog.setDialogActionsListener(this);
        dialog.show(getSupportFragmentManager(), "dialog_actions");
    }

    @Override
    public void onClickComplete(int position) {
        //Mark an item as complete in our database when the user clicks "Mark as Complete"
        mAdapter.markComplete(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
