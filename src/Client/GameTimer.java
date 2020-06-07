package Client;

/**
 * This class is used to run the timer of the game, this timer is set on 120 seconds (2 minutes).
 * The timer will be shown on the screen while the game is running.
 */

public class GameTimer implements Runnable {

    public static final int time = 120;

    private final GameContainer gameContainer;
    private boolean running;

    public GameTimer(GameContainer gameContainer) {
        this.gameContainer = gameContainer;
    }

    /**
     * This method runs and updates the game timer.
     */
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

    /**
     * This method is used to stop Threads.
     */
    public void stop(){
        running = false;
    }
}
