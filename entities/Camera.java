package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import terrains.Terrain;

public class Camera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;

	private Player player;

	public Camera(Player player, Terrain terrain) {
		this.player = player;
	}

	public void move(boolean canMove) {
		
		
		
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance + 10F);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		if (canMove) {
			calculatePitch();
			calculateAngleAroundPlayer();
			calculateZoom();
			if (Mouse.isButtonDown(1)) {
				if (!Mouse.isGrabbed())
					Mouse.setGrabbed(true);
				calculateAngleAroundPlayer();
				calculatePitch();
				if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
					player.increaseRotation(0, angleAroundPlayer, 0);
					angleAroundPlayer = 0;
				}
			} else if (!Mouse.isButtonDown(1)) {
				if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
					angleAroundPlayer /= 1.2f;
					if (angleAroundPlayer >= -0.5f && angleAroundPlayer <= 0.5f)
						angleAroundPlayer = 0;
				}
			}
			if (!Mouse.isButtonDown(1) && Mouse.isGrabbed())
				Mouse.setGrabbed(false);
		}
	}

	public void rotate(float f) {
		this.angleAroundPlayer += f;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
	}
	
	public void setHeight(float height){
		this.position.y = height;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance;
	}

	private float calculateHorizontalDistance() {
		float hD = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		if (hD < 0)
			hD = 0;
		return hD;
	}

	private float calculateVerticalDistance() {
		float vD = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		if (vD < 0)
			vD = 0;
		return vD;
	}

	private void calculateZoom() {

		float zoomLevel = Mouse.getDWheel() * 0.2f;
		distanceFromPlayer -= zoomLevel;

	}

	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * 0.1f;
		pitch -= pitchChange;
		if (pitch < 0)
			pitch = 0;
		else if (pitch > 90)
			pitch = 90;
	}

	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}

	public void invertPitch() {
		this.pitch = -this.pitch;
		this.roll = -this.roll;
	}

}