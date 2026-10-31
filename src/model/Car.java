package model;

public class Car extends Vehicle implements Maintainable {
    public static final double INSURANCE_PER_DAY = 15.0;
    private static final int SERVICE_INTERVAL = 5;

    private int seats;
    private Transmission transmission;

    public Car(String vehicleId, String brand, String model, int year, double dailyPrice,
               int seats, Transmission transmission) {
        super(vehicleId, brand, model, year, dailyPrice);
        setSeats(seats);
        if (transmission == null) {
            throw new IllegalArgumentException("Transmission type is required.");
        }
        this.transmission = transmission;
    }

    public Car(String vehicleId, String brand, String model, int year, double dailyPrice) {
        this(vehicleId, brand, model, year, dailyPrice, 5, Transmission.AUTOMATIC);
    }

    @Override
    public double calculateRentalCost(int days, boolean withInsurance) {
        double cost = baseCost(days);
        if (withInsurance) {
            cost += INSURANCE_PER_DAY * days;
        }
        return cost;
    }

    @Override
    public String getOptionalService() {
        return String.format("Insurance ($%.0f/day)", INSURANCE_PER_DAY);
    }

    @Override
    public void displayInfo() {
        System.out.println("[Car] " + basicInfo());
        System.out.println("      Seats: " + seats + " | Transmission: " + transmission
                + " | Maintenance: " + maintenanceText(needsMaintenance()));
    }

    @Override
    public String getType() {
        return "Car";
    }

    @Override
    public void performMaintenance() {
        System.out.println("Car " + getVehicleId() + ": oil changed, tires rotated, brakes and lights checked.");
        resetServiceStatus();
    }

    @Override
    public boolean needsMaintenance() {
        return isMaintenanceRequired() || getRentalsSinceService() >= SERVICE_INTERVAL;
    }

    public int getSeats() {
        return seats;
    }

    public final void setSeats(int seats) {
        if (seats < 2 || seats > 9) {
            throw new IllegalArgumentException("A car must have between 2 and 9 seats.");
        }
        this.seats = seats;
    }

    public Transmission getTransmission() {
        return transmission;
    }
}
