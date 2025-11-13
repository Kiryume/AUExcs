package week10;
// name: Kirsten Pleskot

public class Person {
    private final String name;
    private final int age;
    public static void main(String[] args) {
        Person person = new Person("Alice", 30);
        Employee employee = new Employee("Bob", 28, "Developer", 5000);
        Manager manager = new Manager("Charlie", 35, 7000, 1500);

        System.out.println(person.getName() + ", Age: " + person.getAge());
        System.out.println(employee.getName() + ", Age: " + employee.getAge() + ", Job Title: " + employee.getJobTitle() + ", Salary: " + employee.getSalary());
        System.out.println(manager.getName() + ", Age: " + manager.getAge() + ", Job Title: " + manager.getJobTitle() + ", Salary: " + manager.getSalary());
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}

class Employee extends Person {
    private final String jobTitle;
    private final int salary;

    public Employee(String name, int age, String jobTitle, int salary) {
        super(name, age);
        this.salary = salary;
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public int getSalary() {
        return salary;
    }
}

class Manager extends Employee {
    private final int monthlyBonus;

    public Manager(String name, int age, int salary, int monthlyBonus) {
        super(name, age, "Manager", salary);
        this.monthlyBonus = monthlyBonus;
    }

    @Override
    public int getSalary() {
        return super.getSalary() + monthlyBonus;
    }
}