package com.example.jawadfinalexampractice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.example.jawadfinalexampractice/databases/";
    private static String DB_NAME = "test_db";
    private SQLiteDatabase db;
    private Context myContext;
    
    
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }
    
    public void createDatabase() throws IOException {
        if (!localDBExists()) {
            // Creates empty database on default file path. We will replace this with our own db.
            this.getReadableDatabase();
            
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
    
    private boolean localDBExists() {
        SQLiteDatabase checkDB = null;
        
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // The database doesn't exist.
        }
        
        if (checkDB != null) {
            checkDB.close();
        }
        
        return checkDB != null;
    }

    private void copyDatabase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME + ".db");

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

        Log.i("DBHelper", "Copied Database");
    }

    public void openDatabase() throws SQLiteException {
        db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
