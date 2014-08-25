package uk.josephmansfield.cwtf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Obstacle implements Renderable {

    private final Body body;
    private PolygonShape shape;
    private final Fixture fixture;
    private float[] vertices;
    private ShapeRenderer shapeRenderer;

    public Obstacle(World physicsWorld, Vector2 position, Vector2[] vertices) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(CwtfGame.pixelsToMeters(position.x), CwtfGame.pixelsToMeters(position.y));

        body = physicsWorld.createBody(bodyDef);

        shape = new PolygonShape();
        shape.set(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        fixture = body.createFixture(fixtureDef);

        this.vertices = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            this.vertices[i*2] = CwtfGame.metersToPixels(position.x + vertices[i].x);
            this.vertices[i*2+1] = CwtfGame.metersToPixels(position.y + vertices[i].y);
        }

        shapeRenderer = new ShapeRenderer();

        shape.dispose();
    }

    public Obstacle(World physicsWorld, Vector2 position, CwtfGame.Dimension<Float> size) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);

        body = physicsWorld.createBody(bodyDef);

        shape = new PolygonShape();
        float width = size.width;
        float height = size.height;
        shape.setAsBox(width / 2, height / 2, new Vector2(width / 2, height / 2), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        fixture = body.createFixture(fixtureDef);

        this.vertices = new float[shape.getVertexCount() * 2];
        for (int i = 0; i < shape.getVertexCount(); i++) {
            Vector2 vertex = new Vector2();
            shape.getVertex(i, vertex);
            this.vertices[i*2] = CwtfGame.metersToPixels(position.x + vertex.x);
            this.vertices[i*2+1] = CwtfGame.metersToPixels(position.y + vertex.y);
        }

        shapeRenderer = new ShapeRenderer();

        shape.dispose();
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.polygon(vertices);
        shapeRenderer.end();

        batch.begin();
    }
}
