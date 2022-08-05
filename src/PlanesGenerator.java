import java.util.Arrays;
import java.util.Random;

public class PlanesGenerator implements Runnable {

    private int planesToGenerate = 4;
    private boolean emergency;
    private final AirportController port;

    public PlanesGenerator(AirportController airport) {
        this.port = airport;
    }

    public PlanesGenerator(AirportController airport, int planesNum, boolean enableEmergency){
        this.port = airport;
        this.planesToGenerate = planesNum;
        this.emergency = enableEmergency;
    }

    @Override
    public void run() {
        int i = 1, randInt;
        if (!this.emergency) {
            while(planesToGenerate > 0){
                new Plane("Plane-" + i, port, PriorityLevel.NORMAL_PRIORITY).create();
                try {
                    Thread.sleep(new Random().nextInt(3000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                i++;
                planesToGenerate--;
            }
        } else {
            while(planesToGenerate > 0){
                randInt = new Random().nextInt(2);
                switch(randInt){
                    case 0:
                        new Plane("Plane-" + i, port, PriorityLevel.NORMAL_PRIORITY).create();
                        break;
                    case 1:
                        new Plane("Plane-" + i, port, PriorityLevel.HIGH_PRIORITY).create();
                        break;
                }
                try {
                    Thread.sleep(new Random().nextInt(3000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                i++;
                planesToGenerate--;
            }
        }
    }
}
