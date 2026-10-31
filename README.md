# Vehicle Rental Management System

Console application in Java for a vehicle rental company (cars, motorcycles and trucks).
It manages vehicles, customers, rentals, returns, revenue and vehicle maintenance.

## Project structure

```
src/
  model/      Vehicle (abstract), Car, Motorcycle, Truck, Maintainable (interface),
              Customer, Rental, RentalStatus, Transmission
  service/    RentalSystem (all business rules), RentalException
  app/        Main, ConsoleMenu, InputHelper (console input/output only)
  test/       ScenarioTest (runs the 17 test scenarios from the assignment)
docs/
  UML_Class_Diagram.png / .pdf
  uml.dot     (Graphviz source of the diagram)
  Report.pdf
  screenshots/  (vehicle creation, customer registration, renting, returns, cost calculation)
```

## Requirements

- JDK 17 or newer (the code uses pattern matching for `instanceof` and switch arrows)

## Compile

From the project root:

**Linux / macOS**
```bash
javac -d out $(find src -name "*.java")
```

**Windows (PowerShell)**
```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

## Run

```bash
java -cp out app.Main
```

To run the automatic test scenarios:

```bash
java -cp out test.ScenarioTest
```

## Menu

```
 1. Add New Vehicle                       11. Display Total Revenue
 2. Display All Vehicles                  12. Flag Vehicle for Maintenance
 3. Search for Vehicle                    13. Display Vehicles Requiring Maintenance
 4. Display Available Vehicles            14. Perform Vehicle Maintenance
 5. Register New Customer                 15. Update Vehicle Daily Price
 6. Display All Customers                 16. Update Customer Phone/Email
 7. Rent a Vehicle                        17. Display Rented Vehicles
 8. Return a Vehicle                      18. Display Active Rentals
 9. Display All Rentals                   19. Display Vehicles Currently Rented by a Customer
10. Display Customer Rental History       20. Search for Customer
                                           0. Exit
```

Options 15-20 cover the extra requirements in the brief (price/contact updates, rented vehicles,
active rentals, a customer's current vehicles and customer search).

## Pricing

| Type       | Cost                                                     |
|------------|----------------------------------------------------------|
| Car        | daily price x days + optional insurance ($15/day)        |
| Motorcycle | daily price x days + optional helmet ($5/day, if the bike has one) |
| Truck      | daily price x days + $50 service fee per rental          |

Each subclass calculates its own cost by overriding `calculateRentalCost`, so there is no
type check anywhere in the cost logic.

## Business rules

- Vehicle, customer and rental IDs are unique (rental IDs are generated: R001, R002, ...).
- A vehicle can only have one active rental; a customer can have many.
- Rental days must be positive.
- Only active rentals can be returned, and a rental cannot be completed twice.
- A vehicle that needs maintenance cannot be rented.
- Maintenance cannot be performed while the vehicle has an active rental.
- Revenue only counts completed rentals.
- Besides manual flagging, a vehicle is also due for maintenance after a number of rentals
  (car: 5, motorcycle: 3 or 2 for 1000cc+, truck: 4 or 2 for 10+ tons).

## Sample data for a quick test

| Vehicle | Type       | Details                         |
|---------|------------|---------------------------------|
| C01     | Car        | Toyota Corolla 2022, $60/day    |
| M01     | Motorcycle | Yamaha MT-07 2021, $40/day, 689cc, helmet |
| T01     | Truck      | Volvo FH16 2020, $100/day, 18 tons |

Customer: `CU01`, Ahmad Saleh, 0599123456, ahmad@mail.com, LIC123

Renting C01 for 4 days with insurance gives $300, and T01 for 3 days gives $350.
