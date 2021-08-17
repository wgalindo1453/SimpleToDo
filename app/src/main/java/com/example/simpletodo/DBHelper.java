package com.example.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Usertasks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {

        DB.execSQL("create TABLE Usertasks(id INTEGER primary key autoincrement, task TEXT,  pri TEXT, date TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop TABLE if exists Usertasks");

    }

    public Boolean insertuserdata(String task, String pri, String date){

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", task);
        contentValues.put("pri", pri);
        contentValues.put("date", date);
        long result = DB.insert("Usertasks", null, contentValues);
        if(result  == -1)
        {
            return false;
        }else{
            return true;
        }

    }


    public Boolean updateuserdata(int id, String task, String pri, String date) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task",task);
        contentValues.put("pri", pri);
        contentValues.put("date", date);
        Cursor cursor = DB.rawQuery("Select * from Usertasks where id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            long result = DB.update("Usertasks", contentValues, "id=?", new String[] {String.valueOf(id)});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }}
    public int gettaskid(String task){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor res = DB.rawQuery("Select * from Usertasks where task = ?", new String[]{task});
        StringBuffer buffer = new StringBuffer();   //id = 0 task = 1 pri = 2 date = 3
        int id = 0;
        while(res.moveToNext()){


            id = Integer.parseInt(res.getString(0));

        }
        return id;

    }


    public Cursor gettaskinfo(String task) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Usertasks where task = ?", new String[]{task});

        return cursor;
      }



    public Boolean deletedata( int id){

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Usertasks where id = ?", new String[]{String.valueOf(id)});
        if(cursor.getCount()>0) {


            long result = DB.delete("usertasks", "id=?", new String[]{String.valueOf(id)});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }else{
            return false;
        }

    }
    public Cursor getdata(){

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Usertasks order by id asc", null);
        return cursor;

    }
}
