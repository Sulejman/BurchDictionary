package ibu.edu.dictionary;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class AboutActivity extends SherlockActivity {

	TextView mDescProject;
	TextView mDescWeb;
	TextView mLinkOpenSource;
	TextView mLinkLibraries;
	TextView mContactMustafa;
	TextView mContactSujeman;
	TextView mContactIhsan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		mDescProject = (TextView) findViewById(R.id.about_desc_project);
		mDescWeb = (TextView) findViewById(R.id.about_desc_web);
		mLinkOpenSource = (TextView) findViewById(R.id.about_link_open_source);
		mLinkLibraries = (TextView) findViewById(R.id.about_link_libraries);
		mContactMustafa = (TextView) findViewById(R.id.about_contact_mustafa);
		mContactSujeman = (TextView) findViewById(R.id.about_contact_sulejman);
		mContactIhsan = (TextView) findViewById(R.id.about_contact_ihsan);

		mDescProject.setMovementMethod(LinkMovementMethod.getInstance());
		mDescWeb.setMovementMethod(LinkMovementMethod.getInstance());
		mLinkOpenSource.setMovementMethod(LinkMovementMethod.getInstance());
		mLinkLibraries.setMovementMethod(LinkMovementMethod.getInstance());
		mContactMustafa.setMovementMethod(LinkMovementMethod.getInstance());
		mContactSujeman.setMovementMethod(LinkMovementMethod.getInstance());
		mContactIhsan.setMovementMethod(LinkMovementMethod.getInstance());

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onNavigateUp() {
		finish();
		return false;
	}
}
