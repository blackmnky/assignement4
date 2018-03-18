package Book;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuditTrailEntry {
	private static Logger logger = LogManager.getLogger();

	private int id;
	private Timestamp dateAdded;
	private String message;
	
	public AuditTrailEntry(Timestamp date, String msg, int ID) {
		logger.info("auditTrailEntry constructor");
		setDateAdded(date);
		setMessage(msg);
		setId(ID);
	}
	
	public AuditTrailEntry() {

	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Timestamp getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Timestamp date) {
		this.dateAdded = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
