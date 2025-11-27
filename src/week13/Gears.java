package week13;
// name: Kirsten Pleskot

/*
Exception:
Exception is a control flow mechanism to divert the normal execution of a program when an unexpected situation arises.
Because we don't know when or where something throws it's hard to reason about state of the program at any given time.
So we have though, of what if every function had to declare every possible exception it could throw?
But because we have horrible type system pretty much anything can create NullPointerException at any time.
So we have two categories of exceptions:
1. Checked Exceptions - these are exceptions that must be declared in the method signature and handled by
    the caller. These are exceptions that are expected to occur during normal operation of the program.
2. Unchecked Exceptions - these are exceptions that do not need to be declared in the method signature
    and can be thrown at any time. These are exceptions that are not expected to occur during
    normal operation of the program.
If only there was a way to solve this problem... cough cough algebraic data types cough cough
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
            // the assignment says the neutral gear does not exist so ig it does not exist
            throw new IllegalArgumentException("Gear cannot be 0");
        }
        // this makes it slightly easier to check gear skipping
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
