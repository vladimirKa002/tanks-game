<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tanks. Fight!</title>
    <link rel="icon" type="image/png" href="/favicon.png"/>
    <link href="./styles/controls.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script type="text/javascript" src="./scripts/game.js"></script>
</head>
<body>
<div class="block center">
    <div class="tools left">
        <img style="width:180px;height:73px;margin-top:20px;" src="/graphics/logo.png" alt="Logo">
        <br>
        <form action="/main">
            <input class="input_button" type="submit" value="Menu"/>
        </form>
    </div>
    <div style="width: 50px;"></div>
    <canvas id="canvas_game"></canvas>
    <div style="width: 50px;"></div>
    <div class="tools">
        <div class="title_text_small">Teams</div>
        <br>
        <div class="text" id="teams_block"></div>
        <br>
        <br>
        <div id="you_title" class="title_text_small">You</div>
        <br>
        <div class="text" id="user_block"></div>
        <br>
        <br>
        <div id="base_title" class="title_text_small">Base</div>
        <br>
        <div class="text" id="base_block"></div>
    </div>
</div>
</body>
</html>