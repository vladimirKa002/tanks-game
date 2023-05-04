<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Танки. Ожидание игроков...</title>
    <link rel="icon" type="image/png" href="/favicon.png"/>
    <link href="./styles/blocks.css" rel="stylesheet">
    <link href="./styles/input_buttons.css" rel="stylesheet">
    <link href="./styles/texts.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script type="text/javascript" src="/scripts/waiting.js"></script>
</head>
<body>
<script>var game_mode = '${game_mode}'</script>
<div class="center_panel">
    <img style="width:250px;height:102px;margin-top:10px;" src="/graphics/logo-rus.png" alt="Logo">
    <div class="space"></div>
    <div class="space"></div>
    <div id="ready_players" class="title_text_small">Ожидание игроков...</div>
        <div class="space"></div>
    <form action="/main">
        <input class="input_button" type="submit" value="Меню"/>
    </form>
</div>
</body>
</html>