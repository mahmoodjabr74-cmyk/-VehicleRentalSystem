package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {
    private static final String PHONE_PATTERN = "\\+?[0-9]{7,15}";
    private static final String EMAIL_PATTERN = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    private final String customerId;
    private String fullName;
    private String phone;
    private String email;
    private final String licenseNumber;
    private final ArrayList<Rental> rentals = new ArrayList<>();

    public Customer(String customerId, String fullName, String phone, String email, String licenseNumber) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("Driving license number cannot be empty.");
        }
        this.customerId = customerId.trim().toUpperCase();
        this.fullName = fullName.trim();
        this.licenseNumber = licenseNumber.trim().toUpperCase();
        setPhone(phone);
        if (email == null) {
            this.email = "";
        } else {
            setEmail(email);
        }
    }

    public Customer(String customerId, String fullName, String phone, String licenseNumber) {
        this(customerId, fullName, phone, null, licenseNumber);
    }

    public void addRental(Rental rental) {
        if (rental != null && rental.getCustomer() == this) {
            rentals.add(rental);
        }
    }

    public List<Rental> getRentals() {
        return Collections.unmodifiableList(rentals);
    }

    public List<Vehicle> getCurrentVehicles() {
        List<Vehicle> current = new ArrayList<>();
        for (Rental r : rentals) {
            if (r.isActive()) {
                current.add(r.getVehicle());
            }
        }
        return current;
    }

    public void displayInfo() {
        System.out.printf("ID: %s | %s | Phone: %s | Email: %s | License: %s | Active rentals: %d%n",
                customerId, fullName, phone, email.isEmpty() ? "-" : email, licenseNumber,
                getCurrentVehicles().size());
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        String cleaned = phone.replaceAll("[\\s-]", "");
        if (!cleaned.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException("Invalid phone number (7-15 digits expected).");
        }
        this.phone = cleaned;
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        if (email == null || !email.trim().matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.email = email.trim().toLowerCase();
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    @Override
    public String toString() {
        return fullName + " (" + customerId + ")";
    }
}
