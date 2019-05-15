
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Bruno Lowagie (iText Software)
 */
public class AddImageToPDF {
    public static final String SRC = "c:/java/tmp/base.pdf";
    public static final String BACK1 = "c:/java/tmp/ownproductfirst.pdf";
    public static final String BACK2 = "c:/java/tmp/ownproductnext.pdf";
    public static final String DEST = "c:/java/tmp/dest.pdf";
    
    public static void main_(String[] args) throws IOException, DocumentException {
    	String numbers = "0234564, /, */, ,54567890;   000234  -  000245  + ; 000455,,, 44444  555555  666666";
    	// System.out.println((int) (Math.random()*10000000));
    	//AddImageToPDF pdf = new AddImageToPDF();
        //pdf.manipulatePdf(SRC, BACK1, BACK2, DEST, pdf.splitOwnCertNumbers("66666666", numbers));
    	// "030539 1000,030539 9090,030541 0000,030542 0000,030543 0000,030544 9000,030549"
    	String ncode = "ִÿעוכ1234 המכבטכ סמסםף!";
    	
    	System.out.println(ncode);
    	System.out.println(ncode.replaceAll("[\\x20-\\x40]", ""));
    }
	
    public void manipulatePdf(String src, String back1, String back2, String dest, List<String> numbers) throws IOException, DocumentException {
    	PdfReader s_back_1 = new PdfReader(back1);
    	PdfReader s_back_2 = new PdfReader(back2);
    	
    	PdfReader reader = new PdfReader(src);
    	ByteArrayOutputStream output = new ByteArrayOutputStream(); 
    	PdfStamper stamper = new PdfStamper(reader, output);
        invert(stamper);
        
        PdfImportedPage back_1 = stamper.getImportedPage(s_back_1, 1);
        PdfImportedPage back_2 = stamper.getImportedPage(s_back_2, 1);
        
        int n = reader.getNumberOfPages();
        PdfContentByte canvas;
        
        for (int i = 1; i <=n; i++) {
        	canvas = stamper.getOverContent(i);
        	canvas.addTemplate(i == 1 ? back_1 : back_2, 0, 0);
        	
        	if (numbers != null && numbers.size() >= i && ! numbers.get(i-1).isEmpty()) {
        		Font ft = new Font() ;
        		ft.setColor(BaseColor.RED);
        		ft.setSize(14);
        	    ColumnText.showTextAligned(canvas,
        	     Element.ALIGN_LEFT, new Phrase(numbers.get(i-1), ft), 470, 765, 0);
        	}
        }
        stamper.close();
        s_back_1.close();
        s_back_2.close();
        reader.close();
        
        FileOutputStream fos = new FileOutputStream(dest);
        fos.write(output.toByteArray());
        fos.close();
        output.close();
    }
        
    void invert(PdfStamper stamper)   {
        for (int i = stamper.getReader().getNumberOfPages(); i>0; i--)  {
            reinvertPage(stamper, i);
        }
    }
       
    void reinvertPage(PdfStamper stamper, int page)  {
        Rectangle rect = stamper.getReader().getPageSize(page);
        PdfContentByte cb = stamper.getOverContent(page);
        
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(1f);
        gs.setBlendMode(PdfGState.BM_DARKEN);
        cb.setGState(gs);
        cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
    }
    
	/* ----------------------------------------------- 
	 * Convert certificate's
	 * numbers splited by delimeter into List 
	 * to write into certificate blanks
	 * ----------------------------------------------
	 */
	public List<String> splitOwnCertNumbers(String number, String addblanks) {
		System.out.println(addblanks);	
		addblanks = addblanks.trim().replaceAll("\\s*-\\s*", "-");
		addblanks = addblanks.replaceAll(",", ";");
		addblanks = addblanks.replaceAll("\\s+", ";");
		addblanks = addblanks.replaceAll(";+", ";");
		addblanks = addblanks.replaceAll(";\\D+;", ";");
		
		System.out.println(addblanks);
        String[] lst = addblanks.split(";");
        List<String> ret = new ArrayList<String>();
        
        ret.add(number);
        for (String str : lst) {
            ret.addAll(getSequenceNumbers(str));	
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
			int firstnumber = Integer.parseInt((addblanks.substring(0, pos)));
			int lastnumber = Integer.parseInt(addblanks.substring(pos + 1));
			
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