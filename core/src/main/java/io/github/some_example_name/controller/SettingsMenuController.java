package io.github.some_example_name.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.HollowKnightGame; // اسم کلاس اصلی خودت را بگذار
import io.github.some_example_name.view.SettingsMenuView;

public class SettingsMenuController implements Screen {
    private HollowKnightGame game;
    private SettingsMenuView view;

    public SettingsMenuController(HollowKnightGame game) {
        this.game = game;
        this.view = new SettingsMenuView();
        setupListeners();
    }

    private void setupListeners() {
        view.musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = view.musicVolumeSlider.getValue() / 100f;
                if (game.menuMusic != null) {
                    game.menuMusic.setVolume(volume);
                }
            }
        });

        view.musicToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClickSound();
                if (game.menuMusic.isPlaying()) {
                    game.menuMusic.pause();
                    view.musicToggleBtn.setText("Music: OFF");
                } else {
                    game.menuMusic.play();
                    view.musicToggleBtn.setText("Music: ON");
                }
            }
        });

        view.sfxToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClickSound();
                game.isSfxEnabled = !game.isSfxEnabled;

                if (game.isSfxEnabled) {
                    view.sfxToggleBtn.setText("SFX: ON");
                } else {
                    view.sfxToggleBtn.setText("SFX: OFF");
                }
            }
        });

        view.resetAudioBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClickSound();

                view.musicVolumeSlider.setValue(100f);
                if (game.menuMusic != null) {
                    game.menuMusic.setVolume(1.0f);
                    if (!game.menuMusic.isPlaying()) {
                        game.menuMusic.play();
                    }
                }
                view.musicToggleBtn.setText("Music: ON");

                game.isSfxEnabled = true;
                view.sfxToggleBtn.setText("SFX: ON");
            }
        });

        view.backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClickSound();
                view.fadeOutTransition(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MainMenuController(game));
                    }
                });
            }
        });
    }

    private void playClickSound() {
        if (game.isSfxEnabled && view.clickSound != null) {
            view.clickSound.play(0.5f);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(view.getStage());

        if (game.menuMusic != null) {
            view.musicVolumeSlider.setValue(game.menuMusic.getVolume() * 100f);
            if (game.menuMusic.isPlaying()) {
                view.musicToggleBtn.setText("Music: ON");
            } else {
                view.musicToggleBtn.setText("Music: OFF");
            }
        }

        if (game.isSfxEnabled) {
            view.sfxToggleBtn.setText("SFX: ON");
        } else {
            view.sfxToggleBtn.setText("SFX: OFF");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        view.render();
    }

    @Override
    public void resize(int width, int height) {
        view.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        view.dispose();
    }
}
