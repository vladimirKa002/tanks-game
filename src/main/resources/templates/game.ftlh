<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Танки. В бой!</title>
    <link rel="icon" type="image/png" href="/favicon.png"/>
    <link href="./styles/blocks.css" rel="stylesheet">
    <link href="./styles/input_buttons.css" rel="stylesheet">
    <link href="./styles/texts.css" rel="stylesheet">
    <link href="./styles/circles.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script type="text/javascript" src="./scripts/game.js"></script>
</head>
<body>
<div class="block center">
    <div class="tools">
        <div class="center_block">
            <div class="title_text_small">Справка</div>
        </div>
        <div class="space"></div>
        <div class="title_text_small">Цель</div>
        <div class="space"></div>
        <div class="text">Уничтожьте всю технику противника или захватите базу</div>
        <div class="space"></div>
        <div class="space"></div>
        <div class="title_text_small">Управление</div>
        <div class="space"></div>
        <div class="text">Вперед - ⬆️ или 'w'</div>
        <div class="text">Назад - ⬇️ или 's'</div>
        <div class="text">Влево - ⬅️ или 'a'</div>
        <div class="text">Вправо - ➡️ или 'd'</div>
        <div class="text">Стрельба - 🖱</div>
        <div class="space"></div>
        <div class="center_block">
            <form action="/main">
                <input class="input_button" type="submit" value="Меню"/>
            </form>
        </div>
    </div>
    <div style="width: 50px;"></div>
    <canvas id="canvas_game"></canvas>
    <div style="width: 50px;"></div>
    <div class="tools">
        <div class="center_block">
            <div id="map_name" class="title_text_small">Битва</div>
        </div>
        <div class="space"></div>
        <div class="title_text_small">Время</div>
        <div class="space"></div>
        <div class="text" id="time_block"></div>
        <div class="space"></div>
        <div class="space"></div>
        <div id="base_title" class="title_text_small">Захват базы</div>
        <div class="space"></div>
        <div class="text" id="base_block"></div>
        <div class="text" id="teams_block"></div>
        <div class="space"></div>
        <div class="space"></div>
        <div id="you_title" class="title_text_small">Состояние танка</div>
        <div class="space"></div>
        <div class="text" id="user_block"></div>
    </div>
</div>
</body>
</html>