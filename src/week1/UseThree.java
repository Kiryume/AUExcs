package week1;

/*
* name: Kirsten Pleskot
* 1.1.5
* a. it says Hi, java
* b. it says Hi, @!&^%
* c. it says Hi, 1234
* d. it does the compilation and execution step with one command
* e. it says Hi, Alice because it only includes the 0th argument and ignores all the other ones
* */

public class UseThree {
    public static void main(String[] args) {
        System.out.printf("Hi, %s, %s and %s!\n", args[2], args[1], args[0]);
    }
}
