package uk.josephmansfield.cwtf;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Player implements Renderable {
    private Texture texture;
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private float animationTime;

    private boolean doJump = false;
    private boolean grounded = true;
    private float lastGroundedTime = 0f;
    private float lastJumpTime = 0f;
    private int lastMotionKey = 0;

    private Body body;
    private Fixture fixture;

    private int world;

    public Player(World physicsWorld, Vector2 gridPosition, int world) {
        walkLeftAnimation = new Animation(0.3f, CwtfGame.sprites[world * 2]);
        walkRightAnimation = new Animation(0.3f, CwtfGame.sprites[world * 2 + 1]);
        animationTime = 0f;
        this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(gridPosition);
        bodyDef.fixedRotation = true;

        body = physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        Vector2 shapeHalfSize = CwtfGame.pixelsToMeters(new Vector2(6, 6.5f));
        Vector2 shapeOffset = CwtfGame.pixelsToMeters(new Vector2(8, 7));
        shape.setAsBox(shapeHalfSize.x, shapeHalfSize.y, shapeOffset, 0);

        fixture = body.createFixture(shape, 1.0f/ 1024f);

        shape.dispose();

        update();
    }

    @Override
    public void update() {
        Vector2 vel = body.getLinearVelocity();
        Vector2 pos = body.getPosition();

        Vector2 newVel = new Vector2(vel);

        float nowTime = CwtfGame.getCurrentWorldTime();
        if (nowTime > lastGroundedTime + 0.2f) {
            grounded = isGrounded(Gdx.graphics.getDeltaTime());
            if (grounded) {
                lastGroundedTime = nowTime;
            }
        }


        boolean leftPressed = (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && CwtfGame.currentWorld == world && CwtfGame.inputEnabled;
        boolean rightPressed = (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && CwtfGame.currentWorld == world && CwtfGame.inputEnabled;

        if (!leftPressed && !rightPressed) {
            if (grounded) {
                newVel.set(newVel.x * 0.9f, newVel.y);
            } else {
                newVel.set(newVel.x * 0.97f, newVel.y);
            }
        }

        int motion = 0;
        if (leftPressed && rightPressed) {
            if (lastMotionKey == Input.Keys.LEFT || lastMotionKey == Input.Keys.A) {
                motion = -1;
            } else {
                motion = 1;
            }
        } else if (leftPressed) {
            motion = -1;
        } else if (rightPressed) {
            motion = 1;
        }

        if ((motion == 1 && newVel.x < 3) || (motion == -1 && newVel.x > -3)) {
            if (grounded) {
                newVel.set(newVel.x + motion * 1f, newVel.y);
            } else {
                newVel.set(newVel.x + motion * 0.5f, newVel.y);
            }
        }
        if (Math.abs(newVel.x) > 3.0f) {
            newVel.x = Math.signum(newVel.x) * 3.0f;
        }

        boolean stillJumping = (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) && (nowTime < lastJumpTime + 0.2f) && CwtfGame.currentWorld == world && CwtfGame.inputEnabled;

        if (doJump && grounded) {
            newVel.set(newVel.x, 8f);
            lastJumpTime = CwtfGame.getCurrentWorldTime();
            CwtfGame.jumpSound.play();
        } else if (stillJumping) {
            newVel.set(newVel.x, 8f);
        }
        doJump = false;

        body.setLinearVelocity(newVel);

        body.setAwake(true);
    }

    public void render(SpriteBatch batch) {
        animationTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = null;

        if ((!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) || !CwtfGame.inputEnabled) {
            animationTime = 0f;
        }

        if (lastMotionKey == Input.Keys.RIGHT || lastMotionKey == Input.Keys.D) {
            currentFrame = walkRightAnimation.getKeyFrame(animationTime, true);
        } else {
            currentFrame = walkLeftAnimation.getKeyFrame(animationTime, true);
        }

        if (currentFrame != null) {
            Vector2 posInPixels = CwtfGame.metersToPixels(body.getPosition());
            posInPixels.set(Math.round(posInPixels.x), Math.round(posInPixels.y));
            batch.draw(currentFrame, posInPixels.x, posInPixels.y);
        }
    }

    public void jump() {
        doJump = true;
    }

    public void setLastMotionKey(int key) {
        this.lastMotionKey = key;
    }

    public boolean isGrounded(float deltaTime) {
        boolean grounded = false;

        World world = body.getWorld();
        Array<Contact> contactList = world.getContactList();
        for(int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            if(contact.isTouching() && (contact.getFixtureA() == fixture || contact.getFixtureB() == fixture)) {
                Vector2 pos = body.getPosition();

                handleContact(contact);

                WorldManifold manifold = contact.getWorldManifold();

                boolean below = true;
                for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                    below &= (manifold.getPoints()[j].y <= pos.y + 0.1f);
                }

                if (below) {
                    grounded = true;
                }
            }
        }
        return grounded;
    }

    public void handleContact(Contact contact) {
        Fixture otherFixture = contact.getFixtureA() == fixture ? contact.getFixtureB() : contact.getFixtureA();
        Object object = otherFixture.getUserData();
        if (object != null) {
            Vector2 pos = body.getPosition();

            WorldManifold manifold = contact.getWorldManifold();

            boolean below = true;
            for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                below &= (manifold.getPoints()[j].y <= pos.y + 0.1f);
            }

            if (object instanceof Button) {
                if (below) {
                    Button button = (Button) object;
                    button.push();
                }
            }
            if (object instanceof Goal) {
                if (below) {
                    Goal goal = (Goal) object;
                    goal.push();
                }
            }
        }
    }

    public Rectangle getBoundingBoxInPixels() {
        Vector2 pos = body.getPosition();
        Vector2 posInPixels = CwtfGame.metersToPixels(pos);
        return new Rectangle(posInPixels.x, posInPixels.y, 16f, 16f);
    }

    public Vector2 getCenterInPixels() {
        Vector2 pos = body.getPosition();
        return CwtfGame.metersToPixels(pos).add(8f, 8f);
    }
}
