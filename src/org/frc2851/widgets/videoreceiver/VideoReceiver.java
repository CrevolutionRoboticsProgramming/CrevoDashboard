package org.frc2851.widgets.videoreceiver;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.frc2851.Constants;
import org.frc2851.widgets.CustomWidget;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

// Code for actually receiving the stream adapted (read: stolen) from:
// http://thistleshrub.net/www/index.php?controller=posts&action=show&id=2012-05-13DisplayingStreamedMJPEGinJava.txt
// We always take about a 5 FPS hit because it takes time to read from the URL
public class VideoReceiver extends CustomWidget
{
    private InputStream mVideoStream;
    private static final String CONTENT_LENGTH_LABEL = "Content-Length: ";
    private static final String TIMESTAMP_LABEL = "X-Timestamp: ";
    private int mFrameRetrieverDelayMs = 10;
    private int mFrameDisplayerDelayMs = 5;
    private boolean mIsRunning = true;

    private Image mRetrievedFrame;
    private long mLastFpsGrabTime;
    private int mFramesRetrievedSinceLastFpsGrab = 0;
    private int mFps;

    @FXML
    private ImageView mImageView;
    @FXML
    private TextField mUrlTextField;
    @FXML
    private Text mFpsText;
    @FXML
    private Button mRefreshButton;

    public VideoReceiver()
    {
        super("VideoReceiver.fxml");

        mUrlTextField.setText(Constants.videoReceiverUrl);

        try
        {
            openVideoStream(Constants.videoReceiverUrl);
        } catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        mUrlTextField.setOnAction(action ->
        {
            try
            {
                openVideoStream(mUrlTextField.getText());
            } catch (IOException e)
            {
                e.printStackTrace();
                return;
            }

            Constants.videoReceiverUrl = mUrlTextField.getText();
        });

        mRefreshButton.setOnAction((action) -> mUrlTextField.fireEvent(action));

        Thread frameRetrieverThread = new Thread(() ->
        {
            while (true)
            {
                if (mIsRunning)
                {
                    try
                    {
                        StringWriter headerStringWriter = new StringWriter(128);
                        boolean haveHeader = false;
                        int currByte;

                        String header = "";

                        // Adds to the header until we reach the timestamp (then we know we have what we need)
                        while ((currByte = mVideoStream.read()) > -1 && !haveHeader)
                        {
                            headerStringWriter.write(currByte);

                            String headerStringFragment = headerStringWriter.toString();
                            if (headerStringFragment.indexOf(TIMESTAMP_LABEL) > 0)
                            {
                                haveHeader = true;
                                header = headerStringFragment;
                            }
                        }

                        // 255 indicates the start of the jpeg image
                        while ((mVideoStream.read()) != 255)
                        {
                            // just skip extras
                        }

                        int contentLength = getContentLength(header);
                        byte[] imageBytes = new byte[contentLength + 1];

                        // since we ate the original 255, shove it back in
                        imageBytes[0] = (byte) 255;

                        int offset = 1;
                        int numRead;
                        while (offset < imageBytes.length && (numRead = mVideoStream.read(imageBytes, offset, imageBytes.length - offset)) >= 0)
                        {
                            offset += numRead;
                        }

                        mRetrievedFrame = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(imageBytes)), null);

                        ++mFramesRetrievedSinceLastFpsGrab;

                        if (System.currentTimeMillis() - mLastFpsGrabTime >= 1000 || mLastFpsGrabTime == 0)
                        {
                            mFps = mFramesRetrievedSinceLastFpsGrab;
                            mFramesRetrievedSinceLastFpsGrab = 0;
                            mLastFpsGrabTime = System.currentTimeMillis();
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                try
                {
                    Thread.sleep(mFrameRetrieverDelayMs);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        frameRetrieverThread.start();

        final Timeline frameDisplayer = new Timeline(
                new KeyFrame(Duration.ZERO, frameDisplayerEvent ->
                {
                    mImageView.setImage(mRetrievedFrame);
                    mFpsText.setText("FPS: " + mFps);
                }),
                new KeyFrame(Duration.millis(mFrameDisplayerDelayMs))
        );
        frameDisplayer.setCycleCount(Timeline.INDEFINITE);
        frameDisplayer.play();
    }

    private void openVideoStream(String url) throws IOException
    {
        mIsRunning = false;
        try
        {
            URLConnection mUrlConnection = new URL(url).openConnection();
            mUrlConnection.setReadTimeout(1000);
            mUrlConnection.connect();
            mVideoStream = mUrlConnection.getInputStream();
            mIsRunning = true;
        } catch (ConnectException e)
        {
            System.out.println("Connection to " + url + " was refused");
        }
    }

    private int getContentLength(String header)
    {
        int indexOfContentLength = header.indexOf(CONTENT_LENGTH_LABEL);
        int lengthStartPosition = indexOfContentLength + CONTENT_LENGTH_LABEL.length();
        int indexOfEoL = header.indexOf('\n', indexOfContentLength);

        if (indexOfEoL != -1)
            return Integer.parseInt(header.substring(lengthStartPosition, indexOfEoL).trim());
        else
            return 0;
    }
}
