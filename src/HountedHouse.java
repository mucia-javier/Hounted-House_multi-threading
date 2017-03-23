/*
Author: Javier Mucia
Summary: Haunted House Class (Driver class)
Description: Main class that Creates and Starts the Cars and Passengers in the Haunted 
	House. 

*/

import java.util.Random;
import java.util.*;

public class HountedHouse{
    public static long time = System.currentTimeMillis();

    /*
    *       Declare default values for: #_Of_Passengers, #_Of_Cars, Car_Capacity
    */
    public static int number_of_passengers =  11;
    public static int number_of_cars = 3;
    private static int cars_capacity = 4;

    // This variable is very important when loading Cars with Passenger objects. A car will wait untill it loads
    // either its full capacity or the number of passgengers who still need a ride.
    public static volatile int number_of_passengers_who_need_a_ride = 11;
    public static volatile int number_of_cars_that_stopped_operations = 0;

    public static volatile boolean last_passenger_leaving = false;
    public static int ID_of_last_passenger_to_leave;

    public static volatile Vector <Passenger> passengers_who_need_ride = new Vector<Passenger>(); // More like "Passengers on the Line"
    public static volatile Vector <Passenger> passengers_ready_to_leave = new Vector<Passenger>();
    public static Vector <Car> all_cars = new Vector<Car>();



    public static void main(String[] args) {
    
    	// If arguments are given: 
    	//							1st -> #Of Passengers = #Of Passengers Who need rides
    	//							2nd -> #Of Cars 
    	//							3rd -> Capacity of cars (how many Passengers they can hold) 
 
        if (args.length == 3) {
            number_of_passengers = Integer.parseInt(args[0]);
            number_of_cars = Integer.parseInt(args[1]);
            cars_capacity = Integer.parseInt(args[2]);
            number_of_passengers_who_need_a_ride =  Integer.parseInt(args[0]);
            }
        else
            System.out.println("Wrong number of Arguments! (Will use default values)");



        System.out.println("\nThe HOUNTED HOUSE OPENS!!!!\n\tThe time is: "+time+"\n\tPassengers: "
                            +number_of_passengers+"\n\tCars: "+number_of_cars+"\n\tCar's Capacity: "+cars_capacity+"\n\n");

        // Release N_cars and Start them Up in the park
        // Give them sequential Numbers as they are created
        for(int i=1; i<=number_of_cars; ++i) {
            Car ci = new Car(i, cars_capacity);
            ci.start();
            goSleep(600);	// Small pause for this action
        }
        // Let N_passengers enter the Park and Start their journey at the Hounted House
        // Give them sequential numbers as they arrive /(are created)
        for(int i=1; i<=number_of_passengers; ++i) {
            Passenger pi = new Passenger(i);
            pi.start();
            goSleep(600); 	// Small pause for this action
        }






        /*  --------------    WAIT FOR ALL PASSENGERS TO BE READY TO LEAVE AFTER THEIR 3 RIDES   ----------- */
        //                    once ALL passengers complete 3 rides, they will (busy) wait to be allowed to leave
        // 					 The last Passenger to leave will shut down the cars
        while( passengers_ready_to_leave.size()!=number_of_passengers )
            goSleep(10);

        System.out.println();
        msg("\t################ ALL PASSENGERS TOOK THEIR 3 RIDES ###########");
        msg("Number of Passengers Ready to Leave: "+passengers_ready_to_leave.size());
        showLineOfCars();

        for(int i = 1; i<=number_of_passengers; ++i){
            for(Passenger pi: passengers_ready_to_leave)
                if(pi.isAlive() && pi.getID() == i){
                    pi.msg("is alive? -> "+pi.isAlive());
                    pi.isAllowedToLeave = true;
                    try {
                        pi.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        }
                }
            }
        
        // wait for all the cars to shut down and then Close the Hounted House
        while(number_of_cars_that_stopped_operations != number_of_cars ){
            goSleep(10);
            }
        System.out.println("\n");
        msg("Ending Operations and Closing the Hounted House!");
        goSleep(600);
        msg("THE HAUNTED HOUSE IS CLOSED NOW.\n\n");
        }









    // Function to make a random number between the given range
    public static int randomInteger(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
        }
    // See if all the expexted passengers have arrived and lined up ready to take a Tour
    public static synchronized boolean allPassengersArrived(){
        return (passengers_who_need_ride.size() == number_of_passengers);
        }

    public static void goSleep(int n){
        try {  Thread.sleep(n); }
        catch (InterruptedException e) { e.printStackTrace();  }
        }

    public static synchronized boolean allPassengersTookThreeRides(){
        return (passengers_ready_to_leave.size() == number_of_passengers);
        }
    public static synchronized void onePassengerIsDoneRiding(){
        --number_of_passengers_who_need_a_ride;
        }
    public static synchronized void oneCarFinishedOperations() { number_of_cars_that_stopped_operations++; };

    public static synchronized void showLineOfPassengers(){
        String s = "Line of Passengers waiting = { ";
        if(passengers_who_need_ride.isEmpty())
            s+= " }   -- Probably everyone is currently on a Tour or wandering around.";
        else {
            for (Passenger pi : passengers_who_need_ride) {
                s += " [" + pi.getName() + ", " + pi.getPriority() + "] ";
            }
            s += "}";
        }
        msg(s);
        }
    public static synchronized void showLineOfCars(){
        String s = "Queue of cars= { ";
        for(Car ci: all_cars)
            s+= "["+ci.getName()+"]   ";
        s+="}";
        msg(s);
        }

    public static void msg(String m){
        System.out.println("["+(System.currentTimeMillis()-HountedHouse.time)+"] Hounted House: "+m);
        }

    public static synchronized void shutDownCars(int lastPassengerID){
    	ID_of_last_passenger_to_leave = lastPassengerID;
        last_passenger_leaving = true;
    }
}
