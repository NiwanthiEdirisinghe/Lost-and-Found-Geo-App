package com.example.lostandfoundappbygeo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lostandfound.db";
    private static final int DATABASE_VERSION = 2;


    private static final String TABLE_ITEMS = "items";


    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_TYPE = "type"; // "Lost" or "Found"
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";


    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PHONE + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_LOCATION + " TEXT, "
            + COLUMN_TYPE + " TEXT, "
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_LATITUDE + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_LONGITUDE + " REAL");
        }
    }


    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_PHONE, item.getPhone());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_LOCATION, item.getLocation());
        values.put(COLUMN_TYPE, item.getType());
        values.put(COLUMN_LATITUDE, item.getLatitude());
        values.put(COLUMN_LONGITUDE, item.getLongitude());

        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }


    @SuppressLint("Range")
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                item.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                item.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                item.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
                item.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));

                if (cursor.getColumnIndex(COLUMN_LATITUDE) != -1) {
                    item.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                }
                if (cursor.getColumnIndex(COLUMN_LONGITUDE) != -1) {
                    item.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                }

                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    @SuppressLint("Range")
    public Item getItem(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ITEMS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        Item item = new Item();
        item.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        item.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        item.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
        item.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
        item.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));

        if (cursor.getColumnIndex(COLUMN_LATITUDE) != -1) {
            item.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
        }
        if (cursor.getColumnIndex(COLUMN_LONGITUDE) != -1) {
            item.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
        }

        cursor.close();
        db.close();
        return item;
    }

    public void deleteItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
