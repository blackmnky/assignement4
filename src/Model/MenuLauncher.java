package Model;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Controller.MenuController;
import Database.ConnectionFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MenuLauncher extends Application {

	private static Logger logger = LogManager.getLogger();
	private Connection connection;
	
	@Override
	public void start(Stage stage) throws Exception {
		logger.info("calling start");

		MenuController controller = MenuController.getInstance();
		controller.setConnection(connection);

		URL fxmlFile = this.getClass().getResource("MasterView.fxml");
		FXMLLoader loader = new FXMLLoader(fxmlFile);

		loader.setController(controller);

		Parent rootNode = loader.load();
		controller.setRootNode((BorderPane) rootNode);

		Scene scene = new Scene(rootNode, 600, 400);
		stage.setTitle("Assignment4");
		stage.setScene(scene);
		stage.show();
	}
	
	@Override
	public void stop() throws Exception {
		logger.info("calling stop");

		super.stop();

		connection.close();
		logger.info("closed connection");
	}
	
	@Override
	public void init() throws Exception {
		logger.info("calling init");

		super.init();
		try {
			connection = ConnectionFactory.createConnection();
			logger.info("made connection");
		} catch (IOException e) { // can possible be AppException
			logger.fatal("cannot connect to db");
			Platform.exit();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
