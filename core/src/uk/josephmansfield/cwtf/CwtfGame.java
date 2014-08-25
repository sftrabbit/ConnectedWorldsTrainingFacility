package uk.josephmansfield.cwtf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.*;

public class CwtfGame extends ApplicationAdapter {
    private Dimension<Integer> screenSize;
    private Dimension<Integer> renderSize;

    private Camera screenCamera;

    private Texture spriteSheet;
    public static TextureRegion[][] sprites;

    private Texture scanlineTexture;
    private Sprite scanlineSprite;
    private Texture recordIconTexture;
    private boolean drawRecordIcon = false;
    private float recordIconTime = 0;
    private Texture recordTextTexture;
    private Texture feedMaleTexture;
    private Texture feedFemaleTexture;

    private SpriteBatch spriteBatch;
    private SpriteBatch fboBatch;

    private Box2DDebugRenderer debugRenderer;

    private InputProcessor inputProcessor;

    private OrthogonalTiledMapRenderer tileMapRenderer;

    private boolean switching = false;
    private boolean staticIncrease = true;
    private Texture staticTexture;
    private Sprite[] staticFrames;
    private int currentStaticFrame = 0;
    private Random random = new Random();
    private float lastStaticChange = 0;
    private float staticOpacity = 0f;

    private static WorldData[] worldData = new WorldData[2];
    public static int currentWorld = 0;
    public int levelNum = 0;
    public Level level;
    private TiledMap tileMap;

    public static int numGoals = 0;

    public static boolean inputEnabled = true;
    private boolean transition = false;
    private boolean transitionForward = true;
    private float transitionOpacity = 0f;
    private ShapeRenderer shapeRenderer;

    private Texture fontTexture;
    public static TextureRegion[] fontSprites;

    public static Sound jumpSound;
    public static Sound buttonDownSound;
    public static Sound buttonUpSound;
    public static Sound goalSound;

    public static class Dimension<T> {
        public T width;
        public T height;

        public Dimension(T width, T height) {
            this.width = width;
            this.height = height;
        }
    }
	
	@Override
	public void create () {
        debugRenderer = new Box2DDebugRenderer();

        spriteSheet = new Texture(Gdx.files.internal("player1.png"));
        sprites = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / 4, spriteSheet.getHeight() / 6);

        scanlineTexture = new Texture(Gdx.files.internal("scanlines.png"));
        scanlineSprite = new Sprite(scanlineTexture);
        staticTexture = new Texture(Gdx.files.internal("static.png"));
        TextureRegion[][] staticFramesTR = TextureRegion.split(staticTexture, staticTexture.getWidth() / 6, staticTexture.getHeight());
        staticFrames = new Sprite[6];
        for (int i = 0; i < staticFramesTR[0].length; i++) {
            staticFrames[i] = new Sprite(staticFramesTR[0][i]);
        }
        recordIconTexture = new Texture(Gdx.files.internal("recordicon.png"));
        recordTextTexture = new Texture(Gdx.files.internal("rec.png"));
        feedMaleTexture = new Texture(Gdx.files.internal("feedmale.png"));
        feedFemaleTexture = new Texture(Gdx.files.internal("feedfemale.png"));

        fontTexture = new Texture(Gdx.files.internal("font.png"));
        TextureRegion[][] fontRegions = TextureRegion.split(fontTexture, fontTexture.getWidth() / 10, fontTexture.getHeight() / 3);
        fontSprites = new TextureRegion[10 * 3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                fontSprites[index] = fontRegions[i][j];
                index++;
            }
        }

        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
        buttonDownSound = Gdx.audio.newSound(Gdx.files.internal("buttondown.wav"));
        buttonUpSound = Gdx.audio.newSound(Gdx.files.internal("buttonup.wav"));
        goalSound = Gdx.audio.newSound(Gdx.files.internal("goal.wav"));

        spriteBatch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();


        loadLevel(levelNum);
	}

    private void loadLevel(int levelNum) {
        level = null;
        switch (levelNum) {
            case 0: level = new IntroLevel(); break;
            case 1: level = new Level1(); break;
            case 2: level = new Level2(); break;
            case 3: level = new OutroLevel(); break;
        }

        tileMap = null;
        if (levelNum != 0 && levelNum != 3) {
            tileMap = new TmxMapLoader().load("level" + levelNum + ".tmx");
        }

        for (int i = 0; i < 2; i++) {
            worldData[i] = new WorldData();

            worldData[i].tileMapLayer = i + 3;
            worldData[i].physicsWorld = new World(new Vector2(0, -30), true);
        }

        if (tileMap != null && tileMap.getLayers().getCount() == 4) {
            worldData[1].tileMapLayer = 3;
        }

        level.constructWorlds(worldData);

        inputProcessor = new InputProcessor(this, worldData[0].player);
        Gdx.input.setInputProcessor(inputProcessor);

        if (tileMapRenderer != null) {
            tileMapRenderer.dispose();
            tileMapRenderer = null;
        }
        if (tileMap != null) {
            tileMapRenderer = new OrthogonalTiledMapRenderer(tileMap, 1f);
        }
    }

    @Override
    public void resize(int width, int height) {
        screenSize = new Dimension<Integer>(width, height);
        screenCamera = new OrthographicCamera(screenSize.width, screenSize.height);
        screenCamera.translate(screenSize.width / 2, screenSize.height / 2, 0);

        loadView();
    }

    private void loadView() {
        renderSize = new Dimension<Integer>(screenSize.width / 4, screenSize.height / 4);

        for (int i = 0; i < 2; i++) {
            if (worldData[i].renderCamera == null) {
                worldData[i].renderCamera = level.getInitialCamera(i, renderSize);
            } else {
                Vector3 camPosition = new Vector3(worldData[i].renderCamera.position);
                worldData[i].renderCamera = new OrthographicCamera(renderSize.width, renderSize.height);
                worldData[i].renderCamera.translate(camPosition);
            }

            if (worldData[i].fbo != null) {
                worldData[i].fbo.dispose();
            }
            worldData[i].fbo = new FrameBuffer(Pixmap.Format.RGB888, renderSize.width, renderSize.height, true);
            worldData[i].fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        if (fboBatch != null) {
            fboBatch.dispose();
        }
        fboBatch = new SpriteBatch();
    }

    public void startNextLevelTransition() {
        inputEnabled = false;
        transition = true;
    }

    public void nextLevel() {
        levelNum++;
        loadLevel(levelNum);
        loadView();
    }

    @Override
	public void render () {
        for (int i = 0; i < 2; i++) {
            worldData[i].time += Gdx.graphics.getDeltaTime();

            /**
             * Update game state
             */
            for (Renderable renderable : worldData[i].renderables) {
                renderable.update();
            }
            if (worldData[i].player != null) {
                worldData[i].player.update();
            }

            worldData[i].physicsWorld.step(Gdx.graphics.getDeltaTime(), 6, 2);
        }
        WorldData currentWorldData = worldData[currentWorld];

        moveCamera();

        /**
         * Render to frame buffer (main world rendering)
         */
        currentWorldData.fbo.begin();

        currentWorldData.renderCamera.update();

		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            if (tileMapRenderer != null) {
                tileMapRenderer.setView(currentWorldData.renderCamera);
                if (currentWorld == 0) {
                    tileMapRenderer.render(new int[]{0, 1, currentWorldData.tileMapLayer});
                } else {
                    tileMapRenderer.render(new int[]{0, 2, currentWorldData.tileMapLayer});
                }
            }

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(currentWorldData.renderCamera.combined);

        for (Renderable renderable : currentWorldData.renderables) {
            renderable.render(spriteBatch);
        }

        if (currentWorldData.player != null) {
            currentWorldData.player.render(spriteBatch);
        }

        if (level instanceof IntroLevel) {
            IntroLevel introLevel = (IntroLevel) level;
            introLevel.renderText(spriteBatch);
        }
        if (level instanceof OutroLevel) {
            OutroLevel outroLevel = (OutroLevel) level;
            outroLevel.renderText(spriteBatch);
        }

        spriteBatch.end();

        //debugRenderer.render(physicsWorld, renderCamera.combined.scale(16.0f, 16.0f, 1.0f));

        currentWorldData.fbo.end();

        /**
         * Render frame buffer (for pixelation)
         */
        screenCamera.update();

        fboBatch.begin();
        fboBatch.setProjectionMatrix(screenCamera.combined);
        fboBatch.draw(currentWorldData.fbo.getColorBufferTexture(), 0, 0, screenSize.width, screenSize.height, 0, 0, 1, 1);
        postProcess();
        fboBatch.end();

        if (transition) {
            if (transitionForward) {
                transitionOpacity += 0.04;

                if (transitionOpacity > 1f) {
                    transitionOpacity = 1f;
                    transitionForward = false;

                    nextLevel();
                    inputEnabled = true;
                }
            } else {
                transitionOpacity -= 0.04;

                if (transitionOpacity < 0f) {
                    transitionOpacity = 0f;
                    transitionForward = true;
                    transition = false;
                }
            }

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(fboBatch.getProjectionMatrix());
            shapeRenderer.setColor(0f, 0f, 0f, transitionOpacity);
            shapeRenderer.box(0f, 0f, 0f, screenSize.width, screenSize.height, 0f);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        if (numGoals == 2) {
            numGoals = 0;
            startNextLevelTransition();
        }
    }

    private void postProcess() {
        float nowTime = getCurrentWorldTime();

        if (levelNum != 0 && levelNum != 3) {
            if (nowTime > recordIconTime + 1.0f) {
                drawRecordIcon = !drawRecordIcon;
                recordIconTime = nowTime;
            }

            if (drawRecordIcon) {
                fboBatch.draw(recordIconTexture, 48f, 48f);
            }

            fboBatch.draw(recordTextTexture, 88f, 52f);

            float feedY = (float) (Math.floor(screenSize.height / 4f) * 4f) - 64f;
            if (currentWorld == 0) {
                fboBatch.draw(feedMaleTexture, 48f, feedY);
            } else {
                fboBatch.draw(feedFemaleTexture, 48f, feedY);
            }
        }

        float scaledWidth = scanlineSprite.getWidth();
        float scaledHeight = scanlineSprite.getHeight();

        int countHorizontal = screenSize.width / (int)scaledWidth + 1;
        int countVertical = screenSize.height / (int)scaledHeight + 1;

        for (int i = 0; i < countHorizontal; i++) {
            for (int j = 0; j < countVertical; j++) {
                scanlineSprite.setPosition(i * scaledWidth, j * scaledHeight);
                scanlineSprite.draw(fboBatch, 0.05f);
            }
        }

        if (switching) {
            if (staticIncrease) {
                staticOpacity += 0.04;

                if (staticOpacity > 1f) {
                    staticOpacity = 1f;
                    staticIncrease = false;

                    changeWorld();
                }
            } else {
                staticOpacity -= 0.04;

                if (staticOpacity < 0f) {
                    staticOpacity = 0f;
                    staticIncrease = true;
                    switching = false;
                }
            }

            if (staticOpacity > 0f) {
                if (nowTime > lastStaticChange + 0.1f) {
                    currentStaticFrame = (currentStaticFrame + random.nextInt(5) + 1) % 6;
                    lastStaticChange = nowTime;
                }

                float width = staticFrames[currentStaticFrame].getRegionWidth();
                float height = staticFrames[currentStaticFrame].getRegionHeight();

                countHorizontal = screenSize.width / (int) width + 1;
                countVertical = screenSize.height / (int) height + 1;

                for (int i = 0; i < countHorizontal; i++) {
                    for (int j = 0; j < countVertical; j++) {
                        staticFrames[currentStaticFrame].setPosition(i * width, j * height);
                        staticFrames[currentStaticFrame].draw(fboBatch, staticOpacity);
                    }
                }
            }
        }
    }

    private void changeWorld() {
        if (currentWorld == 0) {
            currentWorld = 1;
        } else {
            currentWorld = 0;
        }

        inputProcessor.setPlayer(worldData[currentWorld].player);
    }

    @Override
    public void dispose() {
        for (int i = 0; i < 2; i++) {
            worldData[i].fbo.dispose();
            worldData[i].physicsWorld.dispose();
        }

        fboBatch.dispose();

        spriteBatch.dispose();
        spriteSheet.dispose();

        scanlineTexture.dispose();
        staticTexture.dispose();
        recordIconTexture.dispose();
        recordTextTexture.dispose();

        feedMaleTexture.dispose();
        feedFemaleTexture.dispose();

        fontTexture.dispose();

        if (tileMapRenderer != null) {
            tileMapRenderer.dispose();
        }
    }

    private void moveCamera() {
        Player player = worldData[currentWorld].player;
        if (player != null) {
            Camera renderCamera = worldData[currentWorld].renderCamera;
            Vector2 renderCameraVelocity = worldData[currentWorld].renderCameraVelocity;

            float boundaryLeft = renderCamera.position.x - renderCamera.viewportWidth / 4;
            float boundaryBottom = renderCamera.position.y - renderCamera.viewportHeight / 4;
            float boundaryWidth = renderCamera.viewportWidth / 2;
            float boundaryHeight = renderCamera.viewportHeight / 2;

            Rectangle boundary = new Rectangle(boundaryLeft, boundaryBottom, boundaryWidth, boundaryHeight);

            renderCameraVelocity = renderCameraVelocity.scl(0.9f);

            boolean playerInsideBounds = boundary.contains(player.getBoundingBoxInPixels());
            if (!playerInsideBounds) {
                Vector2 playerCenter = player.getCenterInPixels();

                int moveHorizontal = 0;
                if (playerCenter.x < boundaryLeft || playerCenter.x > boundaryLeft + boundaryWidth) {
                    moveHorizontal = 1;
                }
                int moveVertical = 0;
                if (playerCenter.y < boundaryBottom || playerCenter.y > boundaryBottom + boundaryHeight) {
                    moveVertical = 1;
                }

                Vector2 direction = playerCenter.sub(renderCamera.position.x, renderCamera.position.y);
                Vector2 unitDirection = direction.nor();
                direction.scl(0.2f);

                renderCameraVelocity.add(moveHorizontal * direction.x, moveVertical * direction.y);
                renderCameraVelocity = renderCameraVelocity.limit(300f);

                renderCamera.position.add(renderCameraVelocity.x, renderCameraVelocity.y, 0);
            }
        }
    }

    public void switchWorld() {
        if (!switching) {
            switching = true;
            staticIncrease = true;
        }
    }

    public static float pixelsToMeters(float pixels) {
        return pixels / 16f;
    }

    public static Vector2 pixelsToMeters(Vector2 pixelVector) {
        return pixelVector.scl(1/16f);
    }

    public static float metersToPixels(float meters) {
        return 16f * meters;
    }

    public static Vector2 metersToPixels(Vector2 pixelVector) {
        return pixelVector.scl(16f);
    }

    public static float getCurrentWorldTime() {
        return worldData[currentWorld].time;
    }

    public static class WorldData {
        public World physicsWorld;

        public OrthographicCamera renderCamera;
        public Vector2 renderCameraVelocity = new Vector2();

        public FrameBuffer fbo = null;

        public int tileMapLayer;

        public Player player;
        public java.util.List<Renderable> renderables = new ArrayList<Renderable>();

        public float time = 0f;
    }
}
