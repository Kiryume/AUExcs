package com.calculator;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

// Implementation is based on https://en.wikipedia.org/wiki/Shunting_yard_algorithm
public class SimpleFX extends Application {
    Queue<Token> expression = new LinkedList<>();
    int parenthesisBalance = 0;
    TextField display = new TextField();
    TextField intermediatemDisplay = new TextField();
    final Map<TokenKind, Integer> precedence = Map.of(
        TokenKind.PLUS, 2,
        TokenKind.MINUS, 2,
        TokenKind.NEGATE, 2,
        TokenKind.MULTIPLY, 3,
        TokenKind.DIVIDE, 3,
        TokenKind.RAISE, 4
    );
    final List<TokenKind> operations = List.of(
        TokenKind.PLUS,
        TokenKind.MINUS,
        TokenKind.MULTIPLY,
        TokenKind.DIVIDE,
        TokenKind.RAISE
    );

    private void addToken(Token token) {
        switch (token.type) {
            // There is only one button to add parentheses which handles both opening and closing
            case TokenKind.LPAREN -> {
                if (!expression.isEmpty()) {
                    Token last = ((LinkedList<Token>) expression).getLast();
                    if (parenthesisBalance == 0) {
                        // If the parenthesis are balanced we always add the opening parenthesis
                        if (last.type == TokenKind.NUMBER || last.type == TokenKind.RPAREN) {
                            // In case the previous token is a number or closing parenthesis we add a multiplication sign before
                            // The multiplication is hidden. This allows for the following syntax: 2(3+4), 5(5) and so on
                            expression.add(new Token(TokenKind.MULTIPLY, ""));
                            expression.add(token);
                            parenthesisBalance++;
                        } else {
                            expression.add(token);
                            parenthesisBalance++;
                        }
                    } else {
                        if (last.type == TokenKind.NUMBER || last.type == TokenKind.RPAREN) {
                            // in case the previous token is a number or closing parenthesis we add a closing parenthesis
                            // At this point we are in situations like these (4+4 or (3(2+1)
                            expression.add(new Token(TokenKind.RPAREN, ")"));
                            parenthesisBalance--;
                        } else {
                            // Otherwise we add an opening parenthesis
                            // These are situations like these: (4+ or 3+(2*2^
                            expression.add(token);
                            parenthesisBalance++;
                        }
                    }
                } else {
                    // If the expression is empty we just add the opening parenthesis
                    expression.add(token);
                    parenthesisBalance++;
                }
            }
            case TokenKind.NUMBER -> {
                if (!expression.isEmpty()) {
                    Token last = ((LinkedList<Token>) expression).getLast();
                    if (last.type == TokenKind.RPAREN) {
                        // In case the previous token is a closing parenthesis we add a multiplication sign before
                        // This allows for the following syntax: (3+4)2, (5)5+2 and so on
                        expression.add(new Token(TokenKind.MULTIPLY, ""));
                    } else if (last.type == TokenKind.NUMBER) {
                        // If the previous token is a number we inherit whether it's decimal or not
                        token.decimal = last.decimal;
                    } else if (last.type == TokenKind.DECIMAL) {
                        // In case the previous token is a decimal point we set the current number as decimal
                        token.decimal = true;
                    }
                }
                expression.add(token);
            }
            case TokenKind.DECIMAL -> {
                // Decimal is considered a decimal so we cannot write 0..2
                token.decimal = true;
                if (!expression.isEmpty()) {
                    Token last = ((LinkedList<Token>) expression).getLast();
                    if (last.type == TokenKind.NUMBER || last.type == TokenKind.DECIMAL) {
                        // If the last number is already decimal we ignore the input. This prevents inputs like 2.3.4
                        if (!last.decimal) {
                            expression.add(token);
                        }
                    } else {
                        // If the last token is not a number we inject implicit 0 before the decimal point
                        // This allows inputs like .5 to be interpreted as 0.5 and still allows chaining like 2+.5 or (3+2).5
                        addToken(new Token(TokenKind.NUMBER, 0, ""));
                        expression.add(token);
                    }
                } else {
                    // If the expression is empty and the first token is a decimal point we inject implicit 0 before the decimal point
                    addToken(new Token(TokenKind.NUMBER, 0, ""));
                    expression.add(token);
                }
            }
            case TokenKind.MULTIPLY, TokenKind.DIVIDE, TokenKind.PLUS, TokenKind.RAISE, TokenKind.MINUS -> {
                if (!expression.isEmpty()) {
                    Token last = ((LinkedList<Token>) expression).getLast();
                    // If the last token is not an operation we just add the new operation
                    if (!operations.contains(last.type)) {
                        expression.add(token);
                    } else {
                        // If the last token is also an operation we replace it with the new one
                        // This allows to change the operations without needing to delete them first
                        ((LinkedList<Token>) expression).removeLast();
                        expression.add(token);
                    }
                } else if (token.type == TokenKind.MINUS) {
                    // If the expression is empty we can allow unary minus for negative numbers
                    expression.add(token);
                }
            }
            default -> {
                expression.add(token);
            }
        }
        updateDisplay();
    }

    private void deleteToken() {
        if (!expression.isEmpty()) {
            Token removed = ((LinkedList<Token>) expression).removeLast();
            // Adjust parenthesis balance if needed
            if (removed.type == TokenKind.LPAREN) {
                parenthesisBalance--;
            } else if (removed.type == TokenKind.RPAREN) {
                parenthesisBalance++;
            }
            if (!expression.isEmpty()) {
                // In case the last token is a hidden one we delete to so there is not dangling multiplication for example
                // After deleting '4' from (5)4
                Token last = ((LinkedList<Token>) expression).getLast();
                if (last.text == "") {
                    ((LinkedList<Token>) expression).removeLast();
                }
            }
            updateDisplay();
            System.out.println("Deleted token: " + removed.text);
        }
    }

    private void clearExpression() {
        expression.clear();
        parenthesisBalance = 0;
        updateDisplay();
        System.out.println("Cleared expression");
    }

    private void updateDisplay() {
        StringBuilder text = new StringBuilder();
        for (Token t : expression) {
            text.append(t.text);
        }
        display.setText(text.toString());
        try {
            // Try evaluating the expression for intermediate display of a result
            double result = shuntingYard();
            intermediatemDisplay.setText(Double.toString(result));
        } catch (Exception e) {
            intermediatemDisplay.setText("");
        }
    }

    // This is a helper function to read string of tokens to a single number for calculations
    private double eatNumber(int init, Queue<Token> expr) {
        double number = init;
        boolean decimalFound = false;
        double decimalPlace = 10;
        while (!expr.isEmpty()) {
            Token t = expr.peek();
            if (t.type == TokenKind.NUMBER) {
                expr.poll();
                if (!decimalFound) {
                    number = number * 10 + t.value;
                } else {
                    number = number + t.value / decimalPlace;
                    decimalPlace *= 10;
                }
            } else if (t.type == TokenKind.DECIMAL) {
                expr.poll();
                decimalFound = true;
            } else {
                break;
            }
        }
        return number;
    }

    // This is what calculates the expression using RPN and the shunting yard algorithm
    private double shuntingYard() {
        Queue<Token> rpn = new LinkedList<>();
        Stack<Token> operators = new Stack<>();
        Queue<Token> expr = new LinkedList<>(this.expression);
        Token last = ((LinkedList<Token>) expr).getLast();
        // If the last token is an operation we remove it. This allows us to calculate incomplete expressions like 2+3* as 2+3
        // or 1-(4^.5* as 1-(4^0.5)
        if (operations.contains(last.type)) {
            ((LinkedList<Token>) expr).removeLast();
        }

        TokenKind lastType = null;

        // This is pretty much taken from here https://en.wikipedia.org/wiki/Shunting_yard_algorithm#The_algorithm_in_detail
        // With the exception of handling unary minus
        while (!expr.isEmpty()) {
            Token t = expr.poll();

            switch (t.type) {
                case NUMBER -> {
                    rpn.add(new Token(TokenKind.NUMBER, eatNumber(t.value, expr)));
                    lastType = TokenKind.NUMBER;
                }
                case MINUS -> {
                    boolean isUnary = (lastType == null
                        || lastType == TokenKind.LPAREN
                        || precedence.containsKey(lastType));

                    if (isUnary) {
                        while (!operators.isEmpty() && operators.peek().type != TokenKind.LPAREN && (precedence.get(operators.peek().type) > precedence.get(TokenKind.NEGATE) || precedence.get(operators.peek().type).equals(precedence.get(TokenKind.NEGATE)))) {
                            rpn.add(operators.pop());
                        }
                        // We need dedicated token for unary minus because without it 5(-1) would evaluate to
                        // 5,1,MINUS, MULTIPLY and the MINUS would apply to 5 and 1 and then there would be only one number on the stack for the MULTIPLY
                        // Also the strings here are completely irrelevant since we never display them
                        operators.push(new Token(TokenKind.NEGATE, "-"));
                        lastType = TokenKind.NEGATE;
                    } else {
                        // If it's not unary we just handle it like any other operation
                        while (!operators.isEmpty()
                            && operators.peek().type != TokenKind.LPAREN
                            && (precedence.get(operators.peek().type) > precedence.get(t.type)
                            || (precedence.get(operators.peek().type).equals(precedence.get(t.type))))) {
                            rpn.add(operators.pop());
                        }
                        operators.push(t);
                        lastType = TokenKind.MINUS;
                    }
                }
                case PLUS, MULTIPLY, DIVIDE, RAISE -> {
                    while (!operators.isEmpty()
                        && operators.peek().type != TokenKind.LPAREN
                        && (precedence.get(operators.peek().type) > precedence.get(t.type)
                        || ((precedence.get(operators.peek().type).equals(precedence.get(t.type)) && t.type != TokenKind.RAISE)))) {
                        rpn.add(operators.pop());
                    }
                    operators.push(t);
                    lastType = t.type;
                }
                case LPAREN -> {
                    operators.push(t);
                    lastType = TokenKind.LPAREN;
                }
                case RPAREN -> {
                    // In case of closing operation we put all operands into the queue until we find the matching opening parenthesis
                    while (!operators.isEmpty() && operators.peek().type != TokenKind.LPAREN) {
                        rpn.add(operators.pop());
                    }
                    operators.pop();
                    lastType = TokenKind.RPAREN;
                }
                default -> throw new IllegalStateException("Unexpected value: " + t.type);
            }
        }

        // Drain the operator stack
        while (!operators.isEmpty()) {
            rpn.add(operators.pop());
        }
        // Now we evaluate the RPN expression
        Stack<Double> evalStack = new Stack<>();
        while (!rpn.isEmpty()) {
            Token t = rpn.poll();
            switch (t.type) {
                case NUMBER -> evalStack.push(t.dvalue);
                case PLUS -> {
                    double b = evalStack.pop();
                    double a = evalStack.pop();
                    evalStack.push(a + b);
                }
                case MINUS -> {
                    double b = evalStack.pop();
                    double a = evalStack.pop();
                    evalStack.push(a - b);
                }
                case NEGATE -> {
                    double a = evalStack.pop();
                    evalStack.push(-a);
                }
                case MULTIPLY -> {
                    double b = evalStack.pop();
                    double a = evalStack.pop();
                    evalStack.push(a * b);
                }
                case DIVIDE -> {
                    double b = evalStack.pop();
                    double a = evalStack.pop();
                    evalStack.push(a / b);
                }
                case RAISE -> {
                    double b = evalStack.pop();
                    double a = evalStack.pop();
                    evalStack.push(Math.pow(a, b));
                }
            }
        }
        return evalStack.pop();
    }

    @Override
    public void start(Stage stage) {
        // Setup displays with styles and properties
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        display.setPrefHeight(50);

        intermediatemDisplay.setEditable(false);
        intermediatemDisplay.setAlignment(Pos.CENTER_RIGHT);
        intermediatemDisplay.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-text-fill: gray; -fx-background-color: transparent; -fx-border-color: transparent;");
        intermediatemDisplay.setPrefHeight(30);

        var clearBtn = new StyledButton("AC");
        clearBtn.setOnAction(event -> {
            clearExpression();
        });
        var equalsBtn = new StyledButton("=");
        equalsBtn.setOnAction(event -> {
            if (!expression.isEmpty()) {
                // Calculate the expression
                double result = 0.0;
                try {
                    result = shuntingYard();
                } catch (Exception e) {
                    // Handle results
                    display.setText("Error");
                    System.out.println("Calculation error: " + e.getMessage());
                    return;
                } finally {
                    // Always clear the expression after calculation
                    expression.clear();
                }
                if (Double.isInfinite(result) || Double.isNaN(result)) {
                    display.setText("Error");
                    System.out.println("Calculation error: " + result);
                    return;
                }
                // Add token minus to the expression if the result is negative and make the result positive
                if (result < 0) {
                    expression.add(new Token(TokenKind.MINUS, "-"));
                    result = -result;
                }
                var string = Double.toString(result);
                // Convert the result to tokens and add them to the expression
                // var string = Double.toString(result);
                // If the result is an integer we remove the decimal part
                if (string.endsWith(".0")) {
                    string = string.substring(0, string.length() - 2);
                }
                // Add all characters as tokens
                for (char ch : string.toCharArray()) {
                    if (ch == '.') {
                        expression.add(new Token(TokenKind.DECIMAL, "."));
                    } else {
                        expression.add(new Token(TokenKind.NUMBER, Character.getNumericValue(ch)));
                    }
                }
                updateDisplay();
                System.out.println("Calculated result: " + result);
            }
        });
        var deleteBtn = new StyledButton("DEL");
        deleteBtn.setOnAction(event -> {
            if (!expression.isEmpty()) {
                deleteToken();
            }
        });
        var btns = new Button[][]{
            {
                clearBtn,
                new TokenButton(TokenKind.LPAREN, "(", "()"),
                new TokenButton(TokenKind.RAISE, "^"),
                new TokenButton(TokenKind.DIVIDE, "/"),
            },
            {
                new TokenButton(TokenKind.NUMBER, 7),
                new TokenButton(TokenKind.NUMBER, 8),
                new TokenButton(TokenKind.NUMBER, 9),
                new TokenButton(TokenKind.MULTIPLY, "*"),
            },
            {
                new TokenButton(TokenKind.NUMBER, 4),
                new TokenButton(TokenKind.NUMBER, 5),
                new TokenButton(TokenKind.NUMBER, 6),
                new TokenButton(TokenKind.MINUS, "-"),
            },
            {
                new TokenButton(TokenKind.NUMBER, 1),
                new TokenButton(TokenKind.NUMBER, 2),
                new TokenButton(TokenKind.NUMBER, 3),
                new TokenButton(TokenKind.PLUS, "+"),
            },
            {
                new TokenButton(TokenKind.NUMBER, "0"),
                new TokenButton(TokenKind.DECIMAL, "."),
                deleteBtn,
                equalsBtn,
            }

        };

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);


        for (int r = 0; r < btns.length; r++) {
            for (int c = 0; c < btns[r].length; c++) {
                grid.add(btns[r][c], c, r);
            }
        }

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(display, intermediatemDisplay, grid);


        Scene scene = new Scene(root, 300, 250);

        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    enum TokenKind {
        NUMBER,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        RAISE,
        LPAREN,
        RPAREN,
        DECIMAL,
        NEGATE,
    }

    static class StyledButton extends Button {
        StyledButton() {
            this.setMinSize(50, 50);
            this.setMaxSize(50, 50);
            this.setStyle("-fx-font-size: 15px; -fx-background-radius: 25px;");
        }

        StyledButton(String text) {
            this();
            this.setText(text);
        }
    }

    class TokenButton extends StyledButton {
        Token token;

        TokenButton(TokenKind type, String tokenText, String buttonText) {
            this(type, tokenText);
            this.setText(buttonText);
        }

        TokenButton(TokenKind type, String text) {
            token = new Token(type, text);
            this.setText(text);
            this.setOnAction(event -> {
                addToken(token);
                System.out.println("Added token: " + token.text);
            });
        }

        TokenButton(TokenKind type, int value) {
            this(type, Integer.toString(value));
            token = new Token(type, value);
        }
    }

    // I hate how type unsafe this is compared to having sum types but I don't wanna bother with anything more complicated
    // It's a miracle the code works despite probably not knowing about bunch of edge cases I'm not hadnling
    static class Token {
        TokenKind type;
        // This is the string representation of the token
        // Sometimes it's empty for implicit tokens like hidden multiplication or hidden zero
        // e.g. in 2(3+4) or .5+1
        String text;
        // This value is used for representing the number tokens before they are converted to double for calculations
        int value = 0;
        // This is only intended for calculations
        double dvalue = 0.0;
        // This is for number tokens to indicate whether the number contains a decimal point
        boolean decimal = false;

        // Bunch of nice constructors for different token types
        public Token(TokenKind type, int value) {
            this.type = type;
            this.value = value;
            this.text = Integer.toString(value);
        }

        public Token(TokenKind type, int value, String text) {
            this.type = type;
            this.value = value;
            this.text = text;
        }

        public Token(TokenKind type, double dvalue) {
            this.type = type;
            this.dvalue = dvalue;
            this.text = Double.toString(dvalue);
        }

        Token(TokenKind type, String text) {
            this.type = type;
            this.text = text;
        }
    }
}