package com.example.profq.call_block_proto;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class phone_block extends ListFragment implements Button.OnClickListener {

    phone_block_adapter Adapter = null;
    List<phone_block_dataset> item_element = new ArrayList<>();
    View view_fragment;
    ListView list_listview;
    db_contract dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    SwipeDismissListViewTouchListener touchListener;
    private Boolean IS_EDIT = false;

    public phone_block() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view_fragment = inflater.inflate(R.layout.phone_block_layout, container, false);

        FloatingActionButton add = (FloatingActionButton) view_fragment.findViewById(R.id.phone_block_add) ;
        add.setOnClickListener(this) ;
        dbHelper = new db_contract(view_fragment.getContext()) ;
        db = dbHelper.getReadableDatabase() ;
        cursor = db.rawQuery(db_contract.SQL_SELECT_ALL, null);
        while (cursor.moveToNext()) {
            item_element.add(new phone_block_dataset(
                 cursor.getString(1 ),
                 cursor.getString(3 ),
                (cursor.getInt   (2 ) == 1),
                 cursor.getString(4 ),
                (cursor.getInt   (5 ) == 1),
                (cursor.getInt   (6 ) == 1),
                (cursor.getInt   (7 ) == 1),
                (cursor.getInt   (8 ) == 1),
                (cursor.getInt   (9 ) == 1),
                (cursor.getInt   (10) == 1),
                (cursor.getInt   (11) == 1),
                (cursor.getInt   (12) == 1),
                 cursor.getInt   (13),
                 cursor.getInt   (0 )

            ));
        }
        cursor.close();

        if (item_element.size() == 0) {
            view_fragment.findViewById(R.id.block_alert_layout).setVisibility(View.VISIBLE);
        } else {
            view_fragment.findViewById(R.id.block_alert_layout).setVisibility(View.GONE);
        }

        list_listview = (ListView) view_fragment.findViewById(android.R.id.list);
        Adapter = new phone_block_adapter(view_fragment.getContext(), item_element, list_listview);
        list_listview.setAdapter(Adapter);

        touchListener = new SwipeDismissListViewTouchListener(
                    list_listview,
                    new SwipeDismissListViewTouchListener.Callbacks() {
                        @Override
                        public boolean canDismiss(int position) {
                            return true;
                        }

                        @Override
                        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                            Log.d("delete","position : " + 0);
                            for (int position : reverseSortedPositions) {
                                Adapter.remove(Adapter.getItem(position));
                            }
                            Adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void button_click(int position, int button_position) {
                            Intent intent;
                            Log.d("click test","position : " + position + ", button_position : " + button_position);
                            Log.d("SwipeDismiss test","" + this.getClass());
                            if(button_position == 0) {
                                Log.d("view","position : " + position);
                                phone_block.this.touchListener.dismiss(position);
                                //Adapter.remove(Adapter.getItem(position));
                                //Adapter.notifyDataSetChanged();
                            } else if (button_position == 1) {
                                int pid = item_element.get(position).get_pid();
                                int block_count = item_element.get(position).get_blocked_count();
                                intent = new Intent(getContext(), block_add.class);
                                intent.putExtra("mode", "phone_block_edit");
                                intent.putExtra("pid", pid);
                                intent.putExtra("block_count", block_count);
                                intent.putExtra("no", position);
                                startActivityForResult(intent, 1);
                            }
                        }
                    });
        list_listview.setOnTouchListener(touchListener);
        list_listview.setOnScrollListener(touchListener.makeScrollListener());
        touchListener.setEnabled(true);
        return view_fragment;
    }

    @Override
    public void onClick(View view) {
        int count;
        Intent intent;
        Resources res;
        switch (view.getId()) {
            case R.id.phone_block_add :
                intent = new Intent(view.getContext(), block_add.class);
                intent.putExtra("mode", "phone_block");
                startActivityForResult(intent, 1);
                break ;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            switch (requestCode){
                    case 1:
                        Bundle b = data.getExtras();
                        assert b != null;
                        phone_block_dataset phone_block_data = b.getParcelable("data");
                        switch (data.getStringExtra("mode")) {
                            case "phone_block" :
                                item_element.add(phone_block_data);
                                break;
                            case "phone_block_edit" :
                                item_element.set(data.getIntExtra("no", -1), phone_block_data);
                                break;
                        }

                        view_fragment.findViewById(R.id.block_alert_layout).setVisibility(View.GONE);
                        break;
                    default:
                        break;
            }
        } else {
            switch (requestCode){
                default:
                    break;
            }
        }
        Adapter.notifyDataSetChanged();
    }
}