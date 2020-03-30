package org.frc2851;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.frc2851.widgets.crevodashboard.CrevoDashboard;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        stage.setOnCloseRequest(e ->
                System.exit(0));

        stage.setTitle("CrevoDashboard");
        stage.setScene(new Scene(new CrevoDashboard()));
        stage.show();
    }
}