package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.controller.MainMenuController;
import io.github.some_example_name.model.GameData;

public class HollowKnightGame extends Game {
    private SpriteBatch batch;
    public GameData gameData;
    public Music menuMusic, greenPathMusic, greenPathatmosMusic, greenPahtFightMusic;

    public boolean isSfxEnabled = true, greenPath = false, greenPathFight = false;


    @Override
    public void create() {
        batch = new SpriteBatch();
        gameData = new GameData();

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Title.wav"));
        greenPathMusic = Gdx.audio.newMusic(Gdx.files.internal("green path map/S5 Green Path Main.wav"));
        greenPathatmosMusic = Gdx.audio.newMusic(Gdx.files.internal("green path map/green_path_atmos_loop.wav"));
        greenPahtFightMusic = Gdx.audio.newMusic(Gdx.files.internal("green path map/S5 Green Path Action.wav"));

        menuMusic.setLooping(true);
        greenPathMusic.setLooping(true);
        greenPathatmosMusic.setLooping(true);
        greenPahtFightMusic.setLooping(true);

        if (greenPath) {
            if (greenPathFight) {
                greenPahtFightMusic.play();
            }
            else {
                greenPathatmosMusic.play();
                greenPathMusic.play();
            }
        }
        else {
            menuMusic.play();
        }

        this.setScreen(new MainMenuController(this));

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (menuMusic != null) menuMusic.dispose();
    }
}
