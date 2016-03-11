package water;

import terrains.Terrain;

public class WaterTile {
     
    public static float TILE_SIZE ;
     
    private float height;
    private float x,z;
     
    public WaterTile(Terrain terrain, float height){
        this.x = terrain.getSize() / 2F;
        this.z = terrain.getSize() / 2F;
        TILE_SIZE = terrain.getSize() / 2F;
        this.height = height;
    }
 
    public float getHeight() {
        return height;
    }
 
    public float getX() {
        return x;
    }
 
    public float getZ() {
        return z;
    }
 
 
 
}