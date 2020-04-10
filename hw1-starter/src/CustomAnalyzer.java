import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomAnalyzer extends Analyzer {
	
	public static final int maxTokenLength = 255;
    
    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
      final StandardTokenizer src = new StandardTokenizer();
      src.setMaxTokenLength(maxTokenLength);
      TokenStream tok = new StandardFilter(src);
      tok = new LowerCaseFilter(tok);
      tok = new StopFilter(tok, EnglishAnalyzer.getDefaultStopSet());
      tok = new PorterStemFilter(tok);
      return new TokenStreamComponents(src, tok) {
        @Override
        protected void setReader(final Reader reader) {
          src.setMaxTokenLength(maxTokenLength);
          super.setReader(reader);
        }
      };
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
      TokenStream result = new StandardFilter(in);
      result = new LowerCaseFilter(result);
      return result;
    }
}
