import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class FiltextIndexManager {
	
	private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
	//private static final String DB_CONNECTION = "jdbc:oracle:thin:@192.168.0.179:1521:orclpdb";
	private static final String DB_CONNECTION = "jdbc:oracle:thin:@//192.168.0.179:1521/orclpdb";
	private static final String DB_USER = "beltpp";
	private static final String DB_PASSWORD = "123456";
	
	public static void main(String[] str) throws SQLException {
		String indexPath = "c:\\java\\tmp\\index";
		FiltextIndexManager imng = new FiltextIndexManager();
		Connection dbConnection = null;
		Statement statement = null;

		String selectTableSQL = "SELECT * from C_PRODUCT_DENORM";

		try {
			imng.search(indexPath, "спички");
              
			/*dbConnection = getDBConnection();
			statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery(selectTableSQL);
            int i=1;
            
			while (rs.next()) {
				String id = rs.getString("CERT_ID");
				String content = rs.getString("TOVAR");
				imng.textAddOrUpdateToIndex(indexPath, id, content, false);
				System.out.println((i++) + ". " + id);
			}*/
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return dbConnection;
	}
	
	
	public static void test(String[] args) {
		String indexPath = "c:\\java\\tmp\\index";
		
		FiltextIndexManager imng = new FiltextIndexManager();
		try {
		   String docsPath = "C:\\Asysoi\\";
		   //String id = new UUID(System.currentTimeMillis(), System.currentTimeMillis()).toString();
		   // imng.textAddOrUpdateToIndex(indexPath, id, content, false);
		   // imng.filesAddOrUpdateToIndex(indexPath, docsPath, createOrUpdate);
		} catch (Exception e) {
			System.out.println(" Build " + e.getClass() + "\n with message: " + e.getMessage());
		}
		
		try {
			imng.search(indexPath, "добавленная");
		} catch (Exception e) {
			System.out.println(" Search " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}
	
	private void textAddOrUpdateToIndex(String indexPath, String id, String content, Boolean create) throws Exception {
		try {
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode( create ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
  		    iwc.setRAMBufferSizeMB(256.0);
			
			IndexWriter writer = new IndexWriter(dir, iwc);
			Document doc = new Document();
  		    doc.add(new StringField("id", id, Field.Store.YES));	
			doc.add(new TextField("content", content, Field.Store.NO));
			
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				writer.updateDocument(new Term("id", id), doc);
			}
			writer.forceMerge(1);			
			writer.close();
		} catch (Exception e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}
	
	
	private void textAddOrUpdateToIndex(String indexPath, Map values, String content, Boolean create) throws Exception {
		try {
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode( create ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
			
			// iwc.setRAMBufferSizeMB(256.0);
			// writer.forceMerge(1);
			
			IndexWriter writer = new IndexWriter(dir, iwc);
			
			Document doc = new Document();
			Set<String> fields = values.keySet();
			
			for (String field :  fields) {
				doc.add(new StringField(field, (String) values.get(field), Field.Store.YES));	
				System.out.println(field + ": " + (String) values.get(field));
			}
			
			doc.add(new TextField("content", content, Field.Store.NO));
			System.out.println("content : " + content);

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				writer.updateDocument(new Term("id", (String) values.get("id")), doc);
			}
						
			writer.close();
		} catch (Exception e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}
	
	/**
	 * Create index using Lucene framework 
	 */
	public void filesAddOrUpdateToIndex(String indexPath, String docsPath, Boolean create) throws Exception {
		final Path docDir = Paths.get(docsPath);
		Date start = new Date();

		try {
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			// iwc.setRAMBufferSizeMB(256.0);
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			writer.forceMerge(1);
			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");
               
		} catch (Exception e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 * @throws TikaException 
	 * @throws SAXException 
	 */
	private void indexDocs(final IndexWriter writer, Path path)  {
		try {
			if (Files.isDirectory(path)) {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						try {
							indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
						} catch (IOException | SAXException | TikaException ignore) {
							System.out.println("don't index files that can't be read");
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} else {
				indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
			}
		} catch (RuntimeException ex) {
			System.out.println("RuntimeException: " + ex.getLocalizedMessage());
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getLocalizedMessage());
		}
	}

	/** Indexes a single file-document 
	 * @throws TikaException 
	 * @throws SAXException */
	private void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException, SAXException, TikaException {
		try {
			Document doc = new Document();
			Field ID = new StringField("ID", new UUID(lastModified, lastModified).toString(), Field.Store.YES);
			doc.add(ID);
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);
			doc.add(new LongPoint("modified", lastModified));
			doc.add(new TextField("content", 
					// parseToPlainText(file), Field.Store.NO));
					new BufferedReader(new StringReader(parseToPlainText(file)))));
					// new BufferedReader(new InputStreamReader(stream, StandardCharsets.windows_1251))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String parseToPlainText(Path file) {
		BodyContentHandler handler = new BodyContentHandler();
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();

		try {
			String chs = getCharset(file);

			if ("windows-1251".equals(chs) || chs.indexOf("8859") != -1 ) {
				BufferedReader reader = 
					new BufferedReader(new InputStreamReader(Files.newInputStream(file), chs));
				String str = null;
				StringBuilder sb = new StringBuilder();
				while( (str = reader.readLine()) != null) {
					sb.append(str);
				}
				return sb.toString();
			} else {
				InputStream stream = Files.newInputStream(file);
				parser.parse(stream, handler, metadata, context);

				/*
				 * String[] metadataNames = metadata.names(); for (String name :
				 * metadataNames) { System.out.println(name + ": " +
				 * metadata.get(name)); }
				 */
				if (handler.toString().length() == 0) {
					System.out.println("Content lenght: " + handler.toString().length());
					System.out.println("Charset: " + chs);
				}
				return handler.toString();
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getLocalizedMessage());
		}
        return "";
	}
	
	private String getCharset(Path file) throws IOException {
		  CharsetDetector charsetDetector = new CharsetDetector();
		  charsetDetector.setText( new BufferedInputStream(Files.newInputStream(file)));
		  charsetDetector.enableInputFilter(true);
		  CharsetMatch cm = charsetDetector.detect();
		  return cm.getName();
	}
		
	private String tikaParse(Path file) throws IOException, TikaException {
      Tika tika = new Tika();
      String filecontent = tika.parseToString(new FileInputStream(file.toString()));
      System.out.println("Extracted Content: " + filecontent);
      return filecontent;
	}
		
	public void search(String index, String queryString) throws Exception {
		String field = "content";
		String queries = null;
		boolean raw = false;
		int hitsPerPage = 10;

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser(field, analyzer);
		
		while (true) {
			Query query = parser.parse(queryString);
			System.out.println("Searching for: " + query.toString(field));
			searcher.search(query, 100);
			doPagingSearch(searcher, query, hitsPerPage, raw, queries == null && queryString == null);

			if (queryString != null) {
				break;
			}
		}
		reader.close();
	}

	public static void doPagingSearch(IndexSearcher searcher, Query query, int hitsPerPage,
			boolean raw, boolean interactive) throws IOException {

		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = Math.toIntExact(results.totalHits.value);
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) { // output raw format
					System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String id = doc.get("id");
				
				if (id != null) {
					System.out.println((i + 1) + ". " + id);
					// System.out.println("   Cpontent: " + doc.get("content"));
					// System.out.println("   Text: " + doc.get("contents"));
				} else {
					System.out.println((i + 1) + ". " + "No ID for this document");
				}
			}

			if (!interactive || end == 0) {
				break;
			}
		}
	}
}
