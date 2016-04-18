package com.lftechnology.NaapTol.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.lftechnology.NaapTol.R;
import com.lftechnology.NaapTol.adapter.DrawerRecyclerViewAdapter;
import com.lftechnology.NaapTol.bus.EventBus;
import com.lftechnology.NaapTol.bus.FlingListener;
import com.lftechnology.NaapTol.bus.ScrollListener;
import com.lftechnology.NaapTol.bus.SwapFragment;
import com.lftechnology.NaapTol.constant.AppConstant;
import com.lftechnology.NaapTol.fragment.MainFragment;
import com.lftechnology.NaapTol.utils.ApplicationThemeAndDataset;
import com.lftechnology.NaapTol.utils.GeneralUtils;
import com.lftechnology.NaapTol.utils.OnKeyEvents;
import com.lftechnology.NaapTol.utils.SoftKeyBoard;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

/**
 * Handles all the interactions with the app as it is a one-page application
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnKeyEvents, DrawerRecyclerViewAdapter.UpdateFragmentInMainActivity {
    @Bind(R.id.toolbarContainer)
    LinearLayout mToolbarContainer;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.drawer_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.inflated_content_main)
    LinearLayout mLinearLayout;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_header)
    LinearLayout mNavHeader;
    @Bind(R.id.nav_header_tv)
    TextView mTv;
    @Bind(R.id.unito_option_spinner)
    Spinner mSpinner;
    @Bind(R.id.main_content)
    RelativeLayout mMainContent;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;

    private static int DY = 5; // increment/decrement of mToolbar on swipe up/down
    private static final int ROTATE_ANIMATION_DURATION = 300;
    private String mSelectedConversion;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] mDrawerRecyclerViewDataset;
    private boolean spinDirection = true;
    private float lastTranslate = 0.0f;
    private ImageView mSwapButton;

    @Override
    public void onResume() {
        super.onResume();
        EventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setSpinner();
        setNavigationDrawer();
    }

    private void setNavigationDrawer() {
        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setRecyclerView();
    }

    private void setHeaderTextByFragment() {
        if (mSelectedConversion == null) {
            mSelectedConversion = AppConstant.LENGTH;
        }
        mNavHeader.setBackgroundResource(ApplicationThemeAndDataset.getThemeDetails(mSelectedConversion)[1]);
        mTv.setText(AppConstant.DUNITE);
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unit_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SoftKeyBoard.hideSoftKeyboard(v.getContext(), v);
                return false;
            }
        });
        mSpinner.setOnItemSelectedListener(this);
    }

    private void setRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDrawerRecyclerViewDataset = getResources().getStringArray(R.array.unit_options);
        mAdapter = new DrawerRecyclerViewAdapter(mDrawerRecyclerViewDataset, this, mSelectedConversion);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
//            @Override
//            public void onDrawerClosed(View drawerView) {
////                String[] dataset = ApplicationThemeAndDataset.getDataset(mSelectedConversion);
////                if (!Arrays.equals(dataset, mDrawerRecyclerViewDataset)) {
////                    EventBus.post(new NavigationMenuChangeDetails(mSelectedConversion));
////                    mDrawerRecyclerViewDataset = dataset;
////                }
//                super.onDrawerClosed(drawerView);
//            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // hide soft keyboard if it is open
                SoftKeyBoard.hideSoftKeyboard(drawerView.getContext(), drawerView);
                super.onDrawerOpened(drawerView);
            }

            public void onDrawerSlide(View drawerView, float slideOffset) {
                // push out/in main fragment when drawer slides in/out instead of overlaying on top of the main fragments
                float moveFactor = (mRecyclerView.getWidth() * slideOffset);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mMainContent.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    mMainContent.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        mSelectedConversion = spinner.getSelectedItem().toString();
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.inflated_content_main, MainFragment.newInstance(mSelectedConversion));
        fragmentTransaction.commit();

        setRecyclerView();
        setHeaderTextByFragment();
        mToolbarTitle.setText(mSpinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Is called when swap button is clicked, it interchanges the top and bottom fragments
     * Also, animates the swap button
     *
     * @param view
     */
    public void swapFragments(View view) {
        mSwapButton = (ImageView) findViewById(R.id.swapButton);
        animateSwapButton(mSwapButton);
        EventBus.post(new SwapFragment(true));
    }

    private void animateSwapButton(ImageView swapButton) {
        swapButton.clearAnimation();
        Animation animation;
        if (spinDirection) {
            animation = new RotateAnimation(0.0f, 180.0f, swapButton.getWidth() / 2, swapButton.getHeight() / 2);
        } else {
            animation = new RotateAnimation(180.0f, 0.0f, swapButton.getWidth() / 2, swapButton.getHeight() / 2);
        }
        spinDirection = !spinDirection;
        animation.setDuration(ROTATE_ANIMATION_DURATION);
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setFillAfter(true);
        swapButton.setAnimation(animation);
    }

    /**
     * Make sure the height of the linear layout matches full screen when the soft-keyboard is hidden, also animate swap button when show/hide keyboard
     */
    @Override
    public void keyboardHidden() {
        // TODO
        //  RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //  mLinearLayout.setLayoutParams(layoutParams);

        // show swap button
        mSwapButton = (ImageView) findViewById(R.id.swapButton);
        if (mSwapButton.getVisibility() == View.INVISIBLE) {
            mSwapButton = (ImageView) findViewById(R.id.swapButton);
            mSwapButton.clearAnimation();
            Animation scaleOut = new ScaleAnimation(0, 1, 0, 1, mSwapButton.getWidth() / 2, mSwapButton.getHeight() / 2);
            scaleOut.setInterpolator(new AccelerateInterpolator());
            scaleOut.setStartOffset(0); // Start fading out after 500 milli seconds
            scaleOut.setDuration(AppConstant.SWAP_BUTTON_ANIMATION_TIME_400); // Fadeout duration should be 1000 milli seconds

            Animation fadeOut = new AlphaAnimation(0, 1);  // the 1, 0 here notifies that we want the opacity to go from opaque (1) to transparent (0)
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setStartOffset(0); // Start fading out after 500 milli seconds
            fadeOut.setDuration(AppConstant.SWAP_BUTTON_ANIMATION_TIME_400); // Fadeout duration should be 1000 milli seconds

            AnimationSet animation = new AnimationSet(false); // change to false
            animation.addAnimation(scaleOut);
            animation.addAnimation(fadeOut);
            animation.setRepeatCount(1);
            mSwapButton.setAnimation(animation);
            mSwapButton.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void toggleToolBar(ScrollListener scrollListener) {
        int dy;
        if (scrollListener.moveUp) {
            if (GeneralUtils.convertPixelsToDp(mToolbarContainer.getTranslationY(), this) > -56.0) {
                float diff = mToolbarContainer.getTranslationY() - DY;
                if ((GeneralUtils.convertPixelsToDp(diff, this) < -56.0)) {
                    dy = Math.round(diff + GeneralUtils.convertDpToPixel(56, this));
                    diff = mToolbarContainer.getTranslationY() - dy;
                } else {
                    dy = DY;
                }
                mToolbarContainer.setTranslationY(diff);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mLinearLayout.getWidth(), mLinearLayout.getHeight() + dy);
                mLinearLayout.setLayoutParams(layoutParams);
                mLinearLayout.setTranslationY(mLinearLayout.getY() - dy);
            }
        } else {
            if (mToolbarContainer.getTranslationY() < 0.0) {
                float diff = mToolbarContainer.getTranslationY() + DY;
                if ((diff) > 0.0) {
                    dy = Math.round(diff);
                    diff = mToolbarContainer.getTranslationY() + dy;
                } else {
                    dy = DY;
                }
                mToolbarContainer.setTranslationY(diff);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mLinearLayout.getWidth(), mLinearLayout.getHeight() - dy);
                mLinearLayout.setLayoutParams(layoutParams);
                mLinearLayout.setTranslationY(mLinearLayout.getY() + dy);
            }
        }
    }

    @Subscribe
    public void hideOrShowToolbarAfterScrollComplete(FlingListener flingListener) {
        int dy;
        float yTranslationInDP = GeneralUtils.convertPixelsToDp(mToolbarContainer.getTranslationY(), this);

        if (flingListener.flingedUp && (yTranslationInDP > -56) && (yTranslationInDP < 0)) {
            float diff = Math.round(GeneralUtils.convertDpToPixel(-56, this));
            mToolbarContainer.setTranslationY(diff);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mLinearLayout.getWidth(), (int) (mLinearLayout.getHeight() + mLinearLayout.getY()));
            mLinearLayout.setLayoutParams(layoutParams);
            mLinearLayout.setTranslationY(0);
        } else if (!flingListener.flingedUp && (yTranslationInDP < 56.0) && (GeneralUtils.convertPixelsToDp(mLinearLayout.getY(), this) < 56)) {
            float diff = 0;
            dy = Math.round(GeneralUtils.convertDpToPixel(56, this));
            mToolbarContainer.setTranslationY(diff);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mLinearLayout.getWidth(), (int) (mLinearLayout.getHeight() - diff));
            mLinearLayout.setLayoutParams(layoutParams);
            mLinearLayout.setTranslationY(dy);
        }
    }

    @Override
    public void updateFragment(String selectedConversion) {
        mDrawerLayout.closeDrawers();
        if (!mSelectedConversion.equals(selectedConversion)) {
            mSelectedConversion = selectedConversion;

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.inflated_content_main, MainFragment.newInstance(mSelectedConversion));
            fragmentTransaction.commit();

            setRecyclerView();
            setHeaderTextByFragment();
            mToolbarTitle.setText(mSelectedConversion);
        }
    }
}


