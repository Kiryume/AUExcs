package week13;


import java.util.Map;
import java.util.Random;

public class PrinterFromHell {
    Random r;
    int paperLevel;
    Map<Color, Integer> tonerLevels;
    PrinterState state;

    public PrinterFromHell() {
        this.r = new Random();
        this.paperLevel = 100;
        this.tonerLevels = new java.util.HashMap<>();
        for (Color color : Color.values()) {
            tonerLevels.put(color, 100);
        }
        this.state = PrinterState.READY;
        breakPrinter();
    }

    enum Color {
        BLACK, CYAN, MAGENTA, YELLOW
    }

    enum PrinterState {
        READY,
        PAPER_JAM
    }

    enum BreakIssue {
        OUT_OF_PAPER,
        PAPER_JAM,
        OUT_OF_TONER
    }


    BreakIssue breakPrinter() {
        int issue = r.nextInt(3);
        return switch (issue) {
            case 0 -> {
                state = PrinterState.PAPER_JAM;
                yield BreakIssue.PAPER_JAM;
            }
            case 1 -> {
                paperLevel = 0;
                yield BreakIssue.OUT_OF_PAPER;
            }
            case 2 -> {
                Color colorToDeplete = Color.values()[r.nextInt(Color.values().length)];
                tonerLevels.put(colorToDeplete, 0);
                yield BreakIssue.OUT_OF_TONER;
            }
            default -> throw new IllegalStateException("Unexpected value: " + issue);
        };
    }

    public boolean printDocument(String document) throws NotEnoughPaperException, PaperJamException, OutOfTonerException {
        boolean doBreak = r.nextInt(5) == 0;
        if (doBreak) {
            breakPrinter();
        }
        if (paperLevel <= 0) {
            throw new OutOfPaperException("Printer is out of paper.");
        } else if (paperLevel < document.lines().toArray().length) {
            throw new NotEnoughPaperException("Not enough paper to print the document.");
        }else if (state == PrinterState.PAPER_JAM) {
            throw new PaperJamException("Printer has a paper jam.");
        } else if (tonerLevels.values().stream().anyMatch(level -> level <= 0)) {
            throw new OutOfTonerException("Printer is out of toner.");
        }
        paperLevel -= document.lines().toArray().length;
        return true;
    }

    public static void main(String[] args) {
        PrinterFromHell printer = new PrinterFromHell();
        System.out.println("Enter the document to print (end with an empty line):");
        StringBuilder documentBuilder = new StringBuilder();
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            String line;
            while (!(line = scanner.nextLine()).isEmpty()) {
                documentBuilder.append(line).append("\n");
            }
        }
        String document = documentBuilder.toString();
        while (true) {
            try {
                printer.printDocument(document);
                break;
            } catch (NotEnoughPaperException | PaperJamException | OutOfTonerException e) {
                System.out.println(e.getMessage());
                System.out.println("Attempting to fix the issue...");
            }
        }
    }
}

class NotEnoughPaperException extends Exception {
    public NotEnoughPaperException(String message) {
        super(message);
    }
}

class OutOfPaperException extends NotEnoughPaperException {
    public OutOfPaperException(String message) {
        super(message);
    }
}

class PaperJamException extends Exception {
    public PaperJamException(String message) {
        super(message);
    }
}

class OutOfTonerException extends Exception {
    public OutOfTonerException(String message) {
        super(message);
    }
}
