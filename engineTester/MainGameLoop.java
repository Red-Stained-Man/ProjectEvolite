package engineTester;

import org.lwjgl.opengl.Display;

import gameStates.GameState;
import gameStates.GameStates;
import gameStates.IntroState;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class MainGameLoop {

	private static GameStates currentState = GameStates.MAIN_MENU;

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		while (!Display.isCloseRequested()) {
			switch (MainGameLoop.currentState) {
			case SPLASH_SCREEN:
				break;
			case MAIN_MENU:
				//TODO Logo splash screen
				new IntroState(loader);
				break;
			case HELP:
				break;
			case SETTINGS:
				break;
			case GAME:
				//TODO Loading Screen
				new GameState(loader);
				break;
			case PAUSE:
				//TODO Pause Screen
				break;
			case CLOSING:
				DisplayManager.closeDisplay();
				System.exit(0);
				break;
			default:
			}
		}
	}

	public static void setState(GameStates state) {
		currentState = state;
	}

}