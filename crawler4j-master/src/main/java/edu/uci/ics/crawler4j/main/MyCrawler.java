package edu.uci.ics.crawler4j.main;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	public static File storageFolder;
	
	public static void configure(String folder) {
		storageFolder = new File(folder);
	}
	
    private final static Pattern FILTERS1 = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");
    private final static Pattern FILTERS2 = Pattern.compile(".*\\.pdf$", java.util.regex.Pattern.CASE_INSENSITIVE);

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
//         return !FILTERS.matcher(href).matches()
//                 && href.startsWith("http://www.ics.uci.edu/");
         return !FILTERS1.matcher(href).matches()
        		 && href.startsWith("http://www.pdf995.com/samples/pdf.pdf");
//         return href.startsWith("http://www.cs.ubbcluj.ro/~zbodo/");
     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("URL: " + url);

         if (FILTERS2.matcher(url).matches()) {
        	 System.out.println("PDF:");
        	 String extension = url.substring(url.lastIndexOf('.'));
             String hashedName = UUID.randomUUID() + extension;
        	 String filename = storageFolder.getAbsolutePath() + "/" + hashedName;
             try {
                 Files.write(page.getContentData(), new File(filename));
                 logger.info("Stored: {}", url);
             } catch (IOException iox) {
                 logger.error("Failed to write file: " + filename, iox);
             }        	 
         }
         
         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();

             System.out.println("Text length: " + text.length());
             System.out.println("Html length: " + html.length());
             System.out.println("Number of outgoing links: " + links.size());
         }
    }
}