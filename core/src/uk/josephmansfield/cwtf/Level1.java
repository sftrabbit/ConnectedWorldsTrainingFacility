package uk.josephmansfield.cwtf;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

public class Level1 implements Level {
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

            worldData.player = new Player(physicsWorld, new Vector2(3f, 1f), i);

            // Outer walls
            new Obstacle(physicsWorld, new Vector2(0, 0), new CwtfGame.Dimension<Float>(16f, 1f));
            new Obstacle(physicsWorld, new Vector2(15, 1), new CwtfGame.Dimension<Float>(1f, 3f));
            new Obstacle(physicsWorld, new Vector2(15, 4), new CwtfGame.Dimension<Float>(5f, 1f));
            new Obstacle(physicsWorld, new Vector2(19, 3), new CwtfGame.Dimension<Float>(1f, 1f));
            new Obstacle(physicsWorld, new Vector2(19, 1), new CwtfGame.Dimension<Float>(2f, 2f));
            new Obstacle(physicsWorld, new Vector2(21, 1), new CwtfGame.Dimension<Float>(2f, 1f));
            new Obstacle(physicsWorld, new Vector2(23, 1), new CwtfGame.Dimension<Float>(2f, 2f));
            new Obstacle(physicsWorld, new Vector2(24, 3), new CwtfGame.Dimension<Float>(1f, 18f));
            new Obstacle(physicsWorld, new Vector2(19, 20), new CwtfGame.Dimension<Float>(5f, 1f));
            new Obstacle(physicsWorld, new Vector2(19, 21), new CwtfGame.Dimension<Float>(1f, 3f));
            new Obstacle(physicsWorld, new Vector2(1, 23), new CwtfGame.Dimension<Float>(18f, 1f));
            new Obstacle(physicsWorld, new Vector2(1, 15), new CwtfGame.Dimension<Float>(1f, 8f));
            new Obstacle(physicsWorld, new Vector2(2, 15), new CwtfGame.Dimension<Float>(18f, 1f));
            new Obstacle(physicsWorld, new Vector2(19, 8), new CwtfGame.Dimension<Float>(1f, 7f));
            new Obstacle(physicsWorld, new Vector2(12, 8), new CwtfGame.Dimension<Float>(7f, 1f));
            new Obstacle(physicsWorld, new Vector2(12, 6), new CwtfGame.Dimension<Float>(1f, 2f));
            new Obstacle(physicsWorld, new Vector2(0, 6), new CwtfGame.Dimension<Float>(12f, 1f));
            new Obstacle(physicsWorld, new Vector2(0, 1), new CwtfGame.Dimension<Float>(1f, 5f));

            // Top obstacles
            new Obstacle(physicsWorld, new Vector2(5, 19), new CwtfGame.Dimension<Float>(9f, 1f));
            new Obstacle(physicsWorld, new Vector2(5, 17), new CwtfGame.Dimension<Float>(1f, 2f));
            new Obstacle(physicsWorld, new Vector2(13, 17), new CwtfGame.Dimension<Float>(1f, 2f));
            new Obstacle(physicsWorld, new Vector2(7, 21), new CwtfGame.Dimension<Float>(1f, 2f));
            new Obstacle(physicsWorld, new Vector2(11, 21), new CwtfGame.Dimension<Float>(1f, 2f));

            Platform platform = new Platform(physicsWorld, new Vector2(21f, 4f), 2, new Vector2(0f, 11f), 5.0f);
            renderables.add(platform);
        }

        /**
         * WORLD 0
         */
        worldData = worldDatas[0];

        // Door
        final Door door1 = new Door(worldData.physicsWorld, new Vector2(13f, 16f));
        worldData.renderables.add(door1);

        // Bottom obstacles
        new Obstacle(worldData.physicsWorld, new Vector2(8, 1), new CwtfGame.Dimension<Float>(1f, 1f));
        new Obstacle(worldData.physicsWorld, new Vector2(13, 1), new CwtfGame.Dimension<Float>(1f, 2f));

        // Goal
        Goal goal1 = new Goal(worldData.physicsWorld, new Vector2(3f, 16f));
        worldData.renderables.add(goal1);

        /**
         * WORLD 1
         */
        worldData = worldDatas[1];

        // Button
        Button button1 = new Button(worldData.physicsWorld, new Vector2(9f, 20f));
        button1.setButtonListener(new Button.ButtonListener() {
            @Override
            public void onButtonStateChange(boolean down) {
                door1.setOpen(down);
            }
        });
        worldData.renderables.add(button1);

        // Goal
        Goal goal2 = new Goal(worldData.physicsWorld, new Vector2(3f, 16f));
        worldData.renderables.add(goal2);

        // Door
        final Door door2 = new Door(worldData.physicsWorld, new Vector2(7f, 20f));
        worldData.renderables.add(door2);

        // Platform
        Platform platform = new Platform(worldData.physicsWorld, new Vector2(15f, 17f), 2, new Vector2(0f, 2f), 1.0f);
        platform.setStopTime(1f);
        worldData.renderables.add(platform);

        // Bottom obstacles
        new Obstacle(worldData.physicsWorld, new Vector2(13, 3), new CwtfGame.Dimension<Float>(1f, 1f));
        new Obstacle(worldData.physicsWorld, new Vector2(10, 2), new CwtfGame.Dimension<Float>(1f, 1f));

        // Block off doors at top
        new Obstacle(worldData.physicsWorld, new Vector2(5, 16), new CwtfGame.Dimension<Float>(1f, 1f));
        new Obstacle(worldData.physicsWorld, new Vector2(13, 16), new CwtfGame.Dimension<Float>(1f, 1f));


        /**
         * WORLD 0
         */
        worldData = worldDatas[0];

        // Button
        Button button2 = new Button(worldData.physicsWorld, new Vector2(9f, 16f));
        button2.setButtonListener(new Button.ButtonListener() {
            @Override
            public void onButtonStateChange(boolean down) {
                door2.setOpen(down);
            }
        });
        worldData.renderables.add(button2);
    }
}
