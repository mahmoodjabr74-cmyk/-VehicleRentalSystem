package model;

public class Rental {
    private static int nextNumber = 1;

    private final String rentalId;
    private final Customer customer;
    private final Vehicle vehicle;
    private final int days;
    private final boolean optionalService;
    private final double totalCost;
    private RentalStatus status;

    public Rental(Customer customer, Vehicle vehicle, int days, boolean optionalService) {
        if (customer == null || vehicle == null) {
            throw new IllegalArgumentException("A rental needs both a customer and a vehicle.");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be a positive number.");
        }
        this.totalCost = vehicle.calculateRentalCost(days, optionalService);
        this.customer = customer;
        this.vehicle = vehicle;
        this.days = days;
        this.optionalService = optionalService;
        this.status = RentalStatus.ACTIVE;
        this.rentalId = String.format("R%03d", nextNumber++);
    }

    public Rental(Customer customer, Vehicle vehicle, int days) {
        this(customer, vehicle, days, false);
    }

    public void complete() {
        if (status == RentalStatus.COMPLETED) {
            throw new IllegalStateException("Rental " + rentalId + " has already been completed.");
        }
        status = RentalStatus.COMPLETED;
    }

    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    public String getRentalId() {
        return rentalId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getDays() {
        return days;
    }

    public boolean hasOptionalService() {
        return optionalService;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public RentalStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        String extra = optionalService ? " + " + vehicle.getOptionalService() : "";
        return String.format("%s | %-22s | %-32s | %2d day(s)%s | $%.2f | %s",
                rentalId, customer, vehicle, days, extra, totalCost, status);
    }
}
