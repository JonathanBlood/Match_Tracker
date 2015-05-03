package com.jonathanbloodmatchtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jonathanbloodmatchtracker.main.MatchInfo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Class that allows interaction with the Match database such as insert, delete, update.
 *
 * @author Jonathan
 */
public class MatchDbAdapter {

    public static final String KEY_MATCHID  = "match_id";
    public static final String KEY_DATE     = "date";
    public static final String KEY_SPORT    = "sport";
    public static final String KEY_TEAM1    = "team1";
    public static final String KEY_TEAM2    = "team2";
    public static final String KEY_VENUE    = "venue";
    public static final String KEY_TYPE     = "type";
    public static final String KEY_TYPENAME = "type_name";
    public static final String KEY_REFEREE  = "referee";
    private static final String DB_TABLE    = "Match";
    SimpleDateFormat dateFormat;
    private SQLiteDatabase db;
    private BaseHelper dbHelper;
    private Context context;

    public MatchDbAdapter(Context context) {
        this.context = context;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Open database so we can write to it.
     *
     * @return Open DB connection
     * @throws SQLException
     */
    public MatchDbAdapter open() throws SQLException {
        dbHelper    = new BaseHelper(context);
        db          = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close database.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Add match(row) to database.
     *
     * @param sport   Hurling, Gaelic, Football etc
     * @param team1   Team1
     * @param team2   Team2
     * @param venue   Venue
     * @param type    Type
     * @param referee Referee
     */
    public void addMatch(String sport, String team1, String team2, String venue, String type, String typeName, String referee) {
        //Get date and format it.
        Date date       = new Date(System.currentTimeMillis());
        String dateStr  = dateFormat.format(date);

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, dateStr);
        values.put(KEY_SPORT, sport);
        values.put(KEY_TEAM1, team1);
        values.put(KEY_TEAM2, team2);
        values.put(KEY_VENUE, venue);
        values.put(KEY_TYPE, type);
        values.put(KEY_TYPENAME, typeName);
        values.put(KEY_REFEREE, referee);

        //Insert into database.
        try {
            db.insert(DB_TABLE, null, values);
        } catch (Exception e) {
            Log.e("Database error inserting into Match Table", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Get the column values for a specific column provided.
     *
     * @param column     column name
     * @param duplicates Select duplicates or not
     * @return match
     */
    public ArrayList<String> getColumnValues(String column, boolean duplicates) {
        Cursor cursor                   = null;
        ArrayList<String> columnList    = new ArrayList<>();

        //Get column values.
        try {
            if (duplicates) {
                cursor = db.rawQuery("SELECT " + column + " FROM " + DB_TABLE, null);
            } else {
                cursor = db.rawQuery("SELECT DISTINCT " + column + " FROM " + DB_TABLE, null);
            }
        } catch (Exception e) {
            Log.e("Database error selecting matches", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(column);

            do {
                columnList.add(cursor.getString(columnIndex));
            } while (cursor.moveToNext());
        }

        return columnList;
    }

    /**
     * Gets the last match_id value in the database.
     *
     * @return last match ID
     */
    public int getLastMatchID() {
        Cursor cursor = null;
        int result = -1;

        //Get last match_id.
        try {
            cursor = db.rawQuery("SELECT " + KEY_MATCHID + " FROM " + DB_TABLE +
                    " ORDER BY " + KEY_MATCHID + " DESC LIMIT 1", null);
        } catch (Exception e) {
            Log.e("Error getting last match id from Database", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int matchidIndex = cursor.getColumnIndex(KEY_MATCHID);
            result = cursor.getInt(matchidIndex);
        }

        return result;
    }

    /**
     * Get the previous matches sorted depending on date and teamname.
     *
     * @param date     Date
     * @param teamName teamname
     * @param sport    Sport
     * @return Previous matches
     */
    public ArrayList<MatchInfo> getPrevMatches(boolean date, boolean teamName, String sport) {
        Cursor cursor = null;
        ArrayList<MatchInfo> result = new ArrayList<>();

        //Get column values.
        try {
            if (!date && !teamName) {
                cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport=?", new String[]{sport});

            } else if (date && !teamName) {
                cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport=?" +
                        " ORDER BY " + KEY_DATE + " DESC", new String[]{sport});

            } else if (!date) {
                cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport=?" +
                        " ORDER BY " + KEY_TEAM1, new String[]{sport});

            } else {
                cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport=?" +
                        " ORDER BY " + KEY_DATE + " DESC" + ", " + KEY_TEAM1, new String[]{sport});
            }

        } catch (Exception e) {
            Log.e("Database error selecting matches", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int matchIDIndex    = cursor.getColumnIndex(MatchDbAdapter.KEY_MATCHID);
            int dateIndex       = cursor.getColumnIndex(MatchDbAdapter.KEY_DATE);
            int sportIndex      = cursor.getColumnIndex(MatchDbAdapter.KEY_SPORT);
            int team1Index      = cursor.getColumnIndex(MatchDbAdapter.KEY_TEAM1);
            int team2Index      = cursor.getColumnIndex(MatchDbAdapter.KEY_TEAM2);
            int venueIndex      = cursor.getColumnIndex(MatchDbAdapter.KEY_VENUE);
            int typeIndex       = cursor.getColumnIndex(MatchDbAdapter.KEY_TYPE);
            int typeNameIndex   = cursor.getColumnIndex(MatchDbAdapter.KEY_TYPENAME);
            int refereeIndex    = cursor.getColumnIndex(MatchDbAdapter.KEY_REFEREE);

            do {
                //Place information from table in arraylist<MatchInfo>.
                MatchInfo matchInfo = new MatchInfo(cursor.getInt(matchIDIndex),
                        cursor.getString(dateIndex),
                        cursor.getString(sportIndex),
                        cursor.getString(team1Index),
                        cursor.getString(team2Index),
                        cursor.getString(venueIndex),
                        cursor.getString(typeIndex),
                        cursor.getString(typeNameIndex),
                        cursor.getString(refereeIndex));
                result.add(matchInfo);
            } while (cursor.moveToNext());
        }

        return result;
    }

}
