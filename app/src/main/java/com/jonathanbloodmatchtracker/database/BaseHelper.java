package com.jonathanbloodmatchtracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class to setup the database schema.
 *
 * @author Jonathan
 */
public class BaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "matchtracker";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE_MATCH = "create table Match " +
            "(match_id integer primary key autoincrement, " +
            "date text, " +
            "sport text," +
            "team1 text," +
            "team2 text," +
            "venue text," +
            "type text," +
            "type_name text," +
            "referee text);";

    private static final String DATABASE_CREATE_TEAM = "create table Team " +
            "(name text, " +
            "sport text, " +
            "wins integer," +
            "losses integer," +
            "draws integer," +
            "total_points integer);";

    private static final String DATABASE_CREATE_FACTS = "create table Facts " +
            "(match_id integer, " +
            "type text," +
            "player1 text," +
            "player2 text," +
            "team text," +
            "time text);";


    public BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Create database tables.
     */
    public void onCreate(SQLiteDatabase db) {
        // Execute database creation query.
        db.execSQL(DATABASE_CREATE_MATCH);
        db.execSQL(DATABASE_CREATE_TEAM);
        db.execSQL(DATABASE_CREATE_FACTS);
    }


    /**
     * Upgrade database by increasing DATABASE_VERSION number.
     * This method upgrades the database by dropping the tables and creating
     * them again.
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DATABASE", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS Match");
        db.execSQL("DROP TABLE IF EXISTS Team");
        db.execSQL("DROP TABLE IF EXISTS Facts");
        onCreate(db);
    }

}

