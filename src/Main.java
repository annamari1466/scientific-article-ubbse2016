import java.io.BufferedWriter;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;

public class Main {

	private static int pageNumber;
	private static float avgWordsInRow;

	private static PDDocument pd = null;
	private static BufferedWriter wr;
	private static StringBuilder sb = null;
	private static String text;
	
	public static void printStatistics(){
		System.out.println("Page number:"+pageNumber);
		System.out.println("Average words/row:"+avgWordsInRow);
	}
	
	public static float numberOfWords(String[] rows){
		
		int wordsInARow[]=new int[rows.length];
		for (int i = 0; i< rows.length; i++) {
			wordsInARow[i]=rows[i].length();
		}
		
		float sum=(float) 0.0;
		for(int i=0;i< wordsInARow.length;i++){
			sum+=wordsInARow[i];
		}
		
		return sum/rows.length;
	}
	
	public static void processText(){
	
		try {
				File inputFile = new File("E:/BBTE/Csoportos Projekt/PDFs/potra-wright.pdf");
				pd = PDDocument.load(inputFile);
				pageNumber=pd.getNumberOfPages();
				org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
				sb = new StringBuilder();
				// Add text to the StringBuilder from the PDF
				stripper.setStartPage(1); // Start extracting from page 3
				stripper.setEndPage(pageNumber); // Extract till page 4
				sb.append(stripper.getText(pd));
				text=sb.toString();
				
				if (pd != null) {
					pd.close();
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		processText();
		int line=0;
		int wordInLine;

		String[] rows = text.split("\n");
		avgWordsInRow=numberOfWords(rows);
		printStatistics();
		
	}

}
