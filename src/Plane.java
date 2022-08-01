import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Plane implements Runnable, Comparable<Plane> {

    private static final String date_format = "dd-MM-yyyy HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(date_format)
            .withZone(ZoneId.systemDefault());

    private final int id;
    private String name;
    private final AirportController port;
    private Thread t;
    private PriorityLevel priorityLevel;
    private PlaneStates planeStates;
    private int gate;
    private int capacity;

    private Instant startTime, endTime;
    private long duration;

    public Plane(int id, AirportController port) {
        this.id = id;
        this.name = "Plane-" + id;
        this.port = port;
    }

    public Plane(int id, AirportController port, PriorityLevel priorityLevel) {
        this.id = id;
        this.name = "Plane-" + id;
        this.port = port;
        this.priorityLevel = priorityLevel;
        this.capacity = new Random().nextInt(31) + 20; // 20 - 50 Passengers
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void run(){
        this.RequestPermission();
        this.landPlane();
        this.unloadPassengers();
        this.cleanPlane();
        this.refillSupplies();
        this.refuelPlane();
        this.loadPassengers();
        this.takeOff();
    }

//    public void landPlane(){
//        System.out.println("Attempting to get permission to land");
//        if (port.airportQueue.size() == 2) {
//            System.out.println(this.name + ": Failure to acquire permission, Runaway is full!");
//            System.out.println("Awaiting runaway landing clearance");
//        }
//        try {
//            port.airportQueue.put(this);
//            System.out.println(this.name + ": Permission to land receive, Initiating Landing.");
//            Thread.sleep(new Random().nextInt(1000) * 5);
//            System.out.println("Plane has successfully landed");
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public void emptySpot(){
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        port.airportQueue.remove(this);
//    }

    public synchronized void RequestPermission () {
        System.out.println(this.getName() + " : REQUESTING PERMISSION TO LAND!");
        this.planeStates = PlaneStates.AWAITING_LANDING_PERMISSION;
        port.circleQueue.offer(this);
        if (!port.airportHasSpace()) {
            System.out.println(this.getName() + " : AIRPORT IS CURRENTLY FULL, AWAITING FOR RUNAWAY CLEARANCE!");
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            port.circleQueue.remove(this);
            port.decrementCapacity();
            System.out.println("Current Airport Capacity: " + port.getAirportCapacity());
            System.out.println("\n" + this.name + " : LANDING PERMISSION GRANTED!\n");
        }
    }

    public void landPlane(){
        System.out.println(this.getName() + " : ATTEMPTING TO LAND AIR CRAFT!\n");
        if (port.runaway_lock.isLocked()) {
            System.out.println(this.getName() + " : RUNAWAY IS CURRENTLY TAKEN!\n");
            this.planeStates = PlaneStates.AWAITING_RUNAWAY_LANDING;
        }
        port.runaway_lock.lock();
        if (this.planeStates == PlaneStates.AWAITING_RUNAWAY_LANDING) {
            System.out.println(this.getName() + " : RUNAWAY IS AVAILABLE, GOING FOR LANDING!\n");
        }
        this.planeStates = PlaneStates.LANDING;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(this.getName() + " : AIR CRAFT HAS SUCCESSFULLY LANDED!\n");
        this.dockToGate();
        port.runaway_lock.unlock();
    }

    public void dockToGate(){
        this.gate = port.checkEmptyGate();
        try {
            System.out.println(this.getName() + " : GATE-" + this.gate + " IS AVAILABLE, ATTEMPTING DOCKING\n");
            this.planeStates = PlaneStates.DOCKING;
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(this.getName() + " : DOCKED SUCCESSFULLY TO GATE-" + this.gate + "\n");
    }

    private void freeSpace(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        port.incrementCapacity();
        System.out.println("Current Airport Capacity: " + port.getAirportCapacity());
        if(port.circleQueue.size() == 0 && port.airportIsEmpty()){
            port.requestsComing.set(false);
        }
        synchronized (port){
           // System.out.println("\nWaking it up!\n");
            port.notify();
        }
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

    private void cleanPlane(){
        System.out.println(this.getName() + " : STARTING AIRCRAFT CLEANING!\n");
        this.planeStates = PlaneStates.GETTING_CLEANED;
        try {
            Thread.sleep(new Random().nextInt(3000) + 2000); // 2000 - 5000

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(this.getName() + ": CLEANING PROCESS IS COMPLETE!\n");
    }

    private void refillSupplies(){
        System.out.println(this.getName() + ": REFILLING PLANE SUPPLIES!\n");
        this.planeStates = PlaneStates.REFILLING_SUPPLIES;
        try {
            Thread.sleep(new Random().nextInt(3000) + 2000); // 2000 - 5000
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(this.getName() + ": RESUPPLYING HAS BEEN COMPLETED!\n");
    }

    private void refuelPlane(){
        System.out.println(this.getName() + ": ATTEMPTING TO REFUEL THE AIRCRAFT!");
        if (port.refuel_truck.isLocked()){
            System.out.println(this.getName() + ": FUEL TRUCK CURRENTLY IN USE, AWAITING CLEARANCE!\n");
            this.planeStates = PlaneStates.AWAITING_REFUEL_TRUCK;
        }
        port.refuel_truck.lock();
        System.out.println(this.getName() + ": STARTING REFUELLING PROCESS!\n");
        this.planeStates = PlaneStates.REFUELING_TANK;
        try {
            Thread.sleep(new Random().nextInt(3000) + 3000); // 3000 - 6000
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(this.getName() + ": FINISHED REFUELLING TANK!\n");
        port.refuel_truck.unlock();
    }


    private void unloadPassengers(){
        this.planeStates = PlaneStates.UNLOADING_PASSENGERS;
        System.out.println(this.getName() + " : PREPARING TO DISEMBARK PASSENGERS!\n");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = this.capacity; i > 0; i--){
            System.out.println(this.getName() + " : PASSENGER-" + i + " DISEMBARKED FROM THE PLANE!");
            try {
                Thread.sleep((new Random().nextInt(10) + 1) * 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\n" + this.getName() + " : FINISHED DISEMBARKING PASSENGERS!\n");
    }

    private void undockFromGate(){
        System.out.println(this.getName() + ": STARTING UNDOCKING PROCESS FROM GATE-" + this.gate +"!\n");
        this.planeStates = PlaneStates.UNDOCKING;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(this.getName() + ": SUCCESSFULLY UNDOCKED FROM GATE-" + this.gate + "!\n");
        this.freeGate();
    }

    private void takeOff(){
        undockFromGate();
        System.out.println(this.getName() + ": PREPARING TO TAKE OFF, CHECKING RUNAWAY!\n");
        if (port.runaway_lock.isLocked()){
            System.out.println(this.getName() + ": RUNAWAY IS CURRENTLY IN USE, AWAITING CLEARANCE FOR TAKEOFF!\n");
            this.planeStates = PlaneStates.AWAITING_RUNAWAY_TAKEOFF;
        }
        port.runaway_lock.lock();
        System.out.println(this.getName() + ": RUNAWAY IS AVAILABLE, GOING FOR TAKE OFF!\n");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        port.runaway_lock.unlock();
        this.endTime = Instant.now();
        this.duration = Duration.between(startTime, endTime).toMillis() / 1000;
        System.out.println("[ " + formatter.format(this.endTime) + " ] " + this.getName() + ": AIRCRAFT HAS TAKEN OFF SUCCESSFULLY!\n");
        System.out.println(this.getName() + ": SIMULATION FINISHED IN " + this.duration + " SECONDS!");
        port.addReport(this);
        this.freeSpace();
    }

    private void loadPassengers(){
        this.planeStates = PlaneStates.LOADING_PASSENGERS;
        System.out.println(this.getName() + " : PREPARING TO EMBARK PASSENGERS!\n");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = this.capacity; i > 0; i--){
            System.out.println(this.getName() + " : PASSENGER-" + i + " EMBARKED ONTO THE PLANE!");
            try {
                Thread.sleep((new Random().nextInt(10) + 1) * 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\n" + this.getName() + " : FINISHED EMBARKING PASSENGERS!\n");
    }


    public synchronized void create(){
        this.startTime = Instant.now();
        if (priorityLevel == PriorityLevel.NORMAL_PRIORITY) {
            System.out.println("[" + formatter.format(this.startTime) + "] " + this.name + " : JOINED THE BATTLE!");
        } else if (priorityLevel == PriorityLevel.HIGH_PRIORITY) {
            System.out.println("===============================================\n" +
                               "                 EMERGENCY!!!!                 \n" +
                               "===============================================\n" +
                               "[" + formatter.format(this.startTime) + "] " + this.name +  " : JOINED THE BATTLE, HIGH PRIORITY");

        }
        t = new Thread(this);
        t.start();
    }

    public int compareTo(Plane other) {
        return this.getPriorityLevel().getPriority() - other.getPriorityLevel().getPriority();
    }

}
