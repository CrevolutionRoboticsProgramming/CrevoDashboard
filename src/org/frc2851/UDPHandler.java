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
    private int mBufferSize = 8;
    private byte[] mBuffer = new byte[mBufferSize];
    private String mMessage = "";

    private DatagramSocket mServerSocket;
    private DatagramPacket mPacket = new DatagramPacket(mBuffer, mBufferSize);
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
                mServerSocket.receive(mPacket);
                mMessage = new String(mPacket.getData(), 0, mPacket.getLength());
            } catch (java.io.IOException e)
            {
                System.out.println("Cannot receive message");
                e.printStackTrace();
            }
        }
    }

    public void send(String message)
    {
        try
        {
            mSendingSocket.send(new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(mHostIP), mSendPort));
        } catch (IOException e)
        {
            System.out.println("Failed to send message");
            e.printStackTrace();
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
