const createTransitions = (sprite, ts, transitionStates) => {
    if (ts != null) {
        ts.forEach(
            (t) => transitionStates.push({
                transitionName: t.name,
                sprite: sprite,
                params: t.params,
                value: transitions[t.name].constructor(sprite, t.params)
            })
        );
    }
};

const applyFilters = (sprite, filters) => {
    if (filters != null) {
        filters.forEach((filter) => {
            spriteFilters[filter.name](sprite, filter.params);
        });
    }
};

const createSprite = (sp, textures) => {
    return sp.frames.length == 1 ?
        new PIXI.Sprite(textures[sp.frames[0]]) :
        new PIXI.extras.AnimatedSprite(sp.frames.map((name) => textures[name]));
};

const placeSprite = (sprite, entry, config) => {
    sprite.width = config.spriteWidthPx;
    sprite.height = config.spriteHeightPx;
    sprite.x = entry.column * config.spriteWidthPx;
    sprite.y = entry.row * config.spriteHeightPx;
};

const renderSprite = (entry, sp, config, transitionStates, textures) => {
    let sprite = createSprite(sp, textures);
    sprite.texture.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;
    createTransitions(sprite, sp.transitions, transitionStates);
    createTransitions(sprite, entry.transitions, transitionStates);
    placeSprite(sprite, entry, config);
    applyFilters(sprite, sp.filters);
    applyFilters(sprite, entry.filters);
    return sprite;
};

export const configDefaults = {
    spriteWidthPx: 32,
    spriteHeightPx: 32,
    screenWidthInSprites: 5,
    screenHeightInSprites: 5,
    scale: 2,
    animationFps: 12,
    backgroundColor: '0x1099bb',
    outerInSprites: 1
};

export const spriteFilters = {
    "flip": (sprite, params) => {
        if (params & 1) {
            sprite.anchor.x = 1;
            sprite.scale.x = -1;
        }
        if (params & 2) {
            sprite.anchor.y = 1;
            sprite.scale.y = -1;
        }
    },
    "shift": (sprite, params) => {
        if (params.direction == 'left') {
            sprite.x -= params.distancePx;
        } else if (params.direction == 'right') {
            sprite.x += params.distancePx;
        } else if (params.direction == 'up') {
            sprite.y -= params.distancePx;
        } else if (params.direction == 'down') {
            sprite.y += params.distancePx;
        }
    }
};

export const transitions = {
    "animate": {
        "constructor": (sprite, params) => {
            sprite.gotoAndStop(params.sequence[0]);
            const obj = new Object();
            obj.pos = 0;
            return obj;
        },
        "mutator": (sprite, params, state) => {
            sprite.gotoAndStop(params.sequence[state.pos++]);
            if (state.pos >= params.sequence.length) {
                state.pos = params.loopFrom;
            }
            return state.pos != null;
        }
    },
    "move": {
        "constructor": (sprite, params) => {
            const obj = new Object();
            obj.pos = 0;
            return obj;
        },
        "mutator": (sprite, params, state) => {
            const step = Math.min(params.stepPx, params.distancePx - state.pos);
            if (params.direction == 'left') {
                sprite.x -= step;
            } else if (params.direction == 'right') {
                sprite.x += step;
            } else if (params.direction == 'up') {
                sprite.y -= step;
            } else if (params.direction == 'down') {
                sprite.y += step;
            }
            state.pos += step;
            return state.pos < params.distancePx;
        }
    }
};

export default function pixoterm(cfg, PIXI, $, Hammer) {
    const config = Object.assign({}, configDefaults, cfg);

    const result = $.Deferred();
    $.getJSON(config.spriteComposition).done((spriteComposition) => {
        let transitionStates;
        const app = new PIXI.Application(
            config.spriteWidthPx * config.screenWidthInSprites * config.scale,
            config.spriteHeightPx * config.screenHeightInSprites * config.scale,
            {backgroundColor : parseInt(config.backgroundColor)}
        );
        app.stage.scale.x = config.scale;
        app.stage.scale.y = config.scale;

        app.stage.interactive = true;
        app.stage.on('pointertap', (event) => {
            const g = event.data.global;
            const s = app.stage.scale;
            const row = Math.trunc(g.y / (config.spriteHeightPx * s.y));
            const column = Math.trunc(g.x / (config.spriteWidthPx * s.x));
            app.view.dispatchEvent(
                new CustomEvent(
                    'gridpointertap',
                    {
                        detail: {row: row, column: column},
                        bubbles: false,
                        cancelable: true
                    }
                )
            );
        });

        $(app.view)
            .css('image-rendering', '-moz-crisp-edges')
            .css('image-rendering', 'optimizeSpeed')
            .css('image-rendering', '-webkit-optimize-contrast')
            .css('image-rendering', '-o-crisp-edges')
            .css('image-rendering', 'pixelated')
            .css('-ms-interpolation-mode', 'nearest-neighbor');

        app.loader.add(config.spritePack).load((loader, resources) => {
            let tickCount = 0;

            app.ticker.add((delta) => {
                if (transitionStates == null || ++tickCount < app.ticker.FPS / config.animationFps) {
                    return;
                }
                tickCount = 0;
                transitionStates = transitionStates.filter((s) =>
                    transitions[s.transitionName].mutator(s.sprite, s.params, s.value)
                );
            });

            result.resolve({
                view: app.view,
                render: (map) => {
                    transitionStates = [];
                    app.stage.removeChildren();

                    const byZ = {};
                    map.forEach((entry) => {
                        const z = entry != null ?
                            entry.zIndex != null ? entry.zIndex : spriteComposition[entry.sprite].zIndex :
                            Number.MAX_VALUE;
                        if (byZ[z] == null) {
                            byZ[z] = [];
                        }
                        byZ[z].push(entry);
                    });

                    for (let z in byZ) {
                        byZ[z].forEach((entry) =>
                            app.stage.addChild(
                                renderSprite(
                                    entry,
                                    spriteComposition[entry.sprite],
                                    config,
                                    transitionStates,
                                    resources[config.spritePack].textures
                                )
                            )
                        );
                    }
                },
                set scale(value) {
                    app.stage.scale.x = value;
                    app.stage.scale.y = value;
                    app.renderer.resize(
                        config.spriteWidthPx * config.screenWidthInSprites * value,
                        config.spriteHeightPx * config.screenHeightInSprites * value
                    );
                },
                get scale() {
                    return app.stage.scale.x;
                }
            });
        });

    });

    return result;

}
