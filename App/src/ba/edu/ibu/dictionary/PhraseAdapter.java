package ba.edu.ibu.dictionary;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhraseAdapter extends BaseAdapter {

	// Declare Variables
	Context mContext;
	LayoutInflater inflater;
	private List<Word> wordList = null;
	private ArrayList<Word> arraylist;

	// Constructor
	public PhraseAdapter(Context context, List<Word> wordList) {
		mContext = context;
		this.wordList = wordList;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<Word>();
		this.arraylist.addAll(wordList);
	}

	public class ViewHolder {
		TextView textViewBsPh;
		TextView textViewTrPh;
		TextView textViewBsPhTrPr;
		TextView textViewTrPhBsPr;
		TextView textViewEnPh;
		LinearLayout contentHolder;
		LinearLayout noPhrasesHolder;
	}

	@Override
	public int getCount() {
		return wordList.size();
	}

	@Override
	public Word getItem(int position) {
		return wordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.details_each, null);

			// Locate the TextViews in each_item.xml
			holder.textViewEnPh = (TextView) view
					.findViewById(R.id.textViewEnglishPhrase);
			holder.textViewTrPh = (TextView) view
					.findViewById(R.id.textViewTurkishPhrase);
			holder.textViewBsPh = (TextView) view
					.findViewById(R.id.textViewBosnianPhrase);
			holder.textViewTrPhBsPr = (TextView) view
					.findViewById(R.id.textViewTurkishPhraseBosnianPronunciation);
			holder.textViewBsPhTrPr = (TextView) view
					.findViewById(R.id.textViewBosnianPhraseTurkishPronunciation);
			holder.contentHolder = (LinearLayout) view
					.findViewById(R.id.details_item_content_holder);
			holder.noPhrasesHolder = (LinearLayout) view
					.findViewById(R.id.details_no_phrases_holder);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.textViewBsPh.setText(wordList.get(position).KEY_BOSNIAN_PHRASE);
		holder.textViewTrPh.setText(wordList.get(position).KEY_TURKISH_PHRASE);
		try {
			if (!wordList.get(position).KEY_TURKISH_PHRASE_BOSNIAN_PRONUNCIATION
					.equals("")) {
				holder.textViewTrPhBsPr
						.setText("[ "
								+ wordList.get(position).KEY_TURKISH_PHRASE_BOSNIAN_PRONUNCIATION
								+ " ]");
				holder.textViewBsPhTrPr
						.setText("[ "
								+ wordList.get(position).KEY_BOSNIAN_PHRASE_TURKISH_PRONUNCIATION
								+ " ]");
			}
		} catch (NullPointerException e) {
			holder.contentHolder.setVisibility(View.GONE);
			holder.noPhrasesHolder.setVisibility(View.VISIBLE);
		}

		holder.textViewEnPh.setText(wordList.get(position).KEY_ENGLISH_PHRASE);

		return view;
	}

	// Filter Class
	public void filter(String charText) {
		wordList.clear();
		if (charText.length() == 0) {
			wordList.addAll(arraylist);
		} else {
			for (Word wp : arraylist) {
				wordList.add(wp);
			}
		}
		notifyDataSetChanged();
	}

}
