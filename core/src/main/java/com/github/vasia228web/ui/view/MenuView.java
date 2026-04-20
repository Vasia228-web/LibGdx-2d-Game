package com.github.vasia228web.ui.view;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.vasia228web.ui.model.MenuViewModel;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;

public class MenuView extends View<MenuViewModel> {

    private final Image selectionImg;
    private Group selectedImage;

    public MenuView(Stage stage, Skin skin, MenuViewModel viewModel){
        super(stage, skin, viewModel);
        this.selectionImg = new Image(skin, "selection");
    }

    @Override
    protected void setupPropertyChanges() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                selectedImage = stage.getRoot().findActor(MenuOption.PLAY.name());
                selectMenuItem(selectedImage);
            }
        }, 0.1f);
    }

    private void selectMenuItem(Group menuItem) {
        this.selectedImage = menuItem;

        float extraSize = 7f;
        float halfExtraSize = extraSize * 0.5f;
        float resizeTime = 0.2f;
        
        selectionImg.setTouchable(Touchable.disabled);
        selectionImg.setPosition(
            menuItem.getX() - halfExtraSize,
            menuItem.getY() - halfExtraSize
        );
        selectionImg.setSize(
            menuItem.getWidth() + extraSize,
            menuItem.getHeight() + extraSize
        );

        if (selectionImg.getStage() == null) {
            stage.addActor(selectionImg);
        }

        selectionImg.clearActions();
        selectionImg.addAction(Actions.forever(Actions.sequence(
            Actions.parallel(
                Actions.sizeBy(extraSize, extraSize, resizeTime, Interpolation.linear),
                Actions.moveBy(-halfExtraSize, -halfExtraSize, resizeTime, Interpolation.linear)
            ),
            Actions.parallel(
                Actions.sizeBy(-extraSize, -extraSize, resizeTime, Interpolation.linear),
                Actions.moveBy(halfExtraSize, halfExtraSize, resizeTime, Interpolation.linear)
            )
        )));
    }

    @Override
    protected void setupUI() {
        setFillParent(true);

        Table textTable = new Table();
        textTable.setName("text");
        textTable.setFillParent(true);
        textTable.top().padTop(30f);
        Label label = new Label("The Wild Grid", skin);
        label.setColor(skin.getColor("red"));
        textTable.add(label);

        Table boardTable = new Table();
        boardTable.setName("first");
        boardTable.setFillParent(true);
        boardTable.center();
        Image image = new Image(skin, "board");
        boardTable.add(image).minWidth(100f).minHeight(150f);

        Table buttonsTable = new Table();
        buttonsTable.setName("buttons");
        buttonsTable.setFillParent(true);
        buttonsTable.center();

        TextButton playButton = new TextButton("Play", skin);
        playButton.setName(MenuOption.PLAY.name());
        buttonsTable.add(playButton).spaceBottom(10f).minWidth(70f);
        onClick(playButton, viewModel::startGame);
        onEnter(playButton, this::selectMenuItem);
        buttonsTable.row();

        TextButton settingButton = new TextButton("Setting", skin);
        settingButton.setName(MenuOption.SETTING.name());
        buttonsTable.add(settingButton).spaceBottom(10f).minWidth(70f);
        onEnter(settingButton, this::selectMenuItem);
        buttonsTable.row();

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setName(MenuOption.QUIT.name());
        onClick(exitButton, viewModel::quitGame);
        onEnter(exitButton, this::selectMenuItem);
        buttonsTable.add(exitButton).minWidth(70f);

        stage.addActor(textTable);
        stage.addActor(boardTable);
        stage.addActor(buttonsTable);
    }

    private enum MenuOption {
        PLAY,
        SETTING,
        QUIT
    }
}
