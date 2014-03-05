package ba.edu.ibu.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ShowDetailsActivity extends SherlockActivity {
	TextView bosnian;
	TextView turkish;
	TextView english;
	TextView bosnianPr;
	TextView turkishPr;
	String wordParser;
	String actionbarTitle;
	SQLiteAssetHelper myDBHelper;
	Word word;
	Cursor c;
	ListView wordsList;
	ArrayList<Word> words = new ArrayList<Word>();
	PhraseAdapter wordAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailed_word);

		Intent i = getIntent();
		wordParser = i.getStringExtra("textViewWord");
		actionbarTitle = i.getStringExtra("actionbarTitle");

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_action_bar);

		// As we are using a different icon which is wider than normal, the text
		// comes too close to the icon. Thus, we use this little trick.
		getSupportActionBar().setTitle(" " + actionbarTitle);

		wordsList = (ListView) findViewById(R.id.listView2);
		myDBHelper = new SQLiteAssetHelper(this.getApplicationContext());
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

		final PhraseAdapter wordAdapter = new PhraseAdapter(
				ShowDetailsActivity.this, showList());
		wordsList.setAdapter(wordAdapter);

		bosnian = (TextView) findViewById(R.id.textViewBs);
		turkish = (TextView) findViewById(R.id.textViewTr);
		english = (TextView) findViewById(R.id.textViewEn);
		bosnianPr = (TextView) findViewById(R.id.textViewBsPronunciation);
		turkishPr = (TextView) findViewById(R.id.textViewTrPronunciation);

		turkish.setText(wordParser + "  ");
		c = myDBHelper.getParsingWord(wordParser);
		bosnian.setText(c.getString(c.getColumnIndex("BOSNIAN")) + "  ");
		english.setText(c.getString(c.getColumnIndex("ENGLISH")) + "  ");
		bosnianPr.setText("["
				+ c.getString(c.getColumnIndex("BOSNIAN_RONUNCIATION")) + "]");
		turkishPr.setText("["
				+ c.getString(c.getColumnIndex("TURKISH_PRONUNCIATION")) + "]");
		c.close();
	}

	@Override
	public boolean onNavigateUp() {
		finish();
		return false;
	}

	private List<Word> showList() {
		ArrayList<Word> words = new ArrayList<Word>();
		words.clear();

		String Bs = null, En = null, Tr = null, BsTr = null, TrBs = null;
		Cursor c1 = myDBHelper.getBosnianPhrase(wordParser);
		Cursor c2 = myDBHelper.getTurkishPhrase(wordParser);
		Cursor c3 = myDBHelper.getEnglishPhrase(wordParser);
		Cursor c4 = myDBHelper.getBosnianPhraseTurkishPronunciation(wordParser);
		Cursor c5 = myDBHelper.getTurkishPhraseBosnianPronunciation(wordParser);

		for (int i = 1; i <= c1.getCount(); i++) {

			if (i == 1) {
				if (c2.moveToFirst()) {
					Tr = c2.getString(c2.getColumnIndex("TURKISH_PHRASE"));
				} else
					Tr = null;
			} else {
				if (c2.moveToNext()) {
					Tr = c2.getString(c2.getColumnIndex("TURKISH_PHRASE"));
				} else
					Tr = null;
			}

			if (i == 1) {
				if (c1.moveToFirst()) {
					Bs = c1.getString(c1.getColumnIndex("BOSNIAN_PHRASE"));
				} else
					Tr = null;
			} else {
				if (c1.moveToNext()) {
					Bs = c1.getString(c1.getColumnIndex("BOSNIAN_PHRASE"));
				} else
					Bs = null;
			}

			if (i == 1) {
				if (c3.moveToFirst()) {
					En = c3.getString(c3.getColumnIndex("ENGLISH_PHRASE"));
				} else
					En = null;
			} else {
				if (c3.moveToNext()) {
					En = c3.getString(c3.getColumnIndex("ENGLISH_PHRASE"));
				} else
					En = null;
			}

			if (i == 1) {
				if (c5.moveToFirst()) {
					TrBs = c5
							.getString(c5
									.getColumnIndex("TURKISH_PHRASE_BOSNIAN_PRONUNCIATION"));
				} else
					TrBs = null;
			} else {
				if (c5.moveToNext()) {
					TrBs = c5
							.getString(c5
									.getColumnIndex("TURKISH_PHRASE_BOSNIAN_PRONUNCIATION"));
				} else
					TrBs = null;
			}

			if (i == 1) {
				if (c4.moveToFirst()) {
					BsTr = c4
							.getString(c4
									.getColumnIndex("BOSNIAN_PHRASE_TURKISH_PRONUNCIATION"));
				} else
					BsTr = null;
			} else {
				if (c4.moveToNext()) {
					BsTr = c4
							.getString(c4
									.getColumnIndex("BOSNIAN_PHRASE_TURKISH_PRONUNCIATION"));
				} else
					BsTr = null;
			}

			Word word = new Word(Tr, Bs, En, TrBs, BsTr);
			words.add(word);

		}

		c1.close();
		c2.close();
		c3.close();
		c4.close();
		c5.close();

		return words;
	}

}
