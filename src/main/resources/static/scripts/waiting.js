var stompClient = null;
var session_id;
var exit = true;

// Connecting to the socket. Waiting for other players to join
function connect(game_id) {
    var socket = new SockJS('/tanks');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/waiting.' + game_id, function(content) {
            data = JSON.parse(content.body)
            if (data.wait) {
                setReady(data.ready, data.num)
            }
            else {
                exit = false;
                window.location.href = '/game?room_id=' + game_id + "&session_id=" + session_id;
            }
        });
    });
}

// Disconnecting from the socket
function disconnect(){
    stompClient.disconnect();

    if (exit) {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/game/exit?session_id=" + session_id,
            cache: false,
            timeout: 180000,
            success: function (data) {
            },
            error: function (data) {
                alert("Error while execution");
            }
        });
    }
}

// Setting UI
function setReady(ready, num){
    ready_info = 'Waiting for players...'
    var total = 0;
    for (var i = 0; i < ready; i++) {
        if (total === 0) ready_info+="<span class=\"circle right green\"></span>"
        else ready_info+="<span class=\"circle green\"></span>"
        total++;
    }
    for (var i = 0; i < num - ready; i++) {
        if (total === 0) ready_info+="<span class=\"circle right red\"></span>"
        else ready_info+="<span class=\"circle red\"></span>"
        total++;
    }
    document.getElementById('ready_players').innerHTML = ready_info;
}

// Trying to join a game
function start(){
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/game/enter?game_mode=" + game_mode,
        cache: false,
        timeout: 180000,
        success: function (data) {
            // If not enough players ready, wait
            if (data.wait) {
                setReady(data.ready, data.num)
                session_id = data.session_id;
                connect(data.room_id);
            }
            else {
                exit = false;
                window.location.href = '/game?room_id=' + data.room_id + "&session_id=" + data.session_id;
            }
        },
        error: function (data) {
            alert("Error while execution");
        }
    });
}

window.onload = function(){
    start();
}

window.onbeforeunload = disconnect;