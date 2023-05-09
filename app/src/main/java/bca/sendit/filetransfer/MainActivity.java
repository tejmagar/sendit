package bca.sendit.filetransfer;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import bca.sendit.filetransfer.files.FileChooser;
import bca.sendit.filetransfer.files.FileDetails;
import bca.sendit.filetransfer.files.FileDownloadMonitor;
import bca.sendit.filetransfer.files.FileUploadSession;
import bca.sendit.filetransfer.files.SharedFileItem;
import bca.sendit.filetransfer.server.UserSession;
import bca.sendit.filetransfer.service.WebService;
import bca.sendit.filetransfer.ui.navigations.NavigationAdapter;
import bca.sendit.filetransfer.ui.navigations.NavigationItem;
import bca.sendit.filetransfer.ui.navigations.NavigationManager;
import bca.sendit.filetransfer.ui.tabs.DevicesFragment;
import bca.sendit.filetransfer.ui.tabs.DownloadsFragment;
import bca.sendit.filetransfer.ui.tabs.HomeFragment;
import bca.sendit.filetransfer.ui.tabs.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private NavigationManager navigationManager;

    private SwitchCompat runSwitch;
    private ViewPager2 contentViewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabUpload;
    private FileDownloadMonitor fileDownloadMonitor;
    private UserSession.OnNewDeviceConnectedListener onNewDeviceConnectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setToolbarTransparent(this, getSupportActionBar());
        onBoardIfFirstLaunch();
        setContentView(R.layout.activity_main);
        FileUploadSession.init();

        List<NavigationItem> navigationItems = createNavigationItems();
        navigationManager = new NavigationManager(this, navigationItems,
                this::enableUploadAndRunButton);

        initViewPager(navigationManager);
        initTabLayout(navigationManager);
        configureNavigation();

        initFileChooser();
        monitorFileDownload();
        monitorNewDeviceAccess();
        requestNotificationPermission();
    }

    private void configureNavigation() {
        navigationManager.attachViewPager(contentViewPager);
        navigationManager.attachTabs(tabLayout);
        navigationManager.allowTitleChange(true);
        navigationManager.setItem(0);
    }

    private void initFileChooser() {
        FileChooser fileChooser = new FileChooser(this, this::addFilesToSession);
        fabUpload = findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(v -> fileChooser.chooseMultipleFiles());
    }

    private void monitorFileDownload() {
        fileDownloadMonitor = new FileDownloadMonitor(this,
                (path) -> Toast.makeText(MainActivity.this, "File downloaded at " + path,
                        Toast.LENGTH_SHORT).show());
        fileDownloadMonitor.start();
    }

    private void monitorNewDeviceAccess() {
        onNewDeviceConnectedListener = (ipAddress, hostname) -> runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, "Device Alert: Accessed from " + ipAddress,
                    Toast.LENGTH_LONG).show();
        });

        UserSession.attachListener(onNewDeviceConnectedListener);
    }

    private void requestNotificationPermission() {
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        }).launch("android.permission.POST_NOTIFICATIONS");
    }

    private void addFilesToSession(List<Uri> uris) {
        try {
            for (Uri uri : uris) {
                FileDetails fileDetails = new FileDetails(this, uri);
                SharedFileItem sharedFileItem = SharedFileItem.create(
                        fileDetails.getFilename(),
                        fileDetails.getSize(),
                        uri
                );

                Log.i(TAG, "Adding file to FileUploadSession: " + uri.toString());
                FileUploadSession.add(sharedFileItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<NavigationItem> createNavigationItems() {
        return Arrays.asList(
                new NavigationItem(new HomeFragment(), getString(R.string.app_name)),
                new NavigationItem(new DownloadsFragment(), "Downloads"),
                new NavigationItem(new DevicesFragment(), " Online Devices"),
                new NavigationItem(new SettingsFragment(), "Settings")
        );
    }

    private void initViewPager(NavigationManager navigationManager) {
        contentViewPager = findViewById(R.id.content_view_pager);
        NavigationAdapter navigationAdapter = new NavigationAdapter(this,
                navigationManager.getNavigationItems());
        contentViewPager.setAdapter(navigationAdapter);
        contentViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navigationManager.setItem(position);
            }
        });
    }

    private void initTabLayout(NavigationManager navigationManager) {
        tabLayout = findViewById(R.id.menuTabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                navigationManager.setItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private boolean isFirstLaunch() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("first_launch", true);
    }

    private void onBoardIfFirstLaunch() {
        if (isFirstLaunch()) {
            startActivity(new Intent(this, OnBoardActivity.class));
            finish();
        }
    }

    private void enableFabBtn(boolean enable) {
        if (fabUpload != null) {
            if (enable) {
                fabUpload.show();
            } else {
                fabUpload.hide();
            }
        }
    }

    private void enableRunSwitch(boolean enable) {
        if (runSwitch != null) {
            runSwitch.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    private void enableUploadAndRunButton(int currentPosition) {
        boolean isEnable = shouldShowRunAndUploadBtn(currentPosition);
        enableFabBtn(isEnable);
        enableRunSwitch(isEnable);
    }

    private boolean shouldShowRunAndUploadBtn(int currentPosition) {
        return currentPosition == 0;
    }


    private void handleService(boolean start) {
        Intent intent = new Intent(this, WebService.class);

        if (start) {
            if (!App.isNotificationRunning(this)) {
                intent.putExtra("start", true);
                intent.putExtra("port", App.getServerPort(this));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        } else {
            if (App.isNotificationRunning(this)) {
                stopService(intent);
            }
        }
    }

    private void initRunSwitch(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.run_server);
        View view = menuItem.getActionView();
        runSwitch = view.findViewById(R.id.menu_switch);
        runSwitch.setChecked(App.isNotificationRunning(this));
        runSwitch.setOnClickListener(v -> handleService(runSwitch.isChecked()));
        enableUploadAndRunButton(navigationManager.getCurrentPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        initRunSwitch(menu);
        enableUploadAndRunButton(navigationManager.getCurrentPosition());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileDownloadMonitor.stop();
        UserSession.deAttachListener(onNewDeviceConnectedListener);
    }
}