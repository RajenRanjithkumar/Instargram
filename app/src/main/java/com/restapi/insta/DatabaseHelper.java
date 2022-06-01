package com.restapi.insta;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.restapi.insta.Model.Chat;

public class DatabaseHelper extends SQLiteOpenHelper {


    Context context;
    private static String DATABASE_NAME = "chats.db";

    private static int DATABASE_VERSION = 1;

    private static String createTableQuery = "create table chatInfo (isSeen TEXT" + ",message TEXT" + ",messageId TEXT" + ",receiver TEXT" + ",sender TEXT" + ",url TEXT)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            db.execSQL(createTableQuery);
            Toast.makeText(context,"Database created successfully",Toast.LENGTH_SHORT).show();

        }catch (Exception e){

            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void storeChat(Chat chat){


        try {

            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            ContentValues objectContentValues = new ContentValues();
            objectContentValues.put("isSeen", chat.getIsSeen());
            objectContentValues.put("message", chat.getMessage());
            objectContentValues.put("messageId", chat.getMessageId());
            objectContentValues.put("receiver", chat.getReceiver());
            objectContentValues.put("sender", chat.getSender());
            objectContentValues.put("url", chat.getUrl());

            long checkQuery = sqLiteDatabase.insert("chatInfo", null, objectContentValues);
            if (checkQuery !=-1)
            {

                Toast.makeText(context,"Data added successfully to sqLite!!",Toast.LENGTH_SHORT).show();
                sqLiteDatabase.close();
            }else
            {
                Toast.makeText(context,"Failed to add data",Toast.LENGTH_SHORT).show();
            }





        }


        catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


}
