package Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * This class starts the ServerGUI in this GUI will be the possibility to start or stop the server.
 * The GUI will also log the information about what events are going on in the textfield of the GUI for example: "Server started..."
 */

public class ServerGui extends Application {

    private TextArea textArea;

    @Override
    public void start(Stage primaryStage) {
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
                multClientServer.stop();
            }
        }));

        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox(startServer, stopServer);

        textArea = new TextArea();
        textArea.setEditable(false);

        borderPane.setTop(hBox);

        borderPane.setCenter(textArea);

        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(borderPane, 400, 800));
        primaryStage.show();
    }


    /**
     * This method is used to print lines on the logfield in the ServerGUI
     * @param line
     */
    public void printLogLine(String line){
        this.textArea.setText(this.textArea.getText() + "\n" + line);
    }

    public static void main(String[] args) {
        launch(ServerGui.class);
    }
}
