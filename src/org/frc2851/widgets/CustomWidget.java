package org.frc2851.widgets;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class CustomWidget extends Pane
{
    public CustomWidget(String fileName)
    {
        super();

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
            loader.setController(this);
            getChildren().add(loader.load());

            getStylesheets().add(getClass().getResource("../../css/DarkMode.css").toExternalForm());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
