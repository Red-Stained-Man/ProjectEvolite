package gameStates;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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
import models.RawModel;
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
import toolbox.InputHandler;
import toolbox.MousePicker;
import toolbox.Utils;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class GameState extends State {

	public final static Vector4f CLIP_PLANE = new Vector4f(0, -1, 0, 100000000);

	public static List<Entity> entities = new ArrayList<Entity>();
	public static List<Entity> placedEntity = new ArrayList<Entity>();
	public static List<Entity> mouseEntity = new ArrayList<Entity>();
	public static List<Entity> normalEntities = new ArrayList<Entity>();
	public static List<Entity> placedNormalEntity = new ArrayList<Entity>();
	public static List<Entity> mouseNormalEntity = new ArrayList<Entity>();
	public static List<Light> lights = new ArrayList<Light>();

	public static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	public static List<GuiTexture> waterGui = new ArrayList<GuiTexture>();

	public static List<WaterTile> waters = new ArrayList<WaterTile>();

	public static List<Terrain> terrains = new ArrayList<Terrain>();

	public static Random random = new Random();

	public static boolean isPaused = false;

	public GameState(Loader loader) {
		super(GameStates.GAME, loader);
		loop(loader);
	}

	public GameState(Loader loader, List<Entity> entities, List<Entity> placedEntity, List<Entity> normalEntities,
			List<Terrain> terrains) {
		super(GameStates.GAME, loader);

		GameState.entities = entities;
		GameState.placedEntity = placedEntity;
		GameState.normalEntities = normalEntities;
		GameState.terrains = terrains;

		loop(loader);
	}

	@Override
	protected void loop(Loader loader) {
		/*
		 * ID's
		 * 
		 * eevee = 1 fern = 2 Big tree = 3 tree = 4 tall grass = 5 flower = 6
		 * barrel = 7
		 */

		// *********TERRAIN TEXTURE STUFF************

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain/grass_top"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain/dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain/grass_top"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain/cobblestone_mossy"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, 500, 4F);
		terrains.add(terrain);

		// *****************************************

		TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("tree", loader),
				new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
				new ModelTexture(loader.loadTexture("entities/tallgrass")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
				new ModelTexture(loader.loadTexture("entities/flower")));

		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("entities/fern"));
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTexture);

		TexturedModel bobble = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),
				new ModelTexture(loader.loadTexture("lowPolyTree")));

		TexturedModel mew = new TexturedModel(NormalMappedObjLoader.loadOBJ("eevee", loader),
				new ModelTexture(loader.loadTexture("entities/eevee")));

		TexturedModel barrel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("entities/barrel")));
		barrel.getTexture().setShineDamper(10F);
		barrel.getTexture().setReflectivity(0.5F);
		barrel.getTexture().setNormalMap(loader.loadTexture("entities/barrelNormal"));

		Entity mewEntity = new Entity(mew, null, 90F, 90F + 60F, -90F, 0.2F);
		Entity fernEntity = new Entity(fern, null, 0, 0, 0, 1);
		Entity bigtreeEntity = new Entity(bobble, null, 0, 0, 0, 1F);
		Entity treeEntity = new Entity(tree, null, 0, 0, 0, Utils.genRanged(5, 15));
		Entity grassEntity = new Entity(grass, null, 0, 0, 0, 1F);
		Entity flowerEntity = new Entity(flower, 1, null, 0, 0, 0, 1F);
		Entity barrelEntity = new Entity(barrel, null, 0, 0, 0, 1F);

		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		flower.getTexture().setNumberOfRows(2);
		fern.getTexture().setHasTransparency(true);

		MasterRenderer renderer = new MasterRenderer(loader);

		RawModel playerModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel playerTexturedModel = new TexturedModel(playerModel,
				new ModelTexture(loader.loadTexture("playerTexture")));

		Player player = new Player(playerTexturedModel, Utils.getPos(terrain), 0, 0, 0, 1f);
		entities.add(player);

		lights.add(new Light(new Vector3f(75, 100F, 75), new Vector3f(1, 1, 1)));

		Camera camera = new Camera(player, terrain);

		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MousePicker mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		// **********WATER RENDERING**********
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);

		WaterTile water = new WaterTile(terrain, 0F);
		waters.add(water);

		// ********** ENTITY SPAWN**********

		for (int i = 0; i < 1000; i++) {
			if (i % 3 == 0) {
				entities.add(new Entity(grass, Utils.getPos(terrain), 0, 0, 0, 0.9F));
				entities.add(new Entity(fern, Utils.getPos(terrain), 0, 0, 0, 0.9F));
				entities.add(new Entity(flower, random.nextInt(4), Utils.getPos(terrain), 0, 0, 0,
						Utils.genRanged(0.4F, 1.2F)));
			}
			if (i % 25 == 0) {
				entities.add(new Entity(bobble, Utils.getPos(terrain), 0, 0, 0, Utils.genRanged(0.6F, 2.0F)));
				entities.add(new Entity(tree, Utils.getPos(terrain), 0, 0, 0, Utils.genRanged(5, 15)));
			}
		}
		game: while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move(true);
			mousePicker.update();
			System.out.println(player.getPosition());

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();

			renderer.renderScene(entities, normalEntities, placedNormalEntity, mouseNormalEntity, placedEntity,
					mouseEntity, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
			camera.getPosition().y += distance;
			camera.invertPitch();

			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalEntities, placedNormalEntity, mouseNormalEntity, placedEntity,
					mouseEntity, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
			fbos.unbindCurrentFrameBuffer();

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			Vector3f terrainPoint = mousePicker.getCurrentTerrainPoint();
			InputHandler.update();

			if (terrainPoint != null && placedEntity.size() <= 300) {
				mouseEntity.clear();
				mouseNormalEntity.clear();

				if (InputHandler.getKeyPressed() == 1) {
					mouseEntity.clear();
					mouseNormalEntity.clear();
					mouseEntity.add(mewEntity);
					mewEntity.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 5F, terrainPoint.z));
					if (Mouse.isButtonDown(0) && !Keyboard.isKeyDown(Keyboard.KEY_Z)) {
						placedEntity
								.add(new Entity(mew, new Vector3f(terrainPoint.x, terrainPoint.y + 5F, terrainPoint.z),
										90F, 90F + 60F, -90F, 0.2F));
					}
				} else if (InputHandler.getKeyPressed() == 2) {
					mouseEntity.clear();
					mouseNormalEntity.clear();
					mouseEntity.add(fernEntity);
					fernEntity.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z));
					if (Mouse.isButtonDown(0) && !Keyboard.isKeyDown(Keyboard.KEY_Z)) {
						placedEntity.add(new Entity(fern, new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z),
								0, 0, 0, 1F));
					}
				} else if (InputHandler.getKeyPressed() == 3) {
					mouseEntity.clear();
					mouseNormalEntity.clear();
					mouseEntity.add(bigtreeEntity);
					bigtreeEntity.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z));
					if (Mouse.isButtonDown(0) && !Keyboard.isKeyDown(Keyboard.KEY_Z)) {
						placedEntity.add(new Entity(bobble,
								new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z), 0, 0, 0, 1F));
					}
				} else if (InputHandler.getKeyPressed() == 4) {
					mouseEntity.clear();
					mouseNormalEntity.clear();
					mouseEntity.add(treeEntity);
					treeEntity.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z));
					if (Mouse.isButtonDown(0) && !Keyboard.isKeyDown(Keyboard.KEY_Z)) {
						placedEntity.add(new Entity(tree, new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z),
								0, 0, 0, Utils.genRanged(5, 15)));
					}
				} else if (InputHandler.getKeyPressed() == 5) {
					mouseEntity.clear();
					mouseNormalEntity.clear();
					mouseEntity.add(grassEntity);
					grassEntity.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z));
					if (Mouse.isButtonDown(0) && !Keyboard.isKeyDown(Keyboard.KEY_Z)) {
						placedEntity.add(new Entity(grass, new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z),
								0, 0, 0, 1F));
					}
				} else if (InputHandler.getKeyPressed() == 6) {
					mouseEntity.clear();
					mouseNormalEntity.clear();
					mouseEntity.add(flowerEntity);
					flowerEntity.setPosition(terrainPoint);
					if (Mouse.isButtonDown(0) && !Keyboard.isKeyDown(Keyboard.KEY_Z)) {
						placedEntity.add(new Entity(flower, random.nextInt(4),
								new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z), 0, 0, 0, 1F));
					}
				} else if (InputHandler.getKeyPressed() == 7) {
					mouseEntity.clear();
					mouseNormalEntity.add(barrelEntity);
					barrelEntity.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 6F, terrainPoint.z));
					if (Mouse.isButtonDown(0) && !Keyboard.isKeyDown(Keyboard.KEY_Z)) {
						placedNormalEntity.add(new Entity(barrel,
								new Vector3f(terrainPoint.x, terrainPoint.y + 6F, terrainPoint.z), 0, 0, 0, 1F));
					}
				} else {
					mouseEntity.clear();
					mouseNormalEntity.clear();
				}
			}

			if (!placedEntity.isEmpty() && Keyboard.isKeyDown(Keyboard.KEY_Z)) {
				placedEntity.clear();
			}

			if (!placedNormalEntity.isEmpty() && Keyboard.isKeyDown(Keyboard.KEY_Z)) {
				placedNormalEntity.clear();
			}

			if (camera.getPosition().y < water.getHeight()) {
				waterGui.clear();
				GuiTexture waterScreen = new GuiTexture(loader.loadTexture("guis/water"),
						new Vector2f(0, water.getHeight() / 2), new Vector2f(1, 1));
				waterGui.add(waterScreen);
			} else {
				waterGui.clear();
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				isPaused = true;
				break game;
			}

			renderer.processEntity(player);
			renderer.renderScene(entities, normalEntities, placedNormalEntity, mouseNormalEntity, placedEntity,
					mouseEntity, terrains, lights, camera, CLIP_PLANE);
			waterRenderer.render(waters, camera, lights);
			guiRenderer.render(guis);
			guiRenderer.render(waterGui);
			DisplayManager.updateDisplay();
		}

		if (isPaused) {
			
		} else {

			fbos.cleanUp();
			guiRenderer.cleanUp();
			renderer.cleanUp();
			loader.cleanUp();

			MainGameLoop.setState(GameStates.CLOSING);
		}

	}

}
