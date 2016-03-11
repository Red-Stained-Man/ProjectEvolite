package gameStates;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engineTester.MainGameLoop;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import guis.MainMenuCollision;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Utils;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class IntroState extends State {

	private final static Vector4f CLIP_PLANE = new Vector4f(0, -1, 0, 100000000);

	public static List<Entity> entities = new ArrayList<Entity>();
	public static List<Entity> normalEntities = new ArrayList<Entity>();
	public static List<Light> lights = new ArrayList<Light>();
	public static List<GuiTexture> menus = new ArrayList<GuiTexture>();
	public static List<Terrain> terrains = new ArrayList<Terrain>();

	private static Random random = new Random();

	public static boolean helpScreen = false;
	public static boolean exitGame = false;

	public IntroState(Loader loader) {
		super(GameStates.MAIN_MENU, loader);
		loop(loader);
	}

	@Override
	protected void loop(Loader loader) {

		// **********Terrain Texture Stuffs**********
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain/grass_top"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain/dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain/grass_top"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain/cobblestone_mossy"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap", 150F);
		terrains.add(terrain);

		// **********Entities**********

		TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("tree", loader),
				new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
				new ModelTexture(loader.loadTexture("entities/tallgrass")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
				new ModelTexture(loader.loadTexture("entities/flower")));
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
				new ModelTexture(loader.loadTexture("entities/fern")));
		TexturedModel barrel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("entities/barrel")));
		barrel.getTexture().setShineDamper(10F);
		barrel.getTexture().setReflectivity(0.5F);
		barrel.getTexture().setNormalMap(loader.loadTexture("entities/barrelNormal"));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		flower.getTexture().setNumberOfRows(2);
		fern.getTexture().setHasTransparency(true);
		fern.getTexture().setUseFakeLighting(true);

		lights.add(new Light(new Vector3f(75, 100F, 75), new Vector3f(1, 1, 1)));

		MasterRenderer renderer = new MasterRenderer(loader);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		Player player = new Player(barrel, genPlayerPos(terrain), 0F, 0f, 0F, 0f);

		Camera camera = new Camera(player, terrain);

		// **********WATER RENDERING**********
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(terrain, 0F);
		waters.add(water);

		// **********MAIN MENU GUI**********

		// **********PLACING ENTITIES**********

		for (int i = 0; i < 50; i++) {
			entities.add(new Entity(tree, Utils.getPos(terrain), 0, Utils.genRanged(0F, 360F), 0,
					Utils.genRanged(4.6F, 5.6F)));
			entities.add(new Entity(flower, random.nextInt(4), Utils.getPos(terrain), 0, Utils.genRanged(0F, 360F), 0,
					Utils.genRanged(0.4F, 1.3F)));
		}
		for (int i = 0; i < 100; i++) {
			entities.add(new Entity(grass, Utils.getPos(terrain), 0, Utils.genRanged(0F, 360F), 0,
					Utils.genRanged(0.5F, 1.3F)));
			entities.add(new Entity(fern, Utils.getPos(terrain), 0, Utils.genRanged(0F, 360F), 0,
					Utils.genRanged(0.5F, 1.3F)));
		}

		// **********GAME LOOP**********
		while (!MainMenuCollision.playGame && !exitGame) {

			camera.move(false);
			camera.setHeight(12);
			camera.rotate(-0.05F);

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();

			renderer.renderScene(entities, normalEntities, terrains, lights, camera,
					new Vector4f(0, 1, 0, -water.getHeight()));
			camera.getPosition().y += distance;
			camera.invertPitch();

			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalEntities, terrains, lights, camera,
					new Vector4f(0, -1, 0, water.getHeight()));
			fbos.unbindCurrentFrameBuffer();

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			renderer.processEntity(player);
			renderer.renderScene(entities, normalEntities, terrains, lights, camera, CLIP_PLANE);
			waterRenderer.render(waters, camera, lights);
			guiRenderer.render(menus);
			MainMenuCollision.update(loader, guiRenderer);

			if (!helpScreen) {
				menus.clear();
				GuiTexture mainMenu = new GuiTexture(loader.loadTexture("guis/MainMenu"), new Vector2f(-0.2F, 0F),
						new Vector2f(1F, 1F));
				menus.add(mainMenu);
			} else {
				menus.clear();
				GuiTexture helpMenu = new GuiTexture(loader.loadTexture("guis/Help"), new Vector2f(0.5F, -0.4F),
						new Vector2f(1.4F, 1.4F));
				menus.add(helpMenu);
			}

			DisplayManager.updateDisplay();

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Display.isCloseRequested()) {
				cleanUp(fbos, guiRenderer, renderer, loader);
				MainGameLoop.setState(GameStates.CLOSING);
				System.exit(0);
			}

		}
		cleanUp(fbos, guiRenderer, renderer, loader);

		if (!exitGame) {
			MainGameLoop.setState(GameStates.GAME);
		} else {
			MainGameLoop.setState(GameStates.CLOSING);
		}

	}

	private static void cleanUp(WaterFrameBuffers fbos, GuiRenderer guiRenderer, MasterRenderer renderer,
			Loader loader) {
		fbos.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
	}

	private static Vector3f genPlayerPos(Terrain terrain) {
		float x = terrain.getSize() / 2f;
		float y = 1F;
		float z = terrain.getSize() / 2F;

		return new Vector3f(x, y, z);
	}

}
