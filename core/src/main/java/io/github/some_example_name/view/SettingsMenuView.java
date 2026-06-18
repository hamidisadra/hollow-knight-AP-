package io.github.some_example_name.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class SettingsMenuView {
    private Stage stage;
    private Skin skin;

    private Texture blackLayerTexture, bgTexture, beamTexture, titleTexture, gameBorderTexture, pointerTexture;
    public Sound clickSound;

    public TextButton backButton;

    private Texture sliderBgTexture;


    public Slider musicVolumeSlider;
    public Label musicVolumeLabel;

    public TextButton musicToggleBtn;
    public TextButton sfxToggleBtn;
    public TextButton resetAudioBtn;

    public SettingsMenuView() {
        stage = new Stage(new FitViewport(1920, 1080));
        skin = new Skin(Gdx.files.internal("fonts/hk-skin.json"));

        loadTextures();
        loadAudio();

        buildBgLayers();
        buildUILayer();

        fadeInTransition();
    }

    private void fadeInTransition() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        blackLayerTexture = new Texture(pixmap);
        pixmap.dispose();

        Image blackLayerImage = new Image(blackLayerTexture);
        blackLayerImage.setFillParent(true);
        blackLayerImage.setTouchable(Touchable.disabled);

        blackLayerImage.addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.fadeOut(1.5f),
            Actions.removeActor()
        ));

        stage.addActor(blackLayerImage);
    }

    public void fadeOutTransition(final Runnable onFinished) {
        stage.getRoot().setTouchable(Touchable.disabled);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        final Texture tempBlackTex = new Texture(pixmap);
        pixmap.dispose();

        Image blackOverlay = new Image(tempBlackTex);
        blackOverlay.setFillParent(true);
        blackOverlay.getColor().a = 0f;
        blackOverlay.setTouchable(Touchable.disabled);

        stage.addActor(blackOverlay);

        blackOverlay.addAction(Actions.sequence(
            Actions.fadeIn(0.5f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    onFinished.run();
                    tempBlackTex.dispose();
                }
            })
        ));
    }

    private void loadAudio() {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("ui_save.wav"));

        clickSound.play(0f);
    }

    private void loadTextures() {
        bgTexture = new Texture("Voidheart_menu_BG.png");
        beamTexture = new Texture("Main_Menu_Beam.png");
        titleTexture = new Texture("Options_title.png");
        gameBorderTexture = new Texture("border.png");
        pointerTexture = new Texture("main_menu_pointer_anim0010.png");

    }

    private Image createBlendedImage(Texture texture) {
        return new Image(texture) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                int srcFunc = batch.getBlendSrcFunc();
                int dstFunc = batch.getBlendDstFunc();
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                super.draw(batch, parentAlpha);
                batch.setBlendFunction(srcFunc, dstFunc);
            }
        };
    }

    private void buildBgLayers() {
        Image bgImage = new Image(bgTexture);
        bgImage.setFillParent(true);
        bgImage.setColor(Color.DARK_GRAY);
        stage.addActor(bgImage);

        Image beamImage = createBlendedImage(beamTexture);
        beamImage.setSize(800, 1080);
        beamImage.setPosition((1920 - 800) / 2f, 0);
        beamImage.getColor().a = 0.05f;

        beamImage.addAction(Actions.forever(
            Actions.sequence(
                Actions.alpha(0.20f, 3f),
                Actions.alpha(0.05f, 3f)
            )
        ));

        stage.addActor(beamImage);
    }

    private void buildUILayer() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Image titleImage = createBlendedImage(titleTexture);
        titleImage.setScaling(Scaling.fit);

        rootTable.add(titleImage).size(900, 250).padTop(80).padBottom(20).row();

        Table buttonsTable = new Table();

        musicVolumeSlider = new Slider(0f, 100f, 1f, false, createGlowingSliderStyle());
        musicVolumeSlider.setValue(100f);

        musicVolumeLabel = new Label("100%", skin);
        musicVolumeLabel.setColor(Color.WHITE);
        musicVolumeLabel.setFontScale(0.7f);

        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int volumeValue = (int) musicVolumeSlider.getValue();
                musicVolumeLabel.setText(volumeValue + "%");
            }
        });

        Table sliderRow = new Table();
        Label musicTitleLabel = new Label("Music Volume", skin);
        musicTitleLabel.setColor(Color.LIGHT_GRAY);

        sliderRow.add(musicTitleLabel).width(250).left().row();
        sliderRow.add(musicVolumeSlider).width(200).padRight(20);
        sliderRow.add(musicVolumeLabel).width(80).center();

        buttonsTable.add(sliderRow).padBottom(20).row();

        musicToggleBtn = new TextButton("Music: ON", skin);
        buttonsTable.add(createMenuRow(musicToggleBtn, pointerTexture)).padBottom(20).row();

        sfxToggleBtn = new TextButton("SFX: ON", skin);
        buttonsTable.add(createMenuRow(sfxToggleBtn, pointerTexture)).padBottom(20).row();

        resetAudioBtn = new TextButton("Reset Audio", skin);
        buttonsTable.add(createMenuRow(resetAudioBtn, pointerTexture)).padBottom(20).row();

        rootTable.add(buttonsTable).expand().center().row();

        backButton = new TextButton("BACK", skin);
        Table backRow = createMenuRow(backButton, pointerTexture);
        rootTable.add(backRow).padBottom(50).row();

        stage.addActor(rootTable);
    }

    private Slider.SliderStyle createGlowingSliderStyle() {
        Slider.SliderStyle style = new Slider.SliderStyle();

        Pixmap bgPixmap = new Pixmap(10, 4, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(1f, 1f, 1f, 0.3f));
        bgPixmap.fill();
        sliderBgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        style.background = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));

        TextureRegionDrawable glowingKnob = new TextureRegionDrawable(new TextureRegion(pointerTexture)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                int srcFunc = batch.getBlendSrcFunc();
                int dstFunc = batch.getBlendDstFunc();

                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                super.draw(batch, x, y, width, height);
                batch.setBlendFunction(srcFunc, dstFunc);
            }
        };

        glowingKnob.setMinWidth(35f);
        glowingKnob.setMinHeight(35f);
        style.knob = glowingKnob;

        return style;
    }

    private Table createMenuRow(TextButton button, Texture pointerTexture) {
        Table row = new Table();

        final Image leftPointer = new Image(pointerTexture);
        leftPointer.setScaling(Scaling.fit);
        leftPointer.getColor().a = 0f;

        TextureRegion rightRegion = new TextureRegion(pointerTexture);
        rightRegion.flip(true, false);
        final Image rightPointer = new Image(rightRegion);
        rightPointer.setScaling(Scaling.fit);
        rightPointer.getColor().a = 0f;


        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);

                leftPointer.clearActions();
                leftPointer.addAction(Actions.fadeIn(0.2f));
                rightPointer.clearActions();
                rightPointer.addAction(Actions.fadeIn(0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);

                leftPointer.clearActions();
                leftPointer.addAction(Actions.fadeOut(0.2f));
                rightPointer.clearActions();
                rightPointer.addAction(Actions.fadeOut(0.2f));
            }
        });

        float pointerSize = 35f;

        row.add(leftPointer).size(pointerSize, pointerSize).padRight(25);
        row.add(button).expandX().fillX();
        row.add(rightPointer).size(pointerSize, pointerSize).padLeft(25);

        return row;
    }

    public Stage getStage() {
        return stage;
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        if (skin != null) skin.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (beamTexture != null) beamTexture.dispose();
        if (blackLayerTexture != null) blackLayerTexture.dispose();
        if (titleTexture != null) titleTexture.dispose();
        if (gameBorderTexture != null) gameBorderTexture.dispose();
        if (pointerTexture != null) pointerTexture.dispose();
        if (clickSound != null) clickSound.dispose();
    }
}
