package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.AuditTrailEntry;
import Book.Author;
import Book.Book;
import Database.AuthorGateway;
import Database.BookGateway;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class AuditTrailController implements MyController, Initializable {
	private static Logger logger = LogManager.getLogger();
	
	private BookGateway gateway;
	private Book book;
	private AuthorGateway Agateway;
	private Author author;
	private ObservableList<AuditTrailEntry> auditTrail;
	public static final int BOOKTRAIL = 1;
	public static final int AUTHORTRAIL = 2;
	private String title;

	@FXML private TextField bookTitleField;

    @FXML private TableView<AuditTrailEntry> auditTrailTable;

    @FXML private TableColumn<AuditTrailEntry, Timestamp> timestampColumn;

    @FXML private TableColumn<AuditTrailEntry, String> messageColumn;

    @FXML private Button backButton;
    
    public AuditTrailController(int type, Object arg1, Object arg2) {
    		auditTrailTable = new TableView<AuditTrailEntry>();
    		switch(type) {
    		case BOOKTRAIL:
    			setGateway((BookGateway)arg1);
    			setBook((Book)arg2);
    			BookGateway gate = getGateway();
    			Book bk = getBook();
    			setAuditTrail(gate.getAuditTrail(bk));
    			title = bk.getTitle();
    			break;
    		case AUTHORTRAIL:
    			setAgateway((AuthorGateway)arg1);
    			setAuthor((Author)arg2);
    			AuthorGateway aGate = getAgateway();
    			Author author = getAuthor();
    			setAuditTrail(aGate.getAuditTrail(author));
    			title = author.getFirstName() + " " + author.getLastName();
    		}
    }
    
    @FXML
    void backButtonClicked(MouseEvent event) {
    		logger.info("back button clicked");
    		if(event.getClickCount() == 1 && getBook() != null) {
    			try {
					MenuController.getInstance().changeViews(MenuController.BOOKDETAIL, this.getBook());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}else if(event.getClickCount() == 1 && getAuthor() != null) {
    			try {
					MenuController.getInstance().changeViews(MenuController.AUTHORDETAIL, this.getAuthor());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}
    }

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public BookGateway getGateway() {
		return gateway;
	}

	public void setGateway(BookGateway gateway) {
		this.gateway = gateway;
	}

	public ObservableList<AuditTrailEntry> getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(ObservableList<AuditTrailEntry> auditTrail) {
		this.auditTrail = auditTrail;
	}

	public AuthorGateway getAgateway() {
		return Agateway;
	}

	public void setAgateway(AuthorGateway agateway) {
		Agateway = agateway;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookTitleField.setText(title);
		timestampColumn.setCellValueFactory(new PropertyValueFactory("dateAdded"));
		messageColumn.setCellValueFactory(new PropertyValueFactory("message"));
		auditTrailTable.setItems(auditTrail);
		
	}
}
