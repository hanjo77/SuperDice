package oldschool.superdice;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UsersDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_WON_GAMES };

	public UsersDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public User createUser(User user) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_NAME, user.getName());
		values.put(SQLiteHelper.COLUMN_WON_GAMES, user.getGamesWon());
		long insertId = database.insert(SQLiteHelper.TABLE_USERS, null,
				values);
		Cursor cursor = database.query(SQLiteHelper.TABLE_USERS,
				allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		User newUser = cursorToUser(cursor);
		user.setId(insertId);
		System.out.println("User created with id: " + insertId);
		cursor.close();
		return newUser;
	}

	public void deleteUser(User user) {
		long id = user.getId();
		System.out.println("User deleted with id: " + id);
		database.delete(SQLiteHelper.TABLE_USERS, SQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public ArrayList<User> getAllUsers() {
		ArrayList<User> users = new ArrayList<User>();

		Cursor cursor = database.query(SQLiteHelper.TABLE_USERS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User user = cursorToUser(cursor);
			users.add(user);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return users;
	}

	public User getUserByName(String name)
	{
		String whereClause = SQLiteHelper.COLUMN_NAME + " = ?";
		String[] whereArgs = new String[] { name };
		Cursor cursor = database.query(SQLiteHelper.TABLE_USERS, allColumns, whereClause, whereArgs, null, null, null);
		cursor.moveToFirst();
		User user = cursorToUser(cursor);
		cursor.close();
		return user;
	}

	public void update(User user)
	{
		String strFilter = "name='" + user.getName() + "'";
		ContentValues args = new ContentValues();
		args.put(SQLiteHelper.COLUMN_WON_GAMES, user.getGamesWon());
		database.update(SQLiteHelper.TABLE_USERS, args, strFilter, null);
	}

	private User cursorToUser(Cursor cursor)
	{
		User user = null;
		try
		{
			user = new User(cursor.getString(1));
			user.setId(cursor.getLong(0));
			user.setGamesWon(cursor.getInt(2));
		}
		catch (Exception e)
		{
			return null;
		}
		return user;
	}
}
