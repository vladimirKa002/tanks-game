<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Танки. Меню</title>
    <link rel="icon" type="image/png" href="/favicon.png"/>
    <link href="./styles/blocks.css" rel="stylesheet">
    <link href="./styles/input_buttons.css" rel="stylesheet">
    <link href="./styles/texts.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
</head>
<body>
<div class="center_panel">
    <img style="width:250px;height:102px;margin-top:10px;" src="/graphics/logo-rus.png" alt="Logo">
    <br>
    <br>
    <form action="/waiting/page">
        <input class="input_button" name="game_mode"  type="submit" value="В бой!"/>
    </form>
</div>
</body>
</html>