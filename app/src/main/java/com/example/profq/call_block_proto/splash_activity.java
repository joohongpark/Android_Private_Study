package com.example.profq.call_block_proto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class splash_activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainActivity.class 자리에 다음에 넘어갈 액티비티를 넣어주기
        Intent intent = new Intent(this, main.class);
        intent.putExtra("state", "launch");
        startActivity(intent);
        finish();
    }
}