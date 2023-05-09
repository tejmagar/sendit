package bca.sendit.filetransfer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import bca.sendit.filetransfer.ui.onboard.OnBoardOneFragment;
import bca.sendit.filetransfer.ui.onboard.OnBoardPagerAdapter;
import bca.sendit.filetransfer.ui.onboard.OnBoardThreeFragment;
import bca.sendit.filetransfer.ui.onboard.OnBoardTwoFragment;

public class OnBoardActivity extends AppCompatActivity {
    private List<Fragment> onBoardItems;
    private Button nextBtn, backBtn, skipBtn;
    private ViewPager2 onBoardPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        onBoardItems = createOnBoardItems();
        initButtons();
        initViewPager();
    }


    private List<Fragment> createOnBoardItems() {
        return Arrays.asList(
                new OnBoardOneFragment(),
                new OnBoardTwoFragment(),
                new OnBoardThreeFragment()
        );
    }

    private void initButtons() {
        skipBtn = findViewById(R.id.onboard_skip);
        skipBtn.setOnClickListener(v -> completeOnBoard());

        backBtn = findViewById(R.id.onboard_back);
        backBtn.setVisibility(View.GONE);
        nextBtn = findViewById(R.id.onboard_next);

        nextBtn.setOnClickListener(view -> {
            if (onBoardPager.getCurrentItem() == onBoardItems.size() - 1){
                completeOnBoard();
                return;
            }

            // Page content can be scrolled
            if (onBoardItems.size() > onBoardPager.getCurrentItem()) {
                onBoardPager.setCurrentItem(onBoardPager.getCurrentItem() + 1);
            }
        });

        backBtn.setOnClickListener(view -> {
            onBoardPager.setCurrentItem(onBoardPager.getCurrentItem() - 1);
        });

    }

    private void initViewPager() {
        onBoardPager = findViewById(R.id.onboard_view_pager);
        OnBoardPagerAdapter onBoardPagerAdapter = new OnBoardPagerAdapter(this,
                onBoardItems);
        onBoardPager.setAdapter(onBoardPagerAdapter);

        onBoardPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Don't show back button for first page
                backBtn.setVisibility((position == 0) ? View.GONE : View.VISIBLE);

                // Don't show next and skip button for last page
//                nextBtn.setVisibility((position < onBoardItems.size() - 1 ? View.VISIBLE : View.GONE));
                skipBtn.setVisibility((position < onBoardItems.size() - 1 ? View.VISIBLE : View.GONE));

                super.onPageSelected(position);
            }
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
