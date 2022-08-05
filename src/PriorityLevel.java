import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public enum PriorityLevel {
    NORMAL_PRIORITY(1), // Normal Planes
    HIGH_PRIORITY(0); // Emergency Planes

    private final int priority;
    PriorityLevel(int priority){
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public static final Comparator<Plane> priorityCompare = (Plane a, Plane b) -> {
        if (a.getPriorityLevel().getPriority() == b.getPriorityLevel().getPriority()){
            return (a.getStartTime().toEpochMilli() - b.getStartTime().toEpochMilli()) < 0 ? -1 : 1; // Ensures FIFO for same priority planes
        } else {
            return  a.getPriorityLevel().getPriority() - b.getPriorityLevel().getPriority();
        }
    };
}
