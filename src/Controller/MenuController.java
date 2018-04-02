package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.Book;
import Book.Author;
import Book.AuthorBook;
import Database.AuthorBookGateway;
import Database.AuthorGateway;
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
	public static final int BOOKAUDITTRAIL = 5;
	public static final int AUTHORLIST = 6;
	public static final int AUTHORDETAIL = 7;
	public static final int AUTHORAUTIDTRAIL = 8;
	public static final int ADDAUTHOR = 9;
	public static final int UPDATEAUTHOR = 10;
	@FXML private MenuItem Exit;

    @FXML private MenuItem BookList;

    @FXML private MenuItem addBook;

    @FXML private MenuItem helpButton;
   
    @FXML private Button doneButton;
    
    @FXML private MenuItem AuthorList;
    
    @FXML private MenuItem addAuthor;

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
    void addAuthorClicked(ActionEvent event) {
    		if(event.getSource() == addAuthor) {
    			logger.info("Add author clicked");
    			try {
    				Author author = new Author();
    				author.setGateway(new AuthorGateway(connection));
    				changeViews(AUTHORDETAIL, author);
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
    void authorListClicked(ActionEvent event) throws SQLException {
    		logger.info("author List clicked");
    		if(event.getSource() == AuthorList) {
    			try {
    				changeViews(AUTHORLIST, "");
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
				controller = new BookDetailController((Book)arg, gateway.getPublishers(), new BookGateway(connection), new AuthorBookGateway(connection));
				break;
			case ADDBOOK:
				fxmlFile = this.getClass().getResource("BookDetailView.fxml");
				PublisherGateway gateway1 = new PublisherGateway(connection);
				controller = new BookDetailController((Book)arg, gateway1.getPublishers(), new BookGateway(connection), new AuthorBookGateway(connection));
				break;
			case HELP:
				fxmlFile = this.getClass().getResource("aboutScreen.fxml");
				controller = MenuController.getInstance();
				break;
			case BOOKAUDITTRAIL:
				fxmlFile = this.getClass().getResource("auditTrailView.fxml");
				controller = new AuditTrailController(AuditTrailController.BOOKTRAIL, new BookGateway(connection), (Book)arg);
				break;
			case AUTHORLIST:
				fxmlFile = this.getClass().getResource("AuthorListView.fxml");
				controller = new AuthorListController(new AuthorGateway(connection));
				break;
			case AUTHORDETAIL:
				fxmlFile = this.getClass().getResource("AuthorDetailView.fxml");
				controller = new AuthorDetailController((Author)arg);
				break;
			case AUTHORAUTIDTRAIL:
				fxmlFile = this.getClass().getResource("auditTrailView.fxml");
				controller = new AuditTrailController(AuditTrailController.AUTHORTRAIL, new AuthorGateway(connection), (Author)arg);
				break;
			case ADDAUTHOR:
				fxmlFile = this.getClass().getResource("addAuthor.fxml");
				AuthorBook aut = new AuthorBook();
				aut.setBook((Book)arg);
				controller = new AddAuthorController(new AuthorGateway(connection), aut, new AuthorBookGateway(connection), new BookGateway(connection));
				break;	
			case UPDATEAUTHOR:
				fxmlFile = this.getClass().getResource("updateAuthorView.fxml");
				controller = new UpdateAuthorController((AuthorBook)arg, new AuthorBookGateway(connection), new BookGateway(connection));
				break;
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
