# README - PCSS GROUP 306 MINIPROJECT
Tank the Game!
This is Tank the Game a multiplayer game that can be played only with an active server. Join a server and play up to 4 players.

To join the server all you need is to be on the same network and know the ip of the computer running the server.

# How to install and run
First, you must download the javaFX SDK (https://www.jetbrains.com/help/idea/javafx.html#download-javafx) and unpack it somewhere on your computer. We used intelliJ for the project, so the following will describe how to set up javaFX with intelliJ. Go to file, and open up “project structure”. Go to the “Libraries” option, and a new project library. The javaFX SDK contains a “lib” folder, add that folder to as a new project library. The next step is to “Run” and open up “configurations”. Here, add the VM option “ --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml ”. Instead of  %PATH_TO_FX%, enter your path to the unpacked javaFX SDK. You should now be ready to run the project.

# How to run the game and the server
1. Run the server: the class you should run is called "Server.java".
2. Write a port in the console. This should be 8000.
3. Run the client: the class you should run is called "Lobby.java".
4. Enter your name in the console.
5. Enter the IP of the server (the server can get this by writing "ipconfig" in the command prompt of their computer).
6. Either join or create a lobby (a lobby can have up to 4 clients).
7. Start the game by typing "ready".

# Gameplay
Use the arrow keys to move your tank - the right and left arrow keys will rotate your tank either clockwise or counterclockwise. Use the up and down arrow keys to move either forwards or backwards.
Use the space key to fire a bullet and shoot the other tanks. The bullet will only be active for a specified amount of time and you can only have X bullets active at a time.
When you get hit you die. Be the last tank standing and win the game!
