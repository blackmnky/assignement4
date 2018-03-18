package Book;

import Database.PublisherGateway;
import javafx.beans.property.SimpleStringProperty;

public class Publisher {
	private int id;
	private SimpleStringProperty publisherName;
	private PublisherGateway gateway;
	
	public Publisher() {
		publisherName = new SimpleStringProperty();
		this.id = 0;
	}
	
	public Publisher(String name) {
		this();
		setPublisherName(name);
	}
	
	/*
	 * Business Logic
	 */
	public String toString() {
		return getPublisherName();
	}
	
	public String getPublisherName() {
		return publisherName.get();
	}
	
	public void setPublisherName(String name) {
		this.publisherName.set(name);
	}
	
	public SimpleStringProperty pubNameProperty() {
		return publisherName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setGateway(PublisherGateway publisherGateway) {
		this.gateway = publisherGateway;
	}
	
	public PublisherGateway getGateway() {
		return this.gateway;
	}
	
}
