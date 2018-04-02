package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.AuthorBook;
import Database.AuthorBookGateway;
import Database.BookGateway;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.NumberStringConverter;

public class UpdateAuthorController implements Initializable, MyController{
	private static Logger logger = LogManager.getLogger();
	private AuthorBook authBook;
	private AuthorBookGateway abGateway;
	private BookGateway bGateway;;
	
	@FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private Label bookLabel;

    @FXML
    private Label authorLabel;

    @FXML
    private Label royaltyLabel;

    @FXML
    private TextField bookField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField royaltyField;
    
    public UpdateAuthorController() {
    	
    }

    public UpdateAuthorController(AuthorBook ab, AuthorBookGateway abgate, BookGateway bgate) {
    		this.authBook = ab;
    		this.abGateway = abgate;
    		this.bGateway = bgate;
    }
    
    @FXML
    void backClicked(MouseEvent event) {
    	if(event.getClickCount() == 1) {
			try {
				MenuController.getInstance().changeViews(MenuController.BOOKDETAIL, authBook.book);
			} catch (IOException | SQLException e) {
				e.printStackTrace();
			}
		}
    }

    @FXML
    void saveClicked(MouseEvent event) {
	    	if(event.getClickCount() == 1) {
	    		abGateway.updateAuthBook(this.authBook);
	    		bGateway.insertAuditEntry(authBook.book, authBook.getAuthor()  + "Royalty was changed");
	    		try {
					MenuController.getInstance().changeViews(MenuController.BOOKDETAIL, authBook.book);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
	    	}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookField.textProperty().bindBidirectional(authBook.book.titleProperty());
		authorField.textProperty().bindBidirectional(authBook.getAuthor().firstNameFieldProperty());
		royaltyField.textProperty().bindBidirectional(authBook.getRoyal(), new NumberStringConverter());
		
	}
}
