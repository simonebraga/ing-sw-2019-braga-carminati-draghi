# Prova Finale Ingegneria del Software 2019
## Gruppo AM42

- ###   10529465    Simone Braga ([@simonebraga](https://github.com/simonebraga))<br>simone1.braga@mail.polimi.it
- ###   10527022    Alessandro Carminati ([@AleCarminati](https://github.com/AleCarminati))<br>alessandro4.carminati@mail.polimi.it
- ###   10499079    Samuele Draghi ([@Draghi96](https://github.com/Draghi96))<br>samuele1.draghi@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| RMI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Persistence | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Domination or Towers modes | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

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
