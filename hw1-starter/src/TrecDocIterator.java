import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class TrecDocIterator implements Iterator<Document> {

	protected BufferedReader rdr;
	protected boolean at_eof = false;
	
	public TrecDocIterator(File file) throws FileNotFoundException {
		rdr = new BufferedReader(new FileReader(file));
		System.out.println("Reading " + file.toString());
	}
	
	@Override
	public boolean hasNext() {
		return !at_eof;
	}

	@Override
	public Document next() {
		Document doc = new Document();
		StringBuffer sb = new StringBuffer();
		try {
			String line;
			Pattern docno_tag = Pattern.compile("<DOCNO>\\s*(\\S+)\\s*<");
			Pattern headline_start_tag = Pattern.compile("<HEADLINE>\\s*");
			Pattern headline_end_tag = Pattern.compile("</HEADLINE>\\s*");
			String docno = "";
			boolean in_doc = false;
			while (true) {
				line = rdr.readLine();
				if (line == null) {
					at_eof = true;
					break;
				}
				if (!in_doc) {
					if (line.startsWith("<DOC>"))
						in_doc = true;
					else
						continue;
				}
				if (line.startsWith("</DOC>")) {
					in_doc = false;
					sb.append(line);
					break;
				}

				Matcher m = docno_tag.matcher(line);
				if (m.find()) {
					docno = m.group(1);
					doc.add(new StringField("docno", docno, Field.Store.YES));
				}
				
				m = headline_start_tag.matcher(line);
				if (m.find()) {
					StringBuffer headline = new StringBuffer();
					line = rdr.readLine();
					while(true) {
						m = headline_end_tag.matcher(line);
						if(!m.find()) {
							headline.append(line);
							line = rdr.readLine();
						} else {
							break;
						}
					}
					
					if(headline.length() > 20) {
						Field headlineField = new TextField("headline", headline.substring(16), Field.Store.NO);
						headlineField.setBoost(2.0f);
						doc.add(headlineField);
						sb.append("<HEADLINE>" + headline.toString() + "</HEADLINE>");
					}
				}

				sb.append(line);
			}
			if (sb.length() > 0)
				doc.add(new TextField("contents", sb.toString(), Field.Store.NO));
			
		} catch (IOException e) {
			doc = null;
		}
		return doc;
	}

	@Override
	public void remove() {
		// Do nothing, but don't complain
	}

}