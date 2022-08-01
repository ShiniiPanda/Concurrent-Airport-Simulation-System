public class StatusChecker extends Thread{


    public void run() {
        while(true){
            try {
                Thread.sleep(20000);
                System.out.println("========== STATUS REPORT ==========");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

//    public void displayLandingQueue(){
//        System.out.println("Currently there are " + port.runawayController.circleQueue.size() + " planes awaiting landing!");
//        for (Plane plane : port.runawayController.circleQueue){
//            System.out.println(plane.getName() + " is currently waiting for landing");
//        }
//    }
//
//    public void displayRunaway(){
//        System.out.println("Currently there are " + port.runawayController.airportQueue.size() + " planes on the runway");
//        for (Plane plane : port.runawayController.airportQueue){
//            System.out.println(plane.getName() +  " is currently on the runaway");
//        }
//    }
}
