package uk.josephmansfield.cwtf;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Goal implements Renderable {

    private Body body1;
    private Body body2;
    private boolean pushed = false;
    private boolean down = false;
    private float lastPushed = 0f;

    public Goal(World physicsWorld, Vector2 gridPosition) {

        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyDef.BodyType.StaticBody;
        bodyDef1.position.set(gridPosition);
        bodyDef1.fixedRotation = true;

        body1 = physicsWorld.createBody(bodyDef1);

        Vector2[] vertices1 = {
                new Vector2(0f, 0f),
                new Vector2(1f, 0f),
                new Vector2(1f, 1f * 0.0625f),
                new Vector2(0f, 1f * 0.0625f)
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
                new Vector2(3f * 0.0625f, 1f * 0.0625f),
                new Vector2(3f * 0.0625f, 3f * 0.0625f),
                new Vector2(1f - (3f * 0.0625f), 3f * 0.0625f),
                new Vector2(1f - (3f * 0.0625f), 1f * 0.0625f)
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

            CwtfGame.numGoals--;
        }
        if (pushed && !down) {
            body2.setTransform(pos.x + 0f, pos.y - 2f * 0.0625f, 0f);
            down = true;

            CwtfGame.numGoals++;
            CwtfGame.goalSound.play();
        }

        pushed = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 posInPixels = CwtfGame.metersToPixels(body1.getPosition());
        if (down) {
            batch.draw(CwtfGame.sprites[5][3], posInPixels.x, posInPixels.y);
        } else {
            batch.draw(CwtfGame.sprites[5][2], posInPixels.x, posInPixels.y);
        }
    }
}
