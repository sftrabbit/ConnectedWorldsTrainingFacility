package uk.josephmansfield.cwtf;


import com.badlogic.gdx.graphics.OrthographicCamera;

public interface Level {
    public OrthographicCamera getInitialCamera(int world, CwtfGame.Dimension<Integer> size);
    public void constructWorlds(CwtfGame.WorldData[] worldDatas);
}
