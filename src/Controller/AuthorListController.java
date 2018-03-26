package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AuthorGateway;
import Book.Author;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class AuthorListController implements Initializable, MyController {
	private static Logger logger = LogManager.getLogger();
	private static MenuController singleton;
	@FXML private ListView<Author> authorList;
	@FXML private Button deleteButton;
	private ObservableList<Author> aList;
	private Author author;
	private AuthorGateway gateway;

	public AuthorListController(AuthorGateway gate) throws SQLException {
		this.gateway = gate;
		aList = this.gateway.getAuthors();
	}

	public AuthorListController(ObservableList<Author> authors) {
		logger.info("calling AuthorListController");
		singleton = MenuController.getInstance();
		authorList = new ListView<Author>();
		aList = authors;
	}

	@FXML
	void MouseClick(MouseEvent event) throws IOException, SQLException {
		logger.info("Mouse Clicked");
		if (event.getClickCount() == 2) {
			author = authorList.getSelectionModel().getSelectedItem();
			logger.info("Author " + author + "clicked");
			MenuController.getInstance().changeViews(MenuController.AUTHORDETAIL, author);
		}
	}
	
	@FXML
    void deleteButtonClicked(MouseEvent event) {
		logger.info("Delete Button Clicked");
		if(event.getSource() == deleteButton) {
			Author author = authorList.getSelectionModel().getSelectedItem();
			if(author != null) {
				author.delete();
				aList.remove(author);
			}
		}
    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("calling initialize");

		authorList.setItems(aList);
	}

}
