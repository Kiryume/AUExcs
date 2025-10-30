package week9;

public class Person {
    public static void main(String[] args) {
        Person alice = new Person("Alice", "Smith", 30);
        Person bob = new Person("Bob", "Jones", 25);
        Person charlie = new Person("Charlie", "Brown");

        // getters / setters
        System.out.println("Bob last name before: " + bob.getLastName());
        bob.setLastName("Johnson");
        System.out.println("Bob last name after: " + bob.getLastName());

        // birthday
        System.out.println("Charlie age before: " + charlie.getAge());
        charlie.birthday();
        charlie.birthday();
        System.out.println("Charlie age after 2 birthdays: " + charlie.getAge());

        // marriage attempts
        Person teen = new Person("Tim", "Young", 16);
        System.out.println("Teen marrying Alice allowed? " + teen.marry(alice));

        System.out.println("Alice marrying herself allowed? " + alice.marry(alice));

        Person youngAdult = new Person("Yara", "Lee", 18);
        Person older = new Person("Oliver", "Stone", 24);
        System.out.println("Older marrying YoungAdult allowed? " + older.marry(youngAdult));

        // successful marriage
        System.out.println("Before marry: " + alice + " ; " + bob);
        System.out.println("Alice marry Bob success? " + alice.marry(bob));
        System.out.println("After marry: " + alice + " ; " + bob);

        // manual one-sided spouse set
        Person emma = new Person("Emma", "Stone", 28);
        Person frank = new Person("Frank", "Miller", 28);
        emma.setSpouse(frank);
        System.out.println("Emma spouse: " + (emma.getSpouse() == null ? "none" : emma.getSpouse().getFirstName()));
        System.out.println("Frank spouse: " + (frank.getSpouse() == null ? "none" : frank.getSpouse().getFirstName()));

        // final states
        Person[] people = {alice, bob, charlie, teen, youngAdult, older, emma, frank};
        System.out.println("\nFinal states:");
        for (Person p : people) {
            String spouseName = p.getSpouse() == null ? "none" : p.getSpouse().getFirstName();
            System.out.println(p + " spouse: " + spouseName);
        }
    }


    private String firstName;
    private String lastName;
    private int age;
    private Person spouse = null;

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Person(String firstName, String lastName) {
        this(firstName, lastName, 0);
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
    }

    public void birthday() {
        this.age += 1;
    }

    public boolean marry(Person that) {
        if (this.spouse != null || that.getSpouse() != null) {
            return false;
        }
        if (this == that) {
            return false;
        }
        if (this.age < 18 || that.getAge() < 18) {
            return false;
        }
        if (this.age < that.getAge()/2.0 + 7 || that.getAge() < this.age/2.0 + 7) {
            System.out.println("\uD83E\uDD28");
        }
        this.spouse = that;
        that.setSpouse(this);
        var combinedLastName = this.lastName + "-" + that.getLastName();
        this.setLastName(combinedLastName);
        that.setLastName(combinedLastName);
        return true;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + ", " + age + " " + (spouse == null ? "unmarried" : "married");
    }
}
