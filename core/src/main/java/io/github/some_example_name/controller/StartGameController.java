package io.github.some_example_name.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.HollowKnightGame;
import io.github.some_example_name.view.StartGameView;

public class StartGameController implements Screen {

    private HollowKnightGame game;
    private StartGameView view;

    public StartGameController(HollowKnightGame game) {
        this.game = game;
        this.view = new StartGameView();
        setupListeners();
    }

    private void setupListeners() {

        view.backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (game.isSfxEnabled) view.clickSound.play(0.5f);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                view.fadeOutTransition(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MainMenuController(game));
                    }
                });
            }
        });

        for (int i = 0; i < 4; i++) {
            final int slotIndex = i;

            view.saveSlots[i].addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (game.isSfxEnabled) view.clickSound.play(0.5f);
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    view.fadeOutTransition(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Slot " + (slotIndex + 1) + " clicked!");
                        }
                    });
                }
            });
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(view.getStage());
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        view.render();
    }

    @Override
    public void resize(int width, int height) {
        view.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        view.dispose();
    }


}
