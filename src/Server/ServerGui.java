package Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ServerGui extends Application {

    private TextArea textArea;

    @Override
    public void start(Stage primaryStage) throws Exception {
        MultClientServer multClientServer = new MultClientServer(6666, this);

        Button startServer = new Button("Start server");
        Button stopServer = new Button("Stop server");

        startServer.setOnAction((event -> {
            if(!multClientServer.isRunning()){
                multClientServer.start();
            }
        }));

        stopServer.setOnAction((event -> {
            if(multClientServer.isRunning()){
                multClientServer.setRunning(false);
            }
        }));

        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox(startServer, stopServer);

        textArea = new TextArea();

        borderPane.setTop(hBox);

        borderPane.setCenter(textArea);

        primaryStage.setScene(new Scene(borderPane, 400, 800));
        primaryStage.show();
    }

    public void printLogLine(String line){
        System.out.println("print: " + line);
        this.textArea.setText(this.textArea.getText() + "\n" + line);
    }

    public static void main(String[] args) {
        launch(ServerGui.class);
    }
}