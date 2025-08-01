package com.nilscreation.marathiquotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.OnBackPressedCallback;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nilscreation.marathiquotes.fragment.AboutFragment;
import com.nilscreation.marathiquotes.fragment.CategoryFragment;
import com.nilscreation.marathiquotes.fragment.FavouriteFragment;
import com.nilscreation.marathiquotes.fragment.MainFragment;
import com.nilscreation.marathiquotes.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    networkChangListener networkChangListener = new networkChangListener();
    FirebaseAnalytics mFirebaseAnalytics;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView toolbarTitle;
    private ImageView customNavigationIcon;
    private ActivityResultLauncher activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        bottomNavigation = findViewById(R.id.bottonNavigationView);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        customNavigationIcon = findViewById(R.id.custom_navigation_icon);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbarTitle.setText(R.string.app_name);

        inAppUpdate();

        loadFragment(new MainFragment());
//        nightMode();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home) {
                    loadFragment(new MainFragment());
                    toolbarTitle.setText(R.string.app_name);
                } else if (id == R.id.settings) {
                    loadFragment(new SettingsFragment());
                    toolbarTitle.setText("Settings");
                } else if (id == R.id.favourite) {
                    loadFragment(new FavouriteFragment());
                    toolbarTitle.setText("Favourite");
                } else {
                    loadFragment(new CategoryFragment());
                    toolbarTitle.setText(R.string.app_name);
                }
                return true;
            }
        });

        customNavigationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.home) {
                    loadFragment(new MainFragment());
//                    toolbarTitle.setText(R.string.app_name);
                    toolbarTitle.setText(R.string.app_name);
                } else if (id == R.id.about) {
                    loadFragment(new AboutFragment());
                    toolbarTitle.setText("About");
                } else if (id == R.id.favourite) {
                    loadFragment(new FavouriteFragment());
                    toolbarTitle.setText("Favourite");
                } else if (id == R.id.categories) {
                    loadFragment(new CategoryFragment());
                    toolbarTitle.setText(R.string.app_name);
                } else if (id == R.id.privacy) {
                    navigationView.setCheckedItem(R.id.home);
                    String url = "https://thenilscreation.blogspot.com/p/spark-motivation-policy.html";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } else if (id == R.id.share) {
                    navigationView.setCheckedItem(R.id.home);
                    String appUrl = "For daily new Motivational Quotes, Download the Marathi Quotes & Status app now: " + "\nhttps://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();

                    Intent sharing = new Intent(Intent.ACTION_SEND);
                    sharing.setType("text/plain");
                    sharing.putExtra(Intent.EXTRA_SUBJECT, "Download Now");
                    sharing.putExtra(Intent.EXTRA_TEXT, appUrl);
                    startActivity(Intent.createChooser(sharing, "Share via"));
                } else {
                    navigationView.setCheckedItem(R.id.home);
                    callExitDialog();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragInstance = fm.findFragmentById(R.id.mainContainer);

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    if (fragInstance instanceof FavouriteFragment || fragInstance instanceof CategoryFragment || fragInstance instanceof AboutFragment) {
                        loadFragment(new MainFragment());
                        toolbarTitle.setText(R.string.app_name);
                    } else {
                        callExitDialog();
                    }
                }

            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainContainer, fragment);
        ft.commit();
    }

//    @Override
//    protected void onResume() {
//        nightMode();
//        super.onResume();
//    }

//    private void nightMode() {
//        // Saving state of our app using SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
//        final SharedPreferences.Editor editor = sharedPreferences.edit();
//        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
//
//        // When user reopens the app after applying dark/light mode
//        if (isDarkModeOn) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            switchMode.setChecked(true);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            switchMode.setChecked(false);
//        }
//
//        switchMode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (switchMode.isChecked()) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    editor.putBoolean("isDarkModeOn", true);
//                    editor.apply();
//                } else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    // it will set isDarkModeOn
//                    // boolean to false
//                    editor.putBoolean("isDarkModeOn", false);
//                    editor.apply();
//                }
//            }
//        });
//    }

//    @Override
//    public void onBackPressed() {
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragInstance = fm.findFragmentById(R.id.mainContainer);
//
//        if (fragInstance instanceof CategoryFragment) {
//            loadFragment(new MainFragment());
//            bottomNavigation.setSelectedItemId(R.id.home);
//        } else if (fragInstance instanceof FavouriteFragment) {
//            loadFragment(new MainFragment());
//            bottomNavigation.setSelectedItemId(R.id.home);
//        } else if (fragInstance instanceof SettingsFragment) {
//            loadFragment(new MainFragment());
//            bottomNavigation.setSelectedItemId(R.id.home);
//        } else {
//            callExitDialog();
//        }
//    }


    private void inAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // an activity result launcher registered via registerForActivityResult
                        activityResultLauncher,
                        // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                        // flexible updates.
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                // handle callback
                if (result.getResultCode() != RESULT_OK) {
//                            log("Update flow failed! Result code: " + result.getResultCode());
                    // If the update is canceled or fails,
                    // you can request to start the update again.
                }
            }
        });
    }

    private void callExitDialog() {

        AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this);
        exitDialog.setTitle("Exit");
        exitDialog.setMessage("Dou you really want to exit?");
        exitDialog.setCancelable(false);

        exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        exitDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        exitDialog.show();

    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangListener);
        super.onStop();
    }

    private boolean isConnected(MainActivity homeActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) homeActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    public class networkChangListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Internet connection
            if (!isConnected(MainActivity.this)) {

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.internet_dialog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Button btnOk = dialog.findViewById(R.id.btn_retry);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        onReceive(MainActivity.this, intent);
                        loadFragment(new MainFragment());
                    }
                });
                dialog.show();
            }
        }
    }
}