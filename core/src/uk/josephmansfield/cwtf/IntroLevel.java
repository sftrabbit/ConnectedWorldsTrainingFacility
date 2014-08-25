package uk.josephmansfield.cwtf;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntroLevel implements Level {

    private Map<Character, Integer> map = new HashMap<Character, Integer>();

    public IntroLevel() {
        map.put('A', 0);
        map.put('B', 1);
        map.put('C', 2);
        map.put('D', 3);
        map.put('E', 4);
        map.put('F', 5);
        map.put('G', 6);
        map.put('H', 7);
        map.put('I', 8);
        map.put('J', 9);
        map.put('K', 10);
        map.put('L', 11);
        map.put('M', 12);
        map.put('N', 13);
        map.put('O', 14);
        map.put('P', 15);
        map.put('Q', 16);
        map.put('R', 17);
        map.put('S', 18);
        map.put('T', 19);
        map.put('U', 20);
        map.put('V', 21);
        map.put('W', 22);
        map.put('X', 23);
        map.put('Y', 24);
        map.put('Z', 25);
        map.put('.', 26);
        map.put(',', 27);
        map.put('!', 28);
        map.put('?', 29);
        map.put(' ', 30);
    }

    private String[] texts = {"WELCOME TO THE\nCONNECTED WORLDS\nTRAINING FACILITY\n.",
            "WE HAVE DEVELOPED\nA TECHNOLOGY\nFOR COMMUNICATION\nBETWEEN...",
            "...PARALLEL\nUNIVERSES.",
            "THIS TECHNOLOGY\nIS INHERENTLY\nUNSAFE.\n",
            "YOU HAVE BEEN\nCHOSEN FOR THE\nCOMMUNICATION\nTRAINING EXERCISE.",
            "WE WILL ASSESS\nYOUR ABILITY\nTO SOLVE TASKS\nBY...",
            "COMMUNICATING\nEFFECTIVELY\nBETWEEN WORLDS.",
            "GOOD LUCK.",
            "YOULL NEED IT."};

    private int currentTextIndex = 0;

    public OrthographicCamera getInitialCamera(int world, CwtfGame.Dimension<Integer> size) {
        OrthographicCamera camera = new OrthographicCamera(size.width, size.height);
        return camera;
    }

    public void constructWorlds(CwtfGame.WorldData[] worldDatas) {
    }

    public void renderText(SpriteBatch batch) {
        int currentX = 0;
        int currentY = 0;

        String currentText = texts[currentTextIndex];

        for (int i = 0; i < currentText.length(); i++) {
            char character = currentText.charAt(i);

            if (character == '\n') {
                currentX = 0;
                currentY += 2;
                continue;
            }

            int spriteIndex = map.get(character);

            if (spriteIndex == 30) {
                currentX++;
                continue;
            }

            batch.draw(CwtfGame.fontSprites[spriteIndex], -64 + currentX * 8, 32 -8 - currentY*8);

            currentX++;
        }
    }

    public boolean nextText() {
        if (currentTextIndex < texts.length - 1) {
            currentTextIndex++;
            return true;
        } else {
            return false;
        }
    }
}
