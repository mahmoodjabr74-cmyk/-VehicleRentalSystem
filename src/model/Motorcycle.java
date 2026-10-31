package model;

public class Motorcycle extends Vehicle implements Maintainable {
    public static final double HELMET_PER_DAY = 5.0;
    private static final int SERVICE_INTERVAL = 3;
    private static final int BIG_BIKE_CC = 1000;

    private int engineCC;
    private boolean helmetIncluded;

    public Motorcycle(String vehicleId, String brand, String model, int year, double dailyPrice,
                      int engineCC, boolean helmetIncluded) {
        super(vehicleId, brand, model, year, dailyPrice);
        if (engineCC < 50 || engineCC > 2500) {
            throw new IllegalArgumentException("Engine capacity must be between 50 and 2500 CC.");
        }
        this.engineCC = engineCC;
        this.helmetIncluded = helmetIncluded;
    }

    public Motorcycle(String vehicleId, String brand, String model, int year, double dailyPrice, int engineCC) {
        this(vehicleId, brand, model, year, dailyPrice, engineCC, true);
    }

    @Override
    public double calculateRentalCost(int days, boolean withHelmet) {
        double cost = baseCost(days);
        if (withHelmet) {
            if (!helmetIncluded) {
                throw new IllegalArgumentException("This motorcycle does not come with a helmet.");
            }
            cost += HELMET_PER_DAY * days;
        }
        return cost;
    }

    @Override
    public String getOptionalService() {
        return helmetIncluded ? String.format("Helmet ($%.0f/day)", HELMET_PER_DAY) : null;
    }

    @Override
    public void displayInfo() {
        System.out.println("[Motorcycle] " + basicInfo());
        System.out.println("      Engine: " + engineCC + "cc | Helmet: " + (helmetIncluded ? "Yes" : "No")
                + " | Maintenance: " + maintenanceText(needsMaintenance()));
    }

    @Override
    public String getType() {
        return "Motorcycle";
    }

    @Override
    public void performMaintenance() {
        System.out.print("Motorcycle " + getVehicleId() + ": chain cleaned and lubricated, tire pressure set");
        if (helmetIncluded) {
            System.out.print(", helmet sanitized");
        }
        System.out.println(".");
        resetServiceStatus();
    }

    @Override
    public boolean needsMaintenance() {
        int interval = engineCC >= BIG_BIKE_CC ? SERVICE_INTERVAL - 1 : SERVICE_INTERVAL;
        return isMaintenanceRequired() || getRentalsSinceService() >= interval;
    }

    public int getEngineCC() {
        return engineCC;
    }

    public boolean isHelmetIncluded() {
        return helmetIncluded;
    }
}
