package ibu.edu.dictionary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class OptionsMenuActivity extends Activity {

	TextView search;
	TextView appereance;
	TextView about;
	CheckBox isFullScreen;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);

		search = (TextView) findViewById(R.id.textViewSearchOptions);
		appereance = (TextView) findViewById(R.id.textViewAppereanceOptions);
		about = (TextView) findViewById(R.id.textViewAboutDictionary);

		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showAbout();
			}

		});

		appereance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showAppereance();
			}

		});

	}

	public void showAbout() {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}

	public void showAppereance() {
		// Create custom dialog object

		final Dialog dialog = new Dialog(OptionsMenuActivity.this);
		// Include dialog.xml fill

		dialog.setContentView(R.layout.appereance);
		isFullScreen = (CheckBox) dialog.findViewById(R.id.checkBoxFullscreen);

		if (isFullScreen.isChecked()) {

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_search);
			setContentView(R.layout.settings);
		} else {
			// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			// WindowManager.LayoutParams.FLAG_FULLSCREEN);
			// setContentView(R.layout.settings);
		}
		// Set dialog title

		// set values for custom dialog components - text, image and button

		dialog.show();
	}
}
