var rnd = Math.floor(Math.random() * 3);

var images = ["forest", "city", "desert"]

window.addEventListener("load",function(){
    document.body.style.backgroundImage = "url(/graphics/background/" + images[rnd] + ".png)";
},false);