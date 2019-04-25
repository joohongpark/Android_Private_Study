package com.example.profq.call_block_proto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class block_add extends Activity implements Button.OnClickListener {
    db_contract dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    String mode;

    int pid;
    int no;
    int block_count;

    ToggleButton enable_t;

    TextView block_phone_number_t;
    TextView block_phone_name_t;
    TextView block_sms_t;
    ToggleButton mon_t;
    ToggleButton tue_t;
    ToggleButton wed_t;
    ToggleButton thu_t;
    ToggleButton fri_t;
    ToggleButton sat_t;
    ToggleButton sun_t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        setContentView(R.layout.block_add_layout);

        block_phone_number_t = (TextView) findViewById(R.id.block_phone_number);
        block_phone_name_t = (TextView) findViewById(R.id.block_phone_name);
        block_sms_t = (TextView) findViewById(R.id.block_sms);
        mon_t = (ToggleButton) findViewById(R.id.mon);
        tue_t = (ToggleButton) findViewById(R.id.tue);
        wed_t = (ToggleButton) findViewById(R.id.wed);
        thu_t = (ToggleButton) findViewById(R.id.thu);
        fri_t = (ToggleButton) findViewById(R.id.fri);
        sat_t = (ToggleButton) findViewById(R.id.sat);
        sun_t = (ToggleButton) findViewById(R.id.sun);

        Button popup_cancel = (Button) findViewById(R.id.popup_cancel) ;
        popup_cancel.setOnClickListener(this) ;
        Log.d("error?","???????????");
        Button popup_apply = (Button) findViewById(R.id.popup_apply) ;
        popup_apply.setOnClickListener(this) ;
        Log.d("error?","???????????");
        //Button counter_reset = (Button) findViewById(R.id.counter_reset) ;
        //counter_reset.setOnClickListener(this) ;
        enable_t = (ToggleButton) findViewById(R.id.enable) ;
        //enable_t.setOnClickListener(this); ;
        Log.d("error?","???????????");


        switch (mode) {
            case "phone_block" :
                findViewById(R.id.mod_menu).setVisibility(View.GONE);
                //TextView txtText = (TextView) findViewById(R.id.test0);
                //txtText.setText(mode);
                break;
            case "phone_block_edit" :
                findViewById(R.id.mod_menu).setVisibility(View.VISIBLE);
                pid = intent.getIntExtra("pid", -1);
                no = intent.getIntExtra("no", -1);
                block_count = intent.getIntExtra("block_count", -1);
                dbHelper = new db_contract(this);
                db = dbHelper.getReadableDatabase();
                cursor = db.rawQuery(dbHelper.search_by_pid(pid), null);

                Log.d("pid", " -> " + pid);
                cursor.moveToFirst();
                Log.d("name", " -> " + cursor.getString(0));

                block_phone_name_t.setText(cursor.getString(0));
                block_phone_number_t.setText(cursor.getString(2));
                block_sms_t.setText(cursor.getString(3));
                mon_t.setChecked(cursor.getInt(5) == 1);
                tue_t.setChecked(cursor.getInt(6) == 1);
                wed_t.setChecked(cursor.getInt(7) == 1);
                thu_t.setChecked(cursor.getInt(8) == 1);
                fri_t.setChecked(cursor.getInt(9) == 1);
                sat_t.setChecked(cursor.getInt(10) == 1);
                sun_t.setChecked(cursor.getInt(11) == 1);
                enable_t.setChecked(cursor.getInt(1) == 1);

                cursor.close();
                break;
        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return  false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        //백버튼
        Intent resultIntent = new Intent();
        setResult(0, resultIntent);
        finish();
    }


    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        phone_block_dataset d = new phone_block_dataset();
        switch (view.getId()) {
            case R.id.popup_cancel :
                //resultIntent.putExtra("mode","cancel");
                setResult(0, resultIntent);
                finish();
                break ;
            case R.id.popup_apply :
                String block_phone_number = block_phone_number_t.getText().toString();
                String block_phone_name = block_phone_name_t.getText().toString();
                String block_sms = block_sms_t.getText().toString();
                Boolean mon = mon_t.isChecked();
                Boolean tue = tue_t.isChecked();
                Boolean wed = wed_t.isChecked();
                Boolean thu = thu_t.isChecked();
                Boolean fri = fri_t.isChecked();
                Boolean sat = sat_t.isChecked();
                Boolean sun = sun_t.isChecked();
                Boolean enable = enable_t.isChecked();
                dbHelper = new db_contract(this) ;
                db = dbHelper.getReadableDatabase();

                switch (mode) {
                    case "phone_block" :
                        d  = new phone_block_dataset(
                                block_phone_name, block_phone_number, false,
                                block_sms, false, mon, tue,
                                wed, thu, fri, sat, sun, 0, 0
                        );
                        db.execSQL(dbHelper.insert_phone_block_query(
                                d.get_name(),
                                d.get_is_filter(),
                                d.get_phone_number(),
                                d.get_message(),
                                d.get_is_message_reply(),
                                d.get_mon(),
                                d.get_tue(),
                                d.get_wed(),
                                d.get_thu(),
                                d.get_fri(),
                                d.get_sat(),
                                d.get_sun()));
                        break;
                    case "phone_block_edit" :
                        d  = new phone_block_dataset(
                                block_phone_name, block_phone_number, enable,
                                block_sms, false, mon, tue,
                                wed, thu, fri, sat, sun, block_count, pid
                        );
                        db.execSQL(dbHelper.update_phone_bock_query(
                                pid,
                                block_phone_name,
                                enable,
                                block_phone_number,
                                block_sms,
                                false,
                                mon,
                                tue,
                                wed,
                                thu,
                                fri,
                                sat,
                                sun));
                        break;
                }

                setResult(1, resultIntent);
                resultIntent.putExtra("data", d);
                switch (mode) {
                    case "phone_block" :
                        resultIntent.putExtra("mode", "phone_block");
                        break;
                    case "phone_block_edit" :
                        resultIntent.putExtra("mode", "phone_block_edit");
                        break;
                }

                //Toast.makeText(this, "적용버튼 클릭", Toast.LENGTH_LONG).show();
                resultIntent.putExtra("pid", pid);
                resultIntent.putExtra("no", no);
                db.close();
                dbHelper.close();
                finish();
                break ;
                /*
            case R.id.enable :
                dbHelper = new db_contract(this) ;
                db = dbHelper.getReadableDatabase();
                db.execSQL(dbHelper.disable_phone_bock_query(pid, enable.isChecked()));
                Log.d("query", dbHelper.disable_phone_bock_query(pid, enable.isChecked()));
                Toast.makeText(this, "사용버튼 클릭 - " + enable.isChecked(), Toast.LENGTH_SHORT).show();
                db.close();
                dbHelper.close();
                break ;
                */
        }
    }
/*
ToggleButton.OnCheckedChangeListener
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent resultIntent = new Intent();
        switch (buttonView.getId()) {
            case R.id.enable :
                Toast.makeText(this, "사용버튼 클릭 - " + isChecked, Toast.LENGTH_SHORT).show();
                break ;

        }
    }
    */
}
