package org.frc2851;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Vector;

public class UDPHandler
{
    private int mReceivePort;
    private int mBufferSize = 1024;
    private byte[] mBuffer = new byte[mBufferSize];
    private String mMessage = "";
    private DatagramChannel mChannel;
    private boolean mStopFlag = false;

    private Vector<MessageReceiver> mMessageReceivers = new Vector<>();

    public UDPHandler()
    {
    }

    public UDPHandler(int receivePort)
    {
        mReceivePort = receivePort;

        bind(mReceivePort);
    }

    public void addReceiver(MessageReceiver messageReceiver)
    {
        mMessageReceivers.add(messageReceiver);
    }

    // Timeout is expressed in milliseconds; if 0, no timeout
    public void sendTo(String message, String hostIP, int sendPort, int timeout)
    {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();

        try
        {
            mChannel.send(buffer, new InetSocketAddress(hostIP, sendPort));
        } catch (IOException e)
        {
            System.out.println("Failed to send message");
            e.printStackTrace();
        }

        if (timeout > 0)
        {
            long begin = System.currentTimeMillis();
            boolean received = false;

            while (System.currentTimeMillis() - begin < timeout)
            {
                if (getMessage().equals("received"))
                {
                    received = true;
                    clearMessage();
                    break;
                }
            }

            if (!received)
            {
                System.out.println("Timed out waiting for target to send confirmation of reception");
            }
        }
    }

    public void bind(int receivePort)
    {
        mReceivePort = receivePort;

        if (mChannel != null && mChannel.isConnected())
        {
            try
            {
                mChannel.disconnect();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            mChannel = DatagramChannel.open();
            mChannel.socket().bind(new InetSocketAddress(receivePort));
            mChannel.configureBlocking(false);
        } catch (SocketException e)
        {
            System.out.println("Failed to bind to port " + receivePort);
            e.printStackTrace();
            mStopFlag = true;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        final Timeline updater = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                {
                    try
                    {
                        ByteBuffer buffer = ByteBuffer.allocate(2048);
                        buffer.clear();
                        mChannel.receive(buffer);

                        // The buffer is filled with a character equating to 0x0  at the end and I don't know how to escape it so it
                        // knows which character it is. This assumes that the last character is 0x0 and uses it as a reference.
                        mMessage = new String(buffer.array()).replaceAll(String.valueOf((char) buffer.array()[buffer.array().length - 1]), "");

                        for (MessageReceiver messageReceiver : mMessageReceivers)
                        {
                            messageReceiver.run(getMessage());
                        }
                    } catch (IOException e)
                    {
                        System.out.println("Could not receive message");
                        e.printStackTrace();
                    }
                }),
                new KeyFrame(Duration.millis(100))
        );
        updater.setCycleCount(Timeline.INDEFINITE);
        updater.play();
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void clearMessage()
    {
        mMessage = "";
    }

    public String getThisIP()
    {
        String returnString = "Cannot get this IP";

        try
        {
            returnString = InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException e)
        {
            System.out.println("Cannot get this IP");
            e.printStackTrace();
        }

        return returnString;
    }

    public int getThisPort()
    {
        return mReceivePort;
    }

    public interface StringConsumer
    {
        void accept(String value);
    }

    public static class MessageReceiver
    {
        private String mLabel;
        private StringConsumer mStringConsumer;

        public MessageReceiver(String label, StringConsumer stringConsumer)
        {
            mLabel = label;
            mStringConsumer = stringConsumer;
        }

        public void run(String message)
        {
            if (mLabel.length() <= message.length() && message.substring(0, mLabel.length()).equals(mLabel))
            {
                mStringConsumer.accept(message.substring(mLabel.length()));
            }
        }
    }
}
