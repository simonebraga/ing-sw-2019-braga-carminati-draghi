### client.jar options
.jar file must be run using the following command (JavaFX folder must be in the same folder of the .jar file)  
java --module-path javafx-sdk-12.0.1/lib --add-modules javafx.controls,javafx.fxml -jar client.jar  
.jar file can be run with some options, that override the default ones in the configuration files. Default options run the game on local host, with the gui  
-interface (GUI | CLI)  
-resolution (Window height of the GUI, in pixels)  
-serverIp (IP address of the server)  
-clientIp (IP address of the client. Specify this if server.jar is not running on the same machine of client.jar)

### server.jar options
.jar file must be run using the following command  
java -jar server.jar  
.jar file can be run with some options, that override the default ones in the configuration files. Default options run the game on local host  
-serverIp (IP address of the server. Specify this if server.jar should be able to accept connections from clients that are not running on its machine)  
-loginTimer (Lobby countdown length, in seconds)  
-inactivityTime (Timer of the single network request, in seconds)
