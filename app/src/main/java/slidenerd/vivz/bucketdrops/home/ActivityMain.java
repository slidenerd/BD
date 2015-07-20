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
import android.util.Log;
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
    private static final int SERVICE_REQUEST_CODE = 201;
    private BucketRecyclerView mRecyclerTodos;
    private Button mBtnAddDrop;
    private View mEmptyTodos;
    private Toolbar mToolbar;
    private ConcreteAdapter mAdapter;
    private ImageView mImgBackground;
    private Database mDatabase;
    private int sortOption = SORT_DEFAULT;
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
            sortOption = savedInstanceState.getInt(KEY_SORT_OPTION);
        }
        startLoader(sortOption, false);
        Util.notifyUpcomingDrops(this);

    }

    private void startLoader(int currentSortOption, boolean restart) {
        Bundle arguments = new Bundle();
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
            getSupportLoaderManager().initLoader(LOADER_ID, arguments, this);
        } else {
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
        mAdapter.setOnSwipeListener(this);
        mAdapter.setOnFooterClickListener(this);
        mAdapter.setDropClickListener(this);
        mRecyclerTodos = (BucketRecyclerView) findViewById(R.id.recycler_tasks);
        mRecyclerTodos.setEmptyView(mEmptyTodos);
        mRecyclerTodos.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecyclerTodos.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerTodos.setItemAnimator(new DefaultItemAnimator());
        mRecyclerTodos.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerTodos);
    }

    private void initBackgroundImage() {
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
        BucketLoader bucketLoader = new BucketLoader(this, args, mDatabase);
        return bucketLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
        mDatabase.insert(drop);
        startLoader(sortOption, true);
    }

    @Override
    public void onClickShowDialogAdd() {
        showDialogAdd();
    }

    @Override
    public void onSwipeItem(long todoId) {
        mDatabase.delete(todoId);
        startLoader(sortOption, true);
    }

    @Override
    public void onClickFooter() {
        showDialogAdd();
    }

    @Override
    public void onClickDrop(long dropId) {
        Bundle arguments = new Bundle();
        arguments.putLong(DialogActions.Actions.DROP_POSITION, dropId);
        DialogActions dialog = new DialogActions();
        dialog.setArguments(arguments);
        dialog.setDialogActionsListener(this);
        dialog.show(getSupportFragmentManager(), "dialog_actions");
    }

    @Override
    public void onClickComplete(long dropId) {
        Log.d("VIVZ", "onClickComplete " + dropId);
        mDatabase.markAsComplete(dropId);
    }
}
