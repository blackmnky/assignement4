package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.AuditTrailEntry;
import Book.Author;
import Book.AuthorBook;
import Book.Book;
import Book.Publisher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookGateway {
	private static Logger logger = LogManager.getLogger();
	private Connection connection;
	private PublisherGateway pubGateway;
	
	public BookGateway(Connection conn) {
		this.connection = conn;
	}
	
	public ObservableList<Book> getBooks() throws SQLException {
		logger.info("fetching books");
		ObservableList<Book> books = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select a.id as book_id, a.title, a.summary, a.year_published, a.isbn, a.date_added, a.year_published, b.id as pub_id, b.publisher_name"
					+ " from Book a inner join Publisher b on a.publisher_id = b.id"
					+ " order by a.title");
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				String title = rs.getString("title");
				String summary = rs.getString("summary");
				String isbn = rs.getString("isbn");
				int yearPub = rs.getInt("year_published");
				Date date = rs.getDate("date_added");
				LocalDate dateAdd = date.toLocalDate();
				Publisher publisher = new Publisher(rs.getString("publisher_name"));
				publisher.setId(rs.getInt("pub_id"));
				Book book = new Book(title, summary, yearPub, publisher, isbn, dateAdd);
				book.setGateway(this);
				book.setId(rs.getInt("book_id"));
				books.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return books;
	}

	public void deleteBook(Book book) {
		logger.info("gateway deleteing book");
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("delete from Book where id = ?");
			st.setInt(1, book.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Book getBookById(int id) {
		logger.info("gateway get single book");
		PreparedStatement st = null;
		Book book = null;
		try {
			st = connection.prepareStatement("select * from Book where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				book = new Book();
				book.setDateAdded(rs.getDate("date_added").toLocalDate());
				book.setIsbn(rs.getString("isbn"));
				book.setSummary(rs.getString("summary"));
				book.setTitle(rs.getString("title"));
				book.setYearPublished(rs.getInt("year_published"));
				book.setGateway(this);
				book.setId(id);
			} //close while
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return book;		
	}

	public void updateBook(Book book) {
		logger.info("gateway updateing book");
		insertAuditTrail(book);
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("update Book set title = ?, summary = ?, year_published = ?, isbn = ?, publisher_id = ? where id = ?");
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, (int)book.getYearPublished());
			st.setString(4, book.getIsbn());
			st.setInt(5, book.getPublisher().getId());
			st.setInt(6, book.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertBook(Book book) {
		logger.info("gateway inserting book");
		PreparedStatement st = null;
		Instant instant = Instant.now();
		Timestamp date = Timestamp.from(instant);
		try {
			st = connection.prepareStatement("insert into Book set title = ?, summary = ?, year_published = ?, publisher_id = ?, isbn = ?, date_added = ?", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, (int)book.getYearPublished());
			st.setInt(4, book.getPublisher().getId());
			st.setString(5, book.getIsbn());
			st.setTimestamp(6, date);
			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			rs.next();
			insertAuditEntry(getBookById(rs.getInt(1)), "Book Added");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertAuditEntry(Book book, String msg) {
		logger.info("gateway inserting audit entry");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("insert into book_audit_trail set book_id = ?, date_added = ?, entry_msg = ?");
			st.setInt(1, book.getId());
			st.setTimestamp(2, time);
			st.setString(3, msg);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void insertAuditTrail(Book book) {
		logger.info("gatewar inserting audit trail");
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select * from Book where id = ?");
			st.setInt(1, book.getId());
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				if(!book.getTitle().equals(rs.getString("title")))
					insertAuditEntry(book, "Title changed from " + rs.getString("title") + " to " + book.getTitle());
				if(!book.getSummary().equals(rs.getString("summary")))
					insertAuditEntry(book, "Summary changed from " + rs.getString("summary") + " to " + book.getSummary());
				if(book.getYearPublished() != rs.getInt("year_published"))
					insertAuditEntry(book, "Year published changed from " + rs.getInt("year_published") + " to " + book.getYearPublished());
				if(book.getPublisher().getId() != rs.getInt("publisher_id"))
					insertAuditEntry(book, "Publisher changed from " + pubGateway.getPublisherById(rs.getInt("publisher_id")) 
						+ " to " + book.getPublisher());
				if(!book.getIsbn().equals(rs.getString("isbn")))
					insertAuditEntry(book, "Isbn changed from " + rs.getInt("isbn") + " to " + book.getIsbn());
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public ObservableList<Book> getBooks(String name) {
		logger.info("fetching books with certain title");
		PreparedStatement st = null;
		ObservableList<Book> books = FXCollections.observableArrayList();
		try {
			st = connection.prepareStatement("select a.id as book_id, a.title, a.summary, a.year_published, a.isbn, a.date_added, a.year_published, b.id as pub_id, b.publisher_name"
					+ " from Book a inner join Publisher b on a.publisher_id = b.id"
					+ " where a.title like ?");
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				String title = rs.getString("title");
				String summary = rs.getString("summary");
				String isbn = rs.getString("isbn");
				int yearPub = rs.getInt("year_published");
				Date date = rs.getDate("date_added");
				LocalDate dateAdd = date.toLocalDate();
				Publisher publisher = new Publisher(rs.getString("publisher_name"));
				publisher.setId(rs.getInt("pub_id"));
				Book book = new Book(title, summary, yearPub, publisher, isbn, dateAdd);
				book.setGateway(this);
				book.setId(rs.getInt("book_id"));
				books.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return books;		
	}

	public ObservableList<AuditTrailEntry> getAuditTrail(Book book) {
		logger.info("fetching audit trail of" +  book);
		ObservableList<AuditTrailEntry> trail = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select a.id as audit_id, a.date_added, a.entry_msg, b.id as bookid, b.title"
					+ " from book_audit_trail a inner join Book b on a.book_id = b.id"
					+ " where b.title = ?");
			st.setString(1, book.getTitle());
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				Timestamp date = rs.getTimestamp("date_added");
				String msg = rs.getString("entry_msg");
				int id = rs.getInt("audit_id");
				AuditTrailEntry entry = new AuditTrailEntry(date, msg, id);
				trail.add(entry);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return trail;
	}
	
	public ObservableList<AuthorBook> getAuthorsForBook(Book book){
		logger.info("getAuthorsForBook");
		ObservableList<AuthorBook> authors = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select a.author_id as a_id, a.book_id, a.royalty, b.id as b_id, b.first_name, b.last_name, b.dob, b.gender, b.website, b.last_modified"
					+ " from author_book a inner join Author b on a.author_id = b.id "
					+ "where book_id = ?");
			st.setInt(1, book.getId());
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				int royal = rs.getInt("royalty");
				String first = rs.getString("first_name");
				String last = rs.getString("last_name");
				String gender = rs.getString("gender");
				String web = rs.getString("website");
				Date dob = rs.getDate("dob");
				LocalDate dOB = dob.toLocalDate();
				LocalDateTime time = rs.getTimestamp("last_modified").toLocalDateTime();
				Author auth = new Author(first, last, web, gender, dOB);
				auth.setLast_modified(time);
				auth.setId(auth.getId());
				AuthorBook tmp = new AuthorBook(auth, book, royal);
				tmp.setNewRecord(false);
				authors.add(tmp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return authors;
	}
}
