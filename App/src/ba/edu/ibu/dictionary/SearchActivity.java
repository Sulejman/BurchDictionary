package ba.edu.ibu.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends SherlockActivity {

	Button buttonDetails;
	ListView wordsList;
	ProgressBar mLoadingBar;
	SQLiteAssetHelper myDBHelper;
	int textlength = 0;
	ArrayList<Word> words = new ArrayList<Word>();
	ArrayList<Word> wordsTurkish = new ArrayList<Word>();
	ArrayList<Word> wordsBosnian = new ArrayList<Word>();
	ArrayList<Word> wordsEnglish = new ArrayList<Word>();
	ArrayList<Word> wordsAfterSearch = new ArrayList<Word>();
	WordAdapter wordAdapter;
	boolean isFirstEnglish = true, isFirstBosnian = true;
	long idle_min = 700;
	long last_text_edit = 0;
	Handler h = new Handler();
	boolean already_queried = false;
	String searchQuery = "";
	Menu menu;
	String selectedLanguage = "TR";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		getSupportActionBar().setIcon(R.drawable.ic_action_bar);

		// As we are using a different icon which is wider than normal, the text
		// comes too close to the icon. Thus, we use this little trick.
		getSupportActionBar().setTitle(
				" " + getResources().getString(R.string.app_name));

		new LoadTask().execute();
	}

	private void updateSearch(String query) {
		searchQuery = query;
		last_text_edit = System.currentTimeMillis();
		h.postDelayed(input_finish_checker, idle_min);
	}

	public void query_dictionary_after_text_changed() {

		if (selectedLanguage.equals("TR")) {
			if (searchQuery.length() == 0) {
				final WordAdapter wordAdapter = new WordAdapter(
						SearchActivity.this, words, selectedLanguage);
				wordsList.setAdapter(wordAdapter);
				enableFastScroll();
			} else
				wordsAfterSearch = showListAfterSearchPerformed();
		} else if (selectedLanguage.equals("BS")) {
			if (searchQuery.length() == 0) {
				final WordAdapter wordAdapterBosnian = new WordAdapter(
						SearchActivity.this, wordsBosnian, selectedLanguage);
				wordsList.setAdapter(wordAdapterBosnian);
				enableFastScroll();
			} else {
				wordsAfterSearch = showListAfterSearchPerformed();
				Collections.sort(wordsAfterSearch, new Word.OrderByBosnian());
			}
		} else if (selectedLanguage.equals("EN")) {
			if (searchQuery.length() == 0) {
				final WordAdapter wordAdapterEnglish = new WordAdapter(
						SearchActivity.this, wordsEnglish, selectedLanguage);
				wordsList.setAdapter(wordAdapterEnglish);
				enableFastScroll();
			} else {
				wordsAfterSearch = showListAfterSearchPerformed();
				Collections.sort(wordsAfterSearch, new Word.OrderByEnglish());
			}
		}

		if (searchQuery.length() != 0) {
			disableFastScroll();
			final WordAdapter wordAdapterSearch = new WordAdapter(
					SearchActivity.this, wordsAfterSearch, selectedLanguage);
			wordsList.setAdapter(wordAdapterSearch);
		}

		already_queried = false;
	}

	public ArrayList<Word> showList() {
		ArrayList<Word> words = new ArrayList<Word>();
		words.clear();
		Cursor c = myDBHelper.getAllWords();
		String tr = null, bs = null, en = null, tr_temp = null, bs_temp, en_temp, bs_P = null, tr_P = null;
		for (int ID = 0; ID < c.getCount(); ID = ID + 1) {
			if (ID == 0) {
				if (c.moveToFirst()) {
					tr_temp = tr;
					tr = c.getString(c.getColumnIndex("TURKISH"));
					bs_temp = bs;
					bs = c.getString(c.getColumnIndex("BOSNIAN"));
					if (bs == null)
						bs = bs_temp;
					en_temp = en;
					en = c.getString(c.getColumnIndex("ENGLISH"));
					if (en == null)
						en = en_temp;
					bs_P = c.getString(c.getColumnIndex("BOSNIAN_RONUNCIATION"));
					tr_P = c.getString(c
							.getColumnIndex("TURKISH_PRONUNCIATION"));
				} else {
					tr = null;
					bs = null;
					bs_temp = null;
					en = null;
					en_temp = null;
				}
				try {
					if (!tr.equals(tr_temp)) {
						Word word = new Word(tr, bs, en, " [" + bs_P + "]",
								" [" + tr_P + "]", 0);
						words.add(word);
					}
				} catch (NullPointerException e) {
					Word word = new Word(tr, bs, en, " [" + bs_P + "]", " ["
							+ tr_P + "]", 0);
					words.add(word);
				}

			}

			else {
				if (c.moveToNext()) {
					tr_temp = tr;
					tr = c.getString(c.getColumnIndex("TURKISH"));
					bs_temp = bs;
					bs = c.getString(c.getColumnIndex("BOSNIAN"));
					if (bs == null)
						bs = bs_temp;
					en_temp = en;
					en = c.getString(c.getColumnIndex("ENGLISH"));
					if (en == null)
						en = en_temp;
					bs_P = c.getString(c.getColumnIndex("BOSNIAN_RONUNCIATION"));
					tr_P = c.getString(c
							.getColumnIndex("TURKISH_PRONUNCIATION"));
				} else {
					tr = null;
					bs = null;
					bs_temp = null;
					en = null;
					en_temp = null;
				}

				try {
					if (!tr.equals(tr_temp)) {
						Word word = new Word(tr, bs, en, " [" + bs_P + "]",
								" [" + tr_P + "]", 0);
						words.add(word);
					}
				} catch (NullPointerException e) {
					Word word = new Word(tr, bs, en, " [" + bs_P + "]", " ["
							+ tr_P + "]", 0);
					words.add(word);
				}

			}
		}
		return words;
	}

	public ArrayList<Word> showListAfterSearchPerformed() {
		ArrayList<Word> words = new ArrayList<Word>();
		words.clear();
		Cursor c1;

		String tr = null, bs = null, en = null, tr_temp = null, bs_P = null, tr_P = null;

		if (selectedLanguage.equals("TR")) {
			String searchString = searchQuery;
			String newSearchString = searchString.replace("i", "İ");
			c1 = myDBHelper.getSearchTurkish(newSearchString.toUpperCase()); // This
																				// code
																				// works,
																				// ignore
																				// warning
																				// about
																				// locale
			c1.moveToFirst();

			for (int ID = 0; ID < c1.getCount(); ID = ID + 1) {
				if (ID == 0) {
					if (c1.moveToFirst()) {
						tr_temp = tr;
						tr = c1.getString(c1.getColumnIndex("TURKISH"));
					} else {
						tr = null;
						tr_temp = null;
					}
				} else {
					if (c1.moveToNext()) {
						tr_temp = tr;
						tr = c1.getString(c1.getColumnIndex("TURKISH"));
					} else {
						tr = null;
						tr_temp = null;
					}
				}
				bs = c1.getString(c1.getColumnIndex("BOSNIAN"));
				en = c1.getString(c1.getColumnIndex("ENGLISH"));
				bs_P = c1.getString(c1.getColumnIndex("BOSNIAN_RONUNCIATION"));
				tr_P = c1.getString(c1.getColumnIndex("TURKISH_PRONUNCIATION"));

				try {
					if (!tr.equals(tr_temp)) {
						Word word = new Word(tr, bs, en, " [" + bs_P + "]",
								" [" + tr_P + "]", 0);
						words.add(word);
					}
				} catch (NullPointerException e) {
					Word word = new Word(tr, bs, en, " [" + bs_P + "]", " ["
							+ tr_P + "]", 0);
					words.add(word);
				}
			}
			c1.close();
		}

		else if (selectedLanguage.equals("BS")) {
			c1 = myDBHelper.getSearchBosnian(searchQuery.toUpperCase()); // This
																			// code
																			// works,
																			// ignore
																			// warning
																			// about
																			// locales
			c1.moveToFirst();

			for (int ID = 0; ID < c1.getCount(); ID = ID + 1) {
				if (ID == 0) {
					if (c1.moveToFirst()) {
						tr_temp = tr;
						tr = c1.getString(c1.getColumnIndex("TURKISH"));
					} else {
						tr = null;
						tr_temp = null;
					}
				} else {
					if (c1.moveToNext()) {
						tr_temp = tr;
						tr = c1.getString(c1.getColumnIndex("TURKISH"));
					} else {
						tr = null;
						tr_temp = null;
					}
				}
				bs = c1.getString(c1.getColumnIndex("BOSNIAN"));
				en = c1.getString(c1.getColumnIndex("ENGLISH"));
				bs_P = c1.getString(c1.getColumnIndex("BOSNIAN_RONUNCIATION"));
				tr_P = c1.getString(c1.getColumnIndex("TURKISH_PRONUNCIATION"));
				try {
					if (!tr.equals(tr_temp)) {
						Word word = new Word(tr, bs, en, " [" + bs_P + "]",
								" [" + tr_P + "]", 0);
						words.add(word);
					}
				} catch (NullPointerException e) {
					Word word = new Word(tr, bs, en, " [" + bs_P + "]", " ["
							+ tr_P + "]", 0);
					words.add(word);
				}
			}
			c1.close();
		}

		else if (selectedLanguage.equals("EN")) {
			c1 = myDBHelper.getSearchEnglish(searchQuery.toUpperCase()); // This
																			// code
																			// works,
																			// ignore
																			// warning
																			// about
																			// locales
			c1.moveToFirst();

			for (int ID = 0; ID < c1.getCount(); ID = ID + 1) {
				if (ID == 0) {
					if (c1.moveToFirst()) {
						tr_temp = tr;
						tr = c1.getString(c1.getColumnIndex("TURKISH"));
					} else {
						tr = null;
						tr_temp = null;
					}
				} else {
					if (c1.moveToNext()) {
						tr_temp = tr;
						tr = c1.getString(c1.getColumnIndex("TURKISH"));
					} else {
						tr = null;
						tr_temp = null;
					}
				}
				bs = c1.getString(c1.getColumnIndex("BOSNIAN"));
				en = c1.getString(c1.getColumnIndex("ENGLISH"));
				bs_P = c1.getString(c1.getColumnIndex("BOSNIAN_RONUNCIATION"));
				tr_P = c1.getString(c1.getColumnIndex("TURKISH_PRONUNCIATION"));

				try {
					if (!tr.equals(tr_temp)) {
						Word word = new Word(tr, bs, en, " [" + bs_P + "]",
								" [" + tr_P + "]", 0);
						words.add(word);
					}
				} catch (NullPointerException e) {
					Word word = new Word(tr, bs, en, " [" + bs_P + "]", " ["
							+ tr_P + "]", 0);
					words.add(word);
				}
			}
			c1.close();
		}
		return words;
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.search, menu);
		this.menu = menu;

		MenuItem searchItem = menu.findItem(R.id.action_search);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				updateSearch(query);

				// hide keyboard
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				updateSearch(newText);
				return false;
			}

		});

		searchItem.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				updateSearch("");
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {

		com.actionbarsherlock.view.MenuItem languageItem = menu
				.findItem(R.id.menu_language);

		switch (item.getItemId()) {
		case R.id.action_about:
			showAbout();
			break;
		case R.id.languageTurkish:
			switchToTurkish();
			languageItem.setIcon(R.drawable.flag_tr);
			break;
		case R.id.languageBosnian:
			switchToBosnian();
			languageItem.setIcon(R.drawable.flag_bs);
			break;
		case R.id.languageEnglish:
			switchToEnglish();
			languageItem.setIcon(R.drawable.flag_uk);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void switchToTurkish() {
		selectedLanguage = "TR";
		savePreferences("saved_language", "TR");

		WordAdapter.stringLanguage = "TR";

		if (searchQuery.length() == 0) {
			final WordAdapter wordAdapter = new WordAdapter(
					SearchActivity.this, words, selectedLanguage);
			wordsList.setAdapter(wordAdapter);
			enableFastScroll();
		} else {
			disableFastScroll();
			final WordAdapter wordAdapterSearch = new WordAdapter(
					SearchActivity.this, showListAfterSearchPerformed(),
					selectedLanguage);
			wordsList.setAdapter(wordAdapterSearch);
		}
	}

	public void switchToBosnian() {
		selectedLanguage = "BS";
		savePreferences("saved_language", "BS");

		WordAdapter.stringLanguage = "BS";
		Log.d("Sortion of section letters", "Static variable updated to: "
				+ WordAdapter.stringLanguage);
		if (searchQuery.length() == 0) {
			final WordAdapter wordAdapterBosnian = new WordAdapter(
					SearchActivity.this, wordsBosnian, selectedLanguage);
			wordsList.setAdapter(wordAdapterBosnian);
			enableFastScroll();
		} else {
			disableFastScroll();
			wordsAfterSearch = showListAfterSearchPerformed();
			Collections.sort(wordsAfterSearch, new Word.OrderByBosnian());
			final WordAdapter wordAdapterSearch = new WordAdapter(
					SearchActivity.this, wordsAfterSearch, selectedLanguage);
			wordsList.setAdapter(wordAdapterSearch);
		}
	}

	public void switchToEnglish() {
		selectedLanguage = "EN";
		savePreferences("saved_language", "EN");

		WordAdapter.stringLanguage = "EN";
		Log.d("Sortion of section letters", "Static variable updated to: "
				+ WordAdapter.stringLanguage);
		if (searchQuery.length() == 0) {
			final WordAdapter wordAdapterEnglish = new WordAdapter(
					SearchActivity.this, wordsEnglish, selectedLanguage);
			wordsList.setAdapter(wordAdapterEnglish);
			enableFastScroll();
		} else {
			disableFastScroll();
			wordsAfterSearch = showListAfterSearchPerformed();
			Collections.sort(wordsAfterSearch, new Word.OrderByEnglish());
			final WordAdapter wordAdapterSearch = new WordAdapter(
					SearchActivity.this, wordsAfterSearch, selectedLanguage);
			wordsList.setAdapter(wordAdapterSearch);
		}
	}

	public void showAbout() {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}

	private Runnable input_finish_checker = new Runnable() {

		@Override
		public void run() {
			if (System.currentTimeMillis() > (last_text_edit + idle_min - 500)) {
				// user hasn't changed the EditText for longer than the minimum
				// delay (with half second buffer window)
				if (!already_queried) {
					already_queried = true;
					query_dictionary_after_text_changed();
				}
			}
		}
	};

	public class LoadTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			wordsList = (ListView) findViewById(R.id.listViewMain);
			myDBHelper = new SQLiteAssetHelper(getApplicationContext());

			clearUp();

			try {
				myDBHelper.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}

			try {
				myDBHelper.openDataBase();
			} catch (SQLException sqle) {
				throw sqle;
			}

			WordAdapter.stringLanguage = selectedLanguage;
			words = showList();

			SearchActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					final WordAdapter wordAdapter = new WordAdapter(
							SearchActivity.this, words, selectedLanguage);

					wordsList.setAdapter(wordAdapter);
					enableFastScroll();
					wordsBosnian = showList();
					Collections.sort(wordsBosnian, new Word.OrderByBosnian());

					wordsEnglish = showList();
					Collections.sort(wordsEnglish, new Word.OrderByEnglish());
					loadSavedPreferences();
				}
			});

			return null;
		}

		private void clearUp() {
			words.clear();
			wordsAfterSearch.clear();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mLoadingBar = (ProgressBar) findViewById(R.id.search_view_proggress_bar);

			SearchActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mLoadingBar.setVisibility(View.GONE);
					wordsList.setVisibility(View.VISIBLE);
				}
			});

		}
	}

	private void loadSavedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String savedLanguage = sharedPreferences
				.getString("saved_language", "");
		com.actionbarsherlock.view.MenuItem languageItem = menu
				.findItem(R.id.menu_language);

		if (savedLanguage.equals("BS")) {
			switchToBosnian();
			languageItem.setIcon(R.drawable.flag_bs);
		} else if (savedLanguage.equals("EN")) {
			switchToEnglish();
			languageItem.setIcon(R.drawable.flag_uk);
		} else {
			switchToTurkish();
			languageItem.setIcon(R.drawable.flag_tr);
		}
	}

	private void savePreferences(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	@SuppressLint("NewApi")
	public void enableFastScroll() {
		wordsList.setFastScrollEnabled(true);

		if (Build.VERSION.SDK_INT > 11) {
			wordsList.setFastScrollAlwaysVisible(true);
		}
	}

	@SuppressLint("NewApi")
	public void disableFastScroll() {
		wordsList.setFastScrollEnabled(false);

		if (Build.VERSION.SDK_INT > 11) {
			wordsList.setFastScrollAlwaysVisible(false);
		}
	}
}
