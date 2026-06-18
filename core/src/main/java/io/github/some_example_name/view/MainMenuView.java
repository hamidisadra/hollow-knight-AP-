package io.github.some_example_name.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuView {
    private Stage stage;
    private Skin skin;

    private Texture blackLayerTexture;
    private Texture bgTexture, beamTexture, titleTexture, cherryTexture, hiddenDreamTexture, pointerTexture;

    public TextButton startButton, settingsButton, guideButton, achievementsButton, quitButton;

    public Sound clickSound;

    public MainMenuView() {
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
            Actions.fadeOut(2.5f),
            Actions.removeActor()
        ));

        stage.addActor(blackLayerImage);
    }

    public void fadeOutTransition(final Runnable onFinished) {
        stage.getRoot().setTouchable(Touchable.disabled);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        final Texture BlackTex = new Texture(pixmap);
        pixmap.dispose();

        Image blackOverlay = new Image(BlackTex);
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
                    BlackTex.dispose();
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
        titleTexture = new Texture("vheart_title.png");
        cherryTexture = new Texture("team_cherry_logo_main_menu.png");
        hiddenDreamTexture = new Texture("Hidden_Dreams_Logo.png");
        pointerTexture = new Texture("main_menu_pointer_anim0010.png");
    }

    private void buildBgLayers() {
        Image bgImage = new Image(bgTexture);
        bgImage.setFillParent(true);
        bgImage.setColor(Color.DARK_GRAY);
        stage.addActor(bgImage);

        Image beamImage = new Image(beamTexture) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                int srcFunc = batch.getBlendSrcFunc();
                int dstFunc = batch.getBlendDstFunc();

                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

                super.draw(batch, parentAlpha);

                batch.setBlendFunction(srcFunc, dstFunc);
            }
        };

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

        Image titleImage = new Image(titleTexture);
        titleImage.setScaling(Scaling.fit);

        rootTable.add(titleImage).size(900, 300).padTop(80).colspan(3).row();

        Table buttonsTable = new Table();
        startButton = new TextButton("Start Game", skin);
        settingsButton = new TextButton("Settings", skin);
        guideButton = new TextButton("Guide", skin);
        achievementsButton = new TextButton("Achievements", skin);
        quitButton = new TextButton("Quit", skin);

        Table startRow = createMenuRow(startButton, pointerTexture);
        Table settingsRow = createMenuRow(settingsButton, pointerTexture);
        Table guideRow = createMenuRow(guideButton, pointerTexture);
        Table achievementsRow = createMenuRow(achievementsButton, pointerTexture);
        Table quitRow = createMenuRow(quitButton, pointerTexture);

        buttonsTable.add(startRow).padBottom(15).row();
        buttonsTable.add(settingsRow).padBottom(15).row();
        buttonsTable.add(guideRow).padBottom(15).row();
        buttonsTable.add(achievementsRow).padBottom(15).row();
        buttonsTable.add(quitRow).padBottom(15).row();

        rootTable.add(buttonsTable).expand().center().colspan(3).row();

        Image hiddenDreamImage = new Image(hiddenDreamTexture);
        hiddenDreamImage.setScaling(Scaling.fit);

        Image cherryImage = new Image(cherryTexture);
        cherryImage.setScaling(Scaling.fit);

        rootTable.add(hiddenDreamImage).size(200, 80).padBottom(50).padLeft(80);
        rootTable.add();
        rootTable.add(cherryImage).size(150, 150).padBottom(50).padRight(80);
        stage.addActor(rootTable);
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
        row.add(button);
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
        if (titleTexture != null) titleTexture.dispose();
        if (pointerTexture != null) pointerTexture.dispose();
        if (cherryTexture != null) cherryTexture.dispose();
        if (hiddenDreamTexture != null) hiddenDreamTexture.dispose();
        if (blackLayerTexture != null) blackLayerTexture.dispose();
    }
}
