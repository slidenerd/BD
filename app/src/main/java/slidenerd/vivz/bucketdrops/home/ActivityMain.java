package slidenerd.vivz.bucketdrops.home;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import android.widget.Toast;

import slidenerd.vivz.bucketdrops.R;
import slidenerd.vivz.bucketdrops.adapters.ConcreteAdapter;
import slidenerd.vivz.bucketdrops.adapters.Divider;
import slidenerd.vivz.bucketdrops.adapters.OnAddDropListener;
import slidenerd.vivz.bucketdrops.adapters.OnSwipeListener;
import slidenerd.vivz.bucketdrops.adapters.SimpleItemTouchHelperCallback;
import slidenerd.vivz.bucketdrops.adapters.SortOptions;
import slidenerd.vivz.bucketdrops.beans.Drop;
import slidenerd.vivz.bucketdrops.database.Database;
import slidenerd.vivz.bucketdrops.extras.BucketRecyclerView;
import slidenerd.vivz.bucketdrops.extras.Util;
import slidenerd.vivz.bucketdrops.tasks.BucketLoader;

import static slidenerd.vivz.bucketdrops.adapters.SortOptions.KEY_SORT_OPTION;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_COMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SHOW_INCOMPLETE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_ASCENDING_DATE;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DEFAULT;
import static slidenerd.vivz.bucketdrops.adapters.SortOptions.SORT_DESCENDING_DATE;


public class ActivityMain extends AppCompatActivity implements
        OnAddDropListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        ConcreteAdapter.ShowDialogAddListener,
        OnSwipeListener,
        ConcreteAdapter.FooterClickListener,
        ConcreteAdapter.DropClickListener,
        DialogActions.Actions {

    private static final int LOADER_ID = 100;
    //The RecyclerView that displays all our items
    private BucketRecyclerView mRecyclerDrops;
    private Button mBtnAddDrop;
    //The View to be displayed when the RecyclerView is empty.
    private View mEmptyTodos;
    private Toolbar mToolbar;
    private ConcreteAdapter mAdapter;
    private ImageView mImgBackground;
    private Database mDatabase;
    //The default sort option that involves displaying items in the order in which they were added by the user
    private int sortOption = SORT_DEFAULT;
    //When the add drop button is clicked, show a dialog that lets the person add a new drop
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
        mDatabase = new Database(this);
        initBackgroundImage();
        initToolbar();
        initRecycler();
        if (savedInstanceState != null) {

            //retrieve the sort option selected by the user prior to rotating their screen
            sortOption = savedInstanceState.getInt(KEY_SORT_OPTION);
        } else {
            //if its the first time, this Activity is running, launch the Notification Service.
            Util.notifyUpcomingDrops(this);
        }
        //Start a Loader to load the data using the sort option specified by the user
        startLoader(sortOption, false);


    }

    /**
     * @param currentSortOption Sorting option by means of which its decided how the data is displayed in the Cursor
     * @param restart           indicates whether a new loader should be created or an existing one should be restarted
     */
    private void startLoader(int currentSortOption, boolean restart) {
        Bundle arguments = new Bundle();
        //Update the sort option to include what the user wants currently
        sortOption = currentSortOption;
        switch (sortOption) {
            case SORT_ASCENDING_DATE:
                arguments.putInt(KEY_SORT_OPTION, SORT_ASCENDING_DATE);
                break;
            case SORT_DESCENDING_DATE:
                arguments.putInt(KEY_SORT_OPTION, SORT_DESCENDING_DATE);
                break;
            case SHOW_COMPLETE:
                arguments.putInt(KEY_SORT_OPTION, SHOW_COMPLETE);
                break;
            case SHOW_INCOMPLETE:
                arguments.putInt(KEY_SORT_OPTION, SHOW_INCOMPLETE);
                break;
            default:
                arguments.putInt(KEY_SORT_OPTION, SortOptions.SORT_DEFAULT);
                break;
        }
        if (!restart) {
            //Create a new loader
            getSupportLoaderManager().initLoader(LOADER_ID, arguments, this);
        } else {
            //Restart an existing loader
            getSupportLoaderManager().restartLoader(LOADER_ID, arguments, this);
        }
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
    }

    private void initRecycler() {
        mEmptyTodos = findViewById(R.id.recycler_empty_view);
        mBtnAddDrop = (Button) mEmptyTodos.findViewById(R.id.btn_add_drop);
        mBtnAddDrop.setOnClickListener(mOnClickAddDropListener);
        mAdapter = new ConcreteAdapter(this);
        //let our Activity handle the event when the user swipes an item from our RecyclerView
        mAdapter.setOnSwipeListener(this);
        //Let our Activity handle the event when the footer is clicked from our RecyclerView
        mAdapter.setOnFooterClickListener(this);
        //Let our Activity handle the event when the Add Drop button is clicked from the empty view
        mAdapter.setDropClickListener(this);
        mRecyclerDrops = (BucketRecyclerView) findViewById(R.id.recycler_tasks);
        //Set an Empty View to be displayed when the RecyclerView has no items
        mRecyclerDrops.setEmptyView(mEmptyTodos);
        //Add a divider to our RecyclerView
        mRecyclerDrops.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecyclerDrops.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerDrops.setItemAnimator(new DefaultItemAnimator());
        mRecyclerDrops.setAdapter(mAdapter);
        //Handler the swipe from our RecyclerView
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerDrops);
    }

    private void initBackgroundImage() {
        //Convert our background image to a specific size that suits our device's screen size
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
        if (id == R.id.action_add) {
            showDialogAdd();
        }
        //Restart the loader in all cases when the user changes the sorting option
        if (id == R.id.action_show_completed) {
            startLoader(SHOW_COMPLETE, true);
        }
        if (id == R.id.action_show_uncompleted) {
            startLoader(SHOW_INCOMPLETE, true);
        }
        if (id == R.id.action_sort_descending_date) {
            startLoader(SORT_DESCENDING_DATE, true);
        }
        if (id == R.id.action_sort_ascending_date) {
            startLoader(SORT_ASCENDING_DATE, true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SORT_OPTION, sortOption);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Specify the sorting option chosen by the user and initialize the BucketLoader
        BucketLoader bucketLoader = new BucketLoader(this, args, mDatabase);
        return bucketLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //A special case to be handled for showing complete and incomplete items. If there are no complete items or incomplete items, instead of showing an empty RecyclerView , simply display a Toast message and show all the items in the default sort order
        if (sortOption == SHOW_COMPLETE && data.getCount() == 0) {
            Toast.makeText(this, "Patience My Friend, Nothing Is Complete", Toast.LENGTH_SHORT).show();
            startLoader(SORT_DEFAULT, true);
        }
        if (sortOption == SHOW_INCOMPLETE && data.getCount() == 0) {
            Toast.makeText(this, "Awesome, You Finished Everything, Incomplete List Is Empty", Toast.LENGTH_SHORT).show();
            sortOption = SORT_DEFAULT;
            startLoader(SORT_DEFAULT, true);
        }
        mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    @Override
    public void onClickAddDrop(Drop drop) {
        //Add the drop to our database and restart the loader
        mDatabase.insert(drop);
        startLoader(sortOption, true);
    }

    @Override
    public void onClickShowDialogAdd() {
        showDialogAdd();
    }

    @Override
    public void onSwipeItem(long todoId) {
        //Delete the drop from our database and restart the loader
        mDatabase.delete(todoId);
        startLoader(sortOption, true);
    }

    @Override
    public void onClickFooter() {
        showDialogAdd();
    }

    @Override
    public void onClickDrop(long dropId) {
        //Launch the DialogActions which are shown when the user clicks on some item from our RecyclerView
        Bundle arguments = new Bundle();
        arguments.putLong(DialogActions.Actions.DROP_POSITION, dropId);
        DialogActions dialog = new DialogActions();
        dialog.setArguments(arguments);
        dialog.setDialogActionsListener(this);
        dialog.show(getSupportFragmentManager(), "dialog_actions");
    }

    @Override
    public void onClickComplete(long dropId) {
        //Mark an item as complete in our database when the user clicks "Mark as Complete"
        mDatabase.markAsComplete(dropId);
    }
}
