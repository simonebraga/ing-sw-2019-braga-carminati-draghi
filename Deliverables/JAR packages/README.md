### Client options
The jar file must be run using the following command (JavaFX folder must be in the same folder as the jar file)
```
java --module-path javafx-sdk-12.0.1/lib --add-modules javafx.controls,javafx.fxml -jar client.jar
```
The jar file can be run with some options, that override the default ones in the configuration files. Default options run the game on the localhost, with the GUI
* `interface <gui|cli>`
* `resolution <height>`
* `serverIp <ip>`
* `clientIp <ip>` (Specify this only if server.jar is not running on the same machine of client.jar)

### Server options
The jar file must be run using the following command
```
java -jar server.jar
```
The jar file can be run with some options, that override the default ones in the configuration files. Default options run the game on the localhost
* `serverIp <ip>`
* `loginTimer <seconds>`
* `inactivityTime <seconds>`
