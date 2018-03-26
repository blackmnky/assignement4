package Book;

public class AuthorBook {
	private Author author;
	private Book book;
	private int royalty; //multiple by 100000
	private boolean newRecord = true; //default is true
	
	public AuthorBook(Author auth, Book bk, int r) {
		this.author = auth;
		this.book = bk;
		this.royalty = r;
	}
	
	public AuthorBook() {
		author = new Author();
		book = new Book();
		royalty = -1; //init to -1 
	}
	
	public Author getAuthor() {
		return author;
	}
	public void setAuthor(Author author) {
		this.author = author;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getRoyalty() {
		return royalty;
	}
	public void setRoyalty(int royalty) {
		this.royalty = royalty;
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
}
