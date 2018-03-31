package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.AuthorBook;

public class AuthorBookGateway {
	private static Logger logger = LogManager.getLogger();
	private Connection connection;

	public AuthorBookGateway(Connection conn) {
		this.connection = conn;
	}
	
	public void insertAuthBook(AuthorBook authBook) {
		logger.info("calling insertAuthor");
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("insert into author_book set author_id = ?, book_id = ?, royalty = ?");
			st.setInt(1,	 authBook.getAuthor().getId());
			st.setInt(2, authBook.getBook().getId());
			st.setInt(3, authBook.getRoyalty());
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteAuthBook(AuthorBook authBook) {
		logger.info("deleteing authorBook ");
		PreparedStatement st  = null;
		try {
			st = connection.prepareStatement("delete from author_book where author_id = ?");
			st.setInt(1, authBook.getAuthor().getId());
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

}
