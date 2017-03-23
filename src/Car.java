/*
Author: Javier Mucia
Summary: Car (Thread) Class
Description: Denoting Cars in the Haunted House.
*/

import java.util.*;

public class Car extends Thread{
    private int ID;
    private int capacity;
    private boolean isOutOnTour;
    private Vector <Passenger> carCrew;
    private int TOUR_LENGTH = 5000;

    // Default Constructor taking carID and the capacity for a car
    Car(int newID, int newCapacity){
        this.ID = newID;
        this.setName("Car_"+newID);
        this.capacity = newCapacity;
        this.isOutOnTour = false;
        this.carCrew = new Vector<Passenger>();
        }

    public void run() {
        msg("Started Up. It will line up and wait for it's turn to load passengers.");
        while (!(HountedHouse.allPassengersArrived())) {
        }
        // BusyWait till all the passengers arrive // Makes it easier to read the output
        // Once all Passengers arrive, (one by one) Cars start taking up Passengers up to their capacity.
        // (or allow all passengers enter the first car if: [total # of passengers] < [capacity of cars]



        // Cars will remain in operation meanwhile there is still Passengers who need to complete their 3 rides.
        while (!(HountedHouse.allPassengersTookThreeRides())) {
            msg("lined up at the Queue of Cars");
            goSleep(600);  // small pause for this action
            HountedHouse.all_cars.add(this);                            // Add this car again to the Queue of cars at the Hounted House

            // All the cars (except the first) wait on the line to load passengers
            while(HountedHouse.all_cars.firstElement() != this)
                this.goSleep(10);

            // while this car was waiting for its turn, the car in fron of it gave the last ride to the remaining passengers
            // This condition will let this car to shut itself down and allow other cars to do the same
            if(HountedHouse.allPassengersTookThreeRides())
                break;


            // If this car is the first one in the Queue of Cars, let it load passengers to its full capacity
            msg("is now first on the Queue of cars");
            goSleep(600); // small pause for this action

            // This car will either load passengers up to its full capacity or up to the number of passengers who still
            // need to complete three rides (whichever is smaller).
            int number_of_passengers_to_load = Math.min(capacity, HountedHouse.number_of_passengers_who_need_a_ride);


            // Wait until there is enough "number_of_passengers_to_load" in the line of passengers
            if(HountedHouse.passengers_who_need_ride.size()<number_of_passengers_to_load)
                msg("is waiting for "+number_of_passengers_to_load+" passengers to line up.");
            while(HountedHouse.passengers_who_need_ride.size()<number_of_passengers_to_load) {
                goSleep(10);
                number_of_passengers_to_load = Math.min(capacity, HountedHouse.number_of_passengers_who_need_a_ride);
                }

            // In some cases. When a car is waiting for Passengers taking a ride on a different car, those passengers
            // may be in their last ride so after they return to the HountedHouse queue they immediately leave to wnader
            // off and get ready to leave.
            if(number_of_passengers_to_load==0)
                break;

            HountedHouse.showLineOfCars();
            goSleep(600); // small pause for this action
            HountedHouse.showLineOfPassengers();
            goSleep(600); // small pause for this action
            msg("there's enough passengers for a Tour.");
            goSleep(600); // small pause for this action

            // A car will transfer Passengers from the HounteHouse line to the carCrew
            if(number_of_passengers_to_load<this.capacity && number_of_passengers_to_load!=0)
                msg(" will load less than its full capacity because there's only "+number_of_passengers_to_load+" passengers who need rides.");
            for (int passengers_on_board = 0; passengers_on_board < number_of_passengers_to_load; ++passengers_on_board) {
                Passenger pi = HountedHouse.passengers_who_need_ride.remove(0);
                this.carCrew.add(pi);
                pi.msg("is allowed to board " + this.getName()+" but will yield to other passengers.");
                pi.carID = this.getName();
                pi.isAllowedToBoard = true;
                }

            // Let the last Passenger to enter to get the car started
            this.carCrew.lastElement().startCar(this.getName());


            HountedHouse.all_cars.remove(0);    // Detach current car from the main queue of cars

            msg("leaving for a tour");          // Simulate a tour by putting this car to sleep a fixed time for all cars.
            goSleep(TOUR_LENGTH);

            msg("tour ended. Wake up all passeners"); // End of Tour. Wake up and let off all passengers
            goSleep(600); // small pause for this action

            // This Car will interrupt the sweet sleep of all the Passenger threads and let them off
            // Will let first the Passenger with the highest priority (as if it is the most panicked for the Tour)
            for(int priority = 10; priority>=1; --priority){
                for (Passenger pi : this.carCrew) {
                    if( (pi.getPriority()==priority) && !(pi.isInterrupted()) ){
                        pi.interrupt();
                        goSleep(600);  // small pause for this action
                    	}
                    }

                }

            Car.this.carCrew.clear();                                   // Empty the crew of this Car



            }
        // Wait for the signal of the last passenger (who will shut down all cars by allowing all Car methods to reach the end of
        // their "run()" method
        while( !HountedHouse.last_passenger_leaving )
            { goSleep(10);}
        msg("shut down by last Passenger (Passenger_"+HountedHouse.ID_of_last_passenger_to_leave+")");
        HountedHouse.all_cars.remove(this);
        HountedHouse.oneCarFinishedOperations();

        }






    public void goSleep(int n){ // Instead of making a Try & Catch very now and then, just wrap it on a Function
        try {  this.sleep(n); }
        catch (InterruptedException e) { e.printStackTrace();  }
        }

    public void msg(String m){
        System.out.println("["+(System.currentTimeMillis()-HountedHouse.time)+"] "+this.getName()+": "+m);
        }

}
