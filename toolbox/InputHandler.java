package toolbox;

import org.lwjgl.input.Keyboard;

public class InputHandler {

	private static int numPadPressed = 1;
	private static boolean isPPressed = false;

	public static void update() {

		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1) || Keyboard.isKeyDown(Keyboard.KEY_1)) {
			numPadPressed = 1;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2) || Keyboard.isKeyDown(Keyboard.KEY_2)) {
			numPadPressed = 2;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3) || Keyboard.isKeyDown(Keyboard.KEY_3)) {
			numPadPressed = 3;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4) || Keyboard.isKeyDown(Keyboard.KEY_4)) {
			numPadPressed = 4;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5) || Keyboard.isKeyDown(Keyboard.KEY_5)) {
			numPadPressed = 5;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6) || Keyboard.isKeyDown(Keyboard.KEY_6)) {
			numPadPressed = 6;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7) || Keyboard.isKeyDown(Keyboard.KEY_7)) {
			numPadPressed = 7;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8) || Keyboard.isKeyDown(Keyboard.KEY_8)) {
			numPadPressed = 8;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD9) || Keyboard.isKeyDown(Keyboard.KEY_9)) {
			numPadPressed = 9;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0) || Keyboard.isKeyDown(Keyboard.KEY_0)) {
			numPadPressed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_P)){
			isPPressed = true;
		}else{
			isPPressed = false;
		}

	}

	public static int getKeyPressed() {
		return numPadPressed;
	}
	
	public static boolean isPDown(){
		return isPPressed;
	}

}
