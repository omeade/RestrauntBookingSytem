/*
 * Restaurant Booking System: example code to accompany
 *
 * "Practical Object-oriented Design with UML"
 * Mark Priestley
 * McGraw-Hill (2004)
 */

 package booksys.application.domain ;

 import java.sql.Date ;
 import java.sql.Time ;
 import java.util.* ;
 
 public class BookingSystem
 {
   // Attributes:
 
   Date currentDate ;
   Date today ;
   
   // Associations:
 
   Restaurant restaurant = null ;
   Vector currentBookings ;
   Booking selectedBooking ;
 
   // Singleton:
   
   private static BookingSystem uniqueInstance ;
 
   public static BookingSystem getInstance()
   {
     if (uniqueInstance == null) {
       uniqueInstance = new BookingSystem() ;
     }
     return uniqueInstance ;
   }
 
   private BookingSystem()
   {
     today = new Date(Calendar.getInstance().getTimeInMillis()) ;
     restaurant = new Restaurant() ;
   }
 
   // Observer: this is `Subject/ConcreteSubject'
 
   Vector observers = new Vector() ;
 
   public void addObserver(BookingObserver o)
   {
     observers.add(o) ;
   }
   
   public void notifyObservers()
   {
     //Enumeration enumer = observers.elements() ;
     Iterator iter = observers.iterator();
     while (iter.hasNext()) {
       BookingObserver bo = (BookingObserver) iter.next() ;
       bo.update() ;
     }
   }
 
   public boolean observerMessage(String message, boolean confirm)
   {
     BookingObserver bo = (BookingObserver) observers.elementAt(0) ;
     return bo.message(message, confirm) ;
   }
   
   // System messages:
 
   public void display(Date date)
   {
     currentDate = date ;
     currentBookings = restaurant.getBookings(currentDate) ;
     selectedBooking = null ;
     notifyObservers() ;
   }
   
   public void makeReservation(int covers, Date date, Time time, int tno,
                   String name, String phone)
   {
     if (!doubleBooked(time, tno, null) && !overflow(tno, covers)) {
       Booking b = restaurant.makeReservation(covers, date, time, tno, name, phone) ;
       currentBookings.add(b) ;
       notifyObservers() ;
     }
   }
  
   public void makeWalkIn(int covers, Date date, Time time, int tno)
   {
     if (!doubleBooked(time, tno, null) && !overflow(tno, covers)) {
       Booking b = restaurant.makeWalkIn(covers, date, time, tno) ;
       currentBookings.add(b) ;
       notifyObservers() ;
     }
   }
   
   public void selectBooking(int tno, Time time)
   {
     selectedBooking = null ;
     Iterator itr  = currentBookings.iterator() ;
     
     while (itr.hasNext()) {
       Booking b = (Booking) itr.next() ;
       if (b.getTableNumber() == tno) {
     if (b.getTime().before(time)
         && b.getEndTime().after(time)) {
       selectedBooking = b ;
     }
       }
     }
     notifyObservers() ;
   }
 
   public void cancel()
   {
     if (selectedBooking != null) {
       if (observerMessage("Are you sure?", true)) {
     currentBookings.remove(selectedBooking) ;
     restaurant.removeBooking(selectedBooking) ;
     selectedBooking = null ;
     notifyObservers() ;
       }
     }
   }
   
   public void recordArrival(Time time)
   {
     if (selectedBooking != null) {
       if (selectedBooking.getArrivalTime() != null) {
     observerMessage("Arrival already recorded", false) ;
       }
       else {
     selectedBooking.setArrivalTime(time) ;
     restaurant.updateBooking(selectedBooking) ;
     notifyObservers() ;
       }
     }
   }
 
   public void transfer(Time time, int tno)
   {
     if (selectedBooking != null) {
       if (selectedBooking.getTableNumber() != tno) {
     if (!doubleBooked(selectedBooking.getTime(), tno, selectedBooking)
         && !overflow(tno, selectedBooking.getCovers())) {
       selectedBooking.setTable(restaurant.getTable(tno)) ;
       restaurant.updateBooking(selectedBooking) ;
     }
       }
       notifyObservers() ;
     }
   }
   
   // New method for extending dining time
   public void extendDiningTime(int additionalMinutes) {
     if (selectedBooking != null) {
       Time currentEndTime = selectedBooking.getEndTime();
       
       // Calculate the new end time based on the extension
       Time newEndTime = (Time) currentEndTime.clone();
       int totalMinutes = newEndTime.getMinutes() + additionalMinutes;
       int additionalHours = totalMinutes / 60;
       int remainingMinutes = totalMinutes % 60;
       
       newEndTime.setHours(newEndTime.getHours() + additionalHours);
       newEndTime.setMinutes(remainingMinutes);
       
       // Check if extending would create a double booking
       if (timeExtensionConflicts(newEndTime, selectedBooking.getTableNumber(), selectedBooking)) {
         observerMessage("Cannot extend booking due to conflicts with other bookings", false);
       } else {
         // Update the booking with the extended time
         selectedBooking.setExtendedTime(selectedBooking.getExtendedTime() + additionalMinutes);
         restaurant.updateBooking(selectedBooking);
         observerMessage("Booking extended by " + additionalMinutes + " minutes", false);
         notifyObservers();
       }
     } else {
       observerMessage("No booking selected", false);
     }
   }
   
   // Helper method to check if a time extension would create conflicts
   private boolean timeExtensionConflicts(Time newEndTime, int tableNumber, Booking currentBooking) {
     Iterator itr = currentBookings.iterator();
     while (itr.hasNext()) {
       Booking b = (Booking) itr.next();
       // Skip the current booking being modified
       if (b != currentBooking && b.getTableNumber() == tableNumber) {
         // If any booking starts after our current booking's original time
         // and before our proposed new end time, there's a conflict
         if (b.getTime().after(currentBooking.getTime()) && 
             b.getTime().before(newEndTime)) {
           return true;
         }
       }
     }
     return false;
   }
   
   private boolean doubleBooked(Time startTime, int tno, Booking ignore)
   {
     boolean doubleBooked = false ;
 
     Time endTime = (Time) startTime.clone() ;
     endTime.setHours(endTime.getHours() + 2) ;
     
     Iterator itr = currentBookings.iterator() ;
     while (!doubleBooked && itr.hasNext()) {
       Booking b = (Booking) itr.next() ;
       if (b != ignore && b.getTableNumber() == tno
       && startTime.before(b.getEndTime())
       && endTime.after(b.getTime())) {
     doubleBooked = true ;
     observerMessage("Double booking!", false) ;
       }
     }
     return doubleBooked ;
   }
   
   private boolean overflow(int tno, int covers)
   {
     boolean overflow = false ;
     Table t = restaurant.getTable(tno) ;
       
     if (t.getPlaces() < covers) {
       overflow = !observerMessage("Ok to overfill table?", true) ;
     }
     
     return overflow ;
   }
   
   // Other Operations:
 
   public Date getCurrentDate()
   {
     return currentDate ;
   }
   
   public Iterator getBookings()
   {
     return currentBookings.iterator() ;
   }
 
   public Booking getSelectedBooking()
   {
     return selectedBooking ;
   }
 
   public static Vector getTableNumbers()
   {
     return Restaurant.getTableNumbers() ;
   }
 }