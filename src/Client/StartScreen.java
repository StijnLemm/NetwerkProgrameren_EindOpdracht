package Client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class StartScreen extends Application {

    private boolean replay;
    private String ip;

    public void setVar(boolean replay, String ip){
        this.replay = replay;
        if(replay){
            this.ip = ip;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        if(replay){
            GameContainer gameContainer = new GameContainer(primaryStage);
            Client client = new Client(DrawMyThingClient.port, ip, gameContainer);
            gameContainer.setClient(client);
            new Thread(client).start();
            return;
        }

        HBox hBox = new HBox();
        Label ip = new Label("IP:");
        TextField inputIP = new TextField();

        hBox.getChildren().addAll(ip, inputIP);

        VBox vBox = new VBox();

        javafx.scene.control.Button connectButton = new javafx.scene.control.Button();

        connectButton.setText("Play!");

        connectButton.setOnAction((event -> {
            GameContainer gameContainer = new GameContainer(primaryStage);
            Client client = new Client(DrawMyThingClient.port, inputIP.getText(), gameContainer);
            gameContainer.setClient(client);
            new Thread(client).start();
        }));

        vBox.getChildren().addAll(hBox, connectButton);

        BorderPane mainPane = new BorderPane();

        mainPane.setCenter(vBox);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}
