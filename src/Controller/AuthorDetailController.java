package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.Author;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AuthorDetailController implements Initializable, MyController {
	private static Logger logger = LogManager.getLogger();
	private static MenuController singleton;
	private Author author;

	@FXML
	private Label lastNameLabel;

	@FXML
	private TextField webField;

	@FXML
	private DatePicker DOBField;

	@FXML
	private Label DOBLabel;

	@FXML
	private Label genderLabel;

	@FXML
	private Label webLabel;

	@FXML
	private TextField lastNameField;

	@FXML
	private Button saveButton;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField genderField;

	@FXML
	private Label firstNameLabel;
	
	@FXML
    private Button backButton;

    @FXML
    private Button auditTrailButton;


	public AuthorDetailController(Author author) {
		singleton = MenuController.getInstance();
		if(author == null) {
			author = new Author("", "", "" ,"" ,null);
		}
		this.author = author;
	}

	@FXML
	void OnSave(MouseEvent event) throws Exception {
		logger.info("calling onSave");
		logger.info(author.toString());
		// check the validity of variables
		LocalDateTime originaltime = author.getTimeStamp();
		if(originaltime.equals(author.getLast_modified())) {
			if(event.getClickCount() == 1) {
				if(!author.isValidFirstName(author.getFirstName())) 
					logger.error("Invalid first name"); 
		
				if(!author.isValidLastName(author.getLastName())) 
					logger.error("Invalid last name"); 
		
				if(!author.isValidWebsite(author.getWebField())) 
					logger.error("Invalid webiste"); 
					
				if(!author.isValidGender(author.getGender())) 
					logger.error("Invalid gender"); 
		
				if(!author.isValidDate(author.getDOB()))
					logger.error("Invalid Date Of Birth"); 
		
				if(!author.isValidId(author.getId())) 
					logger.error("Invalid Id");
		
				author.save();
				MenuController.getInstance().changeViews(MenuController.AUTHORLIST, null);
			}
		}else {
			Model.AlertHelper.showWarningMessage("Error", "Timestamps not the same", "Please go back to the Author List to fetch a fresh copy of the Author");
			author.update();
			originaltime = author.getLast_modified();		
		}
	}
	
	@FXML
    void auditTrailClicked(MouseEvent event) {
		logger.info("audit trail for author clicked");
		if(event.getClickCount() == 1 && author.getId() != 0) {
			try {
				MenuController.getInstance().changeViews(MenuController.AUTHORAUTIDTRAIL, this.author);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if(author.getId() == 0) {
			logger.info("Tried to access audit trail of new author before saving");
			Model.AlertHelper.showWarningMessage("Error","Please Save Author Before Viewing Audit Trail", "683-25-9-601 new author");
			return;
		}
    }

    @FXML
    void detailBackClicked(MouseEvent event) {
    		logger.info("back button clicked");
    		if(event.getClickCount() == 1) {
    			try {
					MenuController.getInstance().changeViews(MenuController.AUTHORLIST, "");
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
    		}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("calling initialize()");
		logger.info(author);
		firstNameField.textProperty().bindBidirectional(author.firstNameFieldProperty());
		genderField.textProperty().bindBidirectional(author.genderProperty());;
		webField.textProperty().bindBidirectional(author.webFieldProperty());
		lastNameField.textProperty().bindBidirectional(author.lastNameFieldProperty());
		DOBField.valueProperty().bindBidirectional(author.DOBFieldProperty());
	}
}