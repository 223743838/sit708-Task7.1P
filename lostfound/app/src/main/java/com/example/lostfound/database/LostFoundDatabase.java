package com.example.lostfound.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.lostfound.model.Item;

import java.util.ArrayList;
import java.util.List;

public class LostFoundDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_found_db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "items";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESC = "description";
    private static final String COL_LOCATION = "location";
    private static final String COL_DATE = "date";
    private static final String COL_CONTACT = "contact";
    private static final String COL_TYPE = "type"; // Lost or Found

    public LostFoundDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_DESC + " TEXT, "
                + COL_LOCATION + " TEXT, "
                + COL_DATE + " TEXT, "
                + COL_CONTACT + " TEXT, "
                + COL_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int nVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE items ADD COLUMN type TEXT");
        }
    }

    public Item getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC)));
            item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
            item.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
            item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT)));
            item.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
            cursor.close();
            return item;
        }

        return null;
    }

    public void insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, item.getTitle());
        values.put(COL_DESC, item.getDescription());
        values.put(COL_LOCATION, item.getLocation());
        values.put(COL_DATE, item.getDate());
        values.put(COL_CONTACT, item.getContact());
        values.put(COL_TYPE, item.getType());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC)));
                item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
                item.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
                item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT)));
                item.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}