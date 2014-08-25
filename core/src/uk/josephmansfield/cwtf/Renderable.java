package uk.josephmansfield.cwtf;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {
    public void update();
    public void render(SpriteBatch batch);
}
