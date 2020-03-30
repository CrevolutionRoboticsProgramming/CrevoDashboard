package org.frc2851;

public class Config
{
    public String hostIP;
    public int sendPort;
    public int receivePort;
    public String videoReceiverUrl;

    public Config()
    {
    }

    public Config(String hostIP, int sendPort, int receivePort, String videoReceiverUrl)
    {
        this.hostIP = hostIP;
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        this.videoReceiverUrl = videoReceiverUrl;
    }
}
