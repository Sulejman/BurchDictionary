package ibu.edu.dictionary;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class SQLiteAssetHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/ibu.edu.dictionary/databases/"; // Ignore
																				// warning
																				// about
																				// hardcoded
																				// path

	private static String DB_NAME = "DatabaseDictionary.sqlite";
	public static final String KEY_ROWID = "_ID";
	public static final String KEY_TURKISH = "TURKISH";
	public static final String KEY_BOSNIAN_PRONUNCIATION = "BOSNIAN_RONUNCIATION";
	public static final String KEY_ENGLISH = "ENGLISH";
	public static final String KEY_BOSNIAN = "BOSNIAN";
	public static final String KEY_TURKISH_PRONUNCIATION = "TURKISH_PRONUNCIATION";
	public static final String KEY_TURKISH_PHRASE = "TURKISH_PHRASE";
	public static final String KEY_TURKISH_PHRASE_BOSNIAN_PRONUNCIATION = "TURKISH_PHRASE_BOSNIAN_PRONUNCIATION";
	public static final String KEY_BOSNIAN_PHRASE = "BOSNIAN_PHRASE";
	public static final String KEY_BOSNIAN_PHRASE_TURKISH_PRONUNCIATION = "BOSNIAN_PHRASE_TURKISH_PRONUNCIATION";
	public static final String KEY_ENGLISH_PHRASE = "ENGLISH_PHRASE";

	private static final String DATABASE_TABLE = "filecsv";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public SQLiteAssetHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
			Log.d("Database", "Writing Datas to Memory!!!!");
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	public Cursor getTurkishWord(int rowId) throws SQLException {
		String sql = "SELECT TURKISH FROM " + DATABASE_TABLE + " WHERE "
				+ KEY_ROWID + " = ? ";
		String[] selectArgs = { Integer.toString(rowId) };
		Cursor c = myDataBase.rawQuery(sql, selectArgs);
		return c;
	}

	public Cursor getBosnianWord(int rowId) throws SQLException {
		String sql = "SELECT BOSNIAN FROM " + DATABASE_TABLE + " WHERE "
				+ KEY_ROWID + " = ? ";
		String[] selectArgs = { Integer.toString(rowId) };
		Cursor c = myDataBase.rawQuery(sql, selectArgs);
		return c;
	}

	public Cursor getEnglishWord(int rowId) throws SQLException {
		String sql = "SELECT ENGLISH FROM " + DATABASE_TABLE + " WHERE "
				+ KEY_ROWID + " = ? ";
		String[] selectArgs = { Integer.toString(rowId) };
		Cursor c = myDataBase.rawQuery(sql, selectArgs);
		return c;
	}

	public Cursor getAllDetails(String turkish) throws SQLException {
		String sql = "SELECT TURKISH_PHRASE,BOSNIAN_PHRASE,ENGLISH_PHRASE,TURKISH_PHRASE_BOSNIAN_PRONUNCIATION,BOSNIAN_PHRASE_TURKISH_PRONUNCIATION FROM "
				+ DATABASE_TABLE + " WHERE " + KEY_TURKISH + " = ? ";
		String[] selectArgs = { turkish };
		Cursor c = myDataBase.rawQuery(sql, selectArgs);
		return c;
	}

	public Cursor getAllWords() throws SQLException {
		String sql = "SELECT TURKISH,ENGLISH,BOSNIAN,BOSNIAN_RONUNCIATION,TURKISH_PRONUNCIATION FROM "
				+ DATABASE_TABLE;
		Cursor c = myDataBase.rawQuery(sql, null);
		return c;
	}

	public Cursor getWord(String turkish) throws SQLException {
		String sql = "SELECT BOSNIAN FROM " + DATABASE_TABLE
				+ " WHERE TURKISH LIKE ? ";
		String[] selectArgs = { turkish };
		Cursor c = myDataBase.rawQuery(sql, selectArgs);
		return c;
	}

	public Cursor getParsingWord(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE, new String[] {
				KEY_BOSNIAN, KEY_ENGLISH, KEY_BOSNIAN_PRONUNCIATION,
				KEY_TURKISH_PRONUNCIATION }, KEY_TURKISH + "=?",
				new String[] { str }, null, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getBosnianPhrase(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE,
				new String[] { KEY_BOSNIAN_PHRASE }, KEY_TURKISH + "=?",
				new String[] { str }, null, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getTurkishPhrase(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE,
				new String[] { KEY_TURKISH_PHRASE }, KEY_TURKISH + "=?",
				new String[] { str }, null, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getEnglishPhrase(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE,
				new String[] { KEY_ENGLISH_PHRASE }, KEY_TURKISH + "=?",
				new String[] { str }, null, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getBosnianPhraseTurkishPronunciation(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE,
				new String[] { KEY_BOSNIAN_PHRASE_TURKISH_PRONUNCIATION },
				KEY_TURKISH + "=?", new String[] { str }, null, null, null,
				null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getTurkishPhraseBosnianPronunciation(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE,
				new String[] { KEY_TURKISH_PHRASE_BOSNIAN_PRONUNCIATION },
				KEY_TURKISH + "=?", new String[] { str }, null, null, null,
				null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getSearchTurkish(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE, new String[] {
				KEY_TURKISH, KEY_BOSNIAN, KEY_ENGLISH,
				KEY_BOSNIAN_PRONUNCIATION, KEY_TURKISH_PRONUNCIATION },
				KEY_TURKISH + " LIKE ? COLLATE NOCASE", new String[] { str
						+ "%" }, null, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getSearchBosnian(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE, new String[] {
				KEY_TURKISH, KEY_BOSNIAN, KEY_ENGLISH,
				KEY_BOSNIAN_PRONUNCIATION, KEY_TURKISH_PRONUNCIATION },
				KEY_BOSNIAN + " LIKE ? COLLATE NOCASE", new String[] { str
						+ "%" }, null, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getSearchEnglish(String str) {
		Cursor cursor = myDataBase.query(DATABASE_TABLE, new String[] {
				KEY_TURKISH, KEY_BOSNIAN, KEY_ENGLISH,
				KEY_BOSNIAN_PRONUNCIATION, KEY_TURKISH_PRONUNCIATION },
				KEY_ENGLISH + " LIKE ? COLLATE NOCASE", new String[] { str
						+ "%" }, null, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Cursor getWordMatches(String query, String[] columns) {

		String selection = KEY_TURKISH + " IS ?";
		String[] selectionArgs = new String[] { query + "*" };

		return query(selection, selectionArgs, columns);
	}

	private Cursor query(String selection, String[] selectionArgs,
			String[] columns) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(DATABASE_TABLE);
		String myPath = DB_PATH + DB_NAME;

		Cursor cursor = builder.query(SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY), columns, selection,
				selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}
}