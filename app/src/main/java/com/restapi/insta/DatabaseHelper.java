package com.restapi.insta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.restapi.insta.Model.Chat;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    Context context;
    private static String DATABASE_NAME = "chats.db";

    private static int DATABASE_VERSION = 1;

    private static String createTableQuery = "create table chatInfo (isSeen TEXT" + ",message TEXT" + ",messageId TEXT UNIQUE" + ",receiver TEXT" + ",sender TEXT" + ",url TEXT)";


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

            long checkQuery = sqLiteDatabase.insertWithOnConflict("chatInfo", null, objectContentValues, SQLiteDatabase.CONFLICT_REPLACE);

            if (checkQuery !=-1)
            {

                //Toast.makeText(context,"Data added successfully to sqLite!!",Toast.LENGTH_SHORT).show();
                sqLiteDatabase.close();
            }else
            {
                //Toast.makeText(context,"Failed to add data",Toast.LENGTH_SHORT).show();
            }





        }


        catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    public ArrayList<Chat> getUsersChats(){


        try {

            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            ArrayList<Chat> chats =new ArrayList<>();
            Cursor cursor =sqLiteDatabase.rawQuery("select * from chatInfo", null);


                while (cursor.moveToNext()){

                    Boolean isSeen = Boolean.valueOf(cursor.getString(0));
                    String message = cursor.getString(1);
                    String msgId = cursor.getString(2);
                    String Receiver = cursor.getString(3);
                    String Sender = cursor.getString(4);
                    String url = cursor.getString(5);

                    /*Chat chat = new Chat();
                    chat.setIsSeen(Boolean.valueOf((cursor.getString(0))));
                    chat.setMessage(cursor.getString(1));
                    chat.setMessageId(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSender(cursor.getString(4));
                    chat.setUrl(cursor.getString(6));*/

                    chats.add(new Chat(Sender, Receiver, msgId, message, url, isSeen));


                }
                return chats;


            }

        catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }


    public void deleteMessage(String messageId){

        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("chatInfo","messageId=?", new String[]{messageId});
        Toast.makeText(context,""+result,Toast.LENGTH_SHORT).show();

        if (result == 0)
        {
            Toast.makeText(context,"Deletion Failed",Toast.LENGTH_SHORT).show();
        }

        if (result == -1){
            Toast.makeText(context,"Deletion Failed1",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(context,"Deleted Successfully;",Toast.LENGTH_SHORT).show();

        }




    }



}
