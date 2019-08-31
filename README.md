# VisionCommunicator

### Crevolution's JavaFX UDP communication GUI for interfacing with our [OffseasonVision2019](https://github.com/CrevolutionRoboticsProgramming/OffseasonVision2019) vision processing program

## Usage

Download the latest release for your operating system and extract the files. Run the program, configure the IP and ports of the target device, and restart the program for these changes to take effect. Then, run the vision program on the target device and click the Update Values button to sync the configuration. If you change the HSV values, the configuration is automatically sent to the target device. Otherwise, click the Transmit Data button to send your data. The Toggle Stream button sends a request to the target device to change its stream from the driver vision camera to the vision processing camera for tuning. Your ```GENERAL``` configuration will be stored in the profile.txt file in the ```app``` folder on Windows or next to the .jar file on Ubuntu. The Ubuntu version requires you to run the .jar with the Oracle-provided JDK 8.
