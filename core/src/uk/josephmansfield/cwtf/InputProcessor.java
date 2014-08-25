package uk.josephmansfield.cwtf;


import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputProcessor extends InputAdapter {

    CwtfGame game;
    Player player;

    public InputProcessor(CwtfGame game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (CwtfGame.inputEnabled) {
            if (player != null) {
                switch (keycode) {
                    case Input.Keys.UP:
                    case Input.Keys.W:
                        player.jump();
                        break;
                    case Input.Keys.LEFT:
                    case Input.Keys.A:
                    case Input.Keys.RIGHT:
                    case Input.Keys.D:
                        player.setLastMotionKey(keycode);
                        break;
                    case Input.Keys.SPACE:
                        game.switchWorld();
                        break;
                }
            }

            if (game.levelNum == 0 || game.levelNum == 3) {
                if (game.level instanceof IntroLevel) {
                    IntroLevel introLevel = (IntroLevel) game.level;
                    if (!introLevel.nextText()) {
                        game.startNextLevelTransition();
                    }
                }
                if (game.level instanceof OutroLevel) {
                    OutroLevel outroLevel = (OutroLevel) game.level;
                    outroLevel.nextText();
                }
            }

            /*if (keycode == Input.Keys.ENTER) {
                game.startNextLevelTransition();
            }*/
        }

        return true;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
