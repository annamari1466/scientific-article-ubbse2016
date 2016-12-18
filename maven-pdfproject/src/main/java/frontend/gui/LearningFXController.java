package frontend.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import backend.model.PDF;
import backend.repository.DAOFactory;
import backend.weka.LearningDataSet;
import common.PDFContainer;
import common.Scientific;
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
	
	private boolean isDataSetLoaded=false;
	
	@FXML
	public void loadDataFromDB(){
			List<PDF> dbData = new ArrayList<PDF>();
			dbData = DAOFactory.getInstance().getPDFDAO().getAllPDFs();
			PDFContainer.lds = new LearningDataSet();
			PDFContainer.lds.addAllPDF(new PDF[dbData.size()]);
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
			PDFContainer.lds = new LearningDataSet(selectedFile.getAbsolutePath());
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
	
}