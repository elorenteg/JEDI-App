package com.esterlorente.jediapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginHelper extends SQLiteOpenHelper {

    // Declaracion del nombre de la base de datos
    public static final int DATABASE_VERSION = 11;

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
    public static final String LAST_NOTIF = "last_notif";
    public static final String SCORE = "score";
    public static final String NUMCARDS = "numcards";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String USERENTRY = "userentry";

    // sentencia global de cracion de la base de datos
    public static final String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE + " (" +
            USERNAME + " TEXT PRIMARY KEY UNIQUE, " +
            PASSWORD + " TEXT, " +
            EMAIL + " TEXT, " +
            IMAGE + " TEXT, " +
            STREET + " TEXT, " +
            LAST_NOTIF + " TEXT, " +
            LATITUDE + " TEXT, " +
            LONGITUDE + " TEXT);";

    public static final String SCORE_TABLE_CREATE = "CREATE TABLE " + SCORE_TABLE + " (" +
            USERENTRY + " TEXT, " +
            USERNAME + " TEXT, " +
            SCORE + " INT, " +
            NUMCARDS + " INT," +
            "PRIMARY KEY (" + USERENTRY + "," + USERNAME + "," + NUMCARDS + "));";


    public LoginHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(SCORE_TABLE_CREATE);
    }

    public Cursor getUserByName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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

    public Cursor getUserProfileByName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {EMAIL, IMAGE, LAST_NOTIF, STREET, LATITUDE, LONGITUDE};
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
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {SCORE};
        String[] where = {username, username, Integer.toString(numCards)};
        Cursor c = db.query(
                SCORE_TABLE,                                                    // The table to query
                columns,                                                        // The columns to return
                USERENTRY + "=? AND " + USERNAME + "=? AND " + NUMCARDS + "=?", // The columns for the WHERE clause
                where,                                                          // The values for the WHERE clause
                null,                                                           // don't group the rows
                null,                                                           // don't filter by row groups
                null                                                            // The sort order
        );
        return c;
    }

    public void updateScoreByName(ContentValues values, String username, int numCards, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = {username, Integer.toString(numCards), Integer.toString(score)};
        db.update(
                SCORE_TABLE,                                                    // The table to query
                values,                                                         // The new column values
                USERNAME + "=? AND " + NUMCARDS + "=? AND " + SCORE + " >?",    // The columns for the WHERE clause
                where                                                           // The values for the WHERE clause
        );
    }

    public void createScore(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(
                SCORE_TABLE,
                null,
                values);
    }

    public Cursor getAllScoresAndImagesByNumcard(String userentry, int numCards) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"us." + USERNAME, IMAGE, SCORE};
        String[] where = {userentry, Integer.toString(numCards)};
        Cursor c = db.query(
                USER_TABLE + " us " + ", " + SCORE_TABLE + " sc",                                                   // The table to query
                columns,                                                                                            // The columns to return
                USERENTRY + " =? AND " + "us." + USERNAME + " = " + "sc." + USERNAME + " AND " + NUMCARDS + " =?",  // The columns for the WHERE clause
                where,                                                                                              // The values for the WHERE clause
                null,                                                                                               // don't group the rows
                null,                                                                                               // don't filter by row groups
                SCORE + " ASC"                                                                                      // The sort order
        );
        return c;
    }

    public void updateImageByName(ContentValues values, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = {username};
        db.update(
                USER_TABLE,                             // The table to query
                values,                                 // The new column values
                USERNAME + "=?",                        // The columns for the WHERE clause
                where                                   // The values for the WHERE clause
        );
    }

    public Cursor getScoresByName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {SCORE, NUMCARDS};
        String[] where = {username, username};
        Cursor c = db.query(
                SCORE_TABLE,                                // The table to query
                columns,                                    // The columns to return
                USERENTRY + " =? AND " + USERNAME + "=?",    // The columns for the WHERE clause
                where,                                      // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );
        return c;
    }

    public void updateLastNotification(ContentValues values, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = {username};
        db.update(
                USER_TABLE,                             // The table to query
                values,                                 // The new column values
                USERNAME + "=?",                        // The columns for the WHERE clause
                where                                   // The values for the WHERE clause
        );
    }

    public void updateUserStreetByName(ContentValues values, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = {username};
        db.update(
                USER_TABLE,                             // The table to query
                values,                                 // The new column values
                USERNAME + "=?",                        // The columns for the WHERE clause
                where                                   // The values for the WHERE clause
        );
    }

    public void updateUserCoordinatesByName(ContentValues values, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = {username};
        db.update(
                USER_TABLE,                             // The table to query
                values,                                 // The new column values
                USERNAME + "=?",                        // The columns for the WHERE clause
                where                                   // The values for the WHERE clause
        );
    }

    public void createScores(ContentValues[] values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                db.insert(SCORE_TABLE, null, value);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllUserNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USERNAME};
        Cursor c = db.query(
                USER_TABLE,                                 // The table to query
                columns,                                    // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );
        return c;
    }

    public Cursor getUsersWithEntry(String username, int numCards) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USERENTRY};
        String[] where = {username, Integer.toString(numCards)};
        Cursor c = db.query(
                SCORE_TABLE,                                // The table to query
                columns,                                    // The columns to return
                USERNAME + " =? AND " + NUMCARDS + "=?",    // The columns for the WHERE clause
                where,                                      // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );
        return c;
    }

    public void deleteScores(String userentry, int numCards) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] where = {userentry, Integer.toString(numCards)};
        db.delete(
                SCORE_TABLE,
                USERENTRY + " =? AND " + NUMCARDS + "=?",
                where);
    }
}
