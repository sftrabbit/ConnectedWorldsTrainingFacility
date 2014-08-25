package uk.josephmansfield.cwtf;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

public class Level2 implements Level {
    public OrthographicCamera getInitialCamera(int world, CwtfGame.Dimension<Integer> size) {
        OrthographicCamera camera = new OrthographicCamera(size.width, size.height);
        camera.translate(5f * 16f, 3f * 16f);
        return camera;
    }

    public void constructWorlds(CwtfGame.WorldData[] worldDatas) {
        CwtfGame.WorldData worldData;

        for (int i = 0; i < 2; i++) {
            worldData = worldDatas[i];

            World physicsWorld = worldData.physicsWorld;
            List<Renderable> renderables = worldData.renderables;

            worldData.player = new Player(physicsWorld, new Vector2(2f, 1f), i);

            // Outer walls
            new Obstacle(physicsWorld, new Vector2(0, 0), new CwtfGame.Dimension<Float>(11f, 1f));
            new Obstacle(physicsWorld, new Vector2(11, 0), new CwtfGame.Dimension<Float>(1f, 4f));
            new Obstacle(physicsWorld, new Vector2(11, 4), new CwtfGame.Dimension<Float>(4f, 1f));
            new Obstacle(physicsWorld, new Vector2(15, 4), new CwtfGame.Dimension<Float>(1f, 15f));
            new Obstacle(physicsWorld, new Vector2(7, 19), new CwtfGame.Dimension<Float>(9f, 1f));
            new Obstacle(physicsWorld, new Vector2(6, 16), new CwtfGame.Dimension<Float>(1f, 4f));
            new Obstacle(physicsWorld, new Vector2(4, 15), new CwtfGame.Dimension<Float>(3f, 1f));
            new Obstacle(physicsWorld, new Vector2(3, 11), new CwtfGame.Dimension<Float>(1f, 5f));
            new Obstacle(physicsWorld, new Vector2(0, 12), new CwtfGame.Dimension<Float>(3f, 1f));
            new Obstacle(physicsWorld, new Vector2(0, 1), new CwtfGame.Dimension<Float>(1f, 11f));

            // Inside left
            new Obstacle(physicsWorld, new Vector2(1, 3), new CwtfGame.Dimension<Float>(3f, 1f));
            new Obstacle(physicsWorld, new Vector2(4, 2), new CwtfGame.Dimension<Float>(1f, 7f));
            new Obstacle(physicsWorld, new Vector2(3, 6), new CwtfGame.Dimension<Float>(1f, 1f));
            new Obstacle(physicsWorld, new Vector2(3, 9), new CwtfGame.Dimension<Float>(4f, 1f));

            // Inside right
            new Obstacle(physicsWorld, new Vector2(11, 9), new CwtfGame.Dimension<Float>(4f, 1f));
            new Obstacle(physicsWorld, new Vector2(14, 11), new CwtfGame.Dimension<Float>(1f, 1f));
            new Obstacle(physicsWorld, new Vector2(13, 13), new CwtfGame.Dimension<Float>(1f, 1f));
            new Obstacle(physicsWorld, new Vector2(12, 17), new CwtfGame.Dimension<Float>(1f, 2f));
            new Obstacle(physicsWorld, new Vector2(8, 15), new CwtfGame.Dimension<Float>(5f, 1f));
            new Obstacle(physicsWorld, new Vector2(12, 11), new CwtfGame.Dimension<Float>(1f, 4f));

            // Barrels
            new Obstacle(physicsWorld, new Vector2(9, 1), new CwtfGame.Dimension<Float>(2f, 1f));
            new Obstacle(physicsWorld, new Vector2(10, 2), new CwtfGame.Dimension<Float>(1f, 1f));
            new Obstacle(physicsWorld, new Vector2(1, 4), new CwtfGame.Dimension<Float>(1f, 1f));

            Platform platform = new Platform(physicsWorld, new Vector2(8f, 4f), 2, new Vector2(0f, 5f), 3.0f);
            renderables.add(platform);
        }

        /**
         * WORLD 0
         */
        worldData = worldDatas[0];

        // Door
        final Door door1 = new Door(worldData.physicsWorld, new Vector2(4f, 1f));
        worldData.renderables.add(door1);

        // Goal
        Goal goal1 = new Goal(worldData.physicsWorld, new Vector2(2f, 4f));
        worldData.renderables.add(goal1);

        /**
         * WORLD 1
         */
        worldData = worldDatas[1];

        // Button
        Button button1 = new Button(worldData.physicsWorld, new Vector2(14f, 5f));
        button1.setButtonListener(new Button.ButtonListener() {
            @Override
            public void onButtonStateChange(boolean down) {
                door1.setOpen(down);
            }
        });
        worldData.renderables.add(button1);

        // Goal
        Goal goal2 = new Goal(worldData.physicsWorld, new Vector2(2f, 4f));
        worldData.renderables.add(goal2);

        // Door
        final Door door2 = new Door(worldData.physicsWorld, new Vector2(12f, 10f));
        worldData.renderables.add(door2);
        // Door
        final Door door3 = new Door(worldData.physicsWorld, new Vector2(12f, 16f));
        worldData.renderables.add(door3);


        /**
         * WORLD 0
         */
        worldData = worldDatas[0];

        // Button
        Button button2 = new Button(worldData.physicsWorld, new Vector2(8f, 16f));
        button2.setButtonListener(new Button.ButtonListener() {
            @Override
            public void onButtonStateChange(boolean down) {
                door2.setOpen(down);
            }
        });
        worldData.renderables.add(button2);

        // Button
        Button button3 = new Button(worldData.physicsWorld, new Vector2(10f, 16f));
        button3.setButtonListener(new Button.ButtonListener() {
            @Override
            public void onButtonStateChange(boolean down) {
                door3.setOpen(down);
            }
        });
        worldData.renderables.add(button3);

        // Door
        final Door door4 = new Door(worldData.physicsWorld, new Vector2(3f, 10f));
        worldData.renderables.add(door4);

        /**
         * WORLD 1
         */
        worldData = worldDatas[1];

        // Button
        Button button4 = new Button(worldData.physicsWorld, new Vector2(8f, 16f));
        button4.setButtonListener(new Button.ButtonListener() {
            @Override
            public void onButtonStateChange(boolean down) {
                door4.setOpen(down);
            }
        });
        worldData.renderables.add(button4);

        // Door
        final Door door5 = new Door(worldData.physicsWorld, new Vector2(3f, 10f));
        worldData.renderables.add(door5);

        /**
         * WORLD 0
         */
        worldData = worldDatas[0];

        // Button
        Button button5 = new Button(worldData.physicsWorld, new Vector2(3f, 7f));
        button5.setButtonListener(new Button.ButtonListener() {
            @Override
            public void onButtonStateChange(boolean down) {
                door5.setOpen(down);
            }
        });
        worldData.renderables.add(button5);
    }
}
