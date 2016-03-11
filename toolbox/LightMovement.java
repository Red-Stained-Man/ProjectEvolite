package toolbox;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class LightMovement {
	
	private static float x = 0.0F;
	private static float y = 0.0F;
	private static final float z = 0.0F;
	
	public static Vector3f  move(){
		x += DisplayManager.getFrameTimeSeconds() * 1000;
		
		x %= 24000;
		
		
		y = (-0.0001F * (x * x)) + 10000;
		
		return new Vector3f(x, y, z);
		
	}

}
