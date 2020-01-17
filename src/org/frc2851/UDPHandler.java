package org.frc2851;

import java.io.IOException;
import java.net.*;
import java.util.Vector;

public class UDPHandler implements Runnable
{
    private int mReceivePort;
    private int mBufferSize = 1024;
    private byte[] mBuffer = new byte[mBufferSize];
    private String mMessage = "";
    private DatagramSocket mSocket;
    private boolean mStopFlag = false;

    private Vector<MessageReceiver> mMessageReceivers = new Vector<>();

    public UDPHandler()
    {
    }

    public UDPHandler(int receivePort)
    {
        this();

        mReceivePort = receivePort;

        bind(mReceivePort);
    }

    @Override
    public void run()
    {
        while (!mStopFlag)
        {
            try
            {
                DatagramPacket mPacket = new DatagramPacket(mBuffer, mBufferSize);
                mSocket.receive(mPacket);
                mMessage = new String(mPacket.getData(), 0, mPacket.getLength());

                for (MessageReceiver messageReceiver : mMessageReceivers)
                {
                    messageReceiver.run(getMessage());
                }
            } catch (IOException e)
            {
                System.out.println("Could not receive message");
                e.printStackTrace();
            }
        }
    }

    public void addReceiver(MessageReceiver messageReceiver)
    {
        mMessageReceivers.add(messageReceiver);
    }

    // Timeout is expressed in milliseconds; if 0, no timeout
    public void sendTo(String message, String hostIP, int sendPort, int timeout)
    {
        try
        {
            mSocket.send(new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(hostIP), sendPort));
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

        if (mSocket != null && mSocket.isBound())
            mSocket.close();

        try
        {
            mSocket = new DatagramSocket(new InetSocketAddress(receivePort));
        } catch (SocketException e)
        {
            System.out.println("Failed to bind to port " + receivePort);
            e.printStackTrace();
            mStopFlag = true;
        }

        Thread thread = new Thread(this);
        thread.start();
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
