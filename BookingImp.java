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
 
 public abstract class BookingImp implements Booking
 {
   protected int   covers ;
   protected Date  date ;
   protected Time  time ;
   protected Table table ;
   protected int   extendedTimeMinutes = 0; // New attribute for time extension
   
   public BookingImp(int c, Date d, Time t, Table tab) {
     covers    = c ;
     date      = d ;
     time = t ;
     table     = tab ;
   }
 
   public Time getArrivalTime() {
     return null ;
   }
 
   public int getCovers() {
     return covers;
   }
   
   public Date getDate() {
     return date;
   }
 
   // Modified to account for extended time
   public Time getEndTime() {
     Time endTime = (Time) time.clone() ;
     endTime.setHours(endTime.getHours() + 2) ;
     
     // Add extended time if any
     if (extendedTimeMinutes > 0) {
       int totalMinutes = endTime.getMinutes() + extendedTimeMinutes;
       int additionalHours = totalMinutes / 60;
       int remainingMinutes = totalMinutes % 60;
       
       endTime.setHours(endTime.getHours() + additionalHours);
       endTime.setMinutes(remainingMinutes);
     }
     
     return endTime ;
   }
   
   public Time getTime() {
     return time;
   }
   
   public Table getTable() {
     return table;
   }
 
   public int getTableNumber() {
     return table.getNumber() ;
   }
   
   public void setArrivalTime(Time t) { }
 
   public void setCovers(int c) {
     covers = c ;
   }
 
   public void setDate(Date d) {
     date = d ;
   }
 
   public void setTime(Time t) {
     time = t ;
   }
   
   public void setTable(Table t) {
     table = t ;
   }
   
   // Implementation of new methods
   public int getExtendedTime() {
     return extendedTimeMinutes;
   }
   
   public void setExtendedTime(int minutes) {
     extendedTimeMinutes = minutes;
   }
 }