import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StandardMessages {

    private static final String date_format = "dd-MM-yyyy HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(date_format)
            .withZone(ZoneId.systemDefault());

    public static String PLANE_ART() {
        return "            __/\\__\n" +
                "           `==/\\==`\n" +
                " ____________/__\\____________\n" +
                "/____________________________\\\n" +
                "  __||__||__/.--.\\__||__||__\n" +
                " /__|___|___( >< )___|___|__\\\n" +
                "           _/`--`\\_\n" +
                "          (/------\\)";
        // Art by: jgs from https://www.asciiart.eu/vehicles/airplanes
    }

    public static void PLANE_ARRIVED_NORMAL(String name, Instant time){
        System.out.println("[" + time + "] " + name + " : JOINED THE BATTLE!");
    }

    public static void PLANE_ARRIVED_EMERGENCY(String name, Instant time) {
        System.out.println("===============================================\n" +
                           "                 EMERGENCY!!!!                 \n" +
                           "===============================================\n" +
                           "[" + time + "] " + name +  " : JOINED THE BATTLE, HIGH PRIORITY");
    }

    public static void PLANE_LANDING_REQUEST(String name){
        System.out.println(name + " : REQUESTING PERMISSION TO LAND!");
    }

    public static void PLANE_LANDING_AIRPORT_FULL(String name){
        System.out.println(name + " : AIRPORT IS CURRENTLY FULL, AWAITING FOR RUNAWAY CLEARANCE!");
    }

    public static void PLANE_LANDING_PERMISSION_GRANTED(String name){
        System.out.println("\n" + name + " : LANDING PERMISSION GRANTED!\n");
    }

    public static void PLANE_LANDING_ATTEMPT(String name){
        System.out.println(name + " : ATTEMPTING TO LAND AIR CRAFT!\n");
    }

    public static void PLANE_LANDING_RUNAWAY_TAKEN(String name){
        System.out.println(name + " : RUNAWAY IS CURRENTLY TAKEN!\n");
    }

    public static void PLANE_LANDING_RUNAWAY_FREED(String name){
        System.out.println(name + " : RUNAWAY IS AVAILABLE, GOING FOR LANDING!");
    }

    public static void PLANE_LANDING_SUCCESSFUL(String name) {
        System.out.println(name + " : AIR CRAFT HAS SUCCESSFULLY LANDED!\n");
    }

    public static void PLANE_DOCKING_ATTEMPT(String name, int gate){
        System.out.println(name + " : GATE-" + gate + " IS AVAILABLE, ATTEMPTING DOCKING\n");
    }

    public static void PLANE_DOCKING_SUCCESSFUL(String name, int gate){
        System.out.println(name + " : DOCKED SUCCESSFULLY TO GATE-" + gate + "\n");
    }

    public static void PLANE_CLEANING_BEGIN(String name){
        System.out.println(name + " : STARTING AIRCRAFT CLEANING!\n");
    }

    public static void PLANE_CLEANING_FINISH(String name){
        System.out.println(name + ": CLEANING PROCESS IS COMPLETE!\n");
    }

    public static void PLANE_RESUPPLY_BEGIN(String name){
        System.out.println(name + ": REFILLING PLANE SUPPLIES!\n");
    }

    public static void PLANE_RESUPPLY_FINISH(String name){
        System.out.println(name + ": RESUPPLYING HAS BEEN COMPLETED!\n");
    }

    public static void PLANE_REFUEL_ATTEMPT(String name){
        System.out.println(name + ": ATTEMPTING TO REFUEL THE AIRCRAFT!");
    }

    public static void PLANE_REFUEL_TRUCK_TAKEN(String name){
        System.out.println(name + ": FUEL TRUCK CURRENTLY IN USE, AWAITING CLEARANCE!\n");
    }

    public static void PLANE_REFUEL_BEGIN(String name){
        System.out.println(name + ": STARTING REFUELLING PROCESS!\n");
    }

    public static void PLANE_REFUEL_FINISH(String name){
        System.out.println(name + ": FINISHED REFUELLING TANK!\n");
    }

    public static void PLANE_DISEMBARK_BEGIN(String name){
        System.out.println(name + " : PREPARING TO DISEMBARK PASSENGERS!\n");
    }

    public static void PLANE_DISEMBARK_PASSENGER(String name, int passenger_num){
        System.out.println(name + " : PASSENGER-" + passenger_num + " DISEMBARKED FROM THE PLANE!");
    }

    public static void PLANE_DISEMBARK_FINISH(String name){
        System.out.println("\n" + name + " : FINISHED DISEMBARKING PASSENGERS!\n");
    }

    public static void PLANE_EMBARK_BEGIN(String name){
        System.out.println(name + " : PREPARING TO EMBARK PASSENGERS!\n");
    }

    public static void PLANE_EMBARK_PASSENGER(String name, int passenger_num){
        System.out.println(name + " : PASSENGER-" + passenger_num + " EMBARKED ONTO THE PLANE!");
    }

    public static void PLANE_EMBARK_FINISH(String name){
        System.out.println("\n" + name + " : FINISHED EMBARKING PASSENGERS!\n");
    }

    public static void PLANE_UNDOCKING_ATTEMPT(String name, int gate){
        System.out.println(name + ": STARTING UNDOCKING PROCESS FROM GATE-" + gate +"!\n");
    }

    public static void PLANE_UNDOCKING_SUCCESSFUL(String name, int gate){
        System.out.println(name + ": SUCCESSFULLY UNDOCKED FROM GATE-" + gate + "!\n");
    }

    public static void PLANE_TAKEOFF_ATTEMPT(String name){
        System.out.println(name + ": PREPARING TO TAKE OFF, CHECKING RUNAWAY!\n");
    }

    public static void PLANE_TAKEOFF_RUNAWAY_TAKEN(String name){
        System.out.println(name + ": RUNAWAY IS CURRENTLY IN USE, AWAITING CLEARANCE FOR TAKEOFF!\n");
    }

    public static void PLANE_TAKEOFF_BEGIN(String name){
        System.out.println(name + ": RUNAWAY IS AVAILABLE, GOING FOR TAKE OFF!\n");
    }

    public static void PLANE_TAKEOFF_SUCCESSFUL(String name, Instant time){
        System.out.println("[ " + formatter.format(time) + " ] " + name + ": AIRCRAFT HAS TAKEN OFF SUCCESSFULLY!\n");
    }

    public static void PLANE_SIMULATION_FINISH(String name, long duration){
        System.out.println(name + ": SIMULATION FINISHED IN " + duration + " SECONDS!");
    }
}
