package uk.josephmansfield.cwtf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by sftrabbit on 24/08/14.
 */
public class Button implements Renderable {

    private Body body1;
    private Body body2;
    private boolean pushed = false;
    private boolean down = false;
    private float lastPushed = 0f;

    private ButtonListener listener = null;

    public Button(World physicsWorld, Vector2 gridPosition) {

        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyDef.BodyType.StaticBody;
        bodyDef1.position.set(gridPosition);
        bodyDef1.fixedRotation = true;

        body1 = physicsWorld.createBody(bodyDef1);

        Vector2[] vertices1 = {
                new Vector2(0.0625f, 0),
                new Vector2(4f * 0.0625f, 3f * 0.0625f),
                new Vector2(1f - (4f * 0.0625f), 3f * 0.0625f),
                new Vector2(1f - 0.0625f, 0)
        };

        PolygonShape shape1 = new PolygonShape();
        shape1.set(vertices1);
        Fixture fixture1 = body1.createFixture(shape1, 1.0f/ 1024f);


        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.KinematicBody;
        bodyDef2.position.set(gridPosition);
        bodyDef2.fixedRotation = true;

        body2 = physicsWorld.createBody(bodyDef2);

        Vector2[] vertices2 = {
                new Vector2(4f * 0.0625f, 3f * 0.0625f),
                new Vector2(4f * 0.0625f, 6f * 0.0625f),
                new Vector2(1f - (4f * 0.0625f), 6f * 0.0625f),
                new Vector2(1f - (4f * 0.0625f), 3f * 0.0625f)
        };

        PolygonShape shape2 = new PolygonShape();
        shape2.set(vertices2);
        Fixture fixture2 = body2.createFixture(shape2, 1.0f/ 1024f);

        fixture2.setUserData(this);

        shape1.dispose();
        shape2.dispose();
    }

    public void push() {
        pushed = true;
        lastPushed = CwtfGame.getCurrentWorldTime();
    }

    @Override
    public void update() {
        Vector2 pos = body2.getPosition();

        if (down && CwtfGame.getCurrentWorldTime() > lastPushed + 0.3f) {
            body2.setTransform(pos.x + 0f, pos.y + 2f * 0.0625f, 0f);
            down = false;

            listener.onButtonStateChange(false);
            CwtfGame.buttonUpSound.play();
        }
        if (pushed && !down) {
            body2.setTransform(pos.x + 0f, pos.y - 2f * 0.0625f, 0f);
            down = true;

            listener.onButtonStateChange(true);
            CwtfGame.buttonDownSound.play();
        }

        pushed = false;
    }

    void setButtonListener(ButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 posInPixels = CwtfGame.metersToPixels(body1.getPosition());
        if (down) {
            batch.draw(CwtfGame.sprites[4][1], posInPixels.x, posInPixels.y);
        } else {
            batch.draw(CwtfGame.sprites[4][0], posInPixels.x, posInPixels.y);
        }
    }

    public static interface ButtonListener {
        public void onButtonStateChange(boolean down);
    }
}
