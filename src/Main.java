import java.util.Scanner;

public class Main {

    private static int planesNum; // Planes to Generate
    private static boolean emergencyEnable = false; // Option for Emergency Scenarios

    public static void main(String[] args) {
        AirportController controller = new AirportController();
        System.out.println(StandardMessages.PLANE_ART());
        while(!initiateSimulation()); // Get Pre-Simulation Data
        PlanesGenerator planesGenerator = new PlanesGenerator(controller, planesNum, emergencyEnable);
        Thread generatorThread = new Thread(planesGenerator);
        generatorThread.start(); // Start Plane Generation
        Thread controllerThread = new Thread(controller);
        controllerThread.start(); // Start Airport Controller
    }

    private static boolean initiateSimulation(){
        String input;
        char emergencyChoice;
        Scanner scanner = new Scanner(System.in);
        System.out.println("===============================");
        System.out.print("Welcome to the Airport Simulation!\n" +
                "Specify the number of planes to get started: ");
        try {
            input = scanner.next();
            planesNum = Integer.parseInt(input);
        } catch (NumberFormatException e){
            System.out.println("Please enter a valid number of planes to simulate!\n");
            return false;
        }
        if (planesNum <= 0) {System.out.println("Please enter a valid number of planes to simulate!\n"); return false;}
        System.out.print("Would you like to simulate emergency scenarios (Y/N): ");
        try {
            emergencyChoice = scanner.next().charAt(0);
            emergencyChoice = Character.toLowerCase(emergencyChoice);
            if (emergencyChoice == 'y') {
                System.out.println("Emergency scenarios will be simulated!");
                emergencyEnable = true;
            }
            else {
                System.out.println("Emergency scenarios will not be simulated!");
            }
        } catch (IndexOutOfBoundsException e){
            System.out.println("Please enter a valid answer! (Y/N)");
            return false;
        }
        System.out.println("===============================\n");
        return true;
    }
}
