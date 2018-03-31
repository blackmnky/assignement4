package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.AuditTrailEntry;
import Book.Author;
import Book.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AuthorGateway {
	private static Logger logger = LogManager.getLogger();

	private Connection connection;

	public AuthorGateway(Connection conn) {
		this.connection = conn;
	}

	public ObservableList<Author> getAuthors() throws SQLException {
		ObservableList<Author> authors = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select * from Author order by first_name");
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				String first_name = rs.getString("first_name");
				String last_name = rs.getString("last_name");
				String website = rs.getString("website");
				String gender = rs.getString("gender");
				Date dDob = rs.getDate("dob");
				LocalDate dob = dDob.toLocalDate();
				Author author = new Author(first_name, last_name, website, gender, dob);
				author.setLast_modified(rs.getTimestamp("last_modified").toLocalDateTime());
				author.setGateway(this);
				author.setId(rs.getInt("id"));
				authors.add(author);
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
		return authors;
	}

	public LocalDateTime getTimeStamp(Author author) {
		logger.info("obtaining the timestamp for the author");
		LocalDateTime time = null;
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select * from Author where id = ?");
			st.setInt(1, author.getId());
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				time = rs.getTimestamp("last_modified").toLocalDateTime();
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
		return time;
	}
	
	public void updateAuthor(Author author) {
		logger.info("calling updateAuthor");
		PreparedStatement st = null;
		LocalDate bday = author.getDOB();
		Date date = Date.valueOf(bday);
		insertAuditTrail(author);
		try {
			st = connection.prepareStatement("update Author set first_name = ?, last_name = ?, dob = ?, gender = ?, website = ? where id = ?");
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setDate(3, date);
			st.setString(4, author.getGender());
			st.setString(5, author.getWebField());
			st.setInt(6, author.getId());
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
		author.setLast_modified(getTimeStamp(author));
	}
	
	public void insertAuthor(Author author) {
		logger.info("calling insertAuthor");
		PreparedStatement st = null;
		Date date = Date.valueOf(author.getDOB());
		try {
			st = connection.prepareStatement("insert into Author set first_name = ?, last_name = ?, dob = ?, gender = ?, website = ?", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setDate(3, date);
			st.setString(4, author.getGender());
			st.setString(5, author.getWebField());
			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			rs.next();
			insertAuditEntry(getAuthorById(rs.getInt(1)), "Author Added");
			author.setId(rs.getInt(1));
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
		author.setLast_modified(getTimeStamp(author));
	}
	
	public void insertAuditEntry(Author author, String msg) {
		logger.info("gateway inserting audit entry");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("insert into author_audit_trail set author_id = ?, date_added = ?, entry_msg = ?");
			st.setInt(1, author.getId());
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
	
	public void insertAuditTrail(Author author) {
		logger.info("gatewar inserting audit trail");
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select * from Author where id = ?");
			st.setInt(1, author.getId());
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				if(!author.getFirstName().equals(rs.getString("first_name")))
					insertAuditEntry(author, "First Name changed from " + rs.getString("first_name") + " to " + author.getFirstName());
				if(!author.getLastName().equals(rs.getString("last_name")))
					insertAuditEntry(author, "Last Name changed from " + rs.getString("last_name") + " to " + author.getLastName());
				if(!author.getDOB().equals(rs.getDate("dob").toLocalDate()))
					insertAuditEntry(author, "Date of Birth changed from " + rs.getInt("dob") + " to " + author.getDOB());
				if(!author.getGender().equals(rs.getString("gender")))
					insertAuditEntry(author, "Gender changed from " + rs.getString("gender") + "to" + author.getGender()); 
				if(!author.getWebField().equals(rs.getString("website")))
					insertAuditEntry(author, "Website changed from " + rs.getString("website") + " to " + author.getWebField());
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
	
	private Author getAuthorById(int id) {
		Author author = null;
		logger.info("gateway get single book");
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select * from Author where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				author = new Author();
				author.setDOB(rs.getDate("dob").toLocalDate());
				author.setFirstName(rs.getString("first_name"));
				author.setLastName(rs.getString("last_name"));
				author.setGender(rs.getString("gender"));
				author.setWebField(rs.getString("website"));
				author.setGateway(this);
				author.setId(id);
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
		return author;
	}

	public void deleteAuthor(Author author) {
		logger.info("calling deleteAuthor");
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("delete from Author where id = ?");
			st.setInt(1, author.getId());
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
	
	public ObservableList<AuditTrailEntry> getAuditTrail(Author author) {
		logger.info("fetching audit trail of" +  author);
		ObservableList<AuditTrailEntry> trail = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st =  connection.prepareStatement("select a.id as audit_id, a.date_added, a.entry_msg, b.id as authid, b.first_name, b.last_name"
					+ " from author_audit_trail a inner join Author b on a.author_id = b.id"
					+ " where b.last_name like ?");
			st.setString(1, author.getLastName());
			//st.setString(2, author.getLastName());
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

}
