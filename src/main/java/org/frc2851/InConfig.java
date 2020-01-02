package org.frc2851;

public class InConfig
{
    private String mHostIP;
    private int mSendPort;
    private int mReceivePort;
    private int mLowHue;
    private int mLowSaturation;
    private int mLowValue;
    private int mHighHue;
    private int mHighSaturation;
    private int mHighValue;

    public InConfig()
    {}
    
    public InConfig(String hostIP, int sendPort, int receivePort, int lowHue, int lowSaturation,
                    int lowValue, int highHue, int highSaturation, int highValue)
    {
        mHostIP = hostIP;
        mSendPort = sendPort;
        mReceivePort = receivePort;
        mLowHue = lowHue;
        mLowSaturation = lowSaturation;
        mLowValue = lowValue;
        mHighHue = highHue;
        mHighSaturation = highSaturation;
        mHighValue = highValue;
    }

    public String getHostIP()
    {
        return mHostIP;
    }

    public void setHostIP(String hostIP)
    {
        mHostIP = hostIP;
    }

    public int getSendPort()
    {
        return mSendPort;
    }

    public void setSendPort(int sendPort)
    {
        mSendPort = sendPort;
    }

    public int getReceivePort()
    {
        return mReceivePort;
    }

    public void setReceivePort(int receivePort)
    {
        mReceivePort = receivePort;
    }

    public int getLowHue()
    {
        return mLowHue;
    }

    public void setLowHue(int lowHue)
    {
        mLowHue = lowHue;
    }

    public int getLowSaturation()
    {
        return mLowSaturation;
    }

    public void setLowSaturation(int lowSaturation)
    {
        mLowSaturation = lowSaturation;
    }

    public int getLowValue()
    {
        return mLowValue;
    }

    public void setLowValue(int lowValue)
    {
        mLowValue = lowValue;
    }

    public int getHighHue()
    {
        return mHighHue;
    }

    public void setHighHue(int highHue)
    {
        mHighHue = highHue;
    }

    public int getHighSaturation()
    {
        return mHighSaturation;
    }

    public void setHighSaturation(int highSaturation)
    {
        mHighSaturation = highSaturation;
    }

    public int getHighValue()
    {
        return mHighValue;
    }

    public void setHighValue(int highValue)
    {
        mHighValue = highValue;
    }
}
