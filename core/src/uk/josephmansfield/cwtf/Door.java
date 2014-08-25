package uk.josephmansfield.cwtf;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Door implements Renderable {

    private Body body;
    private boolean open = false;

    public Door(World physicsWorld, Vector2 gridPosition) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(gridPosition);
        bodyDef.fixedRotation = true;

        body = physicsWorld.createBody(bodyDef);

        Vector2[] vertices = {
                new Vector2(5f * 0.0625f, 1f),
                new Vector2(7f * 0.0625f, 1f - 2f * 0.0625f),
                new Vector2(7f * 0.0625f, 0f),
                new Vector2(9f * 0.0625f, 0f),
                new Vector2(9f * 0.0625f, 1f - 2f * 0.0625f),
                new Vector2(11f * 0.0625f, 1f),
        };

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        Fixture fixture1 = body.createFixture(shape, 1.0f/ 1024f);

        shape.dispose();
    }

    public void setOpen(boolean open) {
        this.open = open;
        body.setActive(!open);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 posInPixels = CwtfGame.metersToPixels(body.getPosition());
        if (open) {
            batch.draw(CwtfGame.sprites[4][3], posInPixels.x, posInPixels.y);
        } else {
            batch.draw(CwtfGame.sprites[4][2], posInPixels.x, posInPixels.y);
        }
    }

    public static interface ButtonListener {
        public void onButtonStateChange(boolean down);
    }
}
