package Client;

public class GameTimer implements Runnable {

    public static final int time = 120;

    private final GameContainer gameContainer;
    private boolean running;

    public GameTimer(GameContainer gameContainer) {
        this.gameContainer = gameContainer;
    }

    @Override
    public void run() {

        int timeLeft = time;
        long timeMillis = System.currentTimeMillis();
        running = true;

        while(running && timeLeft > 0){
            if(timeMillis + 1000 == System.currentTimeMillis()){
                timeLeft--;
                this.gameContainer.updateTimer(timeLeft);
                timeMillis = System.currentTimeMillis();
            }
        }
        if(timeLeft == 0) {
            this.gameContainer.noMoreTimeLeft();
        }
    }

    public void stop(){
        running = false;
    }
}
