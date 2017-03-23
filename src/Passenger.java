/*
Author: Javier Mucia
Summary: Passenger (Thread) Class
Description: Passengers in the Haunted House.
*/

public class Passenger extends Thread{
    private int ID;
    public volatile int ridesTaken;
    private boolean isOutOnTour;
    public boolean isAllowedToLeave;
    public boolean isAllowedToBoard;
    public volatile String carID;

    // Default constructor for a Passenger
    Passenger(int newID){
        this.ID = newID;
        this.setName("Passenger_"+newID);
        this.ridesTaken = 0;
        this.isOutOnTour = false;
        this.isAllowedToLeave = false;
        this.isAllowedToBoard = false;
        }

    public void run(){
        msg("arrived at the Hounted House. Now it's finding its way to the Line to take a tour");
        this.goSleep(HountedHouse.randomInteger(600, 2500));       // Simulate a Passenger finding his way to the line
											//Random Integer between [600, 2500] (inclusive) milliseconds


        /*      ----------------------   Each Passenger must take 3 rides -------------------------         */
        while(this.ridesTaken<3) {
            this.setPriority(Thread.NORM_PRIORITY);                             // Reset Priority to NORMAL = 5
            HountedHouse.passengers_who_need_ride.add(this);                // Add this passenger to the line waiting for a ride
            msg("joined the line, now it is waiting for its turn to board a Car (Priority: "+this.getPriority()+").");

            while( !(isAllowedToBoard) ){                                   // Busy wait at the Line 'till it is allowed to board a car.
                this.goSleep(10); }                                       // This passenger is waiting at the Car boarding queue

            // As soon as it is allowed to board, it will yield to other passengers
            this.yield();                                                   // When allowed to board, yield to other passengers

            takingRide(30000);                  // This passenger is taking a ride. Simulated by a LONG sleep time
            									// The car who took this passenger for a Tour will wake him up (after the tour that lasts 5000 milliseconds)
            this.setPriority(this.getPriority()+HountedHouse.randomInteger(0, 5)); // Randomly increase this Thread's Priority to get off, while in a ride

            // Once this Passenger has been awakened from his sweet sleep by a Car's Interrupt, it will board off the Car
            // and wander around the park for a little bit before joining the line to take another ride.
            msg("boarding off "+this.carID+" with priority: "+this.getPriority());  // Get off board
            goSleep(600); // small pause for this action
            this.ridesTaken++;
            this.carID = null;                                                      // Clear the carrier Car#
            msg("will wander around the park for a short time.");
            this.goSleep(HountedHouse.randomInteger(1500, 2500));  // (Simulate it's wandering off before joining the line again)


            // Once it comes back from wandering around the park it will join the line of people waiting to take a ride
            }





        /*    ------------ ONCE A PASSENGER HAS TAKEN 3 RIDES IT WILL WANDER AROUND AND ATTEMPT TO LEAVE   ------ */
        HountedHouse.onePassengerIsDoneRiding();
        msg("has finished taking 3 rides and will be wandering for a little. (# of Passengers needing a ride = "
                +HountedHouse.number_of_passengers_who_need_a_ride +") \t ++++++ ");
        goSleep(HountedHouse.randomInteger(1000, 2000));                        // (simulate wandering off)
        msg("is ready to leave and is attempting to leave now.");
        goSleep(600); // small pause for this action
        HountedHouse.passengers_ready_to_leave.add(this);

        while(!this.isAllowedToLeave)
            goSleep(10);
        msg("leaving the park now ("+this.getName()+" Thread dying by reaching the end of its \"run()\" method)");
        goSleep(600); // small pause for this action

        
        // If this is the last passenger leaving. Before reaching the end of its run method 
        // this passenger will allow all cars to shut down. Hounted House will know it's PassengerID
        if(HountedHouse.number_of_passengers== this.getID()) {
            msg("is the last Passenger to leave. Shutting down cars now.");
            HountedHouse.shutDownCars(this.getID());
            }
    }












    public void goSleep(int n){
        try {  this.sleep(n); }
        catch (InterruptedException e) { e.printStackTrace();  }
        }

    public void takingRide(int n){
        try {  this.sleep(n); }
        catch (InterruptedException e) {
            //e.printStackTrace();
            msg("Awakened by "+this.carID);
            }
        }

    public void msg(String m){
        System.out.println("["+(System.currentTimeMillis()-HountedHouse.time)+"] "+this.getName()+": "+m);
        }
    public void startCar(String carName){
        msg("starting "+carName+" because he was the last to enter.");
    }

    public int getID(){ return this.ID;}




}
