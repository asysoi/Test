
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;




public class Test {

	public static void __main(String[] ss) {
		/*
		System.out.println(System.getProperty("file.separator"));
		System.out.println(dateStringConvertToMySQLFormat("21.01.2019"));
 	    List<String> numbers = new Test().splitOwnCertNumbers("1234567", "0091118-20,0091529,0091530,0091399");
		for ( String number : numbers) {
		    System.out.println(number);	
		}
		*/
	    
		try { 
		    new Test().luceneTest();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	
	private void luceneTest() throws IOException, ParseException {
		Analyzer analyzer = new StandardAnalyzer();
		
	    Path indexPath = Files.createTempDirectory("tempIndex");
	    System.out.println(indexPath.getFileName());
	    System.out.println(indexPath.getParent());
	    System.out.println(indexPath.getRoot());
	    
	    Directory directory = FSDirectory.open(indexPath);
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    IndexWriter iwriter = new IndexWriter(directory, config);
	    Document doc = new Document();
	    String text = "This is the text to be indexed.";
	    doc.add(new Field("content", text, TextField.TYPE_STORED));
	    iwriter.addDocument(doc);
	    iwriter.close();
	    
	    // Now search the index:
	    DirectoryReader ireader = DirectoryReader.open(directory);
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    
	    // Parse a simple query that searches for "text":
	    QueryParser parser = new QueryParser("content", analyzer);
	    Query query = parser.parse("text");
	    ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
	    // assertEquals(1, hits.length);
	    // Iterate through the results:
	    for (int i = 0; i < hits.length; i++) {
	      Document hitDoc = isearcher.doc(hits[i].doc);
	      System.out.println(hitDoc.get("fieldname"));
	    }
	    ireader.close();
	    directory.close();
	    IOUtils.rm(indexPath);		
		
	}
	
	
	
	
	


	/*private void solrEmbededTest() throws SolrServerException, IOException {
		  String solrHome = "C:\\Java\\solr-8.0.0\\server\\solr";
		  System.setProperty("solr.solr.home", "C:\\Java\\solr-8.0.0\\server\\solr");
		  
		   //CoreContainer coreContainer = new CoreContainer(solrHome);
		   //EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "asysoi");
		   //coreContainer.load();
		  EmbeddedSolrServer server = new EmbeddedSolrServer(Paths.get(solrHome), "asysoi");
          
          final SolrInputDocument doc = new SolrInputDocument();
  		  doc.addField("id", UUID.randomUUID().toString());
  		  doc.addField("name", "Ёто какое-то им€ и более ничего привет");
  		  doc.addField("text", "ј это текст содержащий какую-то информацию");
          
  		  System.out.println(doc.getField("id"));
		  server.add(doc);
    	  server.commit();
    	  server.close();
		
	}*/


	/*public void solrTest () throws SolrServerException, IOException {
		// SolrClient client = getSolrClient();
		String solrHome = "C:\\Java\\solr-8.0.0\\server\\solr";
		System.setProperty("solr.solr.home", "C:\\Java\\solr-8.0.0\\server\\solr");
		EmbeddedSolrServer server = new EmbeddedSolrServer(Paths.get(solrHome), "asysoi");

		SolrQuery query = new SolrQuery();
		query.setQuery("содержащий");
		query.setStart(0);
		query.setRows(10);
		 
		// QueryResponse qresponse = client.query(query);
		
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", "text:текст");
		queryParamMap.put("fl", "id, name, date, text");
		queryParamMap.put("sort", "date asc");
		queryParamMap.put("srtart", "1");
		queryParamMap.put("rows", "4000");
		MapSolrParams queryParams = new MapSolrParams(queryParamMap);

		final QueryResponse response = server.query(queryParams);
		final SolrDocumentList documents = response.getResults();

		System.out.println("Found " + documents.getNumFound() + " documents");
		int i=1;
		
		for(SolrDocument document : documents) {
		  String id = (String) document.getFirstValue("id");
		  java.util.Date date = (java.util.Date) document.getFirstValue("date");
		  String text = (String) document.getFirstValue("text");
		  
		  if (text != null) {
		    System.out.println((i++) + ". " + id + ": " + text);
		  }
		}
		server.close();
	}*/
	
	/*public  HttpSolrClient getSolrClient() {
	    String solrUrl = "http://localhost:8983/solr";
		return new HttpSolrClient.Builder(solrUrl)
		    .withConnectionTimeout(10000)
		    .withSocketTimeout(60000)
		    .build();
	}*/
	
	
	
	

	/* ----------------------------------------------- 
	 * Convert certificate's
	 * numbers splited by delimeter into List 
	 * to write into certificate blanks
	 * ----------------------------------------------
	 */
	public List<String> splitOwnCertNumbers(String number, String addblanks) {
		List<String> ret = new ArrayList<String>();
		ret.add(number);
		if (addblanks != null && !addblanks.trim().isEmpty()) {

			addblanks = addblanks.trim().replaceAll("\\s*-\\s*", "-");
			addblanks = addblanks.replaceAll(",", ";");
			addblanks = addblanks.replaceAll("\\s+", ";");
			addblanks = addblanks.replaceAll(";+", ";");
			addblanks = addblanks.replaceAll(";\\D+;", ";");

			String[] lst = addblanks.split(";");

			for (String str : lst) {
				ret.addAll(getSequenceNumbers(str));
			}
		}
		return ret;
	}

	
	/* ----------------------------------------------- 
	 * Convert certificate's
	 * numbers range into List of separated numbers to write into certificate
	 * blanks ----------------------------------------------
	 */
	private Collection<String> getSequenceNumbers(String addblanks) {
		List<String> numbers = new ArrayList<String>();
		int pos = addblanks.indexOf("-");
		
		if (pos > 0) {
			String strFirstNumber = addblanks.substring(0, pos);
			String strLastNumber = addblanks.substring(pos + 1);
			
			if (strLastNumber.length() < strFirstNumber.length()) {
				strLastNumber = strFirstNumber.substring(0, strFirstNumber.length()-strLastNumber.length()) + strLastNumber; 
			}
			
			int firstnumber = Integer.parseInt(strFirstNumber);
			int lastnumber = Integer.parseInt(strLastNumber);
			
			for (int i = firstnumber; i <= lastnumber; i++) {
				numbers.add(addnull(i + ""));
			}
		} else if (!addblanks.trim().isEmpty())
			numbers.add(addblanks);
		return numbers;
	}
	
	private String addnull(String number) {
		if (number.length() < 7) {
			number = addnull("0"+number);
		}
		return number;
	}
}




