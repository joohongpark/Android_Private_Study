package com.example.profq.call_block_proto;

import android.os.Parcel;
import android.os.Parcelable;

// intent를 통해 커스텀 클래스를 전달하기 위하여 phone block 클래스 수정 (parcelable 클래스 implements)
// 참조 : https://hashcode.co.kr/questions/882/안드로이드에서-parcelable이-뭔지-자세히-설명해주세요
public class phone_block_dataset implements Parcelable {
    private String name;
    private String phone_number;
    private boolean is_filter;
    private String message;
    private boolean is_message_reply;
    private boolean mon;
    private boolean tue;
    private boolean wed;
    private boolean thu;
    private boolean fri;
    private boolean sat;
    private boolean sun;
    private Integer blocked_count;
    private Integer pid;

    public phone_block_dataset(String name, String phone_number, boolean is_filter, String message, boolean is_message_reply, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun, Integer blocked_count, Integer pid) {
        this.name = name;
        this.phone_number = phone_number;
        this.is_filter = is_filter;
        this.message = message;
        this.is_message_reply = is_message_reply;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
        this.blocked_count = blocked_count;
        this.pid = pid;
    }

    @Override // 용도는 모르지만 오버라이딩 하라함
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { // 데이터 전송시 쓰는 클래스, 순서 지켜야함. (순서를 지켜야 함과 boolean 값 전달할 때 메소드를 보면 바이스 스트림 형태로 전달되는듯)
        dest.writeString(name);
        dest.writeString(phone_number);
        dest.writeString(message);
        dest.writeInt(blocked_count);
        dest.writeInt(pid);
        dest.writeByte((byte) (is_filter ? 1 : 0));
        dest.writeByte((byte) (is_message_reply ? 1 : 0));
        dest.writeByte((byte) (mon ? 1 : 0));
        dest.writeByte((byte) (tue ? 1 : 0));
        dest.writeByte((byte) (wed ? 1 : 0));
        dest.writeByte((byte) (thu ? 1 : 0));
        dest.writeByte((byte) (fri ? 1 : 0));
        dest.writeByte((byte) (sat ? 1 : 0));
        dest.writeByte((byte) (sun ? 1 : 0));
    }

    public phone_block_dataset() { }

    public phone_block_dataset(Parcel in) {  // 데이터 수신시 쓰는 클래스, 순서 지켜야함.
        name = in.readString();
        phone_number = in.readString();
        message = in.readString();
        blocked_count = in.readInt();
        pid = in.readInt();
        is_filter = in.readByte() == 1;
        is_message_reply = in.readByte() == 1;
        mon = in.readByte() == 1;
        tue = in.readByte() == 1;
        wed = in.readByte() == 1;
        thu = in.readByte() == 1;
        fri = in.readByte() == 1;
        sat = in.readByte() == 1;
        sun = in.readByte() == 1;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // 데이터 수신시 쓰는 생성
        public phone_block_dataset createFromParcel(Parcel in) {
            return new phone_block_dataset(in);
        }

        public phone_block_dataset[] newArray(int size) {
            return new phone_block_dataset[size];
        }
    };

    public String get_name() {
        return name;
    }

    public void set_name(String name) { this.name = name; }

    public String get_phone_number() {
        return phone_number;
    }

    public void set_phone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean get_is_filter() {
        return is_filter;
    }

    public void set_is_filter(boolean is_filter) {
        this.is_filter = is_filter;
    }

    public String get_message() {
        return message;
    }

    public void set_message(String message) {
        this.message = message;
    }

    public boolean get_is_message_reply() {
        return is_message_reply;
    }

    public void set_is_message_reply(boolean is_message_reply) { this.is_message_reply = is_message_reply; }

    public boolean get_mon() {
        return mon;
    }

    public void set_mon(boolean mon) {
        this.mon = mon;
    }

    public boolean get_tue() {
        return tue;
    }

    public void set_tue(boolean tue) {
        this.tue = tue;
    }

    public boolean get_wed() {
        return wed;
    }

    public void set_wed(boolean wed) {
        this.wed = wed;
    }

    public boolean get_thu() {
        return thu;
    }

    public void set_thu(boolean thu) {
        this.thu = thu;
    }

    public boolean get_fri() {
        return fri;
    }

    public void set_fri(boolean fri) {
        this.fri = fri;
    }

    public boolean get_sat() {
        return sat;
    }

    public void set_sat(boolean sat) {
        this.sat = sat;
    }

    public boolean get_sun() {
        return sun;
    }

    public void set_sun(boolean sun) {
        this.sun = sun;
    }

    public Integer get_blocked_count() {
        return blocked_count;
    }

    public void set_blocked_count(Integer blocked_count) { this.blocked_count = blocked_count; }

    public Integer get_pid() {
        return pid;
    }

    public void set_pid(Integer pid) { this.pid = pid; }

}
