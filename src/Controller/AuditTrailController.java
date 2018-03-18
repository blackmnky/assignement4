package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.AuditTrailEntry;
import Book.Book;
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
	private ObservableList<AuditTrailEntry> auditTrail;

	@FXML private TextField bookTitleField;

    @FXML private TableView<AuditTrailEntry> auditTrailTable;

    @FXML private TableColumn<AuditTrailEntry, Timestamp> timestampColumn;

    @FXML private TableColumn<AuditTrailEntry, String> messageColumn;

    @FXML private Button backButton;

    public AuditTrailController(BookGateway gate, Book book) {
    		auditTrailTable = new TableView<AuditTrailEntry>();
    		setGateway(gate);
    		setAuditTrail(gateway.getAuditTrail(book));
    		setBook(book);
    }
    
    @FXML
    void backButtonClicked(MouseEvent event) {
    		logger.info("back button clicked");
    		if(event.getClickCount() == 1) {
    			try {
					MenuController.getInstance().changeViews(MenuController.BOOKDETAIL, this.getBook());
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookTitleField.setText(getBook().getTitle());
		timestampColumn.setCellValueFactory(new PropertyValueFactory("dateAdded"));
		messageColumn.setCellValueFactory(new PropertyValueFactory("message"));
		auditTrailTable.setItems(auditTrail);
		
	}
}
