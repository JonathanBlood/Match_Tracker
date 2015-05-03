package com.jonathanbloodmatchtracker.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jonathanbloodmatchtracker.main.MatchEvent;

import java.util.ArrayList;

/**
 * Class that allows interaction with the Facts database such as insert, delete, update.
 *
 * @author Jonathan
 */
public class FactsDbAdapter {

    public static final String KEY_MATCHID  = "match_id";
    public static final String KEY_TYPE     = "type";
    public static final String KEY_PLAYER1  = "player1";
    public static final String KEY_PLAYER2  = "player2";
    public static final String KEY_TEAM     = "team";
    public static final String KEY_TIME     = "time";
    private static final String DB_TABLE    = "Facts";
    private SQLiteDatabase db;
    private BaseHelper dbHelper;
    private Context context;

    public FactsDbAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open database so we can write to it.
     *
     * @return Open DB connection
     * @throws SQLException
     */
    public FactsDbAdapter open() throws SQLException {
        dbHelper = new BaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close database.
     */
    public void close() {
        dbHelper.close();
    }


    /**
     * Add a match fact to the database.
     *
     * @param match_id Match ID
     * @param type     Goal, penalty etc.
     * @param player1  First player involved in event
     * @param player2  Second player involved in event
     * @param team     The player(s) team
     * @param time     The time event took place
     */
    public void addMatchFact(int match_id, String type, String player1, String player2, String team, String time) {

        ContentValues values = new ContentValues();
        values.put(KEY_MATCHID, match_id);
        values.put(KEY_TYPE, type);
        values.put(KEY_PLAYER1, player1);
        values.put(KEY_PLAYER2, player2);
        values.put(KEY_TEAM, team);
        values.put(KEY_TIME, time);

        //Insert into database.
        try {
            db.insert(DB_TABLE, null, values);
        } catch (Exception e) {
            Log.e("Database error when inserting match fact", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Get the column values for a specific column provided.
     *
     * @param column     - column you want to query.
     * @param duplicates - if you want duplicate results.
     * @return columnlist
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
            Log.e("Database error selecting facts", e.toString());
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
     * Count number of types with a given match_id.
     *
     * @param match_id Match ID
     * @param type     Event Type
     * @param teamName Team Name
     * @return count of types
     */
    public int countTypesWithMatchID(int match_id, String type, String teamName) {
        Cursor cursor                   = null;
        ArrayList<String> columnList    = new ArrayList<>();

        //Get column values.
        try {
            cursor = db.rawQuery("SELECT " + KEY_TYPE + " FROM " + DB_TABLE +
                    " WHERE " + KEY_TYPE + "=? AND " + KEY_MATCHID + "=" + match_id +
                    " AND " + KEY_TEAM + "=?", new String[]{type, teamName});
        } catch (Exception e) {
            Log.e("Database error counting match events", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int typeIndex = cursor.getColumnIndex(KEY_TYPE);

            do {
                columnList.add(cursor.getString(typeIndex));
            } while (cursor.moveToNext());
        }

        return columnList.size();
    }

    /**
     * Count the number a certain type with a certain player name comes up in db.
     *
     * @param type   Event Type
     * @param player Player involved in event
     * @return count of types involving a specific player
     */
    public int countTypesWithPlayer(String type, String player) {
        Cursor cursor   = null;
        int result      = 0;

        //Get column values.
        try {
            cursor = db.rawQuery("SELECT COUNT(" + KEY_TYPE + ") FROM " + DB_TABLE +
                            " WHERE " + KEY_TYPE + "=? AND " + KEY_PLAYER1 + "=?", new String[]{type, player});
        } catch (Exception e) {
            Log.e("Database error counting type of events involving a player", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        return result;
    }

    /**
     * Get all match facts for a given match id.
     *
     * @param match_id Match ID
     * @return All match facts
     */
    public ArrayList<MatchEvent> getAllFactsForGivenMatchID(int match_id) {
        Cursor cursor                   = null;
        ArrayList<MatchEvent> result    = new ArrayList<>();

        //Get column values.
        try {
            cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE match_id=" + match_id, null);
        } catch (Exception e) {
            Log.e("Database error selecting all match events", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int typeIndex       = cursor.getColumnIndex(FactsDbAdapter.KEY_TYPE);
            int player1Index    = cursor.getColumnIndex(FactsDbAdapter.KEY_PLAYER1);
            int player2Index    = cursor.getColumnIndex(FactsDbAdapter.KEY_PLAYER2);
            int teamIndex       = cursor.getColumnIndex(FactsDbAdapter.KEY_TEAM);
            int timeIndex       = cursor.getColumnIndex(FactsDbAdapter.KEY_TIME);

            do {
                String type         = cursor.getString(typeIndex);
                String player1      = cursor.getString(player1Index);
                String player2      = cursor.getString(player2Index);
                String team         = cursor.getString(teamIndex);
                String time         = cursor.getString(timeIndex);

                if (player2 != null) {
                    MatchEvent matchEvent = new MatchEvent(time, type, player1, player2, team);
                    result.add(matchEvent);
                } else {
                    MatchEvent matchEvent = new MatchEvent(time, type, player1, team);
                    result.add(matchEvent);
                }

            } while (cursor.moveToNext());
        }

        return result;
    }

}

