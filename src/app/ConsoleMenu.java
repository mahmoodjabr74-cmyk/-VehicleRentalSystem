package app;

import model.Car;
import model.Customer;
import model.Maintainable;
import model.Motorcycle;
import model.Rental;
import model.Transmission;
import model.Truck;
import model.Vehicle;
import service.RentalException;
import service.RentalSystem;

import java.util.List;

public class ConsoleMenu {
    private final RentalSystem system;
    private final InputHelper input;

    public ConsoleMenu(RentalSystem system, InputHelper input) {
        this.system = system;
        this.input = input;
    }

    public void run() {
        System.out.println("=== Vehicle Rental Management System ===");
        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = input.readInt("Enter your choice: ");
                System.out.println();
                running = handleChoice(choice);
            } catch (RentalException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid data: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Input closed. Exiting.");
                running = false;
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------------- MAIN MENU ----------------");
        System.out.println(" 1. Add New Vehicle");
        System.out.println(" 2. Display All Vehicles");
        System.out.println(" 3. Search for Vehicle");
        System.out.println(" 4. Display Available Vehicles");
        System.out.println(" 5. Register New Customer");
        System.out.println(" 6. Display All Customers");
        System.out.println(" 7. Rent a Vehicle");
        System.out.println(" 8. Return a Vehicle");
        System.out.println(" 9. Display All Rentals");
        System.out.println("10. Display Customer Rental History");
        System.out.println("11. Display Total Revenue");
        System.out.println("12. Flag Vehicle for Maintenance");
        System.out.println("13. Display Vehicles Requiring Maintenance");
        System.out.println("14. Perform Vehicle Maintenance");
        System.out.println("15. Update Vehicle Daily Price");
        System.out.println("16. Update Customer Phone/Email");
        System.out.println("17. Display Rented Vehicles");
        System.out.println("18. Display Active Rentals");
        System.out.println("19. Display Vehicles Currently Rented by a Customer");
        System.out.println("20. Search for Customer");
        System.out.println(" 0. Exit");
        System.out.println("-------------------------------------------");
    }

    private boolean handleChoice(int choice) throws RentalException {
        switch (choice) {
            case 1 -> addVehicle();
            case 2 -> showVehicles("All vehicles", system.getAllVehicles());
            case 3 -> searchVehicle();
            case 4 -> showVehicles("Available vehicles", system.getAvailableVehicles());
            case 5 -> registerCustomer();
            case 6 -> showCustomers();
            case 7 -> rentVehicle();
            case 8 -> returnVehicle();
            case 9 -> showRentals("All rentals", system.getAllRentals());
            case 10 -> showCustomerHistory();
            case 11 -> showRevenue();
            case 12 -> flagMaintenance();
            case 13 -> showMaintenanceList();
            case 14 -> performMaintenance();
            case 15 -> updatePrice();
            case 16 -> updateCustomerContact();
            case 17 -> showVehicles("Rented vehicles", system.getRentedVehicles());
            case 18 -> showRentals("Active rentals", system.getActiveRentals());
            case 19 -> showCustomerVehicles();
            case 20 -> searchCustomer();
            case 0 -> {
                System.out.println("Goodbye!");
                return false;
            }
            default -> System.out.println("Invalid choice, please pick a number from the menu.");
        }
        return true;
    }

    private void addVehicle() throws RentalException {
        System.out.println("Vehicle type: 1) Car  2) Motorcycle  3) Truck");
        int type = input.readInt("Choose type: ", 1, 3);

        String id = input.readText("Vehicle ID: ");
        if (system.vehicleExists(id)) {
            throw new RentalException("A vehicle with ID " + id.toUpperCase() + " already exists.");
        }
        String brand = input.readText("Brand: ");
        String model = input.readText("Model: ");
        int year = input.readInt("Manufacturing year: ");
        double price = input.readDouble("Daily rental price ($): ");

        Vehicle vehicle;
        if (type == 1) {
            if (input.readYesNo("Use default setup (5 seats, automatic)?")) {
                vehicle = new Car(id, brand, model, year, price);
            } else {
                int seats = input.readInt("Number of seats: ");
                int t = input.readInt("Transmission 1) Manual 2) Automatic: ", 1, 2);
                Transmission transmission = t == 1 ? Transmission.MANUAL : Transmission.AUTOMATIC;
                vehicle = new Car(id, brand, model, year, price, seats, transmission);
            }
        } else if (type == 2) {
            int cc = input.readInt("Engine capacity (CC): ");
            boolean helmet = input.readYesNo("Helmet included?");
            vehicle = new Motorcycle(id, brand, model, year, price, cc, helmet);
        } else {
            double load = input.readDouble("Maximum load capacity (tons): ");
            vehicle = new Truck(id, brand, model, year, price, load);
        }

        system.addVehicle(vehicle);
        System.out.println("Vehicle added successfully:");
        vehicle.displayInfo();
    }

    private void searchVehicle() {
        System.out.println("Search by: 1) Vehicle ID  2) Brand/Model  3) Max daily price");
        int option = input.readInt("Choose option: ", 1, 3);
        if (option == 1) {
            Vehicle v = system.findVehicle(input.readText("Vehicle ID: "));
            if (v == null) {
                System.out.println("No vehicle found with that ID.");
            } else {
                v.displayInfo();
            }
        } else if (option == 2) {
            showVehicles("Search results", system.findVehicles(input.readText("Brand or model: ")));
        } else {
            showVehicles("Search results", system.findVehicles(input.readDouble("Max daily price ($): ")));
        }
    }

    private void registerCustomer() throws RentalException {
        String id = input.readText("Customer ID: ");
        if (system.customerExists(id)) {
            throw new RentalException("A customer with ID " + id.toUpperCase() + " already exists.");
        }
        String name = input.readText("Full name: ");
        String phone = input.readText("Phone number: ");
        String email = input.readLine("Email address (leave empty to skip): ");
        String license = input.readText("Driving license number: ");

        Customer customer = email.isEmpty()
                ? new Customer(id, name, phone, license)
                : new Customer(id, name, phone, email, license);
        system.registerCustomer(customer);
        System.out.println("Customer registered successfully:");
        customer.displayInfo();
    }

    private void searchCustomer() {
        Customer c = system.findCustomer(input.readText("Customer ID: "));
        if (c == null) {
            System.out.println("No customer found with that ID.");
        } else {
            c.displayInfo();
        }
    }

    private void rentVehicle() throws RentalException {
        String customerId = input.readText("Customer ID: ");
        if (!system.customerExists(customerId)) {
            throw new RentalException("No registered customer with ID " + customerId + ".");
        }
        showVehicles("Available vehicles", system.getAvailableVehicles());
        String vehicleId = input.readText("Vehicle ID to rent: ");
        Vehicle vehicle = system.checkRentable(vehicleId);
        int days = input.readInt("Number of rental days: ");

        boolean extra = false;
        String service = vehicle.getOptionalService();
        if (service != null) {
            extra = input.readYesNo("Add " + service + "?");
        }

        Rental rental = system.rentVehicle(customerId, vehicleId, days, extra);
        System.out.println("Rental created successfully!");
        System.out.println(rental);
        System.out.printf("Total cost: $%.2f%n", rental.getTotalCost());
    }

    private void returnVehicle() throws RentalException {
        List<Rental> active = system.getActiveRentals();
        if (active.isEmpty()) {
            System.out.println("There are no active rentals.");
            return;
        }
        showRentals("Active rentals", active);
        String rentalId = input.readText("Rental ID to return: ");
        Rental rental = system.returnVehicle(rentalId);
        System.out.println("Vehicle returned. Rental " + rental.getRentalId() + " is now completed.");
        System.out.printf("Amount charged: $%.2f%n", rental.getTotalCost());
        if (rental.getVehicle() instanceof Maintainable m && m.needsMaintenance()) {
            System.out.println("Note: " + rental.getVehicle() + " is now due for maintenance.");
        }
    }

    private void showCustomers() {
        List<Customer> customers = system.getAllCustomers();
        System.out.println("--- Customers (" + customers.size() + ") ---");
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
        }
        for (Customer c : customers) {
            c.displayInfo();
        }
    }

    private void showCustomerHistory() throws RentalException {
        String id = input.readText("Customer ID: ");
        List<Rental> history = system.getRentalHistory(id);
        Customer customer = system.findCustomer(id);
        showRentals("Rental history for " + customer, history);
        double spent = 0;
        for (Rental r : history) {
            spent += r.getTotalCost();
        }
        System.out.printf("Total rentals: %d | Total amount: $%.2f%n", history.size(), spent);
    }

    private void showCustomerVehicles() throws RentalException {
        String id = input.readText("Customer ID: ");
        showVehicles("Vehicles currently rented by " + system.findCustomer(id), system.getVehiclesRentedBy(id));
    }

    private void showRevenue() {
        System.out.printf("Total revenue from %d completed rental(s): $%.2f%n",
                system.getCompletedRentalCount(), system.getTotalRevenue());
        int active = system.getActiveRentals().size();
        if (active > 0) {
            System.out.println("(" + active + " active rental(s) are not counted until they are returned)");
        }
    }

    private void flagMaintenance() throws RentalException {
        String id = input.readText("Vehicle ID to flag: ");
        system.flagForMaintenance(id);
        Vehicle v = system.findVehicle(id);
        System.out.println(v + " has been flagged for maintenance and cannot be rented until serviced.");
    }

    private void showMaintenanceList() {
        List<Maintainable> list = system.getVehiclesNeedingMaintenance();
        System.out.println("--- Vehicles requiring maintenance (" + list.size() + ") ---");
        if (list.isEmpty()) {
            System.out.println("All vehicles are in good condition.");
        }
        for (Maintainable m : list) {
            System.out.println(m);
        }
    }

    private void performMaintenance() throws RentalException {
        String id = input.readText("Vehicle ID: ");
        system.performMaintenance(id);
        System.out.println("Maintenance completed. " + system.findVehicle(id) + " is back in service.");
    }

    private void updatePrice() throws RentalException {
        String id = input.readText("Vehicle ID: ");
        Vehicle v = system.findVehicle(id);
        if (v == null) {
            throw new RentalException("No vehicle found with ID " + id + ".");
        }
        System.out.printf("Current price: $%.2f/day%n", v.getDailyPrice());
        system.updateDailyPrice(id, input.readDouble("New daily price ($): "));
        System.out.println("Price updated.");
        v.displayInfo();
    }

    private void updateCustomerContact() throws RentalException {
        String id = input.readText("Customer ID: ");
        if (!system.customerExists(id)) {
            throw new RentalException("No registered customer with ID " + id + ".");
        }
        int option = input.readInt("Update 1) Phone  2) Email: ", 1, 2);
        if (option == 1) {
            system.updateCustomerPhone(id, input.readText("New phone number: "));
        } else {
            system.updateCustomerEmail(id, input.readText("New email address: "));
        }
        System.out.println("Customer updated.");
        system.findCustomer(id).displayInfo();
    }

    private void showVehicles(String title, List<Vehicle> list) {
        System.out.println("--- " + title + " (" + list.size() + ") ---");
        if (list.isEmpty()) {
            System.out.println("No vehicles to show.");
        }
        for (Vehicle v : list) {
            v.displayInfo();
        }
    }

    private void showRentals(String title, List<Rental> list) {
        System.out.println("--- " + title + " (" + list.size() + ") ---");
        if (list.isEmpty()) {
            System.out.println("No rentals to show.");
        }
        for (Rental r : list) {
            System.out.println(r);
        }
    }
}
