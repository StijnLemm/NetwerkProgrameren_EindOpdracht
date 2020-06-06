package Client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class StartScreen extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        Label port = new Label("Port:");
        TextField inputPort = new TextField();
        Label ip = new Label("IP:");
        TextField inputIP = new TextField();

        hBox.getChildren().addAll(port, inputPort, ip, inputIP);

        VBox vBox = new VBox();

        javafx.scene.control.Button connectButton = new javafx.scene.control.Button();

        connectButton.setText("Play!");

        connectButton.setOnAction((event -> {

            int portNumber;

            try{
                portNumber = Integer.parseInt(inputPort.getText());
            } catch (NumberFormatException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invoer niet correct");
                alert.setContentText("De port kan alleen bestaan uit cijfers.");
                alert.show();
                return;
            }

            try {
                GameContainer gameContainer = new GameContainer(primaryStage);
                Client client = new Client(portNumber, inputIP.getText(), gameContainer);
                gameContainer.setClient(client);
                new Thread(client).start();

                gameContainer.start();

                System.out.println("client init success!");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }));

        vBox.getChildren().addAll(hBox, connectButton);

        BorderPane mainPane = new BorderPane();

        mainPane.setCenter(vBox);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}
