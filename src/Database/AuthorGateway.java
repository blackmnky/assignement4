package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			st = connection.prepareStatement("select * from Author where first_name = ?");
			st.setString(1, author.getFirstName());
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
		Date date = Date.valueOf(author.getDOB());
		LocalDateTime dbTime = getTimeStamp(author);
		if(dbTime.equals(author.getLast_modified())) {
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
		}else {
			Model.AlertHelper.showWarningMessage("Error", "Timestamps not the same", "Please go back to the Author List to fetch a fresh copy of the Author");
		}
	}
	
	public void insertAuthor(Author author) {
		logger.info("calling insertAuthor");
		PreparedStatement st = null;
		Date date = Date.valueOf(author.getDOB());
		try {
			st = connection.prepareStatement("insert into Author set first_name = ?, last_name = ?, dob = ?, gender = ?, website = ?");
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setDate(3, date);
			st.setString(4, author.getGender());
			st.setString(5, author.getWebField());
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
					+ " where b.first_name = ?, b.last_name = ?");
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
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
