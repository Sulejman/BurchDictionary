package ibu.edu.dictionary;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class WordAdapter extends BaseAdapter implements SectionIndexer{

	// Declare Variables
	Context mContext;
	LayoutInflater inflater;
	private List<Word> wordList = null;
	private ArrayList<Word> arraylist;
	String languageBar;
	HashMap<String, Integer> alphaIndexer;
	String[] sections;
	Collator bs_BSCollator = Collator.getInstance(new Locale("hr","HR"));
	Collator tr_TRCollator = Collator.getInstance(new Locale("tr","TR"));
	public static String stringLanguage = null;

	//Constructor
	public WordAdapter(Context context,List<Word> wordList,String language) 
	{
		mContext = context;
		this.wordList = wordList;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<Word>();
		this.arraylist.addAll(wordList);
		this.languageBar = language;
		
		
		alphaIndexer = new HashMap<String, Integer>();
		int size = wordList.size();
		
		if(language.equals("TR")){
			for (int x = 0; x < size; x++) {
				String s = wordList.get(x).getTurkishWord();
				// get the first letter of the store
				String ch;
				try{
				ch = s.substring(0, 1);
				}
				catch(NullPointerException e){ch = "a";}
				// convert to uppercase otherwise lowercase a -z will be sorted
				// after upper A-Z
				ch = ch.toUpperCase();
				// put only if the key does not exist
				if (!alphaIndexer.containsKey(ch))
					alphaIndexer.put(ch, x);
			}

			Set<String> sectionLetters = alphaIndexer.keySet();
			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
			WordAdapter.sortStrings(tr_TRCollator, sectionList);
			sections = new String[sectionList.size()];
			sections = sectionList.toArray(sections);
			Log.d("Sortion of section letters","TURKISH sorted!!!");
		}
		
		else if(language.equals("BS")){
			for (int x = 0; x < size; x++) {
				String s = wordList.get(x).getBosnianWord();
				// get the first letter of the store
				String ch;
				try{
				ch = s.substring(0, 1);}
				catch(NullPointerException e){ch = "a";}
				// convert to uppercase otherwise lowercase a -z will be sorted
				// after upper A-Z
				ch = ch.toUpperCase();
				// put only if the key does not exist
				if (!alphaIndexer.containsKey(ch))
					alphaIndexer.put(ch, x);
			}

			Set<String> sectionLetters = alphaIndexer.keySet();
			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(
					sectionLetters);
			WordAdapter.sortStrings(bs_BSCollator, sectionList);
			sections = new String[sectionList.size()];
			sections = sectionList.toArray(sections);
			Log.d("Sortion of section letters","BOSNIAN sorted!!!");

		}
		
		else if(language.equals("EN")){
			for (int x = 0; x < size; x++) {
				String s = wordList.get(x).getEnglishWord();
				// get the first letter of the store
				String ch;
				try{
				ch = s.substring(0, 1);}
				catch(NullPointerException e){ch = "a";}
				// convert to uppercase otherwise lowercase a -z will be sorted
				// after upper A-Z
				ch = ch.toUpperCase();
				// put only if the key does not exist
				if (!alphaIndexer.containsKey(ch))
					alphaIndexer.put(ch, x);
			}

			Set<String> sectionLetters = alphaIndexer.keySet();
			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(
					sectionLetters);
			Collections.sort(sectionList);
			sections = new String[sectionList.size()];
			sections = sectionList.toArray(sections);
			Log.d("Sortion of section letters","ENGLISH sorted!!!");

		}
	}

	public class ViewHolder {
		TextView textViewWord;
		TextView textViewBs;
		TextView textViewEn;
		TextView textViewBsP;
		TextView textViewTrP;
		
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
			if(languageBar.equals("TR"))
				view = inflater.inflate(R.layout.each_item, null);
			else if(languageBar.equals("BS"))
				view = inflater.inflate(R.layout.each_item_bs, null);
			else if(languageBar.equals("EN"))
				view = inflater.inflate(R.layout.each_item_en, null);
			// Locate the TextViews in each_item.xml
			holder.textViewWord = (TextView) view.findViewById(R.id.textViewWord);
			holder.textViewBs = (TextView) view.findViewById(R.id.textViewBs);
			holder.textViewEn = (TextView) view.findViewById(R.id.textViewEn);
			holder.textViewBsP = (TextView) view.findViewById(R.id.textViewBsP);
			holder.textViewTrP = (TextView) view.findViewById(R.id.textViewTrP);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// Set the results into TextViews
		holder.textViewWord.setText(wordList.get(position).getTurkishWord());
		holder.textViewBs.setText(wordList.get(position).getBosnianWord());
		holder.textViewEn.setText(wordList.get(position).getEnglishWord());
		holder.textViewTrP.setText(wordList.get(position).getBosnianPronunciation());
		holder.textViewBsP.setText(wordList.get(position).getTurkishPronunciation());
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Send single item click data to SingleItemView Class
				Intent intent = new Intent(mContext, ShowDetailsActivity.class);
				// Pass all data textViewWord
				intent.putExtra("textViewWord",
						(wordList.get(position).getTurkishWord()));
				// Start SingleItemView Class
				mContext.startActivity(intent);
			}
		});

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

	@Override
	public int getPositionForSection(int section) {
		return alphaIndexer.get(sections[section]);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return sections;
	}
	
	public static void sortStrings(Collator collator, ArrayList<String> words) {
	    String tmp;
	    try{
	    for (int i = 0; i < words.size(); i++) {
	        for (int j = i + 1; j < words.size(); j++) { 
	            if (collator.compare(words.get(i), words.get(j)) > 0) {
	                tmp = words.get(i);
	                words.set(i, words.get(j));
	                words.set(j,tmp);
	            }
	        }
	    }
	    }
	    catch(NullPointerException e){}
	}
	
	

}
