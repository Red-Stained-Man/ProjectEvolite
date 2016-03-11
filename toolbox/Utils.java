package toolbox;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import terrains.Terrain;

public class Utils {
	
	public static Vector3f getPos(Terrain terrain) {
		float x = genRanged(0, terrain.getSize());
		float z = genRanged(0, terrain.getSize());
		float y = terrain.getHeightOfTerrain(x, z);

		if (y < 0) {
			return getPos(terrain);
		} else {
			return new Vector3f(x, y, z);
		}
	}

	public static float genRanged(float min, float max) {
		Random random = new Random();

		if (min >= max)
			return 0.0F;
		float ans = random.nextFloat() * (max - min) + min;
		return ans;
	}

}
