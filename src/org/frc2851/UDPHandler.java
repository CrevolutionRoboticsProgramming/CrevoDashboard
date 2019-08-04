package org.frc2851;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPHandler implements Runnable
{
    private String mHostIP;
    private int mSendPort;
    private int mReceivePort;
    private int mHeaderSize = 4;
    private int mBufferSize = 1024;
    private byte[] mBuffer = new byte[mBufferSize];
    private String mMessage = "";

    private DatagramSocket mServerSocket;
    private DatagramPacket mPacket;
    private DatagramSocket mSendingSocket;

    private Thread mThread = new Thread(this);

    public UDPHandler(String hostIP, int sendPort, int receivePort)
    {
        mHostIP = hostIP;
        mSendPort = sendPort;
        mReceivePort = receivePort;

        try
        {
            mServerSocket = new DatagramSocket(new InetSocketAddress(mReceivePort));
            mSendingSocket = new DatagramSocket();
        } catch (java.net.SocketException e)
        {
            System.out.println("Failed to instantiate server socket");
            e.printStackTrace();
        }

        mThread.start();
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                mPacket = new DatagramPacket(mBuffer, mBufferSize);
                mServerSocket.receive(mPacket);
                String message = new String(mPacket.getData(), 0, mPacket.getLength());
                int length = Integer.parseInt(message.substring(0, mHeaderSize));
                mMessage = message.substring(mHeaderSize, mHeaderSize + length);
            } catch (java.io.IOException e)
            {
                System.out.println("Cannot receive message");
                e.printStackTrace();
            }
        }
    }

    // Timeout is expressed in milliseconds; if 0, no timeout
    public void send(String message, int timeout)
    {
        // Fills first four bytes with message length
        String header = "";
        if (message.length() < 10)
            header += "000";
        else if (message.length() < 100)
            header += "00";
        else if (message.length() < 1000)
            header += "0";
        message = header + message.length() + message;

        try
        {
            mSendingSocket.send(new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(mHostIP), mSendPort));
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
}
