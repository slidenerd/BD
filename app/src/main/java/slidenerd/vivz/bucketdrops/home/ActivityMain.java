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
import io.realm.RealmResults;
import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.adapters.Divider;
import slidenerd.vivz.bucketdrops.adapters.DropRealmAdapter;
import slidenerd.vivz.bucketdrops.adapters.OnAddDropListener;
import slidenerd.vivz.bucketdrops.adapters.SimpleItemTouchHelperCallback;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.extras.BucketRecyclerView;
import slidenerd.vivz.bucketdrops.extras.Util;

import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_COMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_INCOMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_ASCENDING_DATE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DEFAULT;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DESCENDING_DATE;

public class ActivityMain extends AppCompatActivity implements
        OnAddDropListener,
        DropRealmAdapter.FooterClickListener,
        DropRealmAdapter.ItemClickListener,
        DialogActions.ActionListener {

    private Realm mRealm;
    //The RecyclerView that displays all our items
    private BucketRecyclerView mRecycler;
    private Button mBtnAddDrop;
    //The View to be displayed when the RecyclerView is empty.
    private View mEmptyTodos;
    private Toolbar mToolbar;
    private DropRealmAdapter mAdapter;
    private ImageView mImgBackground;
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
        mBtnAddDrop = (Button) findViewById(R.id.btn_add_drop);
        mEmptyTodos = findViewById(R.id.recycler_empty_view);
        mBtnAddDrop.setOnClickListener(mOnClickAddDropListener);
        mAdapter = new DropRealmAdapter(this, mRealm);
        //Let our Activity handle the event when the footer is clicked from our RecyclerView
        mAdapter.setOnFooterClickListener(this);
        //Let our Activity handle the event when the Add Drop button is clicked from the empty view
        mAdapter.setDropClickListener(this);
        //Set an Empty View to be displayed when the RecyclerView has no items
        mRecycler.setEmptyView(mEmptyTodos);

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
        mImgBackground = (ImageView) findViewById(R.id.img_background);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Bitmap bitmapModified = Util.getScaledVersion(this, displayMetrics.widthPixels, displayMetrics.heightPixels);
        mImgBackground.setImageBitmap(bitmapModified);
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
        int sortOption = SORT_DEFAULT;
        boolean needsReloading = false;
        switch (id) {
            case R.id.action_add:
                showDialogAdd();
                break;
            case R.id.action_show_completed:
                sortOption = SHOW_COMPLETE;
                needsReloading = true;
                break;
            case R.id.action_show_uncompleted:
                sortOption = SHOW_INCOMPLETE;
                needsReloading = true;
                break;
            case R.id.action_sort_ascending_date:
                sortOption = SORT_DESCENDING_DATE;
                needsReloading = true;
                break;
            case R.id.action_sort_descending_date:
                sortOption = SORT_ASCENDING_DATE;
                needsReloading = true;
                break;
        }
        BucketDropsApp.storeSortOption(sortOption);
        if (needsReloading) {
            RealmResults<Drop> realmResults = mAdapter.getData(mRealm);
            mAdapter.setData(realmResults);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickAddDrop(Drop drop) {
        mAdapter.add(drop, true);
    }


    @Override
    public void onClickFooter() {
        showDialogAdd();
    }

    @Override
    public void onClickDrop(int position) {
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
