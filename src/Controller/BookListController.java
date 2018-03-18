package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.Book;
import Database.BookGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class BookListController implements MyController, Initializable {
	private static Logger logger = LogManager.getLogger();
	private MenuController singleton;
	private ObservableList<Book> bList;
	private Book book;
	private BookGateway gateway;
	@FXML private TextField searchField;

	@FXML private Button searchButton;

	@FXML private ListView<Book> bookList;
	
	@FXML private Button deleteButton;

	public BookListController(BookGateway gate, String name) throws SQLException {
		this.gateway = gate;
		if(name.isEmpty() || name == null)
			bList = gateway.getBooks();
		else
			bList = gateway.getBooks(name);
	}

	
	public BookListController(ObservableList<Book> books) {
		logger.info("calling BookListController");
		singleton = MenuController.getInstance();
		bookList = new ListView<Book>();
		this.bList = books;
	}
	
    @FXML
    void searchButtonClicked(ActionEvent event) throws IOException, SQLException {
    		logger.info("search button clicked");
    		if(event.getSource() == searchButton) {
    			String name = searchField.getText();
    			logger.info(name);
    			if(name.isEmpty())
    				MenuController.getInstance().changeViews(MenuController.BOOKLIST, null);
    			else 
    				MenuController.getInstance().changeViews(MenuController.BOOKLIST, name);
    		}
    }
    
    @FXML
	void MouseClick(MouseEvent event) throws IOException, SQLException {
		logger.info("Book Clicked");
		if (event.getClickCount() == 2) {
			book = bookList.getSelectionModel().getSelectedItem();
			logger.info("Book " + book + "clicked");
			MenuController.getInstance().changeViews(MenuController.BOOKDETAIL, book);
		}
	}
    
    @FXML
    void deleteButtonClicked(MouseEvent event) {
		logger.info("Delete Button Clicked");
		if(event.getSource() == deleteButton) {
			Book book = bookList.getSelectionModel().getSelectedItem();
			if(book != null) {
				book.delete();
				bList.remove(book);
			}
		}
    }
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("calling initialize");
		bookList.setItems(bList);
	}
}
