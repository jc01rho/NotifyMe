package com.prateek.notifyme.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Log;

import com.prateek.notifyme.Priority;
import com.prateek.notifyme.R;
import com.prateek.notifyme.commons.utils;

import java.util.Date;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String NOTIFICATION_TABLE_NAME = "notification_table";
    private static final String NOTIFICATION_COL1 = "ID";
    private static final String NOTIFICATION_COL2 = "TIMESTAMP";
    private static final String NOTIFICATION_COL3 = "APPNAME";
    private static final String NOTIFICATION_COL4 = "TXT";
    private static final String NOTIFICATION_COL5 = "PRIORITY";
    private static final String NOTIFICATION_COL6 = "appid";

    private static final String USER_TABLE_NAME = "user_table";
    private static final String USER_COL1 = "EMAIL";
    private static final String USER_COL2 = "FNAME";
    private static final String USER_COL3 = "LNAME";
    private static final String USER_COL4 = "DOB";
    private static final String USER_COL5 = "lastLogin";
    private static final String USER_COL6 = "signupTimestamp";

    private static final String APPLICATION_TABLE_NAME = "application_table";
    private static final String APPLICATION_COL1 = "appid";
    private static final String APPLICATION_COL2= "appName";
//    private static final String APPLICATION_COL3= "priority";
    private static final String APPLICATION_COL3= "enabled";
//    private static final String APPLICATION_COL5= "category";
//    private static final String APPLICATION_COL6= "totalNotifications";
    private static final String APPLICATION_COL4= "unreadNotifications";
    private static final String APPLICATION_COL5= "priority";
//    private static final String APPLICATION_COL8= "lastNotificationTimestamp";
//    prZivate static final String APPLICATION_COL9= "readTimestamp";
//    private static final String APPLICATION_COL10= "userId";

    public SQLiteHelper(Context context) {
        super(context, String.valueOf(R.string.app_name), null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);

    }

    private void createTables(SQLiteDatabase db) {
        String createTable_notification = "CREATE TABLE " + NOTIFICATION_TABLE_NAME + " (" +
                NOTIFICATION_COL1+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                NOTIFICATION_COL2+ " DATETIME NOT NULL, "+
                NOTIFICATION_COL3+ " TEXT NOT NULL, "+
                NOTIFICATION_COL4+ " TEXT NOT NULL, "+
                NOTIFICATION_COL5+ " TEXT NOT NULL, "+
                NOTIFICATION_COL6+ " TEXT NOT NULL); ";
        db.execSQL(createTable_notification);

        String createTable_user = "CREATE TABLE " + USER_TABLE_NAME+ " (" +
                USER_COL1+ " TEXT PRIMARY KEY, "+
                USER_COL2+ " TEXT NOT NULL, "+
                USER_COL3+ " TEXT NOT NULL, " +
                USER_COL4+ " TEXT NOT NULL, " +
                USER_COL5+ " TEXT NOT NULL, " +
                USER_COL6+ " TEXT NOT NULL" +"); ";
        db.execSQL(createTable_user);

        String createTable_application ="CREATE TABLE " + APPLICATION_TABLE_NAME+ " (" +
                APPLICATION_COL1+ " TEXT PRIMARY KEY, "+
                APPLICATION_COL2+ " TEXT, "+
                APPLICATION_COL3+ " TEXT, "+
                APPLICATION_COL4+ " TEXT, "+
                APPLICATION_COL5+ " TEXT); ";
//                APPLICATION_COL6+ "TEXT, "+
//                APPLICATION_COL7+ "TEXT, "+
//                APPLICATION_COL8+ "DATETIME, "+
//                APPLICATION_COL9+ "DATETIME, "+
//                APPLICATION_COL10+ "TEXT NOT NULL" +"); ";
        db.execSQL(createTable_application);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion){
            Log.d(TAG, "onUpgrade: UPDATED DATABASE");
            db.execSQL("DROP TABLE IF EXISTS "+ USER_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+ NOTIFICATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+ APPLICATION_TABLE_NAME);
            createTables(db);
        }
    }

    public Cursor getApplicationListingData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+ APPLICATION_COL2 +", "+ APPLICATION_COL4 +", "+ APPLICATION_COL1 +", "+ APPLICATION_COL5 +", "+ APPLICATION_COL3 +" FROM "+APPLICATION_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getAppNotifications(String appName) {
        SQLiteDatabase db = this.getWritableDatabase();
//        String whereVal = "'" + appName + "'";
        String query = "SELECT "+ NOTIFICATION_COL1 +", "+NOTIFICATION_COL4 +", "+ NOTIFICATION_COL2 + ", " + NOTIFICATION_COL6 +" FROM "+NOTIFICATION_TABLE_NAME + " WHERE "+ NOTIFICATION_COL3 +" = "+"'" + appName+"'";

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // saves the notification in NOTIFICATION_TABLE_NAME
    public boolean saveNotificationDB(String appName, Date time, String text, String appId){
        Log.d(TAG, "saveNotificationDB ###: "+appName +" :: "+ time +" :: "+ text +" :: "+ appId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTIFICATION_COL2, utils.timeToString(time));
        values.put(NOTIFICATION_COL3, appName);
        values.put(NOTIFICATION_COL4, text);
        values.put(NOTIFICATION_COL5, R.string.HIGH);
        values.put(NOTIFICATION_COL6, appId);
        long result = db.insert(NOTIFICATION_TABLE_NAME, null, values);
        Log.d(TAG, "saveNotificationDB CHECK: "+result);
        if (result == -1)
            return false;
        else
            return true;
    }
    // checks whether the notfication's application is present in the APPLICATION_TABLE_NAME (DashBoard)
    public boolean isAppPresent(String appId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ APPLICATION_TABLE_NAME+" where " + APPLICATION_COL1 +" = ?"
                ,new String[]{appId});
        if(res.getCount()>0){
            return true;
        }
        return false;

    }
    // updates the APPLICATION_TABLE_NAME with incremented unread counter
    public boolean updateAppTable(String appName, Integer unreadNotificationsCount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPLICATION_COL4, unreadNotificationsCount+1);
        long result = db.update(APPLICATION_TABLE_NAME, values, APPLICATION_COL1 +" = ?", new String[]{appName});
        if (result <=0)
            return false;
        else
            return true;
    }

    public Cursor getUnreadNotificationCount(String appId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+APPLICATION_COL4 +" from " + APPLICATION_TABLE_NAME+" where "
                +APPLICATION_COL1+" = ?" ,new String[]{appId});

        return res;
    }

    // Fetch attributes like priority, category to be inserted into the APPLICATION_TABLE_NAME
    public boolean insertApp(String appId, String appName, String enabled, String unreadNotifications, String priority){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPLICATION_COL1, appId);
        values.put(APPLICATION_COL2, appName);
        //values.put(APPLICATION_COL3, priority);
        values.put(APPLICATION_COL3, enabled);
//        values.put(APPLICATION_COL5, category);
//        values.put(APPLICATION_COL6, totalNotifications);
        values.put(APPLICATION_COL4, unreadNotifications);
        values.put(APPLICATION_COL5, priority);
//        values.put(APPLICATION_COL9, readTimestamp);
//        values.put(APPLICATION_COL10, userId);
        long result = db.insert(APPLICATION_TABLE_NAME, null, values);
        if (result == -1)
            return false;
        else
            return true;
    }

    //Clear all Notifications
    public boolean clearAllNotificationsDB(String appName){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(NOTIFICATION_TABLE_NAME, NOTIFICATION_COL3+" = ?", new String[]{appName});
        if (result <=0)
            return false;
        else
            return true;
    }

    public boolean deleteNotificationDB(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(NOTIFICATION_TABLE_NAME, NOTIFICATION_COL1+" = ?", new String[]{id});
        if (result <=0)
            return false;
        else
            return true;
    }

    public Cursor getEnabledDB(String appName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor checkEnabledCursor = db.rawQuery("select "+APPLICATION_COL3 +" from " + APPLICATION_TABLE_NAME+" where "
                +APPLICATION_COL2+" = ?" ,new String[]{appName});
        return checkEnabledCursor;
    }

    public boolean toggleUpdateApplicationDB(String appName, String toggle){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPLICATION_COL3, toggle);
        long result = db.update(APPLICATION_TABLE_NAME, values, APPLICATION_COL2 +" = ?", new String[]{appName});
        if (result <=0)
            return false;
        else
            return true;
    }

    public Cursor getEnableStatusForAppsDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select "+APPLICATION_COL2 + ","+APPLICATION_COL3+","+APPLICATION_COL5+" from " + APPLICATION_TABLE_NAME, null);
    }

    public boolean setAppPriorityDB(String appName, Enum priority){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPLICATION_COL5, priority.name());
        long result = db.update(APPLICATION_TABLE_NAME, values, APPLICATION_COL2 +" = ?", new String[]{appName});
        if (result <=0)
            return false;
        else
            return true;

    }

    public boolean resetApp(String appName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPLICATION_COL4, 0);
        long result = db.update(APPLICATION_TABLE_NAME, values, APPLICATION_COL2 +" = ?", new String[]{appName});
        if (result <=0)
            return false;
        else
            return true;
    }


}
    