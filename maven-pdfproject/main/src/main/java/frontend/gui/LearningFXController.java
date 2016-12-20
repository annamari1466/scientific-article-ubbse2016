package frontend.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import backend.model.PDF;
import backend.repository.DAOFactory;
import backend.weka.LearningDataSet;
import common.PDFContainer;
import common.Scientific;
import common.Settings;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.main.MyCrawler;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import frontend.app.Main;
import frontend.app.TextProcessor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LearningFXController {

	@FXML
	private Button loadFromDbButton;
	
	@FXML
	private CheckBox scientificCheck;
	
	@FXML
	private Button loadFromFileButton;
	
	@FXML
	private Button saveTrainingSetButton;
	
	@FXML
	private Button loadOtherSceneButton;
	
	@FXML 
	private Button loadPdfToDbButton;
	
	@FXML
	private Button loadDirOfPdfButton;

	@FXML 
	private Button runCrawlerButton;
	
	private boolean isDataSetLoaded=false;
	
	@FXML
	public void initialize(){
		// ...
	}
	
	@FXML 
	public void runCrawler(){
		String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setIncludeBinaryContentInCrawling(true);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;;
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
//        controller.addSeed("http://www.ics.uci.edu/~lopes/");
//        controller.addSeed("http://www.ics.uci.edu/~welling/");
//        controller.addSeed("http://www.ics.uci.edu/");
      
        controller.addSeed("http://www.pdf995.com/samples/pdf.pdf");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        String storageFolder = "E:/PDFProject/scientific-article-ubbse2016/maven-pdfproject/crawler/src/main/java/edu/uci/ics/crawler4j/data/crawl/pdfs";
        MyCrawler.configure(storageFolder);
        controller.start(MyCrawler.class, numberOfCrawlers);
    }	
	
	
	@FXML
	public void loadDataFromDB(){
			List<PDF> dbData = new ArrayList<PDF>();
			dbData = DAOFactory.getInstance().getPDFDAO().getAllPDFs();
			
			Settings.weightedAvg = true;
			PDFContainer.lds = new LearningDataSet();
			
			
			PDFContainer.lds.addAllPDF(dbData);
			PDFContainer.lds.write();
			isDataSetLoaded = true;
	}
	
	@FXML
	public void loadDataFromFile(){
		Stage stage = (Stage) loadFromFileButton.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Attribute Relational File Format", "*.arff");
        fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Open Training Set File");
		File selectedFile= fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			
			Settings.weightedAvg = true;
			PDFContainer.lds = new LearningDataSet();
			
			PDFContainer.lds.buildFromFile(selectedFile.getAbsolutePath());
			PDFContainer.lds.write();
			isDataSetLoaded=true;
		}			
	}
	
	public void saveFile(File file){
		FileWriter fileWriter = null;
        try {
        	 fileWriter = new FileWriter(file);
			 fileWriter.write(PDFContainer.lds.toString());
			 fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}     
	}
	
	@FXML
	public void saveTrainingSet(){
		Stage stage = (Stage) saveTrainingSetButton.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Attribute Relational File Format", "arff");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save training set");
		File file = fileChooser.showSaveDialog(stage);
		
		
		if(file!=null){
			saveFile(file);
		}
	}
	
	@FXML
	public void changeScene(){
	  if(isDataSetLoaded==true){
			 Stage stage= (Stage) saveTrainingSetButton.getScene().getWindow();
			 FXMLLoader loader = new FXMLLoader();
			 loader.setLocation(Main.class.getResource("../gui/ScientificArticleApp.fxml"));
			 AnchorPane myApp;
			 
			try {
				 myApp = (AnchorPane) loader.load();
				 Scene scene = new Scene(myApp);
				 stage.setScene(scene);
				 scene.getStylesheets().add(getClass().getResource("../gui/styles.css").toExternalForm());
				 stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error dialog");
			alert.setHeaderText("You have not set the training set!");
			alert.setContentText("You can build up your training set from file or"
					+ " from database.");
			alert.showAndWait();
		}
	}
	
	@FXML
	public void loadPdf() {
		Stage stage = (Stage) loadPdfToDbButton.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open File");
		File selectedFile= fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			Scientific sc;
			if(scientificCheck.isSelected()){
				sc = Scientific.SCIENTIFIC;
			}else{
				sc = Scientific.NONSCIENTIFIC;
			}
			
			TextProcessor tp=new TextProcessor(selectedFile, sc);
		}		
	}
	
	 public File[] pdfFinder( File selectedDirectory){
		 
		   FilenameFilter fileNameFilter = new FilenameFilter() {

				public boolean accept(File dir, String name) {
			        return name.endsWith(".pdf");
				}
		 	
		 	};

	        return selectedDirectory.listFiles(fileNameFilter);
	        
	    }
	
	@FXML
	public void loadDirectoryOfPDFs(){
		Stage stage = (Stage) loadDirOfPdfButton.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Load Directory of PDF's");
		File selectedDirectory = chooser.showDialog(stage);
		if(selectedDirectory!=null){
			Scientific sc;
			if(scientificCheck.isSelected()){
				sc = Scientific.SCIENTIFIC;
			}else{
				sc = Scientific.NONSCIENTIFIC;
			}
			
			File [] pdfs = pdfFinder(selectedDirectory);
			
			for(int i=0;i<pdfs.length;i++){
				System.out.println("Processed PDF:"+pdfs[i]);
				new TextProcessor(pdfs[i],sc);
			}
		
		}
	}
	
}
