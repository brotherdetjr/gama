//import pixoterm from 'https://brotherdetjr-time.firebaseapp.com/pixoterm.js'
import pixoterm from './pixoterm.js'

const toDirection = (event, perception) => {
    const verticalVelocity = event.detail.row - perception.screenHeightInSprites / 2;
    const horizontalVelocity = event.detail.column - perception.screenWidthInSprites / 2;
    if (verticalVelocity != 0 && Math.abs(verticalVelocity) > Math.abs(horizontalVelocity)) {
        return verticalVelocity < 0 ? 'UP' : 'DOWN';
    } else if (horizontalVelocity != 0 && Math.abs(horizontalVelocity) > Math.abs(verticalVelocity)) {
        return horizontalVelocity < 0 ? 'LEFT' : 'RIGHT';
    } else {
        return null;
    }
};

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
                const direction = toDirection(event, perception);
                if (direction != null) {
                    socket.send(JSON.stringify({direction: direction}));
                }
            }
        }
    );
});