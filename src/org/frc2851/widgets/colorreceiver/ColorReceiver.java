package org.frc2851.widgets.colorreceiver;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;
import org.frc2851.widgets.CustomWidget;

public class ColorReceiver extends CustomWidget
{
    @FXML
    private Rectangle mRectangle;
    @FXML
    private Label mLabel;

    public ColorReceiver()
    {
        super("ColorReceiver.fxml");

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("COLOR:", (message) ->
        {
            if (message.length() > 0)
            {
                mLabel.setText(message);
                if (message.contains("B"))
                {
                    mRectangle.setFill(Color.rgb(0, 255, 255));
                } else if (message.contains("G"))
                {
                    mRectangle.setFill(Color.rgb(0, 255, 0));
                } else if (message.contains("R"))
                {
                    mRectangle.setFill(Color.rgb(255, 0, 0));
                } else if (message.contains("Y"))
                {
                    mRectangle.setFill(Color.rgb(255, 255, 0));
                } else
                {
                    mRectangle.setFill(Color.rgb(100, 100, 100));
                }
            }
        }));
    }
}
