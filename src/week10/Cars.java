package week10;
// name: Kirsten Pleskot


import java.util.Arrays;

public class Cars {
    public static void main(String[] args) {
        Vehicle gasolineCar = new GasolineCar(50, 5);
        Vehicle hybridCar = new HybridCar(40, 4, 20, 10);

        System.out.println("Gasoline Car Range: " + gasolineCar.getRemainingRange() + " km");
        gasolineCar.drive(200);
        System.out.println("Gasoline Car Range after driving 200 km: " + gasolineCar.getRemainingRange() + " km");

        System.out.println("Hybrid Car Range: " + hybridCar.getRemainingRange() + " km");
        hybridCar.drive(300);
        System.out.println("Hybrid Car Range after driving 300 km: " + hybridCar.getRemainingRange() + " km");
        Integer a = 300;
        Integer b = 300;
        System.out.println(a == b);

    }

}


interface Vehicle {
    int getRemainingRange();
    void drive(int distance);
}

class GasolineCar implements Vehicle {
    private int fuelTankCapacity;
    private final int fuelConsumptionPer100km;

    public GasolineCar(int fuelTankCapacity, int fuelConsumptionPer100km) {
        this.fuelTankCapacity = fuelTankCapacity;
        this.fuelConsumptionPer100km = fuelConsumptionPer100km;
    }

    @Override
    public int getRemainingRange() {
        return (fuelTankCapacity * 100) / fuelConsumptionPer100km;
    }

    @Override
    public void drive(int distance) {
        int fuelNeeded = (distance * fuelConsumptionPer100km) / 100;
        if (fuelNeeded > fuelTankCapacity) {
            throw new IllegalArgumentException("Not enough fuel to drive the requested distance.");
        }
        fuelTankCapacity -= fuelNeeded;
    }
}

class HybridCar implements Vehicle {
    private int fuelTankCapacity;
    private final int fuelConsumptionPer100km;
    private int batteryCapacity;
    private final int electricConsumptionPer100km;

    public HybridCar(int fuelTankCapacity, int fuelConsumptionPer100km, int batteryCapacity, int electricConsumptionPer100km) {
        this.fuelTankCapacity = fuelTankCapacity;
        this.fuelConsumptionPer100km = fuelConsumptionPer100km;
        this.batteryCapacity = batteryCapacity;
        this.electricConsumptionPer100km = electricConsumptionPer100km;
    }

    @Override
    public int getRemainingRange() {
        int rangeFromFuel = (fuelTankCapacity * 100) / fuelConsumptionPer100km;
        int rangeFromElectric = (batteryCapacity * 100) / electricConsumptionPer100km;
        return rangeFromFuel + rangeFromElectric;
    }

    @Override
    public void drive(int distance) {
        int electricRange = (batteryCapacity * 100) / electricConsumptionPer100km;
        int fuelRange = (fuelTankCapacity * 100) / fuelConsumptionPer100km;
        if (distance > electricRange + fuelRange) {
            throw new IllegalArgumentException("Not enough resources to drive the requested distance.");
        }
        if (distance <= electricRange) {
            int electricUsed = (distance * electricConsumptionPer100km) / 100;
            batteryCapacity -= electricUsed;
        } else {
            batteryCapacity = 0;
            int remainingDistance = distance - electricRange;
            int fuelUsed = (remainingDistance * fuelConsumptionPer100km) / 100;
            fuelTankCapacity -= fuelUsed;
        }
    }
}