package Book;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.AuthorGateway;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Author {

	private static Logger logger = LogManager.getLogger();
	private SimpleStringProperty firstName;
	private SimpleStringProperty webField;
	private SimpleStringProperty lastName;
	private SimpleStringProperty gender;
	private SimpleObjectProperty<LocalDate> DOB;
	private LocalDateTime last_modified;
	private AuthorGateway gateway;
	private int id;

	public Author() {
		firstName = new SimpleStringProperty();
		webField = new SimpleStringProperty();
		lastName = new SimpleStringProperty();
		gender = new SimpleStringProperty();
		DOB = new SimpleObjectProperty<LocalDate>();
		this.id = 0;
		last_modified = null;
		
		setFirstName("");
		setLastName("");
		setWebField("");
		setGender("");
		setDOB(null);
	}

	public Author(String first, String last, String web, String gender, LocalDate DOB) {
		this();
		
		this.firstName.set(first);
		this.lastName.set(last);
		this.webField.set(web);
		this.gender.set(gender);
		this.DOB.set(DOB);
	}

	public void update() throws Exception {
		logger.info("calling update()");
		logger.info(this);
		if(this.getId() == 0) {
			gateway.insertAuthor(this);
		}else{
			gateway.updateAuthor(this);
		}
	}
	
	public void delete() {
		gateway.deleteAuthor(this);
	}

	public void save() throws Exception {
		logger.info("calling Author.save()");
		if(!isValidFirstName(getFirstName())) {
			logger.error("Invalid first name" + getFirstName()); 
			Model.AlertHelper.showWarningMessage("Error","Author First Name is invalid", "Arcane error number plus description");
			return; 
		}

		if(!isValidLastName(getLastName())) {
			logger.error("Invalid last name" + getLastName()); 
			Model.AlertHelper.showWarningMessage("Error", "Author Last Name is invalid", "Arcane error number plus description");
			return; 
		}

		if(!isValidWebsite(getWebField())) {
			logger.error("Invalid webiste" + getWebField()); 
			Model.AlertHelper.showWarningMessage("Error","Author Website is invalid", "Arcane error number plus description");
			return; 
		}

		if(!isValidGender(getGender())) {
			logger.error("Invalid gender" + getGender()); 
			Model.AlertHelper.showWarningMessage("Error","Author Gender is invalid", "Arcane error number plus description");
			return; 
		}

		if(!isValidDate(getDOB())) {
			logger.error("Invalid Date Of Birth" + getDOB()); 
			Model.AlertHelper.showWarningMessage("Error","Author Date Of Birth is invalid", "Arcane error number plus description");
			return; 
		}

		if(!isValidId(getId())) {
			logger.error("Invalid Id");
			Model.AlertHelper.showWarningMessage("Error","Author Id is invalid", "Arcane error number plus description");
			return; 
		}
		this.update();
	}


	/*
	 * Buisness logic from here out
	 */
	public boolean isValidId(int id) {
		if(id < 0)
			return false;
		return true;
	}

	public boolean isValidFirstName(String name){
		if(name.length() < 1 || name.length() > 100)
			return false;
		return true;
	}

	public boolean isValidLastName(String name) {
		if(name.length() < 1 || name.length() > 100)
			return false;
		return true;
	}

	public boolean isValidGender(String gender) {
		if(gender.length() < 1 || gender.length() > 1) 
			return false;
		if(gender.equals("F") || gender.equals("M") || gender.equals("U"))
			return true;
		else
			return false;
	}

	public boolean isValidWebsite(String web) {
		if(web.length() > 100)
			return false;
		return true;
	}

	public boolean isValidDate(LocalDate localDate) {
		LocalDate today = LocalDate.now();
		if(localDate.isAfter(today)) 
			return false;
		return true;	
	}

	public String toString() {
		return getFirstName() + " " + getLastName();
	}

	public AuthorGateway getGateway() {
		return gateway;
	}

	public void setGateway(AuthorGateway gateway) {
		this.gateway = gateway;
	}

	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public String getWebField() {
		return webField.get();
	}

	public void setWebField(String webField) {
		this.webField.set(webField);
	}

	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public String getGender() {
		return gender.get();
	}

	public void setGender(String gender) {
		this.gender.set(gender);
	}

	public LocalDate getDOB() {
		return DOB.get();
	}

	public void setDOB(LocalDate dOB) {
		this.DOB.set(dOB);
	}

	public SimpleStringProperty firstNameFieldProperty() {
		// TODO Auto-generated method stub
		return firstName;
	}

	public SimpleStringProperty webFieldProperty() {
		// TODO Auto-generated method stub
		return webField;
	}

	public SimpleStringProperty lastNameFieldProperty() {
		// TODO Auto-generated method stub
		return lastName;
	}

	public SimpleStringProperty genderProperty() {
		return gender;
	}

	public SimpleObjectProperty<LocalDate> DOBFieldProperty() {
		return DOB;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getLast_modified() {
		return last_modified;
	}

	public void setLast_modified(LocalDateTime last_modified) {
		this.last_modified = last_modified;
	}

}
