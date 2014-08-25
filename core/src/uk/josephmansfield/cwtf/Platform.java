package uk.josephmansfield.cwtf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

public class Platform implements Renderable {
    private Body body;
    private int length;
    private Vector2 fromPosition;
    private Vector2 motion;
    private float motionTime;
    private float lastStartTime;
    private float lastStopTime;
    private boolean moving = false;
    private boolean forward = true;
    private float stopTime = 2f;

    public Platform(World physicsWorld, Vector2 gridPosition, int length, Vector2 motion, float motionTime) {
        this.length = length;
        this.fromPosition = gridPosition;
        this.motion = motion;
        this.motionTime = motionTime;
        this.lastStartTime = 0;
        this.lastStopTime = CwtfGame.getCurrentWorldTime();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(gridPosition);
        bodyDef.fixedRotation = true;

        body = physicsWorld.createBody(bodyDef);

        Vector2[] vertices = {
                new Vector2(0f, 11f * 0.0625f),
                new Vector2(length, 11f * 0.0625f),
                new Vector2(length, 1f),
                new Vector2(0f, 1f)
        };

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        Fixture fixture1 = body.createFixture(shape, 1.0f/ 1024f);

        shape.dispose();
    }

    public void setStopTime(float stopTime) {
        this.stopTime = stopTime;
    }

    @Override
    public void update() {
        float nowTime = CwtfGame.getCurrentWorldTime();
        if (!moving && nowTime > lastStopTime + stopTime) {
            lastStartTime = nowTime;
            moving = true;
        }

        if (moving) {
            float timeMoving = nowTime - lastStartTime;
            float portion = timeMoving / motionTime;

            float transformX = 0;
            float transformY = 0;

            if (portion >= 1f) {
                portion = 1f;
                moving = false;
                lastStopTime = nowTime;

                if (forward) {
                    transformX = fromPosition.x + motion.x;
                    transformY = fromPosition.y + motion.y;
                } else {
                    transformX = fromPosition.x;
                    transformY = fromPosition.y;
                }

                forward = !forward;
            } else {
                Vector2 displacement = new Vector2(motion);
                displacement.scl(forward ? portion : 1f - portion);
                transformX = fromPosition.x + displacement.x;
                transformY = fromPosition.y + displacement.y;
            }

            transformX = Math.round(transformX * 16) / 16f;
            transformY = Math.round(transformY * 16) / 16f;
            body.setTransform(transformX, transformY, 0);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 posInPixels = CwtfGame.metersToPixels(body.getPosition());

        batch.draw(CwtfGame.sprites[5][0], posInPixels.x, posInPixels.y);
        if (length > 1) {
            batch.draw(CwtfGame.sprites[5][0], posInPixels.x + CwtfGame.metersToPixels(length - 1), posInPixels.y);

            for (int i = 0; i < length - 1; i++) {
                batch.draw(CwtfGame.sprites[5][1], posInPixels.x + CwtfGame.metersToPixels(0.5f + i), posInPixels.y);
            }
        }
    }
}
