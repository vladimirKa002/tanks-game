var stompClient = null;

const urlParams = new URLSearchParams(window.location.search);
var room_id = urlParams.get("room_id");
var session_id = urlParams.get("session_id");

var canvas;
var ctx;

var updated = true;

var mouseCanvasPosition = null;
var keysPressed = {};
var graphics_loaded = false;

var tankUser = null;
var teamOpponent;
var fieldImage;
var canvasField;
var canvasObstacles;


///////////////////////////////////////////////////////////////////////////////////
// Functions for socket connection
///////////////////////////////////////////////////////////////////////////////////

// Initialization
window.onload = function (){
    // Getting canvas
    canvas = document.getElementById("canvas_game");
    ctx = canvas.getContext('2d');

    // Setting event handlers
    canvas.addEventListener('mousedown', (event) => {
       keysPressed["mouse"] = true;
       // console.log(event.offsetX + ", " +event.offsetY)
       event.preventDefault();
    });
    document.addEventListener('keydown', (event) => {
       keysPressed[event.keyCode] = true;
    });
    document.addEventListener('keyup', (event) => {
       delete this.keysPressed[event.keyCode];
    });

    canvas.width = 500;
    canvas.height = 500;

    // Connecting to the socket
    connect(room_id);
    // Starting the game
    start();

    canvas.addEventListener('mousemove', e => {
        mouseCanvasPosition = [e.offsetX, e.offsetY];
    });
    canvas.addEventListener('mouseleave', e => {
        mouseCanvasPosition = null;
    });
}

// Socket connection
function connect(game_id) {
    var socket = new SockJS('/tanks');
    stompClient = Stomp.over(socket);
    stompClient.debug = null
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/game.' + game_id, function(content) {
            updateGame(content.body);
        });
    });
}

// Game updating. Updates game everytime client receives data package from the server
function updateGame(game_state){
    updated = false;

    game_state = JSON.parse(game_state)

    ctx.drawImage(canvasField, 0, 0, game_state.field.units, game_state.field.units);

    // Setting tankUser
    for (var i = 0; i < game_state.tanks.length; i++){
        const tank = game_state.tanks[i];
        if (tank.user === session_id) {
            tankUser = tank;
            break;
        }
    }

    setBase(game_state.base)

    // Tank bodies
    for (var i = 0; i < game_state.tanks.length; i++){
        const tank = game_state.tanks[i];

        if (tank.health === 0) {
            tank_body_graphics = "destroyed_body"
        }
        else {
            tank_body_graphics = tank.id + "_body"
        }

        x = tank.position[0]
        y = tank.position[1]

        width = canvas.width * (tank.bodyShape[0] / game_state.field.units)
        height = canvas.height * (tank.bodyShape[1] / game_state.field.units)

        drawImageAtAngle(tank.rotation, x, y, width, height, graphics[tank_body_graphics], ctx);
    }

    // Projectiles
    for (var i = 0; i < game_state.projectiles.length; i++){
        const projectile = game_state.projectiles[i];

        const x = projectile.position[0]
        const y = projectile.position[1]

        const w = projectile.shape[0]
        const h = projectile.shape[1]

        const a = projectile.rotation

        ctx.translate(x, y);
        ctx.rotate(a * (Math.PI/180));

        ctx.fillStyle = 'white';
        ctx.fillRect(-w / 2, -h / 2, w, h);

        ctx.rotate(-a * (Math.PI/180));
        ctx.translate(-x, -y);
    }

    // Tank heads
    for (var i = 0; i < game_state.tanks.length; i++){
        const tank = game_state.tanks[i];

        if (tank.health === 0) {
            tank_head_graphics = "destroyed_head"
        }
        else {
            tank_head_graphics = tank.id + "_head"
        }

        x = tank.position[0]
        y = tank.position[1]

        h_x = tank.headPosition[0]
        h_y = tank.headPosition[1]

        hr_x = tank.headRotationPos[0]
        hr_y = tank.headRotationPos[1]

        h_width = canvas.width * (tank.headShape[0] / game_state.field.units)
        h_height = canvas.height * (tank.headShape[1] / game_state.field.units)


        ctx.translate(x, y);
        ctx.rotate(tank.rotation * (Math.PI/180));
        ctx.translate(hr_x, hr_y);
        ctx.rotate(tank.headRotation * (Math.PI/180));

        ctx.drawImage(graphics[tank_head_graphics], -h_width / 2, -h_height / 2 + h_y, h_width, h_height);

        ctx.rotate(-tank.headRotation * (Math.PI/180));
        ctx.translate(-hr_x, -hr_y);
        ctx.rotate(-tank.rotation * (Math.PI/180));
        ctx.translate(-x, -y);
    }

    // Obstacles
    ctx.drawImage(canvasObstacles, 0, 0, game_state.field.units, game_state.field.units);

    // Visual effects
    for (var i = 0; i < game_state.visualEffects.length; i++){
        const effect = game_state.visualEffects[i];

        x = effect.position[0]
        y = effect.position[1]

        width = canvas.width * (effect.shape[0] / game_state.field.units)
        height = canvas.height * (effect.shape[1] / game_state.field.units)

        drawImageAtAngle(effect.rotation, x, y, width, height, graphics[effect.graphics], ctx);
    }

    // Audio effects
    for (var i = 0; i < game_state.audioEffects.length; i++){
        const effect = game_state.audioEffects[i];
        playAudio(effect.name)
    }

    enemies_total = 0
    enemies_alive = 0
    allies_total = 0
    allies_alive = 0

    // HP's
    for (var i = 0; i < game_state.tanks.length; i++){
        const tank = game_state.tanks[i];

        x = tank.position[0]
        y = tank.position[1] + 5

        my_team = false;

        if (tankUser.id === tank.id){
            ctx.fillStyle = '#1E90FF';
            my_team = true;
            if (tank.health === 0) disable_actions = true;
        }
        else if (tankUser.team === tank.team) {
            ctx.fillStyle = 'green';
            my_team = true;
            allies_total++;
        }
        else {
            ctx.fillStyle = 'red';
            my_team = false;
            enemies_total++;
            teamOpponent = tank.team
        }

        if (tank.health === 0) {
            continue;
        }
        if (my_team) {
            if (tankUser.id != tank.id) allies_alive++;
        }
        else enemies_alive++;

        const r = 7
        ctx.beginPath();
        ctx.arc(x, y-3, r, 0, 2 * Math.PI);
        ctx.fill();
        ctx.font = '10px tahoma';
        ctx.fillStyle = 'white';
        ctx.textAlign = 'center';
        ctx.fillText(tank.health, x, y);
    }

    /*
        // Objects's
        for (var i = 0; i < obstacles.length; i++){
            const obst = obstacles[i];

            x = obst.position[0]
            y = obst.position[1]

            ctx.font = '10px tahoma';
            ctx.fillStyle = 'white';
            ctx.textAlign = 'center';
            ctx.fillText(x + ", " + y, x, y);
        }
    */


    if (game_state.state === 'finished') {
        stopAudio('back-sound')
        if (tankUser.team == game_state.winner) {
            img = graphics["victory"]
        }
        else {
            img = graphics["defeat"]
        }
        drawImageAtAngle(0, canvas.width / 2, canvas.height / 2, img.width, img.height,
            img, ctx)
        disable_actions = true
    }

    setAliveTanksUI(allies_alive, allies_total, enemies_alive, enemies_total);

    updated = true;
}


///////////////////////////////////////////////////////////////////////////////////
// Functions for game loop
///////////////////////////////////////////////////////////////////////////////////

var graphics = {};
var audios = {};
var obstacles;

// Requesting graphics
function start(){
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/game/init?room_id=" + room_id,
        cache: false,
        timeout: 180000,
        success: function (content) {
            images = Object.entries(content.images)
            audios = Object.entries(content.audios)

            for (const [key, value] of audios) {
                audios[key] = new Audio("data:audio/mp3;base64," + value);
            }

            num = images.length

            for (const [key, value] of images) {
                const image = new Image();
                image.onload = function () {
                    graphics[key] = image
                    num -= 1;
                    if (num === 0) {
                        setCanvasField(content.gameMap, content.gameState)
                        updateGame(content.gameState)
                        startUpdate();
                    }
                }
                image.src = "data:image/png;base64," + value;
            }
        }
    });
}

// Initializing canvas
function setCanvasField(map_content, game_state){
    game_state = JSON.parse(game_state)
    map_content = JSON.parse(map_content).obstacles
    obstacles = map_content

    canvasField = document.createElement('canvas');
    ctxField = canvasField.getContext('2d');
    canvasField.width = 500;
    canvasField.height = 500;

    ctxField.drawImage(graphics["field"], 0, 0, game_state.field.units, game_state.field.units);

    canvasObstacles = document.createElement('canvas');
    ctxObstacles = canvasObstacles.getContext('2d');
    canvasObstacles.width = 500;
    canvasObstacles.height = 500;

    for (var i = 0; i < map_content.length; i++){
        var obj = map_content[i];

        x = obj.position[0]
        y = obj.position[1]
        if (obj.type === 'circle') {
            w = obj.radius * 2
            h = obj.radius * 2
        }
        else {
            w = obj.shape[0]
            h = obj.shape[1]
        }
        drawImageAtAngle(obj.rotation, x, y, w, h, graphics[obj.graphics], ctxObstacles)
    }

    playAudio('back-sound', true)

    graphics_loaded = true;
}

// Game loop
function startUpdate() {
    loop = setInterval(function() {
        if (!updated || !graphics_loaded) return

        actions_str = ''

        if (!disable_actions) {
            actions = []
            a = setTankHead();
            if (a != '') actions.push(a)
            as = setMovement();
            if (as.length != 0)
                for (const element of as){
                    actions.push(element)
                }

            actions_str = ""
            for (const element of actions){
                actions_str = actions_str + ";" + element
            }

            if (actions_str.length > 0) actions_str = actions_str.substring(1)
        }

        updateGameRequest(actions_str)
    }, 1000/60);
}

// If some actions were performed, update game
var disable_actions = false;
function updateGameRequest(actions){
     stompClient.send("/app/game/update", {},
        JSON.stringify({'room_id':room_id, 'session_id':session_id, 'actions': actions}));
}


///////////////////////////////////////////////////////////////////////////////////
// Utility functions
///////////////////////////////////////////////////////////////////////////////////

// Drawing base
function setBase(base){
    x = base.position[0];
    y = base.position[1];
    r = base.radius

    ctx.lineWidth = 4;
    ctx.strokeStyle = 'white';
    ctx.beginPath();
    ctx.arc(x, y, r, 0, 2 * Math.PI);
    ctx.stroke();

    if (base.team != 'null') {
        if (tankUser.team === base.team) ctx.strokeStyle = 'green';
        else ctx.strokeStyle = 'red';

        ctx.beginPath();
        ctx.arc(x, y, r, - Math.PI / 2, 2 * Math.PI * base.progress / 100 - Math.PI / 2);
        ctx.stroke();
    }

    td_green = '<td style=\"vertical-align: bottom;color: green;\">'
    td_red = '<td style=\"vertical-align: bottom;color: red;\">'
    td = '<td style=\"vertical-align: bottom;\">'
    base_info = "<table cellspacing=\"0\"><tr><td style=\"color" +
        ": red;text-align: center;" +
        "vertical-align: bottom\">&#128681;</td>" + '!!!' +
        base.progress + "%</td></tr></table>";

    if (base.team != 'null') {
        if (tankUser.team === base.team) base_info = base_info.replace('!!!', td_green)
        else base_info = base_info.replace('!!!', td_red)
    }
    else base_info = base_info.replace('!!!', td)

    document.getElementById('base_block').innerHTML = base_info
    if (base.team != 'null') document.getElementById('base_title').innerHTML =
        "<div style=\"line-height: 25px;vertical-align: middle\">Base " + base.team + "</div>";
    else document.getElementById('base_title').innerHTML =
        "<div style=\"line-height: 25px;vertical-align: middle\">Base</div>";
}

// Tank head rotation
function setTankHead(){
    if (mouseCanvasPosition == null) return ''

    x = mouseCanvasPosition[0]
    y = mouseCanvasPosition[1]

    action = ''
    head_angle = (tankUser.rotation + tankUser.headRotation + 720) % 360
    mouse_angle = (angle(tankUser.position[0], tankUser.position[1], x, y) + 90 + 360) % 360

    min_angle = Math.min(mouse_angle, head_angle)
    max_angle = Math.max(mouse_angle, head_angle)
    difference_angle = max_angle - min_angle

    if (head_angle < mouse_angle) {
        if (difference_angle < 180) action = "head_right"
        if (difference_angle > 180) action = "head_left"
    }
    else {
        if (difference_angle > 180) action = "head_right"
        if (difference_angle < 180) action = "head_left"
    }

    if (difference_angle > 180) difference_angle = 360 - difference_angle;
    if (difference_angle < tankUser.headRotationSpeed) return '';

    return  action;
}

function setMovement(){
    actions_m = []

    if (!updated) return;

    if(keysPressed['38'] || keysPressed['87'] || keysPressed['119']) actions_m.push('forward');
    if(keysPressed['40'] || keysPressed['83'] || keysPressed['115']) actions_m.push('backward');
    if(keysPressed['37'] || keysPressed['65'] || keysPressed['97']) actions_m.push('left');
    if(keysPressed['39'] || keysPressed['68'] || keysPressed['100']) actions_m.push('right');
    if(keysPressed["mouse"]) {
        keysPressed["mouse"] = false;
        actions_m.push('shot');
    }

    return actions_m;
}

// Setting UI for alive tanks while game updating
function setAliveTanksUI(allies_alive, allies_total, enemies_alive, enemies_total){
    td = '<td style=\"vertical-align: bottom;\">'
    teams_info = '<table cellspacing=\"0\"><tr>' + td + tankUser.team +'</td>' + td + '!!!'
    for (var i = 0; i < allies_alive; i++) {
        teams_info+="<span class=\"circle green\"></span>"
    }
    for (var i = 0; i < allies_total - allies_alive; i++) {
        teams_info+="<span class=\"circle red\"></span>"
    }

    if (tankUser.health > 0) {
        teams_info = teams_info.replace('!!!', "<span class=\"circle blue\"></span>")
    }
    else {
        teams_info = teams_info.replace("!!!", "")
        teams_info += "<span class=\"circle red\"></span>"
    }

    teams_info += "</td></tr><tr>" + td + teamOpponent + "</td>" + td
    for (var i = 0; i < enemies_alive; i++) {
        teams_info+="<span class=\"circle green\"></span>"
    }
    for (var i = 0; i < enemies_total - enemies_alive; i++) {
        teams_info+="<span class=\"circle red\"></span>"
    }
    teams_info += "</td></tr></table>"
    document.getElementById('teams_block').innerHTML = teams_info;

    user_info = "<table cellspacing=\"0\"><tr><td style=\"color: red;text-align: center;" +
                    "vertical-align: bottom\">&#10084;</td>" + td +
                    tankUser.health + "%</td><tr>" + td + "&#128165;</td>" + td;
    if (tankUser.reloading < 100) user_info += tankUser.reloading + "%";
    else user_info += "Ready &#9989;";
    user_info += "</td></tr></table>"

    document.getElementById('user_block').innerHTML = user_info
    document.getElementById('you_title').innerHTML = "You " + tankUser.team;
}

function drawImageAtAngle(angle, x, y, width, height, image, ctx){
    ctx.translate(x, y);
    ctx.rotate(angle * (Math.PI/180));
    ctx.drawImage(image, -width / 2, -height / 2, width, height);
    ctx.rotate(-angle * (Math.PI/180));
    ctx.translate(-x, -y);
}

function angle(cx, cy, ex, ey) {
  var dy = ey - cy;
  var dx = ex - cx;
  var theta = Math.atan2(dy, dx); // range (-PI, PI]
  theta *= 180 / Math.PI; // rads to degs, range (-180, 180]
  if (theta < 0) theta = 360 + theta; // range [0, 360)
  return theta;
}

function playAudio(name, loop=false){
    let sound = audios[name]
    if (sound !== undefined){
        sound.loop = loop;
        sound.play()
    }
}

function stopAudio(name){
    let sound = audios[name]
    if (sound !== undefined) {
        sound.pause();
        sound.currentTime = 0;
    }
}