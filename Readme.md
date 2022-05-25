# Tanks
<img src="https://github.com/vladimirKa002/tanks-game/blob/main/src/main/resources/static/graphics/logo.png" width="200" title="logo">

Analogue of a gameplay of a world-famous World of Tanks game implemented in 2D.
The game was written purely by using Java Spring framework and Javascript. 
It is available on desktop-computer browsers (there were some performance 
issues on Firefox v100.0.2, on other browsers it should work fine).

To use the app launch docker container. Do this by using the following commands.  
Build image:&nbsp;&nbsp; docker build -t tanks-game .  
Launch:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp; docker run -p 8080:8080 tanks-game

## Implementation
Multiplayer connection is performed by using WebSockets. On both client side (browser)
and server side game loops (60 FPS) are working. On client side it sends requests via a socket
and attaches user actions and checks presence of any changes on the server.
While handling these requests, after applying all actions server converts 
the game object to json format (state) and then compares it with previous state. If they 
are not equal, it sends updated game state to all clients. The server game loop updates some game
features (base timer, projectiles, etc).

There are a number of gameobjects with individual parameters and functionalities.

![UML](readme/uml.png?raw=true "Simplified UML for Game classes")
Some parts of classes are intentionally omitted or simplified.

## Gameplay
First of all, go to menu page and select game mode. It can be duel (1 vs 1)
and 2x2 (team fight). After this you will be redirected to waiting page. It 
will be showing a number of connected users. Each user will have 
a unique session id parameter. After a sufficient number of users connect 
to the game, you will be redirected to the game page.

### Game field
During the initialization of the game, a new field is generated. It can
be any of three types: forest, city and desert. For each field there 
is a json file in static/maps folder which defines objects positions.
Tanks cannot shot and move through these obstacles.

### Tanks
Your tank will have a blue label, allies - green, enemies - red. Info bar on the 
right will display teams (alive players), your team, health points,
reloading progress and base capture progress. You can find health points of any
tank on corresponding labels.

### Controls
To **drive** the tank use keyboard:
'w' - move forward
's' - move backward
'a' - rotate left
'd' - rotate right
The **head** of a tank will always rotate towards the mouse cursor. To 
**shot** just left-click on the mouse on the game field.
![game2](readme/game2.png?raw=true "Forest")

### Shooting & Reloading
The closer you are to the enemy, there more damage the projectile will deal.
There is also some random effect that can increase damage. Make sure to shot
precisely, because **reloading time** is 3 seconds.
![game1](readme/game1.png?raw=true "Desert")

### Base capture
In order to capture the base your tank needs to stand on it. In total,
base capturing will take 10 seconds to take it. If there are several (>1) tanks from
the same team are capturing the base, it will be captured faster.
If the some of ally tanks were damaged on the base, there will be applied 
some time punishment. The enemy tank can pause base capturing if it will 
stand on the base with your team.
![game3](readme/game3.png?raw=true "City")

### Winner
In order to win you need to either destroy all enemy tanks or capture the base.

## Author notes
It was very good holidays spending, because I always 
wanted to create multiplayer app especially about tanks. Hopefully, I will find 
some people who will want to play with me.  
If you have any suggestions, feel free to contact me via email kvvgames18@gmail.com