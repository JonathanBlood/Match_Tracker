package com.jonathanbloodmatchtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jonathanbloodmatchtracker.main.TeamInfo;

import java.util.ArrayList;

/**
 * Class that allows interaction with the Team database such as insert, delete, update.
 *
 * @author Jonathan
 */
public class TeamDbAdapter {

    public static final String KEY_NAME         = "name";
    public static final String KEY_SPORT        = "sport";
    public static final String KEY_WINS         = "wins";
    public static final String KEY_LOSSES       = "losses";
    public static final String KEY_DRAWS        = "draws";
    public static final String KEY_TOTALPOINTS  = "total_points";
    private static final String DB_TABLE        = "Team";
    private SQLiteDatabase db;
    private BaseHelper dbHelper;
    private Context context;

    public TeamDbAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open database so we can write to it.
     *
     * @return Open Connection
     * @throws SQLException
     */
    public TeamDbAdapter open() throws SQLException {
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
     * Add a team to the Team table.
     *
     * @param name         Team name
     * @param sport        Sport team is playing
     * @param wins         Number of wins
     * @param losses       Number of losses
     * @param draws        Number of draws
     * @param total_points Total points
     */
    public void addTeam(String name, String sport, int wins, int losses, int draws, int total_points) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_SPORT, sport);
        values.put(KEY_WINS, wins);
        values.put(KEY_LOSSES, losses);
        values.put(KEY_DRAWS, draws);
        values.put(KEY_TOTALPOINTS, total_points);

        //Insert into database.
        try {
            db.insert(DB_TABLE, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Update a single column in the Team table with a type Integer.
     *
     * @param teamName     Team name
     * @param column       column name
     * @param updatedValue - Of type int.
     */
    public void updateSingleColumn(String teamName, String column, int updatedValue) {

        ContentValues values = new ContentValues();
        values.put(column, updatedValue);

        //Insert into database.
        try {
            db.update(DB_TABLE, values, KEY_NAME + "=" + "'" + teamName + "'", null);
        } catch (Exception e) {
            Log.e("Database error updating column in Team table", e.toString());
            e.printStackTrace();
        }

    }

    /**
     * Get the column values for a specific column provided of type string.
     *
     * @param column     column name
     * @param duplicates Duplicate values or not
     * @return ArrayList<String> containing results of query.
     */
    public ArrayList<String> getColumnValuesStr(String column, boolean duplicates) {
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
            Log.e("Database error selecting values from Team table", e.toString());
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
     * Get a column value where this value is of type int for a given team name.
     *
     * @param column   column name
     * @param teamName Team name
     * @return result - of type int.
     */
    public int getColumnValueForTeamInt(String column, String teamName) {
        Cursor cursor   = null;
        int result      = 0;

        //Get column values.
        try {
            cursor = db.rawQuery("SELECT " + column + " FROM " + DB_TABLE + " WHERE " + KEY_NAME + "='" + teamName + "'", null);
        } catch (Exception e) {
            Log.e("Database error getting values from team table", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(column);
            result = cursor.getInt(columnIndex);
        }

        return result;
    }

    /**
     * Get a column value where this value is of type String for a given team name.
     *
     * @param column   column name
     * @param teamName Team name
     * @return result - of type String.
     */
    public String getColumnValueForTeamStr(String column, String teamName) {
        Cursor cursor = null;
        String result = "";

        //Get column values.
        try {
            cursor = db.rawQuery("SELECT " + column + " FROM " + DB_TABLE + " WHERE " + KEY_NAME + "='" + teamName + "'", null);
        } catch (Exception e) {
            Log.e("Database error getting values from team table", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(column);
            result = cursor.getString(columnIndex);
        }

        return result;
    }

    /**
     * Get all values for a given sport.
     *
     * @param orderCol - sort by column.
     * @param sport    sport
     * @return team
     */
    public ArrayList<TeamInfo> getAllValuesForGivenSport(String orderCol, String sport) {
        Cursor cursor               = null;
        ArrayList<TeamInfo> result  = new ArrayList<>();

        //Get column values.
        try {
            cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport='" + sport +
                    "' ORDER BY " +
                    orderCol + " DESC", null);
        } catch (Exception e) {
            Log.e("Database error getting values from team table", e.toString());
            e.printStackTrace();
        }

        //Query result is not empty.
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex           = cursor.getColumnIndex(TeamDbAdapter.KEY_NAME);
            int winsIndex           = cursor.getColumnIndex(TeamDbAdapter.KEY_WINS);
            int lossesIndex         = cursor.getColumnIndex(TeamDbAdapter.KEY_LOSSES);
            int drawsIndex          = cursor.getColumnIndex(TeamDbAdapter.KEY_DRAWS);
            int totalPointsIndex    = cursor.getColumnIndex(TeamDbAdapter.KEY_TOTALPOINTS);

            do {
                //Place information from table in arraylist<TeamInfo>.
                TeamInfo teamInfo = new TeamInfo(cursor.getString(nameIndex),
                        cursor.getInt(winsIndex),
                        cursor.getInt(lossesIndex),
                        cursor.getInt(drawsIndex),
                        cursor.getInt(totalPointsIndex));
                result.add(teamInfo);
            } while (cursor.moveToNext());
        }

        return result;
    }
}