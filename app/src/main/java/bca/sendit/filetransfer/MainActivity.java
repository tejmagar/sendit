package bca.sendit.filetransfer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.adapters.ViewPagerAdapter;
import bca.sendit.filetransfer.server.Events;
import bca.sendit.filetransfer.server.auths.AuthToken;
import bca.sendit.filetransfer.server.auths.Auths;
import bca.sendit.filetransfer.server.files.FileUploadSession;
import bca.sendit.filetransfer.service.WebService;
import bca.sendit.filetransfer.ui.tabs.DevicesFragment;
import bca.sendit.filetransfer.ui.tabs.DownloadsFragment;
import bca.sendit.filetransfer.ui.tabs.HomeFragment;
import bca.sendit.filetransfer.ui.tabs.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SwitchCompat runSwitch;
    private ViewPager2 contentViewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabUpload;

    AlertDialog requestAccessDialog;
    ActivityResultLauncher<String> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setToolbarTransparent(this, getSupportActionBar());
        onBoardIfFirstLaunch();
        setContentView(R.layout.activity_main);

        initViewPager();
        initMenuTabs();


        fabUpload = findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(v -> selectFiles());

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), result -> {
            for (Uri uri : result) {
                FileUploadSession.allowFile(uri);
                Log.d(TAG, uri.toString());
            }

            Log.d(TAG, "Files selected: " + result.size());
        });


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 0);

        ServerEventsReceiver serverEventsReceiver = new ServerEventsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Events.ACTION_REQUEST_ALLOW);
        registerReceiver(serverEventsReceiver, intentFilter);
    }

    private void selectFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activityResultLauncher.launch("*/*");
    }

    private void onBoardIfFirstLaunch() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // For now set false to ignore onboard
        boolean isFirstLaunch = sharedPreferences.getBoolean("first_launch", true);

        if (isFirstLaunch) {
            startActivity(new Intent(this, OnBoardActivity.class));
            finish();
        }
    }

    /**
     * Sets title in current activity with args from Fragment. If no title specified, app name is
     * used as title
     *
     * @param fragment Fragment
     */
    private void setTitle(Fragment fragment) {
        String defaultTitle = getString(R.string.app_name);
        setTitle(defaultTitle);

        if (fragment.getArguments() == null) {
            return;
        }

        String pageTitle = fragment.getArguments().getString("title");
        if (pageTitle != null) {
            setTitle(pageTitle);
        }
    }

    private void selectTab(int position) {
        // First get Tab from TabLayout
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        tabLayout.selectTab(tab);
    }

    private void setFragmentTitle(Fragment fragment, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
    }

    /**
     * Run menu switch will be hidden if position is 0
     * @param position index
     */
    private void setMenuAndFabVisibility(int position) {
        if (runSwitch != null) {
            runSwitch.setVisibility((position == 0) ? View.VISIBLE : View.GONE);
        }

        if (position == 0) {
            fabUpload.show();
        } else {
            fabUpload.hide();
        }

    }

    private void initViewPager() {
        List<Fragment> pages = new ArrayList<>();

        Fragment homePage = new HomeFragment();
        setFragmentTitle(homePage, getString(R.string.app_name));

        // Downloads Tab
        Fragment downloadsPage = new DownloadsFragment();
        setFragmentTitle(downloadsPage, "Downloads");

        // Devices Tab
        Fragment devicesPage = new DevicesFragment();
        setFragmentTitle(devicesPage, "Devices");

        // Settings Tab
        Fragment settingsPage = new SettingsFragment();
        setFragmentTitle(settingsPage, "Settings");

        pages.add(homePage);
        pages.add(downloadsPage);
        pages.add(devicesPage);
        pages.add(settingsPage);

        contentViewPager = findViewById(R.id.content_view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(pages, getSupportFragmentManager(),
                getLifecycle());
        contentViewPager.setAdapter(viewPagerAdapter);
        contentViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectTab(position);
                setTitle(pages.get(position));
                setMenuAndFabVisibility(position);
            }
        });
    }

    private void initMenuTabs() {
        tabLayout = findViewById(R.id.menuTabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                contentViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Send broadcast event to WebServer
     * @param authToken token
     * @param isAllowed permissionAllowed
     */
    private void sendApprovedBroadcast(String authToken, boolean isAllowed) {
        Intent intent = new Intent(Events.ACTION_REQUEST_RESULT);
        intent.putExtra(Events.AUTH_TOKEN, authToken);
        intent.putExtra(Events.REQUEST_RESULT, isAllowed);
        sendBroadcast(intent);
    }

    private void requestDialog(String ipAddress, String token) {
        // If any previous request dialog exist, cancel it
        if (requestAccessDialog != null) {
            requestAccessDialog.cancel();
        }

        MaterialAlertDialogBuilder requestDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
        requestDialogBuilder.setTitle(ipAddress + " is requesting...");
        requestDialogBuilder.setMessage("Allow this device to access your shared file?\nID: " + token);
        requestDialogBuilder.setNegativeButton("Deny", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            sendApprovedBroadcast(token, false);
        });
        requestDialogBuilder.setPositiveButton("Allow", (dialogInterface, i) ->
                new Thread(() -> {
                    AuthToken authToken = new AuthToken(token, true);
                    Auths.saveToken(MainActivity.this, authToken);
                    sendApprovedBroadcast(token, true);

                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Allowed access to " + ipAddress, Toast.LENGTH_SHORT).show());
                }).start());

        requestDialogBuilder.setCancelable(false);
        requestAccessDialog = requestDialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.run_server);
        View view = menuItem.getActionView();
        runSwitch = view.findViewById(R.id.menu_switch);

        runSwitch.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebService.class);
            if (runSwitch.isChecked()) {
                startService(intent);
            } else {
                stopService(intent);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    class ServerEventsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(Events.ACTION_REQUEST_ALLOW)) {
                String ipAddress = intent.getStringExtra(Events.REMOTE_IP_ADDRESS);
                String authToken = intent.getStringExtra(Events.AUTH_TOKEN);
                requestDialog(ipAddress, authToken);
                Toast.makeText(context, "Incoming request...", Toast.LENGTH_SHORT).show();
            }
        }
    }

}