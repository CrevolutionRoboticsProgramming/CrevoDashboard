package org.frc2851.widgets.colorreceiver;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;
import org.frc2851.widgets.CustomWidget;

public class ColorReceiver extends CustomWidget
{
    @FXML
    private Rectangle rectangle;
    @FXML
    private Text text;

    public ColorReceiver()
    {
        super("ColorReceiver.fxml");

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("COLOR:", (message) ->
        {
            if (message.length() > 0)
            {
                text.setText(message);
                if (message.contains("B"))
                {
                    rectangle.setFill(Color.rgb(0, 255, 255));
                } else if (message.contains("G"))
                {
                    rectangle.setFill(Color.rgb(0, 255, 0));
                } else if (message.contains("R"))
                {
                    rectangle.setFill(Color.rgb(255, 0, 0));
                } else if (message.contains("Y"))
                {
                    rectangle.setFill(Color.rgb(255, 255, 0));
                } else
                {
                    rectangle.setFill(Color.rgb(100, 100, 100));
                }
            }
        }));
    }
}
