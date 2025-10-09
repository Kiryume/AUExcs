package week6;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calendar {

    public static List<ScholarCalendar> getAllScholars() {
        // these values are pulled from https://en.wikipedia.org/wiki/Julian_calendar#Leap_year_error
        // that's why are there weird hacks in the Date adn ScholarCalendar constructors
        // I'm aligning it with the program representation of years
        List<ScholarCalendar> scholars = new ArrayList<>();
        scholars.add(new ScholarCalendar("Bennett",
                new Date(-46, 12, 31), new Date(-1, 2, 25),
                Arrays.asList(44, 41, 38, 35, 32, 29, 26, 23, 20, 17, 14, 11, 8)));
        scholars.add(new ScholarCalendar("Soltau",
                new Date(-45, 1, 2), new Date(4, 2, 25),
                Arrays.asList(45, 41, 38, 35, 32, 29, 26, 23, 20, 17, 14, 11)));
        scholars.add(new ScholarCalendar("Matzat",
                new Date(-45, 1, 1), new Date(-1, 2, 25),
                Arrays.asList(44, 41, 38, 35, 32, 29, 26, 23, 20, 17, 14, 11)));
        scholars.add(new ScholarCalendar("Ideler",
                new Date(-45, 1, 1), new Date(4, 2, 25),
                Arrays.asList(45, 42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12, 9)));
        scholars.add(new ScholarCalendar("Kepler",
                new Date(-45, 1, 2), new Date(4, 2, 25),
                Arrays.asList(43, 40, 37, 34, 31, 28, 25, 22, 19, 16, 13, 10)));
        scholars.add(new ScholarCalendar("Harriot",
                new Date(-45, 1, 1), new Date(-1, 2, 25),
                Arrays.asList(43, 40, 37, 34, 31, 28, 25, 22, 19, 16, 13, 10)));
        scholars.add(new ScholarCalendar("Bünting",
                new Date(-45, 1, 1), new Date(-1, 2, 25),
                Arrays.asList(45, 42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12)));
        scholars.add(new ScholarCalendar("Christmann",
                new Date(-45, 1, 2), new Date(4, 2, 25),
                Arrays.asList(43, 40, 37, 34, 31, 28, 25, 22, 19, 16, 13, 10)));
        scholars.add(new ScholarCalendar("Scaliger",
                new Date(-45, 1, 2), new Date(4, 2, 25),
                Arrays.asList(42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12, 9)));
        return scholars;
    }
    // Zeller's Congruence for Gregorian calendar
    public static int dayOfWeekGregorian(int year, int month, int day) {
        // january and february are not affected by leap year so we treat them as months 13 and 14 of the previous year
        if (month <= 2) {
            month += 12;
            year -= 1;
        }

        int J = year / 100;
        int K = year % 100;

        int h = (day + (13 * (month + 1)) / 5 + K + K / 4 + J / 4 - 2 * J) % 7;

        // adjust to make Sunday 0 (gregorian calendar is only used for positive years so no need to handle negative)
        return (h + 6) % 7;
    }

    // Zeller's Congruence for Julian calendar
    // not taking into consideration
    public static int dayOfWeekProlepticJulian(int year, int month, int day) {
        if (month <= 2) {
            month += 12;
            year -= 1;
        }

        // This is the correct way to floorDiv negative numbers in Java 🫩
        int J = Math.floorDiv(year, 100);
        int K = Math.floorMod(year, 100);

        // The formula for h
        int h = (day + Math.floorDiv(13 * (month + 1), 5) + K + Math.floorDiv(K, 4) + 5 - J) % 7;

        // Adjust to make the result positive and map Sunday to 0
        int dayIndex = (h + 7) % 7;
        return (dayIndex + 6) % 7;
    }

    public static boolean isLeapYear(int year) {
        if (year < 1582) {
            // Julian calendar leap rule
            return year % 4 == 0;
        } else {
            // Gregorian calendar calendar leap rules
            return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        }
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Calendar <month> <year>");
            return;
        }

        int monthInput = Integer.parseInt(args[0]);
        int yearInput = Integer.parseInt(args[1]);

        int month = monthInput - 1; // adjust for 0 based index

        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        int[] daysInMonth = {
                31, 28, 31, 30, 31, 30,
                31, 31, 30, 31, 30, 31
        };

        if (isLeapYear(yearInput)) {
            daysInMonth[1] = 29;
        }

        // determine which function to use
        int startDay;
        if (yearInput < 1582 || (yearInput == 1582 && monthInput <= 10)) {
            startDay = dayOfWeekProlepticJulian(yearInput, monthInput, 1);
        } else {
            startDay = dayOfWeekGregorian(yearInput, monthInput, 1);
        }

        // print out the header
        String yearDisplay = (yearInput < 1) ? (1 - yearInput) + " BC" : yearInput + " AD";
        System.out.println("     " + months[month] + " " + yearDisplay);
        System.out.println("Su Mo Tu We Th Fr Sa");

        for (int i = 0; i < startDay; i++) {
            System.out.print("   ");
        }

        int dayOfWeek = startDay;
        for (int day = 1; day <= daysInMonth[month]; day++) {
            // Thursday 4 October 1582 was followed by Friday 15 October 1582
            // https://en.wikipedia.org/wiki/Gregorian_calendar
            // 1st of October 1582 was Monday on the Julian calendar
            // 1st of November 1582 was Monday on the Gregorian calendar
            // https://www.timeanddate.com/calendar/?year=1582&country=22
            // FAKE DAYS!!!!
            if (yearInput == 1582 && monthInput == 10 && day > 4 && day < 15) {
                continue;
            }

            System.out.printf("%2d ", day);
            dayOfWeek++;

            if (dayOfWeek % 7 == 0 || day == daysInMonth[month]) {
                System.out.println();
            }
        }
    }
    static class Date {
        public int year;
        public int month;
        public int day;

        public Date(int year, int month, int day) {
            // 46 BC is supposed to be year -45 in our program representation
            if (year < 1) {
                year = year + 1;
            }
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }
    static class ScholarCalendar {
        public String name;
        public Date firstJulianDay;
        public Date firstAlignedDay;
        public List<Integer> triennialLeapYearsBC;
        public ScholarCalendar(String name,Date firstJulianDay, Date firstAlignedDay, List<Integer> triennialLeapYearsBC) {
            this.name = name;
            this.firstJulianDay = firstJulianDay;
            this.firstAlignedDay = firstAlignedDay;
            // turn all numbers negative to represent BC years in our program representation
            this.triennialLeapYearsBC = triennialLeapYearsBC.stream().map(y -> -y+1).toList();
        }

        public boolean IsScholarConcernedWithDate(int year, int month) {
            if (year < firstJulianDay.year || (year == firstJulianDay.year && month < firstJulianDay.month)) {
                return false;
            }
            if (year > firstAlignedDay.year || (year == firstAlignedDay.year && month > firstAlignedDay.month)) {
                return false;
            }
            return true;
        }

    }
}