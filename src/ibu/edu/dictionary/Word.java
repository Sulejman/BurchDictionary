package ibu.edu.dictionary;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class Word implements Comparable<Word>{
	
	public int KEY_ROWID;
    public String KEY_TURKISH;
    public String KEY_BOSNIAN_PRONUNCIATION;
    public String KEY_ENGLISH;
    public String KEY_BOSNIAN;
    public String KEY_TURKISH_PRONUNCIATION;
    public String KEY_TURKISH_PHRASE;
    public String KEY_TURKISH_PHRASE_BOSNIAN_PRONUNCIATION;
    public String KEY_BOSNIAN_PHRASE;
    public String KEY_BOSNIAN_PHRASE_TURKISH_PRONUNCIATION;
    public String KEY_ENGLISH_PHRASE;
    
    public Word(String tr,String bs, String en)
    {
    	KEY_TURKISH = tr;
    	KEY_BOSNIAN = bs;
    	KEY_ENGLISH = en;
    }
    
    public Word(String tr,String bs, String en, String bsP, String trP, int ID)
    {
    	KEY_TURKISH = tr;
    	KEY_BOSNIAN = bs;
    	KEY_ENGLISH = en;
    	KEY_BOSNIAN_PRONUNCIATION = bsP;
    	KEY_TURKISH_PRONUNCIATION = trP;
    }
    
    public Word(String tr)
    {
    	KEY_TURKISH = tr;
    }
    
    public String getTurkishWord(){
    	return KEY_TURKISH;
    }
    
    public String getBosnianPronunciation(){
    	return KEY_BOSNIAN_PRONUNCIATION;
    }
    
    public String getTurkishPronunciation(){
    	return KEY_TURKISH_PRONUNCIATION;
    }
    
    public Word(String tr,String bs, String en, String tr_pr, String bs_pr, String tr_ph, String bs_ph, String en_ph, String tr_ph_bs_pr, String bs_ph_tr_pr)
    {
    	KEY_TURKISH = tr;
    	KEY_BOSNIAN = bs;
    	KEY_ENGLISH = en;
    	KEY_TURKISH_PRONUNCIATION = tr_pr;
    	KEY_BOSNIAN_PRONUNCIATION = bs_pr;
    	KEY_BOSNIAN_PHRASE = bs_ph;
    	KEY_TURKISH_PHRASE = tr_ph;
    	KEY_TURKISH_PHRASE_BOSNIAN_PRONUNCIATION = tr_ph_bs_pr;
    	KEY_BOSNIAN_PHRASE_TURKISH_PRONUNCIATION = bs_ph_tr_pr;
    	KEY_ENGLISH_PHRASE = en_ph;
    }
    
    public Word(String tr_ph, String bs_ph, String en_ph, String tr_ph_bs_pr, String bs_ph_tr_pr)
    {
    	KEY_BOSNIAN_PHRASE = bs_ph;
    	KEY_TURKISH_PHRASE = tr_ph;
    	KEY_TURKISH_PHRASE_BOSNIAN_PRONUNCIATION = tr_ph_bs_pr;
    	KEY_BOSNIAN_PHRASE_TURKISH_PRONUNCIATION = bs_ph_tr_pr;
    	KEY_ENGLISH_PHRASE = en_ph;
    }
    
    
    public String getBosnianWord(){
    	return KEY_BOSNIAN;
    }
    
    public String getEnglishWord(){
    	return KEY_ENGLISH;
    }
    
    public Word(){}

    public static class OrderByEnglish implements Comparator<Word> {

		@Override
		public int compare(Word arg0, Word arg1) {
			try{
			return arg0.KEY_ENGLISH.compareTo(arg1.KEY_ENGLISH);}
			catch(NullPointerException e){
				return 0;
			}
		}
    }
    
    public static class OrderByBosnian implements Comparator<Word> {
    	
    	Collator bs_BSCollator = Collator.getInstance(new Locale("hr","HR"));
    	
		@Override
		public int compare(Word arg0, Word arg1) {
			try{
			return bs_BSCollator.compare(arg0.KEY_BOSNIAN, arg1.KEY_BOSNIAN );}
			catch(NullPointerException e){
				return 0;
			}
		}
    }
    
    public static class OrderByTurkish implements Comparator<Word> {

		@Override
		public int compare(Word arg0, Word arg1) {
			try{
			return arg0.KEY_TURKISH.compareTo(arg1.KEY_TURKISH);}
			catch(NullPointerException e){
				return 0;
			}
		}
    }

	@Override
	public int compareTo(Word another) {
		return this.KEY_ROWID > another.KEY_ROWID ? 1 : (this.KEY_ROWID < another.KEY_ROWID ? -1 : 0);
	}

}
