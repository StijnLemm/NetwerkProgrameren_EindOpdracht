package Client;

import javafx.scene.canvas.GraphicsContext;

import java.io.DataOutputStream;
import java.io.IOException;

import static javafx.application.Application.launch;

public class DrawMyThingClient {

    private GameContainer gameContainer;

    public DrawMyThingClient() {
        this.gameContainer = new GameContainer();

        try {
            Client client = new Client(6667, gameContainer);
            System.out.println("client init success!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        launch(GameContainer.class);
    }

    public static void main(String[] args) {
        DrawMyThingClient drawMyThingClient = new DrawMyThingClient();
    }
}
