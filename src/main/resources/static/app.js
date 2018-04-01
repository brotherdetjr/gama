//import pixoterm from 'https://brotherdetjr-time.firebaseapp.com/pixoterm.js'
import pixoterm from './pixoterm.js'

pixoterm(
    {
        spritePack: 'sprites.json',
        spriteComposition: 'composition.json'
    },
    PIXI, $
).done((term) => {
    document.body.appendChild(term.view);
    const socket = new WebSocket('ws://localhost:8080/websocket?token=' + token);
    let perception = null;
    socket.onmessage = (event) => {
        perception = JSON.parse(event.data);
        term.render(perception);
    }
    term.view.addEventListener(
        'gridpointertap',
        (event) => {
            if (perception != null) {
                socket.send(JSON.stringify({row: event.detail.row, column: event.detail.column}));
            }
        }
    );
});