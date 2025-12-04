package week14;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(SimpleFX.class, args);
    }
}

class SimpleFX extends Application {
    @Override
    public void start(Stage stage) {
        // 1. Create a Button
        Button btn = new Button("Click Me!");

        // 2. Define what happens when the button is clicked
        btn.setOnAction(event -> {
            System.out.println("Hello! JavaFX is working.");
            btn.setText("Clicked!");
        });

        // 3. Create a Layout (StackPane centers items)
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        // 4. Create the Scene (Width 300, Height 250)
        Scene scene = new Scene(root, 300, 250);

        // 5. Set up the Stage (The Window)
        stage.setTitle("My First JavaFX App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}