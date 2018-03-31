package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.Author;
import Book.AuthorBook;
import Book.Book;
import Book.Publisher;
import Database.AuthorBookGateway;
import Database.AuthorGateway;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.NumberStringConverter;

public class AddAuthorController implements MyController, Initializable {
	private static Logger logger = LogManager.getLogger();
	private Book book;
	public AuthorGateway agateway;
	private AuthorBookGateway bgateway;
	private ObservableList<Author> authors;
	private Author author;
	private AuthorBook authBook;

	@FXML
    private Label authorsLabel;

    @FXML
    private Label royaltyLabel;

    @FXML
    private Label bookLabel;

    @FXML
    private TextField bookField;

    @FXML
    private ComboBox<Author> authorsComboBox;

    @FXML
    private TextField royaltyField;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;
	
	public AddAuthorController() {
		
	}
	
	public AddAuthorController(AuthorGateway agate, AuthorBook ab, AuthorBookGateway bgateway) {
		this();
		
		this.authBook = ab;
		this.agateway = agate;
		this.bgateway = bgateway;
		//this.book = bk;
		try {
			this.authors = agateway.getAuthors();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
    void backButtonClicked(MouseEvent event) {
		if(event.getClickCount() == 1) {
			try {
				MenuController.getInstance().changeViews(MenuController.BOOKDETAIL, authBook.book);
			} catch (IOException | SQLException e) {
				e.printStackTrace();
			}
		}
    }

    @FXML
    void saveButtonClicked(MouseEvent event) {
    		if(event.getClickCount() == 1) {
    			logger.info("saving author");
    			AuthorBook tmp =  new AuthorBook(authBook.getAuthor(), authBook.book, authBook.getRoyalty());
    			tmp.setNewRecord(true);
    			bgateway.insertAuthBook(tmp);
    			
    			try {
					MenuController.getInstance().changeViews(MenuController.BOOKDETAIL, tmp.book);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
    		}
    }

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    		logger.info("calling addAuthor init");
    		logger.info(authBook.book);
    		bookField.textProperty().bindBidirectional(authBook.book.titleProperty());
    		authorsComboBox.setItems(authors);
    		authorsComboBox.valueProperty().bindBidirectional(authBook.AuthorProperty());
    		royaltyField.textProperty().bindBidirectional(authBook.getRoyal(), new NumberStringConverter());

    }
}
