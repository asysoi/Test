import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class IndexFiles {
	
	
	public static void main(String[] args) {
		String indexPath = "c:\\java\\tmp\\index";
		Boolean create = true;
		
		try {
		   String docsPath = "C:\\Asysoi\\Делопроизводство\\2015";
		   IndexFiles.build(indexPath, docsPath, create);
		} catch (Exception e) {
			System.out.println(" Build " + e.getClass() + "\n with message: " + e.getMessage());
		}
		
		try {
			SearchFiles.search(indexPath, "черному");
		} catch (Exception e) {
			System.out.println(" Search " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}
	
	public static void build(String indexPath, String docsPath, Boolean create) throws Exception {

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

			//iwc.setRAMBufferSizeMB(256.0);
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			// writer.forceMerge(1);
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
	static void indexDocs(final IndexWriter writer, Path path)  {
		try {
			if (Files.isDirectory(path)) {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						try {
							indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
						} catch (IOException | SAXException | TikaException ignore) {
							// don't index files that can't be read.
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

	/** Indexes a single document 
	 * @throws TikaException 
	 * @throws SAXException */
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException, SAXException, TikaException {
		try {
			Document doc = new Document();
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);
			doc.add(new LongPoint("modified", lastModified));

			doc.add(new TextField("contents",
					new BufferedReader(new StringReader(parseToPlainText(file)))));
					// new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

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

	public static String parseToPlainText(Path file)  {
	    BodyContentHandler handler = new BodyContentHandler();
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    ParseContext context = new ParseContext();
	    
	    try {
	    	String chs = guessEncoding(file);	    	
	    	InputStream stream = Files.newInputStream(file);
	        parser.parse(stream, handler, metadata, context);
	        
	        /* String[] metadataNames = metadata.names();
			for (String name : metadataNames) {
			   System.out.println(name + ": " + metadata.get(name));
			}*/
	        if  (handler.toString().length() == 0 ) {
		        System.out.println("Content lenght: " + handler.toString().length());
		        System.out.println("Charset: " + chs);
	        }
		    //System.out.println("Content: " + handler.toString().substring(0, 10));
	    } catch (Exception e) {
			System.out.println("Exception: " + e.getLocalizedMessage());
	    }
	    return handler.toString();
	}
	
	public static String guessEncoding(Path file) throws IOException {
		  CharsetDetector charsetDetector=new CharsetDetector();
		  charsetDetector.setText( new BufferedInputStream(Files.newInputStream(file)));
		  charsetDetector.enableInputFilter(true);
		  CharsetMatch cm=charsetDetector.detect();
		  return cm.getName();
	}
	
	
	private static String msoff(Path file) {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		try {
			FileInputStream inputstream = new FileInputStream(new File(file.toString()));
			
			OOXMLParser msofficeparser = new OOXMLParser();
			msofficeparser.parse(inputstream, handler, metadata, pcontext);
			
		} catch (RuntimeException ex) {
			System.out.println("RuntimeException: " + ex.getLocalizedMessage()); 
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getLocalizedMessage());  
		}

		return handler.toString();
	}

	public static String parseFile(Path file)  {
	    AutoDetectParser parser = new AutoDetectParser();
	    BodyContentHandler handler = new BodyContentHandler();
	    Metadata metadata = new Metadata();
	    
	    try (InputStream stream = Files.newInputStream(file.getFileName())) {
	       parser.parse(stream, handler, metadata);
	       System.out.println("Content lenght: " + handler.toString().length());
	    } catch (Exception e) {
			System.out.println("Exception: " + e.getLocalizedMessage());
	    }
	    return handler.toString();
	   
	}
	
	public static String tika(Path file) throws IOException, TikaException {
      Tika tika = new Tika();
      String filecontent = tika.parseToString(new FileInputStream(file.toString()));
      System.out.println("Extracted Content: " + filecontent);
      return filecontent;
	}
	
}
