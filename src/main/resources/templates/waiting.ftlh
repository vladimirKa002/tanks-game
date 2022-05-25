<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tanks. Waiting...</title>
    <link rel="icon" type="image/png" href="/favicon.png"/>
    <link href="/styles/controls.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script type="text/javascript" src="/scripts/waiting.js"></script>
</head>
<body>
<script>var game_mode = '${game_mode}'</script>
<div class="center_panel">
    <img style="width:250px;height:102px;margin-top:10px;" src="/graphics/logo.png" alt="Logo">
    <br>
    <br>
    <div id="ready_players" class="title_text_small">Waiting for players...</div>
    <br>
    <form action="/main">
        <input class="input_button" type="submit" value="Menu"/>
    </form>
</div>
</body>
</html>