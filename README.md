College Project:
Restaurant Booking System
A Java-based restaurant reservation management system demonstrating object-oriented design patterns.
Features

Reservations & Walk-ins: Handle both advance bookings and walk-in customers
Table Management: Assign tables and prevent double bookings
Extended Dining: Allow customers to extend their dining time
Arrival Tracking: Record when customers actually arrive
Real-time Updates: UI automatically updates when bookings change

Key Classes

BookingSystem: Main controller (Singleton pattern)
Booking: Interface for reservations and walk-ins
Reservation: Bookings with customer details
WalkIn: Simple bookings without customer info
Table: Restaurant table with capacity
Customer: Customer contact information

Architecture
Uses Observer pattern for UI updates, Singleton for system management, and prevents booking conflicts automatically. Default booking duration is 2 hours, extendable based on availability.
