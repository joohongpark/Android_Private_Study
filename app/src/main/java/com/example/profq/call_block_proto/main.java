package com.example.profq.call_block_proto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class main extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // 광고 초기화
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // 네비게이션 리스너
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.main_bottom_menu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        callFragment(1);

    }


    private void callFragment(int no){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (no){
            case 1:
                phone_block phone_block_frag = new phone_block();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, phone_block_frag).commit();
                break;
            case 3:
                // more
                etc etc_frag = new etc(); // 프라그먼트 생성
                transaction.replace(R.id.main_frame, etc_frag); // 액티비티의 프레임 레이아웃에 프라그먼트 집어넣음
                transaction.commit(); // 프라그먼트 적용
                break;
            case 4:
                // more
                option option_frag = new option(); // 프라그먼트 생성
                transaction.replace(R.id.main_frame, option_frag); // 액티비티의 프레임 레이아웃에 프라그먼트 집어넣음
                transaction.commit(); // 프라그먼트 적용
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.call_block:
                    callFragment(1);
                    //mTextMessage.setText("1");
                    return true;
                case R.id.option:
                    callFragment(3);
                    //mTextMessage.setText("3");
                    return true;
                case R.id.etc:
                    callFragment(4);
                    //mTextMessage.setText("4");
                    return true;
            }
            return false;
        }
    };

}
