### Client options
Jar file must be run using the following command (JavaFX folder must be in the same folder of the jar file)
```
java --module-path javafx-sdk-12.0.1/lib --add-modules javafx.controls,javafx.fxml -jar client.jar
```
Jar file can be run with some options, that override the default ones in the configuration files. Default options run the game on local host, with GUI
* `interface <gui|cli>`
* `resolution <height>`
* `serverIp <ip>`
* `clientIp <ip>` (Specify this only if server.jar is not running on the same machine of client.jar)

### Server options
Jar file must be run using the following command
```
java -jar server.jar
```
Jar file can be run with some options, that override the default ones in the configuration files. Default options run the game on local host
* `serverIp <ip>`
* `loginTimer <seconds>`
* `inactivityTime <seconds>`
