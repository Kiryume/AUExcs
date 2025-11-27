package week13;
// name: Kirsten Pleskot

/*
Exception:
Exception is a control flow mechanism to divert the normal execution of a program when an unexpected situation arises.


*/

//  NullPointerException  -- Unchecked
//  IOException -- Checked
//  FileNotFoundException -- Checked
//  IllegalArgumentException -- Unchecked
//  ArrayIndexOutOfBoundsException -- Unchecked
//  NumberFormatException -- Unchecked
//  ConcurrentModificationException -- Unchecked
//  InterruptedException -- Checked


public class Gears {
    public static void main(String[] args) {
        Gears car = new Gears();
        try {
            car.changeGear(2);
            System.out.printf("Current gear: %d%n", car.getGear());
            car.changeGear(4);
        } catch (IllegalGearException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        car.changeGear(1);
        System.out.printf("Current gear: %d%n", car.getGear());
        car.changeGear(-1);
        System.out.printf("Current gear: %d%n", car.getGear());
        try {
            car.changeGear(2);
        } catch (IllegalGearException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }

    int gear = 1;
    public void changeGear(int newGear) {
        if (this.gear == newGear) {
            return;
        } else if (newGear == 0) {
            throw new IllegalArgumentException("Gear cannot be 0");
        }
        newGear = (newGear == -1) ? 0 : newGear;
        if ((newGear < 0 || newGear > 5)) {
            throw new IllegalArgumentException("Gear must be -1 (reverse) or between 1 and 5");
        } else if (Math.abs(this.gear - newGear) > 1) {
            throw new IllegalGearException("Cannot skip gears when shifting");
        }
        this.gear = (newGear == 0) ? -1 : newGear;
    }

    public int getGear() {
        return this.gear;
    }

    public class IllegalGearException extends RuntimeException {
        public IllegalGearException(String message) {
            super(message);
        }
    }
}
