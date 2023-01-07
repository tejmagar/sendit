package bca.sendit.filetransfer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import bca.sendit.filetransfer.adapters.ViewPagerAdapter;
import bca.sendit.filetransfer.ui.onboard.NextPageFragment;
import bca.sendit.filetransfer.ui.onboard.WelcomePageFragment;

public class OnBoardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        Button skipBtn = findViewById(R.id.onboard_skip);
        skipBtn.setOnClickListener(v -> completeOnBoard());

        Button backBtn = findViewById(R.id.onboard_back);
        backBtn.setVisibility(View.GONE);
        Button nextBtn = findViewById(R.id.onboard_next);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new WelcomePageFragment());
        fragments.add(new NextPageFragment());

        ViewPager2 onBoardPager = findViewById(R.id.onboard_view_pager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(fragments,
                getSupportFragmentManager(), getLifecycle());
        onBoardPager.setAdapter(pagerAdapter);

        onBoardPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Don't show back button for first page
                backBtn.setVisibility((position == 0) ? View.GONE : View.VISIBLE);

                // Don't show next and skip button for last page
                nextBtn.setVisibility((position < fragments.size() - 1 ? View.VISIBLE : View.GONE));
                skipBtn.setVisibility((position < fragments.size() - 1 ? View.VISIBLE : View.GONE));

                super.onPageSelected(position);
            }
        });

        nextBtn.setOnClickListener(view -> {
            // Page content can be scrolled
            if (fragments.size() > onBoardPager.getCurrentItem()) {
                onBoardPager.setCurrentItem(onBoardPager.getCurrentItem() + 1);
            }
        });

        backBtn.setOnClickListener(view -> {
            onBoardPager.setCurrentItem(onBoardPager.getCurrentItem() - 1);
        });

    }

    private void completeOnBoard() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Disables onboard next time in MainActivity
        sharedPreferences.edit().putBoolean("first_launch", false).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
