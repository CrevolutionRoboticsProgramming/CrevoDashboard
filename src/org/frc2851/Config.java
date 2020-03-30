package org.frc2851;

public class Config
{
    public String hostIP;
    public int sendPort;
    public int receivePort;

    public Config()
    {
    }

    public Config(String hostIP, int sendPort, int receivePort)
    {
        this.hostIP = hostIP;
        this.sendPort = sendPort;
        this.receivePort = receivePort;
    }
}
