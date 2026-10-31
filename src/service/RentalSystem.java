package service;

import model.Customer;
import model.Maintainable;
import model.Rental;
import model.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RentalSystem {
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final ArrayList<Customer> customers = new ArrayList<>();
    private final ArrayList<Rental> rentals = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) throws RentalException {
        if (vehicle == null) {
            throw new RentalException("Vehicle cannot be null.");
        }
        if (vehicleExists(vehicle.getVehicleId())) {
            throw new RentalException("A vehicle with ID " + vehicle.getVehicleId() + " already exists.");
        }
        vehicles.add(vehicle);
    }

    public boolean vehicleExists(String vehicleId) {
        return findVehicle(vehicleId) != null;
    }

    public Vehicle findVehicle(String vehicleId) {
        if (vehicleId == null) {
            return null;
        }
        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equalsIgnoreCase(vehicleId.trim())) {
                return v;
            }
        }
        return null;
    }

    public List<Vehicle> findVehicles(String keyword) {
        List<Vehicle> result = new ArrayList<>();
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        for (Vehicle v : vehicles) {
            if (v.getBrand().toLowerCase().contains(key) || v.getModel().toLowerCase().contains(key)) {
                result.add(v);
            }
        }
        return result;
    }

    public List<Vehicle> findVehicles(double maxDailyPrice) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v.getDailyPrice() <= maxDailyPrice) {
                result.add(v);
            }
        }
        return result;
    }

    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (canBeRented(v)) {
                result.add(v);
            }
        }
        return result;
    }

    public List<Vehicle> getRentedVehicles() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (!v.isAvailable()) {
                result.add(v);
            }
        }
        return result;
    }

    public void updateDailyPrice(String vehicleId, double newPrice) throws RentalException {
        Vehicle v = requireVehicle(vehicleId);
        try {
            v.setDailyPrice(newPrice);
        } catch (IllegalArgumentException e) {
            throw new RentalException(e.getMessage());
        }
    }

    public void registerCustomer(Customer customer) throws RentalException {
        if (customer == null) {
            throw new RentalException("Customer cannot be null.");
        }
        if (customerExists(customer.getCustomerId())) {
            throw new RentalException("A customer with ID " + customer.getCustomerId() + " already exists.");
        }
        for (Customer c : customers) {
            if (c.getLicenseNumber().equals(customer.getLicenseNumber())) {
                throw new RentalException("License number " + customer.getLicenseNumber()
                        + " is already registered to " + c.getFullName() + ".");
            }
        }
        customers.add(customer);
    }

    public boolean customerExists(String customerId) {
        return findCustomer(customerId) != null;
    }

    public Customer findCustomer(String customerId) {
        if (customerId == null) {
            return null;
        }
        for (Customer c : customers) {
            if (c.getCustomerId().equalsIgnoreCase(customerId.trim())) {
                return c;
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public void updateCustomerPhone(String customerId, String phone) throws RentalException {
        Customer c = requireCustomer(customerId);
        try {
            c.setPhone(phone);
        } catch (IllegalArgumentException e) {
            throw new RentalException(e.getMessage());
        }
    }

    public void updateCustomerEmail(String customerId, String email) throws RentalException {
        Customer c = requireCustomer(customerId);
        try {
            c.setEmail(email);
        } catch (IllegalArgumentException e) {
            throw new RentalException(e.getMessage());
        }
    }

    public List<Vehicle> getVehiclesRentedBy(String customerId) throws RentalException {
        return requireCustomer(customerId).getCurrentVehicles();
    }

    public Rental rentVehicle(String customerId, String vehicleId, int days, boolean optionalService)
            throws RentalException {
        if (days <= 0) {
            throw new RentalException("Rental days must be a positive number.");
        }
        Customer customer = requireCustomer(customerId);
        Vehicle vehicle = checkRentable(vehicleId);

        Rental rental;
        try {
            rental = new Rental(customer, vehicle, days, optionalService);
        } catch (IllegalArgumentException e) {
            throw new RentalException(e.getMessage());
        }
        rentals.add(rental);
        customer.addRental(rental);
        vehicle.markRented();
        return rental;
    }

    public Vehicle checkRentable(String vehicleId) throws RentalException {
        Vehicle vehicle = requireVehicle(vehicleId);
        Rental existing = findActiveRental(vehicle);
        if (!vehicle.isAvailable() || existing != null) {
            throw new RentalException(vehicle + " is already rented"
                    + (existing != null ? " (rental " + existing.getRentalId() + ")." : "."));
        }
        if (vehicle instanceof Maintainable m && m.needsMaintenance()) {
            throw new RentalException(vehicle + " needs maintenance and cannot be rented right now.");
        }
        return vehicle;
    }

    public Rental rentVehicle(String customerId, String vehicleId, int days) throws RentalException {
        return rentVehicle(customerId, vehicleId, days, false);
    }

    public Rental returnVehicle(String rentalId) throws RentalException {
        Rental rental = findRental(rentalId);
        if (rental == null) {
            throw new RentalException("No rental found with ID " + rentalId + ".");
        }
        if (!rental.isActive()) {
            throw new RentalException("Rental " + rental.getRentalId() + " is already completed.");
        }
        rental.complete();
        rental.getVehicle().markReturned();
        return rental;
    }

    public Rental findRental(String rentalId) {
        if (rentalId == null) {
            return null;
        }
        for (Rental r : rentals) {
            if (r.getRentalId().equalsIgnoreCase(rentalId.trim())) {
                return r;
            }
        }
        return null;
    }

    public Rental findActiveRental(Vehicle vehicle) {
        for (Rental r : rentals) {
            if (r.isActive() && r.getVehicle() == vehicle) {
                return r;
            }
        }
        return null;
    }

    public List<Rental> getAllRentals() {
        return Collections.unmodifiableList(rentals);
    }

    public List<Rental> getActiveRentals() {
        List<Rental> result = new ArrayList<>();
        for (Rental r : rentals) {
            if (r.isActive()) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Rental> getRentalHistory(String customerId) throws RentalException {
        return requireCustomer(customerId).getRentals();
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Rental r : rentals) {
            if (!r.isActive()) {
                total += r.getTotalCost();
            }
        }
        return total;
    }

    public int getCompletedRentalCount() {
        return rentals.size() - getActiveRentals().size();
    }

    public void flagForMaintenance(String vehicleId) throws RentalException {
        Vehicle vehicle = requireVehicle(vehicleId);
        if (!(vehicle instanceof Maintainable)) {
            throw new RentalException(vehicle + " does not support maintenance.");
        }
        if (vehicle.isMaintenanceRequired()) {
            throw new RentalException(vehicle + " is already flagged for maintenance.");
        }
        vehicle.flagForMaintenance();
    }

    public List<Maintainable> getVehiclesNeedingMaintenance() {
        List<Maintainable> result = new ArrayList<>();
        for (Maintainable m : getMaintainableVehicles()) {
            if (m.needsMaintenance()) {
                result.add(m);
            }
        }
        return result;
    }

    public void performMaintenance(String vehicleId) throws RentalException {
        Vehicle vehicle = requireVehicle(vehicleId);
        if (!(vehicle instanceof Maintainable maintainable)) {
            throw new RentalException(vehicle + " does not support maintenance.");
        }
        Rental active = findActiveRental(vehicle);
        if (active != null) {
            throw new RentalException(vehicle + " is currently rented (rental " + active.getRentalId()
                    + "). Maintenance can only be done after it is returned.");
        }
        if (!maintainable.needsMaintenance()) {
            throw new RentalException(vehicle + " does not need maintenance at the moment.");
        }
        maintainable.performMaintenance();
    }

    private List<Maintainable> getMaintainableVehicles() {
        List<Maintainable> list = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v instanceof Maintainable m) {
                list.add(m);
            }
        }
        return list;
    }

    private boolean canBeRented(Vehicle v) {
        if (!v.isAvailable()) {
            return false;
        }
        return !(v instanceof Maintainable m) || !m.needsMaintenance();
    }

    private Vehicle requireVehicle(String vehicleId) throws RentalException {
        Vehicle v = findVehicle(vehicleId);
        if (v == null) {
            throw new RentalException("No vehicle found with ID " + vehicleId + ".");
        }
        return v;
    }

    private Customer requireCustomer(String customerId) throws RentalException {
        Customer c = findCustomer(customerId);
        if (c == null) {
            throw new RentalException("No registered customer with ID " + customerId + ".");
        }
        return c;
    }
}
