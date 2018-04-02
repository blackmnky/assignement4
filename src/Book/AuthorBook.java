package Book;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AuthorBookGateway;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AuthorBook {
	private static Logger logger = LogManager.getLogger();

	//public Author author;
	public Book book = new Book();
	SimpleDoubleProperty royalty = new SimpleDoubleProperty();
	public SimpleObjectProperty<Author> author = new SimpleObjectProperty<Author>();
	private boolean newRecord = true; //default is true
	private AuthorBookGateway gateway;
	
	public AuthorBook(Author auth, Book bk, double d) {
		//this.author = auth;
		setAuth(auth);
		this.book = bk;
		setRoyalty(d);
	}
	
	public AuthorBook() {
		logger.info("new AuthorBook");
		//author = new Author();
		setRoyalty(0.0);
	}
	
	public void delete() {
		logger.info(gateway);
		gateway.deleteAuthBook(this);
	}
	
	public String toString() {
		return getAuthor().getFirstName() + " " + getAuthor().getLastName() + " " + royalty;
	}
	
	public Author getAuthor() {
		return author.get();
	}
	public void setAuth(Author author) {
		this.author.set(author);
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public double getRoyalty() {
		return royalty.get();
	}
	public void setRoyalty(double royalty) {
		this.royalty.set(royalty);
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public AuthorBookGateway getGateway() {
		return gateway;
	}

	public void setGateway(AuthorBookGateway gateway) {
		this.gateway = gateway;
	}

	public SimpleDoubleProperty getRoyal() {
		return royalty;
	}

	public void setRoyal(SimpleDoubleProperty royal) {
		this.royalty = royal;
	}

	public void setAuthor(SimpleObjectProperty<Author> author) {
		this.author = author;
	}
	
	public SimpleObjectProperty<Author> AuthorProperty(){
		return author;
	}
}
