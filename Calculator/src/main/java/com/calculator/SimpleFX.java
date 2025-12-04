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

public class SimpleFX extends Application {
    Queue<Token> expression = new LinkedList<>();
    int parenthesisBalance = 0;
    TextField display = new TextField();
    TextField intermediatemDisplay = new TextField();
    Map<TokenKind, Integer> precedence = Map.of(
        TokenKind.PLUS, 2,
        TokenKind.MINUS, 2,
        TokenKind.MULTIPLY, 3,
        TokenKind.DIVIDE, 3,
        TokenKind.RAISE, 4
    );

    private void addToken(Token token) {
//        if (token.type == TokenKind.LPAREN) {
//            if (!expression.isEmpty()) {
//                Token last = ((LinkedList<Token>) expression).getLast();
//                if (parenthesisBalance == 0) {
//                    if (last.type == TokenKind.NUMBER || last.type == TokenKind.RPAREN) {
//                        expression.add(new Token(TokenKind.MULTIPLY, "*"));
//                        expression.add(token);
//                        parenthesisBalance++;
//                    } else {
//                        expression.add(token);
//                        parenthesisBalance++;
//                    }
//                } else {
//                    if (last.type == TokenKind.NUMBER || last.type == TokenKind.RPAREN) {
//                        expression.add(new Token(TokenKind.RPAREN, ")"));
//                        parenthesisBalance--;
//                    } else {
//                        expression.add(token);
//                        parenthesisBalance++;
//                    }
//                }
//            } else {
//                expression.add(token);
//                parenthesisBalance++;
//            }
//        } else {
//            expression.add(token);
//        }
        expression.add(token);
        updateDisplay();
    }

    private void updateDisplay() {
        StringBuilder text = new StringBuilder();
        for (Token t : expression) {
            text.append(t.text);
        }
        display.setText(text.toString());
        try {
            double result = shuntingYard();
            intermediatemDisplay.setText(Double.toString(result));
        } catch (Exception e) {
            intermediatemDisplay.setText("");
        }
    }

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

    private double shuntingYard() {
        Queue<Token> rpn = new LinkedList<>();
        Stack<Token> operators = new Stack<>();
        Queue<Token> expr = new LinkedList<>(this.expression);
        while (!expr.isEmpty()) {
            Token t = expr.poll();
            switch (t.type) {
                case NUMBER -> rpn.add(new Token(TokenKind.NUMBER, eatNumber(t.value, expr)));
                case PLUS, MINUS, MULTIPLY, DIVIDE -> {
                    while (!operators.isEmpty()
                        && operators.peek().type != TokenKind.LPAREN
                        && (precedence.get(operators.peek().type) > precedence.get(t.type)
                            || ((precedence.get(operators.peek().type).equals(precedence.get(t.type)) && t.type != TokenKind.RAISE))))
                    {
                        rpn.add(operators.pop());
                    }
                    operators.push(t);
                }
                case LPAREN -> operators.push(t);
                case RPAREN -> {
                    while (!operators.isEmpty() && operators.peek().type != TokenKind.LPAREN) {
                        rpn.add(operators.pop());
                    }
                    operators.pop();
                }
                default -> throw new IllegalStateException("Unexpected value: " + t.type);
            }
        }
        while (!operators.isEmpty()) {
            rpn.add(operators.pop());
        }
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
                    if (evalStack.size() == 1) {
                        double a = evalStack.pop();
                        evalStack.push(-a);
                    } else {
                        double b = evalStack.pop();
                        double a = evalStack.pop();
                        evalStack.push(a - b);
                    }
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
            }
        }
        return evalStack.pop();
    }

    @Override
    public void start(Stage stage) {
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
            expression.clear();
            updateDisplay();
            System.out.println("Cleared expression");
        });
        var equalsBtn = new StyledButton("=");
        equalsBtn.setOnAction(event -> {
            if (!expression.isEmpty()) {
                double result = 0.0;
                try {
                    result = shuntingYard();
                } catch (Exception e) {
                    display.setText("Error");
                    System.out.println("Calculation error: " + e.getMessage());
                    return;
                } finally {
                    expression.clear();
                }
                if (Double.isInfinite(result) || Double.isNaN(result)) {
                    display.setText("Error");
                    System.out.println("Calculation error: " + result);
                    return;
                }
                if (result < 0) {
                    expression.add(new Token(TokenKind.MINUS, "-"));
                    result = -result;
                }
                var string = Double.toString(result);
                if (string.endsWith(".0")) {
                    string = string.substring(0, string.length() - 2);
                }
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
                Token removed = ((LinkedList<Token>) expression).removeLast();
                updateDisplay();
                System.out.println("Deleted token: " + removed.text);
            }
        });
        var btns = new Button[][]{
            {
                clearBtn,
                new TokenButton(TokenKind.LPAREN, "()"),
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

        stage.setTitle("My First JavaFX App");
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

    static class Token {
        TokenKind type;
        String text;
        int value = 0;
        double dvalue = 0.0;

        public Token(TokenKind type, int value) {
            this.type = type;
            this.value = value;
            this.text = Integer.toString(value);
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