package test;

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

import java.util.ArrayList;
import java.util.List;

public class ScenarioTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws RentalException {
        RentalSystem system = new RentalSystem();

        Car car = new Car("C01", "Toyota", "Corolla", 2022, 60, 5, Transmission.AUTOMATIC);
        Motorcycle bike = new Motorcycle("M01", "Yamaha", "MT-07", 2021, 40, 689, true);
        Truck truck = new Truck("T01", "Volvo", "FH16", 2020, 100, 18);
        system.addVehicle(car);
        system.addVehicle(bike);
        system.addVehicle(truck);
        check(1, "Add car, motorcycle and truck", system.getAllVehicles().size() == 3);

        check(2, "Duplicate vehicle ID rejected",
                throwsError(() -> system.addVehicle(new Car("c01", "Kia", "Rio", 2020, 40))));

        system.registerCustomer(new Customer("CU01", "Ahmad Saleh", "0599123456", "ahmad@mail.com", "LIC123"));
        check(3, "Register a customer", system.findCustomer("CU01") != null);

        Rental carRental = system.rentVehicle("CU01", "C01", 4, true);
        check(4, "Rent available car ($300, car unavailable)",
                carRental.isActive() && !car.isAvailable() && carRental.getTotalCost() == 300);

        check(5, "Rent same car again rejected", throwsError(() -> system.rentVehicle("CU01", "C01", 2)));

        Rental bikeRental = system.rentVehicle("CU01", "M01", 2, true);
        check(6, "Motorcycle with helmet (2 x 40 + 2 x 5 = 90)", bikeRental.getTotalCost() == 90);

        check(8, "Zero / negative days rejected",
                throwsError(() -> system.rentVehicle("CU01", "T01", 0))
                        && throwsError(() -> system.rentVehicle("CU01", "T01", -3)));

        Rental truckRental = system.rentVehicle("CU01", "T01", 3);
        check(7, "Truck 3 days (100 x 3 + 50 = 350)", truckRental.getTotalCost() == 350);

        system.returnVehicle(carRental.getRentalId());
        check(9, "Return active rental", !carRental.isActive() && car.isAvailable());

        check(10, "Return rental twice rejected", throwsError(() -> system.returnVehicle(carRental.getRentalId())));

        check(11, "Customer rental history", system.getRentalHistory("CU01").size() == 3);

        check(12, "Revenue counts completed rentals only", system.getTotalRevenue() == 300);

        system.flagForMaintenance("C01");
        check(13, "Flag available car", car.needsMaintenance() && !system.getAvailableVehicles().contains(car));

        check(14, "Renting flagged car rejected", throwsError(() -> system.rentVehicle("CU01", "C01", 1)));

        system.returnVehicle(truckRental.getRentalId());
        system.flagForMaintenance("T01");
        system.performMaintenance("T01");
        check(15, "Maintenance on idle flagged truck", !truck.needsMaintenance()
                && system.getAvailableVehicles().contains(truck));

        system.flagForMaintenance("M01");
        boolean rejected = throwsError(() -> system.performMaintenance("M01"));
        system.returnVehicle(bikeRental.getRentalId());
        system.performMaintenance("M01");
        check(16, "Maintenance on rented motorcycle rejected until returned",
                rejected && !bike.needsMaintenance());

        List<Maintainable> fleet = new ArrayList<>();
        fleet.add(new Car("X1", "Honda", "Civic", 2023, 55));
        fleet.add(new Motorcycle("X2", "Honda", "CBR", 2023, 35, 600));
        fleet.add(new Truck("X3", "MAN", "TGX", 2019, 120, 25));
        boolean allOk = true;
        for (Maintainable m : fleet) {
            ((Vehicle) m).flagForMaintenance();
            allOk &= m.needsMaintenance();
            m.performMaintenance();
            allOk &= !m.needsMaintenance();
        }
        check(17, "Maintenance through Maintainable references", allOk);

        System.out.println();
        System.out.println("Passed: " + passed + " | Failed: " + failed);
    }

    private interface Action {
        void run() throws Exception;
    }

    private static boolean throwsError(Action action) {
        try {
            action.run();
            return false;
        } catch (Exception e) {
            System.out.println("   (rejected: " + e.getMessage() + ")");
            return true;
        }
    }

    private static void check(int number, String name, boolean ok) {
        System.out.printf("%s  Test %2d: %s%n", ok ? "PASS" : "FAIL", number, name);
        if (ok) {
            passed++;
        } else {
            failed++;
        }
    }
}
