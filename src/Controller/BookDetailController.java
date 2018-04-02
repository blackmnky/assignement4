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
import Database.BookGateway;
import Model.AlertHelper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.NumberStringConverter;


public class BookDetailController implements MyController, Initializable {
	private static Logger logger = LogManager.getLogger();
	private Book book;
	private ObservableList<Publisher> publishers;
	private ObservableList<AuthorBook> abList;
	private BookGateway gateway;
	private AuthorBookGateway abGate;

	
	@FXML private ComboBox<Publisher> publisherBox;

    @FXML private Label publisherLabel;

    @FXML private Label titleLabel;

    @FXML private TextField titleField;

    @FXML private Label summaryLabel;

    @FXML private TextArea summaryField;

    @FXML private Label yearPublishedLabel;

    @FXML private TextField yearPublishedField;

    @FXML private Label isbnLabel;

    @FXML private TextField isbnField;

    @FXML private Button saveButton;
    
    @FXML private Button auditTrailButton;
   
    @FXML private Button backButton;
    
    @FXML private TableView<AuthorBook> authorTable;
    
    @FXML private TableColumn<AuthorBook, String> authorField;

    @FXML private TableColumn<AuthorBook, Double> royaltyField;

    @FXML private Button addAuthorButton;

    @FXML private Button deleteAuthorButton;

    public BookDetailController() {
    	
    }
    
    public BookDetailController(Book book, ObservableList<Publisher> publishers, BookGateway gate, AuthorBookGateway abGate) {
    		this();
    		logger.info(book);
    		
    		authorTable = new TableView<AuthorBook>();
    		this.gateway = gate;
    		this.abGate = abGate;
		this.book = book;
		this.publishers = publishers;
		abList = gateway.getAuthorsForBook(this.book);
	}
    
    @FXML
    void saveButtonClicked(MouseEvent event) {
    		logger.info("calling saveButtonClicked");
    		if(event.getClickCount() == 1) {
    			if(!book.isValidTitle(book.getTitle())) {
    				logger.info("Title is invalid");
    				AlertHelper.showWarningMessage("Oops!", "Title is invalid", "Title must be between 1-255 characters");
    				titleField.clear();
    				return;
    			}	
    			if(!book.isVaildSummary(book.getSummary())) {
    				logger.info("summary is too long");
    				AlertHelper.showWarningMessage("Oops!", "Summary is invalid", "Summary must be less than 65536 characters");
    				summaryField.clear();
    				return;
    			}
    			if(!book.isValidIsbn(book.getIsbn())) {
    				logger.info("ISBN is invalid");
    				AlertHelper.showWarningMessage("Oops!", "ISBN is invalid", "ISBN must be less than 13 numbers");
    				isbnField.clear();
    				return;
    			}
    			if(!book.isValidYearPublished(book.getYearPublished())) {
    				logger.info("Year Published is invalid");
    				AlertHelper.showWarningMessage("Oops!", "Year Published is invalid", "Year cannot be after current year");
    				yearPublishedField.clear();
    				return;
    			}
    			book.save();
    			try {    					
					MenuController.getInstance().changeViews(MenuController.BOOKLIST, "");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}
    }
    
    @FXML
    void detailBackClicked(MouseEvent event) {
    		logger.info("Back button clicked");
    		
    		if(event.getClickCount() == 1) {
    			try {
					MenuController.getInstance().changeViews(MenuController.BOOKLIST, "");
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
    		}
    }
    
    @FXML
    void addAuthorClicked(MouseEvent event) {
    		logger.info("Add Author Button Clicked");
    		if(event.getClickCount() == 1) {
    			try {
					MenuController.getInstance().changeViews(MenuController.ADDAUTHOR, book);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
    		}
    }
    
    @FXML
    void deleteAuthorClicked(MouseEvent event) {
    		logger.info("Delete Author Button Clicked");
    		if(event.getClickCount() == 1) {
    			AuthorBook tmp = authorTable.getSelectionModel().getSelectedItem();
    			tmp.setGateway(abGate);
    			if(abList.size() == 1) {
    				AlertHelper.showWarningMessage("Oops!", "You must have at least one Author for a book", "Please Delete Book before Deleteing Author");
    			}else {
    				logger.info(tmp);
    				tmp.delete();
    				abList.remove(tmp);
    			}
    			
    		}
    }
    
    @FXML
    void authorClicked(MouseEvent event) {
    		logger.info("Author Clicked");
    		if(event.getClickCount() == 2) {
    			AuthorBook auth = authorTable.getSelectionModel().getSelectedItem();
    			try {
					MenuController.getInstance().changeViews(MenuController.UPDATEAUTHOR, auth);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
    		}
    }
    
    @FXML
    void auditTrailClicked(MouseEvent event) {
    		logger.info("Audit Trail Button Clicked");
    		
    		if(event.getClickCount() == 1 && book.getId() != 0) {
    			try {
					MenuController.getInstance().changeViews(MenuController.BOOKAUDITTRAIL, this.book);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}else if(book.getId() == 0) {
    			logger.info("Tried to access audit trail of new book before saving");
    			Model.AlertHelper.showWarningMessage("Error","Please Save Book Before Viewing Audit Trail", "683-25-9-601 new book");
    			return;
    		}
    }
    
    
    
    public BookGateway getGateway() {
		return gateway;
	}

	public void setGateway(BookGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("bookdetailcontroller init");
		titleField.textProperty().bindBidirectional(book.titleProperty());
		publisherBox.setItems(publishers);
		publisherBox.getSelectionModel().selectFirst();
		publisherBox.valueProperty().bindBidirectional(book.publisherProperty());
		summaryField.textProperty().bindBidirectional(book.summaryProperty());
		isbnField.textProperty().bindBidirectional(book.isbnProperty());
		yearPublishedField.textProperty().bindBidirectional(book.yearPublishedProperty(), new NumberStringConverter());
		authorField.setCellValueFactory(new PropertyValueFactory("author"));
		royaltyField.setCellValueFactory(new PropertyValueFactory("royalty"));
		authorTable.setItems(abList);


    }
}
