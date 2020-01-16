# CrevoDashboard

### Crevolution's JavaFX UDP communication GUI for interfacing with our [Vision2020](https://github.com/CrevolutionRoboticsProgramming/Vision2020) vision processing program

## Usage

Download the latest release for your operating system and extract the files. Configure the settings and save them. Restart or press Enter in the host IP or send/receive port fields to update the UDPCommunicator. Then, run the vision program on the target device and click the Update Values button to sync the configuration. If you change the HSV values, the configuration is automatically sent to the target device. Otherwise, click the Transmit Data button to send your data. The Toggle Stream button sends a request to the target device to change its stream from the driver vision camera to the vision processing camera for tuning. Your ```GENERAL``` configuration will be stored in the config.yaml file.

## Problems

* SceneBuilder doesn't display custom nodes when using Gradle in any way, so we have to actually download dependencies

## Acknowledgements

YAML parsing done with [SnakeYAML](https://bitbucket.org/asomov/snakeyaml/)
