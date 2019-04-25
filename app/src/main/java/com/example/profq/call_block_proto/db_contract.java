package com.example.profq.call_block_proto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

// Contract Class
public class db_contract extends SQLiteOpenHelper {
    // 공통
    public static final String P_BLOCK = "P_BLOCK"; // phone block table name
    public static final String PID = "PID"; // column ID
    public static final String NAME = "NAME"; // name about block
    public static final String IS_FILTER = "IS_FILTER"; // filtering now?
    public static final String MESSAGE = "MESSAGE"; // message to be replied
    public static final String IS_MESSAGE_REPLY = "IS_MESSAGE_REPLY"; // have a message to reply
    public static final String MON = "MON";
    public static final String TUE = "TUE";
    public static final String WED = "WED";
    public static final String THU = "THU";
    public static final String FRI = "FRI";
    public static final String SAT = "SAT";
    public static final String SUN = "SUN";
    public static final String BLOCK_COUNT = "BLOCK_COUNT"; // count blocked calling

    public static final int DB_VERSION = 1 ;
    public static final String DBFILE_CONTACT = "block_data.db" ;

    // 전화번호 차단
    public static final String PHONE_NUMBER = "PHONE_NUMBER";


    // CREATE TABLE IF NOT EXISTS phone block
    public static final String SQL_CREATE_TBL_P_BLOCK = "CREATE TABLE IF NOT EXISTS " + P_BLOCK + " " +
            "(" +
            PID              + " INTEGER PRIMARY KEY AUTOINCREMENT" +   ", " +
            NAME             + " TEXT"                              +   ", " +
            IS_FILTER        + " INTEGER"                           +   ", " +
            PHONE_NUMBER     + " TEXT"                              +   ", " +
            MESSAGE          + " TEXT"                              +   ", " +
            IS_MESSAGE_REPLY + " INTEGER"                           +   ", " +
            MON              + " INTEGER"                           +   ", " +
            TUE              + " INTEGER"                           +   ", " +
            WED              + " INTEGER"                           +   ", " +
            THU              + " INTEGER"                           +   ", " +
            FRI              + " INTEGER"                           +   ", " +
            SAT              + " INTEGER"                           +   ", " +
            SUN              + " INTEGER"                           +   ", " +
            BLOCK_COUNT      + " INTEGER"                           +
            ")" ;

    // phone block select
    public static final String SQL_SELECT_ALL = "SELECT " +
            PID              + ", " +
            NAME             + ", " +
            IS_FILTER        + ", " +
            PHONE_NUMBER     + ", " +
            MESSAGE          + ", " +
            IS_MESSAGE_REPLY + ", " +
            MON              + ", " +
            TUE              + ", " +
            WED              + ", " +
            THU              + ", " +
            FRI              + ", " +
            SAT              + ", " +
            SUN              + ", " +
            BLOCK_COUNT      +

            " FROM " + P_BLOCK + " WHERE 1";

    public db_contract(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(db_contract.SQL_CREATE_TBL_P_BLOCK) ;
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL(db_contract.SQL_DROP_TBL) ;
        onCreate(db) ;
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // onUpgrade(db, oldVersion, newVersion);
    }

    public String insert_phone_block_query(      String name ,
                                                Boolean is_filter ,
                                                String phone_number ,
                                                String message ,
                                                Boolean is_message_reply ,
                                                Boolean mon ,
                                                Boolean tue ,
                                                Boolean wed ,
                                                Boolean thu ,
                                                Boolean fri ,
                                                Boolean sat ,
                                                Boolean sun ) {
        return "INSERT INTO " + P_BLOCK + "(" +
                NAME + ", " +
                IS_FILTER + ", " +
                PHONE_NUMBER + ", " +
                MESSAGE + ", " +
                IS_MESSAGE_REPLY + ", " +
                MON + ", " +
                TUE + ", " +
                WED + ", " +
                THU + ", " +
                FRI + ", " +
                SAT + ", " +
                SUN + ", " +
                BLOCK_COUNT + ") " +
                "VALUES (" +
                "'" + name + "'"  + ", " +
                ((is_filter) ? "1" : "0") + ", " +
                "'" + phone_number + "'"  + ", " +
                "'" + message + "'"  + ", " +
                ((is_message_reply) ? "1" : "0")  + ", " +
                ((mon) ? "1" : "0") + ", " +
                ((tue) ? "1" : "0") + ", " +
                ((wed) ? "1" : "0") + ", " +
                ((thu) ? "1" : "0") + ", " +
                ((fri) ? "1" : "0") + ", " +
                ((sat) ? "1" : "0") + ", " +
                ((sun) ? "1" : "0") + ", " +
                "0" +
                ")";
    }

    public String update_phone_bock_query(      int pid ,
                                                String name ,
                                                Boolean is_filter ,
                                                String phone_number ,
                                                String message ,
                                                Boolean is_message_reply ,
                                                Boolean mon ,
                                                Boolean tue ,
                                                Boolean wed ,
                                                Boolean thu ,
                                                Boolean fri ,
                                                Boolean sat ,
                                                Boolean sun ) {
        return "UPDATE " + P_BLOCK +
                " SET " +
                NAME              + " = " + "'" + name + "'"                    + ", " +
                IS_FILTER         + " = " + ((is_filter) ? "1" : "0")           + ", " +
                PHONE_NUMBER      + " = " + "'" + phone_number + "'"            + ", " +
                MESSAGE           + " = " + "'" + message + "'"                 + ", " +
                IS_MESSAGE_REPLY  + " = " + ((is_message_reply) ? "1" : "0")    + ", " +
                MON               + " = " + ((mon) ? "1" : "0")                 + ", " +
                TUE               + " = " + ((tue) ? "1" : "0")                 + ", " +
                WED               + " = " + ((wed) ? "1" : "0")                 + ", " +
                THU               + " = " + ((thu) ? "1" : "0")                 + ", " +
                FRI               + " = " + ((fri) ? "1" : "0")                 + ", " +
                SAT               + " = " + ((sat) ? "1" : "0")                 + ", " +
                SUN               + " = " + ((sun) ? "1" : "0")                 +  " " +
                "WHERE " +
                PID + " = " + pid;
    }

    public String search_by_pid(int pid) {
        return  "SELECT " +
                NAME             + ", " +
                IS_FILTER        + ", " +
                PHONE_NUMBER     + ", " +
                MESSAGE          + ", " +
                IS_MESSAGE_REPLY + ", " +
                MON              + ", " +
                TUE              + ", " +
                WED              + ", " +
                THU              + ", " +
                FRI              + ", " +
                SAT              + ", " +
                SUN              + ", " +
                BLOCK_COUNT      +

                " FROM " + P_BLOCK + " WHERE " + PID + " = " + pid;
    }

    public String delete_phone_bock_query(List<Integer> list) {
        int length = list.size();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < length; i++) {
            query.append(list.get(i)).append( (length == (i + 1)) ? "" : ", ");
        }
        return "DELETE FROM " + P_BLOCK + " WHERE " + PID + " IN (" + query + ")";
    }

    /*
    public String disable_phone_block_query(int pid, boolean enable) {
        return "UPDATE " +
                P_BLOCK +
                " SET " +
                IS_FILTER +
                " = " +
                (enable ? 1 : 0) +
                " WHERE " +
                PID +
                " = " +
                pid ;
    }
    */
}
