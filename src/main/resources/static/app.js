import pixoterm from 'https://brotherdetjr-time.firebaseapp.com/pixoterm.js'

pixoterm(
    {
        outerInSprites: 2,
        screenWidthInSprites: 5,
        screenHeightInSprites: 5,
        spritePack: 'sprites.json',
        spriteComposition: 'composition.json'
    },
    PIXI, $
).done((term) => {
    term.view.addEventListener(
        'gridpointertap',
        (event) => console.log(event.detail.row + " / " + event.detail.column)
    );
    document.body.appendChild(term.view);
    var socket = new WebSocket('ws://localhost:8080/websocket?token=' + token);
    socket.onmessage = (event) => term.render(JSON.parse(event.data));
});