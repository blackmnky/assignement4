package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.Book;
import Database.BookGateway;
import Database.PublisherGateway;
import Model.AppException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public class MenuController implements Initializable, MyController{
	private static Logger logger = LogManager.getLogger();
	private static MenuController singleton = null;
	private BorderPane rootNode;
	private Connection connection;
	public static final int BOOKLIST = 1;
	public static final int BOOKDETAIL = 2;
	public static final int ADDBOOK = 3;
	public static final int HELP = 4;
	public static final int AUDITTRAIL = 5;
	@FXML private MenuItem Exit;

    @FXML private MenuItem BookList;

    @FXML private MenuItem addBook;

    @FXML private MenuItem helpButton;
   
    @FXML private Button doneButton;

	
	public static MenuController getInstance() {
		if (singleton == null) {
			singleton = new MenuController();
		}
		return singleton;
	}
	
	@FXML
    void OnExit(ActionEvent event) {
		logger.info("exitClicked");

		if (event.getSource() == Exit) {
			logger.info("calling exit");
			Platform.exit();
		}
    }

    @FXML
    void addBookClicked(ActionEvent event) {
    	if(event.getSource() == addBook){
			logger.info("Add Book Clicked");
			try {
				Book add = new Book();
				add.setGateway(new BookGateway(connection));
				changeViews(ADDBOOK, add);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
    }

    @FXML
    void bookListClicked(ActionEvent event) throws SQLException {
    	logger.info("bookListClicked");

		if (event.getSource() == BookList) {
			try {
				changeViews(BOOKLIST, "");
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
    }

    @FXML
    void helpScreen(ActionEvent event) {
    	logger.info("about button clicked");
		if (event.getSource() == helpButton) {
			try {
				changeViews(HELP, "");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
    
    @FXML
	void doneButtonClicked(ActionEvent event) throws IOException, SQLException {
		logger.info("Button clicked");
		if (event.getSource() == doneButton) {
			changeViews(BOOKLIST, "");
		}
	}
	
	public void changeViews(int type, Object arg) throws IOException, SQLException {
		logger.info("changeViews");
		try {
			MyController controller = null;
			URL fxmlFile = null;
			switch (type) {
			case BOOKLIST:
				fxmlFile = this.getClass().getResource("BookListView.fxml");
				controller = new BookListController(new BookGateway(connection), (String)arg);
				break;
			case BOOKDETAIL:
				fxmlFile = this.getClass().getResource("BookDetailView.fxml");
				PublisherGateway gateway = new PublisherGateway(connection);
				controller = new BookDetailController((Book)arg, gateway.getPublishers());
				break;
			case ADDBOOK:
				fxmlFile = this.getClass().getResource("BookDetailView.fxml");
				PublisherGateway gateway1 = new PublisherGateway(connection);
				controller = new BookDetailController((Book)arg, gateway1.getPublishers());
				break;
			case HELP:
				fxmlFile = this.getClass().getResource("aboutScreen.fxml");
				controller = MenuController.getInstance();
				break;
			case AUDITTRAIL:
				fxmlFile = this.getClass().getResource("auditTrailView.fxml");
				controller = new AuditTrailController(new BookGateway(connection), (Book)arg);
			}
			FXMLLoader loader = new FXMLLoader(fxmlFile);
			loader.setController(controller);

			Parent viewNode = loader.load();
			rootNode.setCenter(viewNode);
			BorderPane.setAlignment(viewNode, Pos.CENTER);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
	
	public void setRootNode(BorderPane rootNode) {
		this.rootNode = rootNode;
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("calling initialize");
	}

	public void setConnection(Connection conn) {
		this.connection = conn;
	}

	public Connection getConnection() {
		return connection;
	}

	public BorderPane getRootNode() {
		return rootNode;
	}

	public void setRootPane(BorderPane rootNode) {
		this.rootNode = rootNode;
	}
}
