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
 
 public class WalkIn extends BookingImp
 {
   public WalkIn(int c, Date d, Time t, Table tab)
   {
     super(c, d, t, tab) ;
   }
 
   public String getDetails() {
     StringBuffer details = new StringBuffer(64);
     details.append("Walk-in (");
     details.append(covers);
     details.append(")");
     
     // Add extended time information if applicable
     if (extendedTimeMinutes > 0) {
       details.append(" +");
       details.append(extendedTimeMinutes);
       details.append(" mins");
     }
     return details.toString();
   }
 }