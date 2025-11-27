package week13;


import java.util.*;
import java.util.function.BooleanSupplier;

// This is probably the worst code I've ever written
// The core functions of this printer are the If, While, Break, and Return functions which allow to have control flow without using any built in control flow mechanism
// (The program contains 0 if, for, switch or while statements)
public class PrinterFromHell {
    Random r;
    int paperLevel;
    Map<Color, Integer> tonerLevels;
    List<Breakage> breakages;
    BreakageFactory breakageFactory;
    Owner owner;

    public PrinterFromHell() throws ReturnException {
        this.r = new Random();
        this.paperLevel = 100;
        this.tonerLevels = new HashMap<>();
        final Integer[] i = {0};
        While(() -> i[0] < Color.values().length, () -> {
            tonerLevels.put(Color.values()[i[0]], 100);
            i[0]++;
        });
        this.breakages = new ArrayList<>();
        this.breakageFactory = new BreakageFactory();
        try {
            this.owner = new Owner();
        } catch (ReturnException e) {
            this.owner = (Owner) e.value;
        }
        Return(this);
    }

    void BreakagesContain(Breakage b) throws ReturnException {
        final Integer[] i = {0};
        While(() -> i[0] < breakages.size(), () -> {
            try {
                boolean equals = false;
                try {
                    breakages.get(i[0]).Equals(b);
                } catch (ReturnException e) {
                    equals = (boolean) e.value;
                }
                If(equals);
            } catch (ArithmeticException e) {
                Return(true);
            }
            i[0]++;
        });
        Return(false);
    }

    void addBreakage(Breakage b) {
        try {
            boolean contains = false;
            try {
                BreakagesContain(b);
            } catch (ReturnException e) {
                contains = (boolean) e.value;
            }
            If(!contains);
        } catch (ArithmeticException e) {
            breakages.add(b);
        }
    }

    void breakPrinter() throws OutOfPaperException, PaperJamException, OutOfTonerException {
        int issue = r.nextInt(3);
        breakIssue breakage = null;
        try {
            If(issue == 0);
        } catch (ArithmeticException e) {
            breakage = breakIssue.PAPER_JAM;
        }
        try {
            If(issue == 1);
        } catch (ArithmeticException e) {
            paperLevel = 0;
            breakage = breakIssue.OUT_OF_PAPER;
        }
        try {
            If(issue == 2);
        } catch (ArithmeticException e) {
            Color colorToDeplete = Color.values()[r.nextInt(Color.values().length)];
            tonerLevels.put(colorToDeplete, 0);
            breakage = breakIssue.OUT_OF_TONER;
        }
        Breakage b = null;
        try {
            breakageFactory.createBreakage(breakage);
        } catch (ReturnException e) {
            b = (Breakage) e.value;
        }
        addBreakage(b);
        assert b != null;
        try {
            If(breakage == breakIssue.OUT_OF_PAPER);
        } catch (ArithmeticException e) {
            throw new OutOfPaperException(b.message);
        }
        try {
            If(breakage == breakIssue.PAPER_JAM);
        } catch (ArithmeticException e) {
            throw new PaperJamException(b.message);
        }
        try {
            If(breakage == breakIssue.OUT_OF_TONER);
        } catch (ArithmeticException e) {
            throw new OutOfTonerException(b.message);
        }
    }

    void getCurrentBreakagesMessages() throws ReturnException {
        var o = breakages.stream().map(b -> b.fixMessage).toArray(String[]::new);
        Return(o);
    }

    public void fixBreakage(int index) throws Exception {
        try {
            If(index < 0 || index >= breakages.size());
        } catch (ArithmeticException e) {
            throw new IndexOutOfBoundsException("No breakage at index " + index);
        }
        try {
            If(r.nextBoolean());
        } catch (ArithmeticException e) {
            breakPrinter();
        }
        Breakage b = breakages.get(index);
        b.fix();
        breakages.remove(index);
    }

    public void printDocument(String document) throws NotEnoughPaperException, PaperJamException, OutOfTonerException, PrinterNotFixedException {
        try {
            If(r.nextBoolean() || breakages.isEmpty());
        } catch (ArithmeticException e) {
            breakPrinter();
        }
        try {
            If(!breakages.isEmpty());
        } catch (ArithmeticException e) {
            throw new PrinterNotFixedException("Printer has unresolved breakages!");
        }
        try {
            If(paperLevel >= document.lines().toArray().length);
        } catch (ArithmeticException e) {
            throw new NotEnoughPaperException("Not enough paper to print the document!");
        }

        System.out.println("Printing document:\n" + document);
        paperLevel -= document.lines().toArray().length;
    }

    public static void main(String[] args) {
        PrinterFromHell printer;
        try {
            printer = new PrinterFromHell();
        } catch (ReturnException e) {
            printer = (PrinterFromHell) e.value;
        }
        System.out.println("Enter the document to print (end with an empty line):");
        StringBuilder documentBuilder = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        While(() -> true, () -> {
            String l = scanner.nextLine();
            try {
                If(l.isEmpty());
            } catch (ArithmeticException e) {
                Break();
            }
            documentBuilder.append(l).append("\n");
        });

        String document = documentBuilder.toString();
        PrinterFromHell finalPrinter = printer;
        While(() -> true, () -> {
            try {
                finalPrinter.printDocument(document);
                Break();
            } catch (NotEnoughPaperException | PaperJamException | OutOfTonerException | PrinterNotFixedException e) {
                System.out.println("Issue Happened While Printing: " + e.getMessage());
                System.out.println("Please fix the following issues:");
                String[] issues = new String[0];
                try {
                    finalPrinter.getCurrentBreakagesMessages();
                } catch (ReturnException re) {
                    issues = (String[]) re.value;
                }
                final Integer[] i = {0};
                String[] finalIssues = issues;
                While(() -> i[0] < finalIssues.length, () -> {
                    System.out.println(i[0] + ": " + finalIssues[i[0]]);
                    i[0]++;
                });
                System.out.println("Enter the index of the issue you want to fix:");
                try {
                    int index = scanner.nextInt();
                    finalPrinter.fixBreakage(index);
                } catch (IndexOutOfBoundsException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("While fixing the issue, another problem occurred: " + ex.getMessage());
                }

            }
        });
    }

    public static <T> void Return(T value) throws ReturnException {
        throw new ReturnException(value);
    }

    public static void If(boolean condition) {
        int i = 1 / (4 - String.valueOf(condition).length());
    }

    public static void While(BooleanSupplier condition, Runnable body) {
        try {
            If(condition.getAsBoolean());
        } catch (ArithmeticException e) {
            try {
                body.run();
                While(condition, body);

            } catch (BreakException ignored) {
            }
        }
    }

    public static void Break() {
        throw new BreakException();
    }

    static class Owner {
        int money;
        EmploymentStatus employmentStatus;

        enum EmploymentStatus {
            JOBLESS, EMPLOYED
        }

        public Owner() throws ReturnException {
            this.money = 0;
            this.employmentStatus = EmploymentStatus.JOBLESS;
            Return(this);
        }

        public void goToWork() throws NotEmployedException {
            try {
                If(employmentStatus == EmploymentStatus.JOBLESS);
            } catch (ArithmeticException e) {
                throw new NotEmployedException("Owner is not employed!");
            }
            System.out.println("You made 50 dollars at work.");
            money += 50;
        }
    }

    enum Color {
        BLACK, CYAN, MAGENTA, YELLOW
    }

    enum breakIssue {
        OUT_OF_PAPER, PAPER_JAM, OUT_OF_TONER
    }

    abstract class Breakage {
        String message;
        String fixMessage;

        public Breakage(String message, String fixMessage) {
            this.message = message;
            this.fixMessage = fixMessage;
        }

        abstract void fix() throws Exception;

        public void Equals(Object o) {
            try {
                If(this == o);
            } catch (ArithmeticException e) {
                Return(true);
            }
            try {
                If(o == null || getClass() != o.getClass());
            } catch (ArithmeticException e) {
                Return(false);
            }
            Breakage breakage = (Breakage) o;
            Return(message.equals(breakage.message) && fixMessage.equals(breakage.fixMessage));
        }
    }

    class OutOfTonerBreakage extends Breakage {
        Color color;

        OutOfTonerBreakage(Color color, String message, String fixMessage) {
            super(message, fixMessage);
            this.color = color;
        }

        @Override
        void fix() throws CardDeclinedException {
            try {
                If(owner.money <= 20);
            } catch (ArithmeticException e) {
                addBreakage(new CardDeclinedBreakage("Card declined while buying toner subscription!", "Card declined, go earn money."));
                throw new CardDeclinedException("Your card got declined!");
            }
            tonerLevels.put(color, 100);
        }

        @Override
        public void Equals(Object o) {
            try {
                If(this == o);
            } catch (ArithmeticException e) {
                Return(true);
            }
            try {
                If(o == null || getClass() != o.getClass());
            } catch (ArithmeticException e) {
                Return(false);
            }
            OutOfTonerBreakage that = (OutOfTonerBreakage) o;
            var superEquals = false;
            try {
                super.Equals(o);
            } catch (ReturnException e) {
                superEquals = (boolean) e.value;
            }
            Return(color == that.color && superEquals);
        }
    }

    class OutOfPaperBreakage extends Breakage {
        public OutOfPaperBreakage(String message, String fixMessage) {
            super(message, fixMessage);
        }

        @Override
        void fix() {
            paperLevel = 100;
        }

        @Override
        public void Equals(Object o) {
            var superEquals = false;
            try {
                super.Equals(o);
            } catch (ReturnException e) {
                superEquals = (boolean) e.value;
            }
            Return(o instanceof OutOfPaperBreakage && superEquals);
        }
    }

    class PaperJamBreakage extends Breakage {
        public PaperJamBreakage(String message, String fixMessage) {
            super(message, fixMessage);
        }

        @Override
        void fix() {
        }

        @Override
        public void Equals(Object o) {
            var superEquals = false;
            try {
                super.Equals(o);
            } catch (ReturnException e) {
                superEquals = (boolean) e.value;
            }
            Return(o instanceof PaperJamBreakage && superEquals);
        }
    }

    class CardDeclinedBreakage extends Breakage {
        public CardDeclinedBreakage(String message, String fixMessage) {
            super(message, fixMessage);
        }

        @Override
        void fix() throws NotEmployedException {
            try {
                owner.goToWork();
            } catch (NotEmployedException e) {
                addBreakage(new NotEmployedBreakage("Cannot earn money owner is unemployed!", "Find a job."));
                throw e;
            }
        }

        @Override
        public void Equals(Object o) {
            var superEquals = false;
            try {
                super.Equals(o);
            } catch (ReturnException e) {
                superEquals = (boolean) e.value;
            }
            Return(o instanceof CardDeclinedBreakage && superEquals);
        }
    }

    class NotEmployedBreakage extends Breakage {
        public NotEmployedBreakage(String message, String fixMessage) {
            super(message, fixMessage);
        }

        @Override
        void fix() {
            System.out.println("You found a job, good job!");
            owner.employmentStatus = Owner.EmploymentStatus.EMPLOYED;
        }

        @Override
        public void Equals(Object o) {
            var superEquals = false;
            try {
                super.Equals(o);
            } catch (ReturnException e) {
                superEquals = (boolean) e.value;
            }
            Return(o instanceof NotEmployedBreakage && superEquals);
        }
    }

    class BreakageFactory {
        void createBreakage(breakIssue issue) throws ReturnException {
            Object o = null;
            try {
                If(issue == breakIssue.OUT_OF_PAPER);
            } catch (ArithmeticException e) {
                o = new OutOfPaperBreakage("Out of paper!", "Refill paper tray.");
            }
            try {
                If(issue == breakIssue.PAPER_JAM);
            } catch (ArithmeticException e) {
                o = new PaperJamBreakage("Paper jam!", "Clear the paper jam.");
            }
            try {
                If(issue == breakIssue.OUT_OF_TONER);
            } catch (ArithmeticException e) {
                Color color = Color.values()[r.nextInt(Color.values().length)];
                o = new OutOfTonerBreakage(color, "Subscription for " + color + " has expired!", "Buy subscription for " + color);
            }
            Return(o);
        }
    }

    public static class ReturnException extends RuntimeException {
        public final Object value;

        public ReturnException(Object value) {
            this.value = value;
        }
    }

    public static class BreakException extends RuntimeException {
    }

    public static class CardDeclinedException extends Exception {
        public CardDeclinedException(String message) {
            super(message);
        }
    }

    public static class NotEmployedException extends Exception {
        public NotEmployedException(String message) {
            super(message);
        }
    }

    public static class PrinterNotFixedException extends Exception {
        public PrinterNotFixedException(String message) {
            super(message);
        }
    }

    public static class NotEnoughPaperException extends Exception {
        public NotEnoughPaperException(String message) {
            super(message);
        }
    }

    public static class OutOfPaperException extends NotEnoughPaperException {
        public OutOfPaperException(String message) {
            super(message);
        }
    }

    public static class PaperJamException extends Exception {
        public PaperJamException(String message) {
            super(message);
        }
    }

    public static class OutOfTonerException extends Exception {
        public OutOfTonerException(String message) {
            super(message);
        }
    }
}

