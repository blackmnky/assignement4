package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.Author;
import Book.AuthorBook;
import Book.Book;
import Book.Publisher;
import Database.AuthorGateway;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AddAuthorController implements MyController {
	private static Logger logger = LogManager.getLogger();
	private Book book;
	public AuthorGateway gateway;
	private ObservableList<Author> authors;
	private Author author;

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
	
	public AddAuthorController(Book bk, AuthorGateway gate) {
		this();
		
		this.gateway = gate;
		this.book = bk;
		try {
			this.authors = gateway.getAuthors();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
    void backButtonClicked(MouseEvent event) {

    }

    @FXML
    void saveButtonClicked(MouseEvent event) {

    }
    
    public void initialize(URL location, ResourceBundle resources) {
    		logger.info("calling addAuthor init");
    		
    		bookField.textProperty().bindBidirectional(book.titleProperty());
    		authorsComboBox.setItems(authors);
    		//authorsComboBox.valueProperty().bindBidirectional(author.firstNameFieldProperty());

    }
}
