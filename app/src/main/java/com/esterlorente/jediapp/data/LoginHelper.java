package com.esterlorente.jediapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginHelper extends SQLiteOpenHelper {

    // Declaracion del nombre de la base de datos
    public static final int DATABASE_VERSION = 6;

    // Declaracion global de la version de la base de datos
    public static final String DATABASE_NAME = "USER_DB";

    // Declaracion del nombre de la tabla
    public static final String USER_TABLE = "USER";
    public static final String SCORE_TABLE = "SCORE_TB";

    // Declaracion de las columnas de la tabla
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String IMAGE = "image";
    public static final String STREET = "street";
    public static final String SCORE = "score";
    public static final String NUMCARDS = "numcards";

    // sentencia global de cracion de la base de datos
    public static final String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE + " (" +
            USERNAME + " TEXT PRIMARY KEY UNIQUE, " +
            PASSWORD + " TEXT, " +
            EMAIL + " TEXT, " +
            IMAGE + " BLOB, " +
            STREET + " TEXT );";

    public static final String SCORE_TABLE_CREATE = "CREATE TABLE " + SCORE_TABLE + " (" +
            USERNAME + " TEXT, " +
            SCORE + " INT, " +
            NUMCARDS + " INT," +
            "PRIMARY KEY (" + USERNAME + "," + NUMCARDS + "));";


    public LoginHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(SCORE_TABLE_CREATE);
    }

    public Cursor getUserByName(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {USERNAME, PASSWORD};
        String[] where = {username};
        Cursor c = db.query(
                USER_TABLE,                             // The table to query
                columns,                                // The columns to return
                USERNAME + "=?",                        // The columns for the WHERE clause
                where,                                  // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );
        return c;
    }

    public Cursor getUserImageByName(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {IMAGE};
        String[] where = {username};
        Cursor c = db.query(
                USER_TABLE,                             // The table to query
                columns,                                // The columns to return
                USERNAME + "=?",                        // The columns for the WHERE clause
                where,                                  // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );
        return c;
    }

    public void createUser(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(
                USER_TABLE,
                null,
                values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    public Cursor getUserScoreByName(String username, int numCards) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {SCORE};
        String[] where = {username, Integer.toString(numCards)};
        Cursor c = db.query(
                SCORE_TABLE,                            // The table to query
                columns,                                // The columns to return
                USERNAME + "=? AND " + NUMCARDS + "=?", // The columns for the WHERE clause
                where,                                  // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );
        return c;
    }

    public void updateScoreByName(ContentValues values, String username, int numCards) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = {username, Integer.toString(numCards)};
        db.update(
                SCORE_TABLE,                            // The table to query
                values,                                 // The new column values
                USERNAME + "=? AND " + NUMCARDS + "=?", // The columns for the WHERE clause
                where                                   // The values for the WHERE clause
        );
    }

    public void createScore(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(
                SCORE_TABLE,
                null,
                values);
    }

    public Cursor getAllScoresByNumcard(int numCards) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {USERNAME, SCORE};
        String[] where = {Integer.toString(numCards)};
        Cursor c = db.query(
                SCORE_TABLE,                            // The table to query
                columns,                                // The columns to return
                NUMCARDS + "=?", // The columns for the WHERE clause
                where,                                  // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );
        return c;
    }
}
