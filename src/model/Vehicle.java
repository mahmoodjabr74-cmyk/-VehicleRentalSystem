package model;

import java.time.Year;

public abstract class Vehicle {
    public static final int MIN_YEAR = 1990;

    private final String vehicleId;
    private String brand;
    private String model;
    private int year;
    private double dailyPrice;
    private boolean available;
    private boolean maintenanceRequired;
    private int rentalsSinceService;

    public Vehicle(String vehicleId, String brand, String model, int year, double dailyPrice) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("Vehicle ID cannot be empty.");
        }
        int maxYear = Year.now().getValue() + 1;
        if (year < MIN_YEAR || year > maxYear) {
            throw new IllegalArgumentException("Year must be between " + MIN_YEAR + " and " + maxYear + ".");
        }
        this.vehicleId = vehicleId.trim().toUpperCase();
        this.brand = requireText(brand, "Brand");
        this.model = requireText(model, "Model");
        this.year = year;
        setDailyPrice(dailyPrice);
        this.available = true;
        this.maintenanceRequired = false;
        this.rentalsSinceService = 0;
    }

    public abstract double calculateRentalCost(int days, boolean withOptionalService);

    public abstract void displayInfo();

    public abstract String getType();

    public double calculateRentalCost(int days) {
        return calculateRentalCost(days, false);
    }

    public String getOptionalService() {
        return null;
    }

    protected double baseCost(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be a positive number.");
        }
        return dailyPrice * days;
    }

    protected String basicInfo() {
        return String.format("ID: %s | %s %s (%d) | $%.2f/day | %s",
                vehicleId, brand, model, year, dailyPrice, available ? "Available" : "Rented");
    }

    protected String maintenanceText(boolean needsService) {
        return needsService ? "Required" : "OK (" + rentalsSinceService + " rentals since last service)";
    }

    public void markRented() {
        this.available = false;
    }

    public void markReturned() {
        this.available = true;
        this.rentalsSinceService++;
    }

    public void flagForMaintenance() {
        this.maintenanceRequired = true;
    }

    protected void resetServiceStatus() {
        this.maintenanceRequired = false;
        this.rentalsSinceService = 0;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public final void setDailyPrice(double dailyPrice) {
        if (dailyPrice <= 0) {
            throw new IllegalArgumentException("Daily price must be greater than 0.");
        }
        this.dailyPrice = dailyPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isMaintenanceRequired() {
        return maintenanceRequired;
    }

    public int getRentalsSinceService() {
        return rentalsSinceService;
    }

    @Override
    public String toString() {
        return getType() + " " + vehicleId + " (" + brand + " " + model + ")";
    }
}
