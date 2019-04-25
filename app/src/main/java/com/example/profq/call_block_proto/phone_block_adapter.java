package com.example.profq.call_block_proto;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class phone_block_adapter extends ArrayAdapter<phone_block_dataset> {
    // 어댑터의 기본 변수
    private Context context;
    private List mList;
    private ListView mListView;
    // 라디오버튼 관련
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;

    // 어댑터 생성자 정의
    phone_block_adapter(Context context, List<phone_block_dataset> list, ListView listview) {
        super(context, 0, list);
        this.context = context;
        this.mList = list;
        this.mListView = listview;
    }

    // 리스트뷰의 효율적인 사용을 위해 뷰홀더를 정의한다.
    class UserViewHolder { // 뷰를 보관하기 위한 홀더
        public TextView name;
        public TextView phone_number;
        public TextView message;
        public TextView is_message_reply;
        public TextView block_week;
        public TextView blocked_count;
        public boolean is_show;
        public TextView message_none;
    }

    // 리스트뷰를 렌더링할 때 호출되는 메소드
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parentViewGroup) {
        // position : 인덱스
        // convertView : 줄에 대한 뷰 (새로 생성 혹은 재활용)
        // parentViewGroup : 부모 뷰 그룹 (XML -> view 객체 변환할 때 씀)
        View row = convertView; // 코드 가독성을 위해서 rowView 변수를 사용함. (오버라이딩 한 메소드라 막 바꾸면 안됨)
        final UserViewHolder viewHolder;
        //생성자를 통해 생성된 데이터 리스트의 position 위치에 있는 객체를 가져옴.
        final phone_block_dataset row_data = (phone_block_dataset) mList.get(position);

        // 기존에 만들은 적 있는 뷰인가? 아니면 새로 만듬
        if (row == null) {

            // 리스트뷰 xml에 접근해 계층 구조의 객체로 반환.
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(R.layout.listview_call_block, parentViewGroup, false);

            // 뷰홀더에다 미리 꽃아놓음. 이렇게 만들어 두면 나중에 findViewById로 접근할 필요가 없어 성능 상승됨
            viewHolder = new UserViewHolder();
            viewHolder.is_show = false;
            viewHolder.name = (TextView) row.findViewById(R.id.name);
            viewHolder.phone_number = (TextView) row.findViewById(R.id.phone_number);
            viewHolder.message = (TextView) row.findViewById(R.id.message);
            viewHolder.blocked_count = (TextView) row.findViewById(R.id.blocked_count);
            viewHolder.block_week = (TextView) row.findViewById(R.id.phone_block_day);
            viewHolder.message_none = (TextView) row.findViewById(R.id.message_none);

            // 뷰홀더를 해당 열 (뷰)에 태깅
            row.setTag(viewHolder);
        } else {
            // 기존에 태깅해둔거 불러옴
            viewHolder = (UserViewHolder) row.getTag();
        }



        //현재 선택된 Vocal 객체를 화면에 보여주기 위해서 앞에서 미리 찾아 놓은 뷰에 데이터를 집어넣음.
        viewHolder.name.setText(row_data.get_name());
        viewHolder.phone_number.setText(row_data.get_phone_number());
        if(!row_data.get_message().equals("")) {
            viewHolder.message_none.setVisibility(View.GONE);
            viewHolder.message.setVisibility(View.VISIBLE);
            viewHolder.message.setText(row_data.get_message());
        } else {
            viewHolder.message_none.setVisibility(View.VISIBLE);
            viewHolder.message.setVisibility(View.GONE);
        }

        //차단횟수 표기
        viewHolder.blocked_count.setText(row_data.get_blocked_count() > 99 ? "99+" : "" + row_data.get_blocked_count());
        if (row_data.get_blocked_count() == 0) {
            row.findViewById(R.id.blocked_count).setVisibility(View.INVISIBLE);
        } else {
            row.findViewById(R.id.blocked_count).setVisibility(View.VISIBLE);
        }

        // 요일 텍스트
        if(!row_data.get_is_filter()) {
            viewHolder.block_week.setText(week_to_text(
                    row_data.get_mon(),
                    row_data.get_tue(),
                    row_data.get_wed(),
                    row_data.get_thu(),
                    row_data.get_fri(),
                    row_data.get_sat(),
                    row_data.get_sun()));
        } else {
            viewHolder.block_week.setText(week_to_text(
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false));
        }

        // 줄 반환
        return row;
    }

    // 리스트뷰 접근시켜주는 메소드. 출처 - https://stackoverflow.com/questions/24811536/android-listview-get-item-view-by-position
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private String week_to_text(Boolean mon,
                                Boolean tue,
                                Boolean wed,
                                Boolean thu,
                                Boolean fri,
                                Boolean sat,
                                Boolean sun) {
        Resources res = getContext().getResources();
        String return_text = "";
        //String return_text = (mon && tue && wed && thu && fri && sat && sun) ? res.getString(R.string.allday) : "";
        String weekend = (sat && sun) ? res.getString(R.string.weekend) : (sat ? res.getString(R.string.sat) : "" + (sun ? res.getString(R.string.sun) : ""));
        if(mon && tue && wed && thu && fri && sat && sun) {
            return_text = res.getString(R.string.all_day);
        } else if( mon && tue && wed && thu && fri ) {
            return_text = weekend + (weekend.isEmpty() ? "" : ", ") + res.getString(R.string.weekday);
        } else {
            return_text += weekend;
            if(mon) {
                return_text += (return_text.isEmpty() ? "" : ", ") + res.getString(R.string.mon);
            }
            if(tue) {
                return_text += (return_text.isEmpty() ? "" : ", ") + res.getString(R.string.tue);
            }
            if(wed) {
                return_text += (return_text.isEmpty() ? "" : ", ") + res.getString(R.string.wed);
            }
            if(thu) {
                return_text += (return_text.isEmpty() ? "" : ", ") + res.getString(R.string.thu);
            }
            if(fri) {
                return_text += (return_text.isEmpty() ? "" : ", ") + res.getString(R.string.fri);
            }
        }
        if (!(mon || tue || wed || thu || fri || sat || sun)) {
            return_text = res.getString(R.string.free);
        }
        return return_text;
    }

}
