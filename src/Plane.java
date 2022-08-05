import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Plane implements Runnable {

    private String name;
    private final AirportController port;
    private Thread t;
    private PriorityLevel priorityLevel;
    private PlaneStates planeStates;
    private int gate;
    private int capacity;

    private Instant startTime, endTime;
    private long duration;

    public Plane(String name, AirportController port) {
        this.name = name;
        this.port = port;
    }

    public Plane(String name, AirportController port, PriorityLevel priorityLevel) {
        this.name = name;
        this.port = port;
        this.priorityLevel = priorityLevel;
        this.capacity = new Random().nextInt(31) + 20; // 20 - 50 Passengers
    }

    public String getName() {
        return name;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void run(){
        this.requestPermission();
        this.landPlane();
        this.unloadPassengers();
        this.cleanPlane();
        this.refillSupplies();
        this.refuelPlane();
        this.loadPassengers();
        this.takeOff();
    }

    public synchronized void requestPermission() {
        StandardMessages.PLANE_LANDING_REQUEST(name);
        this.planeStates = PlaneStates.AWAITING_LANDING_PERMISSION;
        port.circleQueue.offer(this);
        if (!port.airportHasSpace()) {
            StandardMessages.PLANE_LANDING_AIRPORT_FULL(name);
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            port.circleQueue.remove(this);
            port.decrementCapacity();
            //System.out.println("Current Airport Capacity: " + port.getAirportCapacity());
            StandardMessages.PLANE_LANDING_PERMISSION_GRANTED(name);
        }
    }

    public void landPlane(){
        StandardMessages.PLANE_LANDING_ATTEMPT(name);
        if (port.runway_lock.isLocked()) {
            StandardMessages.PLANE_LANDING_RUNAWAY_TAKEN(name);
            this.planeStates = PlaneStates.AWAITING_RUNAWAY_LANDING;
        }
        port.runway_lock.lock();
        if (this.planeStates == PlaneStates.AWAITING_RUNAWAY_LANDING) {
            StandardMessages.PLANE_LANDING_RUNAWAY_FREED(name);
        }
        this.planeStates = PlaneStates.LANDING;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StandardMessages.PLANE_LANDING_SUCCESSFUL(name);
        this.dockToGate();
        port.runway_lock.unlock();
    }

    public void dockToGate(){
        this.gate = port.checkEmptyGate();
        try {
            StandardMessages.PLANE_DOCKING_ATTEMPT(name, gate);
            this.planeStates = PlaneStates.DOCKING;
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StandardMessages.PLANE_DOCKING_SUCCESSFUL(name, gate);
    }

    private void cleanPlane(){
        StandardMessages.PLANE_CLEANING_BEGIN(name);
        this.planeStates = PlaneStates.GETTING_CLEANED;
        try {
            Thread.sleep(new Random().nextInt(3000) + 2000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StandardMessages.PLANE_CLEANING_FINISH(name);
    }

    private void refillSupplies(){
        StandardMessages.PLANE_RESUPPLY_BEGIN(name);
        this.planeStates = PlaneStates.REFILLING_SUPPLIES;
        try {
            Thread.sleep(new Random().nextInt(3000) + 2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StandardMessages.PLANE_RESUPPLY_FINISH(name);
    }

    private void refuelPlane(){
        StandardMessages.PLANE_REFUEL_ATTEMPT(name);
        if (port.refuel_truck.isLocked()){
            StandardMessages.PLANE_REFUEL_TRUCK_TAKEN(name);
            this.planeStates = PlaneStates.AWAITING_REFUEL_TRUCK;
        }
        port.refuel_truck.lock();
        StandardMessages.PLANE_REFUEL_BEGIN(name);
        this.planeStates = PlaneStates.REFUELING_TANK;
        try {
            Thread.sleep(new Random().nextInt(3000) + 3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StandardMessages.PLANE_REFUEL_FINISH(name);
        port.refuel_truck.unlock();
    }

    private void unloadPassengers(){
        this.planeStates = PlaneStates.UNLOADING_PASSENGERS;
        StandardMessages.PLANE_DISEMBARK_BEGIN(name);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = this.capacity; i > 0; i--){
            StandardMessages.PLANE_DISEMBARK_PASSENGER(name, i);
            try {
                Thread.sleep((new Random().nextInt(10) + 1) * 85); // Passenger Generated every 85-850 seconds at random
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        StandardMessages.PLANE_DISEMBARK_FINISH(name);
    }

    private void loadPassengers(){
        this.planeStates = PlaneStates.LOADING_PASSENGERS;
        StandardMessages.PLANE_EMBARK_BEGIN(name);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = this.capacity; i > 0; i--){
            StandardMessages.PLANE_EMBARK_PASSENGER(name, i);
            try {
                Thread.sleep((new Random().nextInt(10) + 1) * 85); // Passenger Generated every 85-850 seconds at random
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        StandardMessages.PLANE_EMBARK_FINISH(name);
    }

    private void freeGate(){
        switch(this.gate){
            case 1:
                port.gate1_available = true;
                break;
            case 2:
                port.gate2_available = true;
                break;
        }
    }
    private void undockFromGate(){
        StandardMessages.PLANE_UNDOCKING_ATTEMPT(name, gate);
        this.planeStates = PlaneStates.UNDOCKING;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StandardMessages.PLANE_UNDOCKING_SUCCESSFUL(name, gate);
        this.freeGate();
    }

    private void takeOff(){
        undockFromGate();
        StandardMessages.PLANE_TAKEOFF_ATTEMPT(name);
        if (port.runway_lock.isLocked()){
            StandardMessages.PLANE_TAKEOFF_RUNAWAY_TAKEN(name);
            this.planeStates = PlaneStates.AWAITING_RUNAWAY_TAKEOFF;
        }
        port.runway_lock.lock();
        this.planeStates = PlaneStates.TAKING_OFF;
        StandardMessages.PLANE_TAKEOFF_BEGIN(name);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        port.runway_lock.unlock();
        this.endTime = Instant.now();
        this.duration = Duration.between(startTime, endTime).toMillis() / 1000;
        StandardMessages.PLANE_TAKEOFF_SUCCESSFUL(name, endTime);
        StandardMessages.PLANE_SIMULATION_FINISH(name, duration);
        port.addReport(this);
        port.totalPassengers.addAndGet(this.capacity);
        this.planeStates = PlaneStates.FINISHED;
        this.freeSpace();
    }

    private void freeSpace(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        port.incrementCapacity();
        //System.out.println("Current Airport Capacity: " + port.getAirportCapacity());
        if(port.circleQueue.size() == 0 && port.airportIsEmpty()){
            port.requestsComing.set(false);
        }
        synchronized (port){
            // System.out.println("\nWaking it up!\n");
            port.notify();
        }
    }

    public synchronized void create(){
        this.startTime = Instant.now();
        if (priorityLevel == PriorityLevel.NORMAL_PRIORITY) {
            StandardMessages.PLANE_ARRIVED_NORMAL(name, startTime);
        } else if (priorityLevel == PriorityLevel.HIGH_PRIORITY) {
            StandardMessages.PLANE_ARRIVED_EMERGENCY(name, startTime);
        }
        t = new Thread(this);
        t.start();
    }

}
