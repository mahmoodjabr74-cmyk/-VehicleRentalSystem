package model;

public class Truck extends Vehicle implements Maintainable {
    public static final double SERVICE_FEE = 50.0;
    private static final int SERVICE_INTERVAL = 4;
    private static final double HEAVY_LOAD_TONS = 10.0;

    private double maxLoadTons;

    public Truck(String vehicleId, String brand, String model, int year, double dailyPrice, double maxLoadTons) {
        super(vehicleId, brand, model, year, dailyPrice);
        if (maxLoadTons <= 0 || maxLoadTons > 60) {
            throw new IllegalArgumentException("Load capacity must be between 0 and 60 tons.");
        }
        this.maxLoadTons = maxLoadTons;
    }

    @Override
    public double calculateRentalCost(int days, boolean withOptionalService) {
        return baseCost(days) + SERVICE_FEE;
    }

    @Override
    public void displayInfo() {
        System.out.println("[Truck] " + basicInfo());
        System.out.printf("      Max load: %.1f tons | Service fee: $%.0f per rental | Maintenance: %s%n",
                maxLoadTons, SERVICE_FEE, maintenanceText(needsMaintenance()));
    }

    @Override
    public String getType() {
        return "Truck";
    }

    @Override
    public void performMaintenance() {
        System.out.print("Truck " + getVehicleId() + ": brakes, suspension and hydraulics inspected");
        if (maxLoadTons >= HEAVY_LOAD_TONS) {
            System.out.print(", axle load test passed");
        }
        System.out.println(".");
        resetServiceStatus();
    }

    @Override
    public boolean needsMaintenance() {
        int interval = maxLoadTons >= HEAVY_LOAD_TONS ? 2 : SERVICE_INTERVAL;
        return isMaintenanceRequired() || getRentalsSinceService() >= interval;
    }

    public double getMaxLoadTons() {
        return maxLoadTons;
    }
}
