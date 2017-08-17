package com.example.tapan.digitallibraryadmin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tapan.digitallibraryadmin.R;
import com.example.tapan.digitallibraryadmin.fragments.AddBookFragment;
import com.example.tapan.digitallibraryadmin.fragments.BookHistoryFragment;
import com.example.tapan.digitallibraryadmin.fragments.IssueBookFragment;
import com.example.tapan.digitallibraryadmin.fragments.LibrarianInfoFragment;
import com.example.tapan.digitallibraryadmin.fragments.ReturnBookFragment;
import com.example.tapan.digitallibraryadmin.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Handler mHandler;
    private NavigationView navigationView;
    public static int navItemIndex = 0;
    private static final String TAG_ADD_BOOK = "addBook";
    private static final String TAG_ISSUE_BOOK = "issueBook";
    private static final String TAG_RETURN_BOOK = "returnBook";
    private static final String TAG_LIBRARIAN_INFO = "librarianInfo";
    private static final String TAG_BOOOK_HISTORY = "BOOK_HISTORY";
    public static String CURRENT_TAG = TAG_ADD_BOOK;
    Intent intent;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        // Navigation view header
//        navHeader = navigationView.getHeaderView(0);
//        txtName = (TextView) navHeader.findViewById(R.id.name);
//        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
//        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
//        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_ISSUE_BOOK;
            loadHomeFragment();
        }
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
//        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // add
                AddBookFragment addBookFragment = new AddBookFragment();
                return addBookFragment;
            case 1:
                // issue
                IssueBookFragment issueBookFragment = new IssueBookFragment();
                return issueBookFragment;
            case 2:
                // return book fragment
                ReturnBookFragment returnBookFragment = new ReturnBookFragment();
                return returnBookFragment;
            case 3:
                // Search fragment
                BookHistoryFragment bookHistoryFragment = new BookHistoryFragment();
                return bookHistoryFragment;
            case 4:
                // Librarian info fragment
                LibrarianInfoFragment librarianInfoFragment = new LibrarianInfoFragment();
                return librarianInfoFragment;
//
//            case 4:
//                // settings fragment
//                SettingsFragment settingsFragment = new SettingsFragment();
//                return settingsFragment;
            default:
                return new IssueBookFragment();
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            intent = new Intent();
            intent.setClass(Main2Activity.this, LoginActivity.class);
            firebaseAuth.signOut();
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_book) {
            // Handle the camera action
            navItemIndex = 0;
            CURRENT_TAG = TAG_ADD_BOOK;
        } else if (id == R.id.issue_book) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_ISSUE_BOOK;
        } else if (id == R.id.return_book) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_RETURN_BOOK;

        } else if (id == R.id.book_history) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_BOOOK_HISTORY;
        } else if (id == R.id.librarian_info) {
            navItemIndex = 4;
            CURRENT_TAG = TAG_LIBRARIAN_INFO;

        }

        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        item.setChecked(true);

        loadHomeFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
