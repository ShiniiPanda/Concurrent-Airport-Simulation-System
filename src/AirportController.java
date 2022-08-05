import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class AirportController implements Runnable {

    private static final int MAXIMUM_AIRPORT_CAPACITY = 2;

    private final AtomicInteger AIRPORT_CAPACITY = new AtomicInteger(MAXIMUM_AIRPORT_CAPACITY); // Semaphore Implementation

    public volatile boolean gate1_available = true,
                            gate2_available = true;
    private final List<Long> durationList = new ArrayList<>(); // Duration List for Report Generation

    public AtomicInteger totalPassengers = new AtomicInteger(0);
    public volatile PriorityBlockingQueue<Plane> circleQueue =
            new PriorityBlockingQueue<>(6, PriorityLevel.priorityCompare);
    public ReentrantLock runway_lock = new ReentrantLock(),
                         refuel_truck = new ReentrantLock();

    public AtomicBoolean requestsComing = new AtomicBoolean(true);

    public void run(){
        awaitRequests();
    }

    private void grantPermission(Plane p){
        synchronized (p){
            p.notify();
            StandardMessages.PLANE_LANDING_PERMISSION_GRANTED(p.getName());
        }
    }
    private void awaitRequests(){
        while (requestsComing.get()){
            if (!airportHasSpace() || circleQueue.size() <= 0){
                synchronized (this){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                AIRPORT_CAPACITY.decrementAndGet();
                grantPermission(Objects.requireNonNull(circleQueue.poll()));
            }
        }
        generateReport();
    }

    public void decrementCapacity(){
        if (airportHasSpace()) {
            AIRPORT_CAPACITY.decrementAndGet();
        }
    }

    public void incrementCapacity(){
        if (!airportIsEmpty()) {
            AIRPORT_CAPACITY.incrementAndGet();
        }
    }

    private void generateReport(){
        long min = durationList.get(0), max = 0, sum = 0, avg;
        for (long val : durationList){
            sum += val;
            if (val > max) {
                max = val;
            } else if (val < min) {
                min = val;
            }
        }
        avg = sum / durationList.size();

        System.out.println("\n======================================");
        System.out.println("            Status Report            ");
        System.out.println("======================================");
        System.out.println("Total Number Of Plane Operations: " + durationList.size());
        System.out.println("Minimum Plane Operation Time: " + min + " Seconds");
        System.out.println("Maximum Plane Operation Time: " + max + " Seconds");
        System.out.println("Average Plane Operation Time: " + avg + " Seconds");
        System.out.println("Total Number Of Passengers: " + totalPassengers);
        System.out.println("Gate-1 Status: " + (this.gate1_available ? "FREE" : "OCCUPIED"));
        System.out.println("Gate-2 Status: " + (this.gate2_available ? "FREE" : "OCCUPIED"));
        System.out.println("======================================\n");
    }

    public void addReport(Plane p) {
        this.durationList.add(p.getDuration());
    }
    public boolean airportHasSpace(){
        return AIRPORT_CAPACITY.get() > 0;
    }

    public boolean airportIsEmpty(){
        return AIRPORT_CAPACITY.get() == 2;
    }

    public int getAirportCapacity(){
        return AIRPORT_CAPACITY.get();
    }

    public synchronized int checkEmptyGate(){
        if (gate1_available){
            gate1_available = false;
            return 1;
        }
        if (gate2_available){
            gate2_available = false;
            return 2;
        }
        return 0;
    }
}
