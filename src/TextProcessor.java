import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class TextProcessor {

	private static int pageNumber;
	private static float avgWordsInRow;
	private static int line=0;
	private static int wordInLine;
	private static PDDocument pd = null;
	private static BufferedWriter wr;
	private static StringBuilder sb = null;
	private static String text;
	private static boolean bibliography;
	
	public static void printStatistics(){
		System.out.println("Page number:" + pageNumber);
		System.out.println("Average words/row:" + avgWordsInRow);
		System.out.println("Bibliography available:" + bibliography);
	}
	
	public static boolean bibliographyExistence(String [] rows){

		for (int i = 0; i< rows.length; i++) {
			if(rows[i].contains("References")){
				//an extra character is added at the end of the row, we need the text without it
				String row=rows[i].substring(0,rows[i].length()-1); 
				if(row.matches("\\[.*\\]References"))
					return true;
			}
		}
		
		return false;
		
	}
	
	public static void printRows(String[] rows){
		for(int i=0; i<rows.length; i++)
			System.out.println(rows[i]);
	}
	
	public static void processTextByRow(){
		String[] rows = text.split("\n");
		printRows(rows);
		avgWordsInRow=numberOfWords(rows);
		bibliography=bibliographyExistence(rows);
	}
	
	public static float numberOfWords(String[] rows){
		char c;
		int count=1;
		float sum=(float) 0.0;

		for (int i = 0; i< rows.length; i++) {
			//number of words=number of spaces+1
			count=1;
			for(int j=0;j<rows[i].length();j++){
				c = rows[i].charAt(j);
				if(c == ' '){
					count++;
				}
			}
			
			sum+=count;
		}
		//Every page has a number at the end, that don't counts as a row
		sum=sum - pageNumber;
		int rowNumber=rows.length - pageNumber;
		
		return sum / rowNumber;
	}
	
	public static void processText(){
	
		try {
				File inputFile = new File("E:/BBTE/Csoportos Projekt/PDFs/CiteSeerX.pdf");
				pd = PDDocument.load(inputFile);
				pageNumber=pd.getNumberOfPages();
				PDFTextStripper stripper = new PDFTextStripper() {
				    String prevBaseFont;

				    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
				    {
				        StringBuilder builder = new StringBuilder();

				        for (TextPosition position : textPositions)
				        {
				            String baseFont = position.getFont().getName();
				            if (baseFont != null && !baseFont.equals(prevBaseFont))
				            {
				            	float size=position.getFontSizeInPt();
				                builder.append('[').append(baseFont).append(',').append(size+"").append(']');
				                prevBaseFont = baseFont;
				            }
				            builder.append(position);
				        }

				        writeString(builder.toString());
				    }
				};
				
				sb = new StringBuilder();
				// Add text to the StringBuilder from the PDF
				stripper.setStartPage(1); // Start extracting from page 3
				stripper.setEndPage(pageNumber); // Extract till page 4
				sb.append(stripper.getText(pd));
				text=sb.toString();

				if (pd != null) {
					pd.close();
				}
				
				processTextByRow();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}