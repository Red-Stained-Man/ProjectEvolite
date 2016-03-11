package guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import engineTester.MainGameLoop;
import gameStates.GameStates;
import gameStates.IntroState;
import renderEngine.Loader;

public class MainMenuCollision {

	private static List<GuiTexture> selector = new ArrayList<GuiTexture>();

	public static boolean playGame = false;

	public static void update(Loader loader, GuiRenderer renderer) {
		playGame = false;
		System.out.println(Mouse.getX() + ", " + Mouse.getY());

		int x = Mouse.getX();
		int y = Mouse.getY();

		if (!IntroState.helpScreen) {
			if (x >= 160 && y >= 440 && x <= 260 && y <= 550) {
				placeSelector(loader, -0.63F, 0.36F, 0.1F, 0.161F);

				if (Mouse.isButtonDown(0)) {
					MainGameLoop.setState(GameStates.GAME);
					playGame = true;
				}

			} else if (x >= 275 && y >= 400 && x <= 380 && y <= 480) {
				
				placeSelector(loader, -0.46F, 0.17F, 0.105F, 0.175F);

				if (Mouse.isButtonDown(0)) {
					IntroState.helpScreen = true;
				}

			} else if (x >= 160 && y >= 330 && x <= 270 && y <= 390) {
				placeSelector(loader, -0.64F, -0.05F, 0.105F, 0.175F);
				
				if (Mouse.isButtonDown(0)) {
					IntroState.exitGame = true;
				}

			} else {
				if (!selector.isEmpty()) {
					selector.clear();
				}
			}

		} else {
			if (x >= 1040 && y >= 640 && x <= 1170 && y <= 700) {
				placeSelector(loader, 0.75F, 0.84F, 0.105F, 0.15F);

				if (Mouse.isButtonDown(0)) {
					IntroState.helpScreen = false;
				}
			} else {
				if (!selector.isEmpty()) {
					selector.clear();
				}
			}
		}

		if (!selector.isEmpty()) {
			renderer.render(selector);
		}
	}
	
	public static void placeSelector(Loader loader, float x, float y, float sx, float sy){
		GuiTexture select = new GuiTexture(loader.loadTexture("guis/select"), new Vector2f(x, y),
				new Vector2f(sx, sy));

		if (!selector.isEmpty()) {
			selector.clear();
		}
		
		selector.add(select);
	}

}
