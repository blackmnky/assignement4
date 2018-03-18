package Book;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.BookGateway;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Book {
	private static Logger logger = LogManager.getLogger();

	private int id;
	private SimpleStringProperty title;
	private SimpleStringProperty summary;
	private SimpleIntegerProperty yearPublished;
	private SimpleObjectProperty<Publisher> publisher;
	private SimpleStringProperty isbn;
	private SimpleObjectProperty<LocalDate> dateAdded;
	private BookGateway gateway;
	
	public Book() {
		logger.info("calling book");
		title = new SimpleStringProperty();
		summary = new SimpleStringProperty();
		yearPublished = new SimpleIntegerProperty();
		publisher = new SimpleObjectProperty<Publisher>();
		isbn = new SimpleStringProperty();
		dateAdded = new SimpleObjectProperty<LocalDate>();
		this.id = 0;
		
		setTitle("");
		setSummary("");
		setYearPublished(-1);
		setPublisher(null);
		setIsbn("");
		setDateAdded(null);
	}
	
	public Book(String title, String summary, int year, Publisher publisher, String ibn, LocalDate date) {
		this();
		this.title.set(title);
		this.summary.set(summary);
		this.yearPublished.set(year);
		this.publisher.set(publisher);
		this.isbn.set(ibn);
		this.dateAdded.set(date);
	}
	
	public void update() throws Exception {
		logger.info("calling update()");
		if(this.getId() == 0) {
			gateway.insertBook(this);
		}else{
			gateway.updateBook(this);
		}
	}
	
	public void delete() {
		logger.info("calling book.delete");
		gateway.deleteBook(this);
	}

	public void save() {
		logger.info("calling Book.save");
		logger.info(this);
		if(!isValidTitle(getTitle())) {
			logger.error("Invalid Book Title" + getTitle()); 
			Model.AlertHelper.showWarningMessage("Error","Book Title is invalid", "Title must be between 1-255 letters");
			return;
		}

		if(!isVaildSummary(getSummary())) {
			logger.error("Invalid Book Summary" + getSummary()); 
			Model.AlertHelper.showWarningMessage("Error", "Book Summary is invalid", "Summary must be less than 65536 characters");
			return;
		}

		if(!isValidIsbn(getIsbn())) {
			logger.error("Invalid Book ISBN" + getIsbn()); 
			Model.AlertHelper.showWarningMessage("Error","Book ISBN is invalid", "ISBN cannot be larger than 13 numbers");
			return;
		}

		if(!isValidYearPublished(getYearPublished())) {
			logger.error("Invalid Year published of Book" + getYearPublished()); 
			Model.AlertHelper.showWarningMessage("Error","Year Published of Book is invalid", "Year cannot be after current year");
			return; 
		}
		
		if(!isValidPublisher(publisher.getName())) {
			logger.error("Invalid Publisher name" + publisher.getName());
			Model.AlertHelper.showWarningMessage("Error", "Publisher Name is Too Long", "Publisher name wrong");
			return;
		}
		try {
			this.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Business Logic
	 */
	public boolean isValidTitle(String title) {
		if(title.length() < 1 || title.length() > 255)
			return false;
		return true;
	}

	public boolean isVaildSummary(String sum) {
		if(sum.length() > 65536)
			return false;
		return true;
	}
	
	public boolean isValidYearPublished(int year) {
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		if(year > curYear)
			return false;
		return true;
	}
	
	public boolean isValidIsbn(String ibn) {
		if(ibn.length() > 13)
			return false;
		return true;
	}
	
	public boolean isValidPublisher(String name) {
		if(name.length() > 255)
			return false;
		return true;
	}
	
	public String toString() {
		return getTitle() + "    " + getSummary() + "    " + getYearPublished() + "    " + getPublisher() + "    " + getIsbn()
		+ "    " + getDateAdded();
	}
	
	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		Book.logger = logger;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public String getSummary() {
		return summary.get();
	}

	public void setSummary(String summary) {
		this.summary.set(summary);
	}

	public int getYearPublished() {
		return yearPublished.get();
	}

	public void setYearPublished(int year) {
		this.yearPublished.set(year);
	}

	public Publisher getPublisher() {
		return publisher.get();
	}

	public void setPublisher(Publisher publisher) {
		this.publisher.set(publisher);
	}

	public String getIsbn() {
		return isbn.get();
	}

	public void setIsbn(String isbn) {
		this.isbn.set(isbn);
	}

	public LocalDate getDateAdded() {
		return dateAdded.get();
	}

	public void setDateAdded(LocalDate dateAdded) {
		this.dateAdded.set(dateAdded);
	}
	
	public SimpleStringProperty titleProperty() {
		return title;
	}
	
	public SimpleStringProperty summaryProperty() {
		return summary;
	}
	
	public SimpleIntegerProperty yearPublishedProperty() {
		return yearPublished;
	}
	
	public SimpleObjectProperty<Publisher> publisherProperty(){
		return publisher;
	}
	
	public SimpleStringProperty isbnProperty() {
		return isbn;
	}
	
	public SimpleObjectProperty<LocalDate> dateProperty(){
		return dateAdded;
	}

	public void setGateway(BookGateway bookGateway) {
		this.gateway = bookGateway;
	}
	
	public BookGateway getGateway() {
		return this.gateway;
	}
}
