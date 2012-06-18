package com.android.database;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.android.main.MatchInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Class that allows interaction with the Match database such as insert, delete, update.
 * 
 * @author Jonathan
 */
public class MatchDbAdapter
{

	// Fields in Database.
	private static final String DB_TABLE = "Match";
	public static final String KEY_MATCHID = "match_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_SPORT = "sport";
	public static final String KEY_TEAM1 = "team1";
	public static final String KEY_TEAM2 = "team2";
	public static final String KEY_VENUE = "venue";
	public static final String KEY_TYPE = "type";
	public static final String KEY_TYPENAME = "type_name";
	public static final String KEY_REFEREE = "referee";
	private SQLiteDatabase db;
	private BaseHelper dbHelper;
	private Context context;
	SimpleDateFormat dateFormat;

	public MatchDbAdapter(Context context)
	{
		this.context = context;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	}

	/**
	 * Open database so we can write to it.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public MatchDbAdapter open() throws SQLException
	{
		dbHelper = new BaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close database.
	 */
	public void close()
	{
		dbHelper.close();
	}

	/**
	 * Add match(row) to database.
	 * 
	 * @param sport
	 * @param team1
	 * @param team2
	 * @param venue
	 * @param type
	 * @param referee
	 */
	public void addMatch(String sport, String team1, String team2, String venue, String type, String typeName,
			String referee)
	{
		// Get date and format it.
		Date date = new Date(System.currentTimeMillis());
		String dateStr = dateFormat.format(date);

		ContentValues values = new ContentValues();
		values.put(KEY_DATE, dateStr);
		values.put(KEY_SPORT, sport);
		values.put(KEY_TEAM1, team1);
		values.put(KEY_TEAM2, team2);
		values.put(KEY_VENUE, venue);
		values.put(KEY_TYPE, type);
		values.put(KEY_TYPENAME, typeName);
		values.put(KEY_REFEREE, referee);

		// Insert into database.
		try
		{
			db.insert(DB_TABLE, null, values);
		}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Delete match(row) from database.
	 * 
	 * @param matchID
	 */
	public void deleteMatch(int matchID)
	{
		try
		{
			db.delete(DB_TABLE, KEY_MATCHID + "=" + matchID, null);
		}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Update a match that already exists in the database.
	 * 
	 * @param matchID
	 * @param sport
	 * @param team1
	 * @param team2
	 * @param venue
	 * @param type
	 * @param typeName
	 * @param referee
	 */
	public void updateMatch(long matchID, String sport, String team1, String team2, String venue, String type,
			String typeName, String referee)
	{
		// Get date and format it.
		Date date = new Date(System.currentTimeMillis());
		String dateStr = dateFormat.format(date);

		ContentValues values = new ContentValues();
		values.put(KEY_DATE, dateStr);
		values.put(KEY_SPORT, sport);
		values.put(KEY_TEAM1, team1);
		values.put(KEY_TEAM2, team2);
		values.put(KEY_VENUE, venue);
		values.put(KEY_TYPE, type);
		values.put(KEY_TYPENAME, typeName);
		values.put(KEY_REFEREE, referee);

		// Insert into database.
		try
		{
			db.update(DB_TABLE, values, KEY_MATCHID + "=" + matchID, null);
		}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Get the column values for a specific column provided.
	 * 
	 * @param column
	 * @param duplicates
	 * @return
	 */
	public ArrayList<String> getColumnValues(String column, boolean duplicates)
	{
		Cursor cursor = null;
		ArrayList<String> columnList = new ArrayList<String>();

		// Get column values.
		try
		{
			if (duplicates)
				cursor = db.rawQuery("SELECT " + column + " FROM " + DB_TABLE, null);
			else
				cursor = db.rawQuery("SELECT DISTINCT " + column + " FROM " + DB_TABLE, null);
		}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		// Query result is not empty.
		if (cursor.moveToFirst())
		{
			int columnIndex = cursor.getColumnIndex(column);

			do
			{
				columnList.add(cursor.getString(columnIndex));
			}
			// move the cursor's pointer up one position.
			while (cursor.moveToNext());
		}

		return columnList;
	}

	/**
	 * Gets the last match_id value in the database.
	 * 
	 * @return
	 */
	public int getLastMatchID()
	{
		Cursor cursor = null;
		int result = -1;

		// Get last match_id.
		try
		{
			cursor = db.rawQuery("SELECT " + KEY_MATCHID + " FROM " + DB_TABLE + " ORDER BY " + KEY_MATCHID
					+ " DESC LIMIT 1", null);
		}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		// Query result is not empty.
		if (cursor.moveToFirst())
		{
			int matchidIndex = cursor.getColumnIndex(KEY_MATCHID);
			result = cursor.getInt(matchidIndex);
		}

		return result;
	}

	/**
	 * Get the previous matches sorted depending on date and teamname.
	 * 
	 * @param date
	 * @param teamName
	 * @param sport
	 * @return
	 */
	public ArrayList<MatchInfo> getPrevMatches(boolean date, boolean teamName, String sport)
	{
		Cursor cursor = null;
		ArrayList<MatchInfo> result = new ArrayList<MatchInfo>();

		// Get column values.
		try
		{
			if (date == false && teamName == false)
			{
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport=?", new String[] { sport });

			}
			else if (date == true && teamName == false)
			{
				cursor = db.rawQuery(
						"SELECT * FROM " + DB_TABLE + " WHERE sport=?" + " ORDER BY " + KEY_DATE + " DESC",
						new String[] { sport });

			}
			else if (date == false && teamName == true)
			{
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport=?" + " ORDER BY " + KEY_TEAM1,
						new String[] { sport });

			}
			else if (date == true && teamName == true)
			{
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE sport=?" + " ORDER BY " + KEY_DATE + " DESC"
						+ ", " + KEY_TEAM1, new String[] { sport });
			}

		}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		// Query result is not empty.
		if (cursor.moveToFirst())
		{
			int matchIDIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_MATCHID);
			int dateIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_DATE);
			int sportIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_SPORT);
			int team1Index = cursor.getColumnIndex(MatchDbAdapter.KEY_TEAM1);
			int team2Index = cursor.getColumnIndex(MatchDbAdapter.KEY_TEAM2);
			int venueIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_VENUE);
			int typeIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_TYPE);
			int typeNameIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_TYPENAME);
			int refereeIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_REFEREE);

			do
			{
				// Place information from table in arraylist<MatchInfo>.
				MatchInfo matchInfo = new MatchInfo(cursor.getInt(matchIDIndex), cursor.getString(dateIndex),
						cursor.getString(sportIndex), cursor.getString(team1Index), cursor.getString(team2Index),
						cursor.getString(venueIndex), cursor.getString(typeIndex), cursor.getString(typeNameIndex),
						cursor.getString(refereeIndex));
				result.add(matchInfo);
			}
			// move the cursor's pointer up one position.
			while (cursor.moveToNext());
		}

		return result;
	}

	public ArrayList<MatchInfo> getPrevMatchesBetween(int start, int end)
	{
		Cursor cursor = null;
		ArrayList<MatchInfo> result = new ArrayList<MatchInfo>();

		// Get column values.
		try
		{
			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE " + KEY_MATCHID + " BETWEEN " + start + " AND "
					+ end, null);
		}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		// Query result is not empty.
		if (cursor.moveToFirst())
		{
			int matchIDIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_MATCHID);
			int dateIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_DATE);
			int sportIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_SPORT);
			int team1Index = cursor.getColumnIndex(MatchDbAdapter.KEY_TEAM1);
			int team2Index = cursor.getColumnIndex(MatchDbAdapter.KEY_TEAM2);
			int venueIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_VENUE);
			int typeIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_TYPE);
			int typeNameIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_TYPENAME);
			int refereeIndex = cursor.getColumnIndex(MatchDbAdapter.KEY_REFEREE);

			do
			{
				// Place information from table in arraylist<MatchInfo>.
				MatchInfo matchInfo = new MatchInfo(cursor.getInt(matchIDIndex), cursor.getString(dateIndex),
						cursor.getString(sportIndex), cursor.getString(team1Index), cursor.getString(team2Index),
						cursor.getString(venueIndex), cursor.getString(typeIndex), cursor.getString(typeNameIndex),
						cursor.getString(refereeIndex));
				result.add(matchInfo);
			}
			// move the cursor's pointer up one position.
			while (cursor.moveToNext());
		}

		return result;
	}

}
