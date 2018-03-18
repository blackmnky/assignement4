package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Book.Publisher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PublisherGateway {
	private static Logger logger = LogManager.getLogger();
	private Connection connection;
	
	public PublisherGateway(Connection conn) {
		this.connection = conn;
	}
	
	public ObservableList<Publisher> getPublishers() throws SQLException {
		logger.info("fetching publishers");
		ObservableList<Publisher> publishers = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement("select * from Publisher order by id");
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				String name = rs.getString("publisher_name");
				Publisher pub = new Publisher(name);
				//pub.setGateway(this);
				pub.setId(rs.getInt("id"));
				publishers.add(pub);
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
		return publishers;
	}
	
	public Publisher getPublisherById(int id) {
		logger.info("fetching publisher by id");
		PreparedStatement st = null;
		Publisher pub = null;
		try {
			st = connection.prepareStatement("select * from Publisher where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			String name = rs.getString("publisher_name");
			pub = new Publisher(name);
			pub.setId(rs.getInt("id"));
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return pub;
	}
}
