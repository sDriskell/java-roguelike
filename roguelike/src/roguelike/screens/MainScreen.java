package roguelike.screens;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.Game;
import roguelike.GameLoader;
import roguelike.TurnEvent;
import roguelike.TurnResult;
import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.DisplayManager;
import roguelike.ui.InputManager;
import roguelike.ui.LookDisplay;
import roguelike.ui.MainWindow;
import roguelike.ui.MessageDisplay;
import roguelike.ui.StatsDisplay;
import roguelike.ui.animations.AnimationManager;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.ArrayUtils;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVTranslator;
import squidpony.squidgrid.fov.TranslucenceWrapperFOV;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;
import squidpony.squidutility.Pair;

/**
 * 
 */
public class MainScreen extends Screen {
  private static final int WINDOW_WIDTH = WIDTH - MainWindow.STAT_WIDTH;
  private static final int WINDOW_HEIGHT = HEIGHT;

  private final FOVTranslator fov = new FOVTranslator(new TranslucenceWrapperFOV());
  private final RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;

  TerminalBase windowTerminal;

  Game game;
  MessageDisplay messageDisplay;
  StatsDisplay statsDisplay;
  LookDisplay lookDisplay;

  DisplayManager displayManager;
  AnimationManager animationManager;

  TurnResult currentTurn;

  private Rectangle[] screenQuadrants = new Rectangle[4];

  /**
   * 
   * @param argTerm
   * @param argInitGame
   */
  public MainScreen(TerminalBase argTerm, Game argInitGame) {
    super(argTerm);

    if (argInitGame == null) {
      throw new IllegalArgumentException("initialGame cannot be null");
    }

    game = argInitGame;

    int midX = WINDOW_WIDTH / 2;
    int midY = WINDOW_HEIGHT / 2;

    screenQuadrants[0] = new Rectangle(0, 0, midX, midY);
    screenQuadrants[1] = new Rectangle(0, midY, midX, midY);
    screenQuadrants[2] = new Rectangle(midX, 0, midX, midY);
    screenQuadrants[3] = new Rectangle(midX, midY, midX, midY);

    game.initialize();
    windowTerminal = argTerm.getWindow(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

    Log.debug("Window tile size: " + WINDOW_WIDTH + "x" + WINDOW_HEIGHT);

    /* used for FOV lighting */
    SColorFactory.addPallet("light",
        SColorFactory.asGradient(SColor.WHITE, SColor.DARK_SLATE_GRAY));

    animationManager = new AnimationManager();
    displayManager = DisplayManager.instance();

    int messageLines = 21;
    TerminalBase messageTerminal = argTerm.getWindow(WIDTH - MainWindow.STAT_WIDTH + 1,
        messageLines - 1, MainWindow.STAT_WIDTH - 2, HEIGHT - messageLines);

    TerminalBase statsTerminal = argTerm.getWindow(WIDTH - MainWindow.STAT_WIDTH, 0,
        MainWindow.STAT_WIDTH, HEIGHT);

    messageDisplay = new MessageDisplay(Game.current().messages(), messageTerminal, messageLines);
    statsDisplay = new StatsDisplay(statsTerminal);
    statsDisplay.setPlayer(game.getPlayer());

    Rectangle statsSize = statsTerminal.size();
    int lookWidth = Math.min(20, WINDOW_WIDTH - 4);
    int lookHeight = Math.min(20, WINDOW_HEIGHT - 4);
    lookDisplay = new LookDisplay(
        statsTerminal.getWindow(statsSize.x + 2, statsSize.height - 23, statsSize.width - 4, 22),
        lookWidth, lookHeight);

    doFOV();
    drawMap();
    drawStats();

    InputManager.setInputEnabled(true);
    InputManager.previousKeyMap();
    displayManager.setDirty();
  }

  @Override
  public Rectangle getDrawableArea() {
    return new Rectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
  }

  @Override
  public void onDraw() {
    drawFrame();
  }

  @Override
  public void process() {
    if (game.isPlayerDead()) {
      System.out.println("You died");
      Player player = game.getPlayer();
      AttackAttempt killedBy = player.getLastAttackedBy();

      System.out.println("Switching to game over screen");
      Actor killedByActor = null;

      if (killedBy != null) {
        killedByActor = killedBy.getActor();
      }

      setNextScreen(new PlayerDiedScreen(killedByActor, terminal), false);
    }
    else {
      TurnResult run;
      run = game.processTurn();
      currentTurn = run;

      if (!run.isRunning()) {
        GameLoader.save(this.game);
        Log.debug("Saving game...");
        setNextScreen(new TitleScreen(terminal), false);
      }

      /* recalculate FOV if player moved/acted */
      if (run.playerActedThisTurn()) {
        doFOV();
      }
    }
  }

  /**
   * 
   */
  private void drawFrame() {
    if (currentTurn == null) {
      return;
    }

    drawMap();
    drawStats();
    drawLookDisplay(currentTurn);
    drawMessages(currentTurn);
    drawEvents(currentTurn);

    /*
     * this will only refresh if player input has occurred or something has reset
     * the dirty flag
     */
    boolean animationProcessed = animationManager.nextFrame(terminal);

    if (animationProcessed || animationManager.shouldRefresh()) {
      displayManager.setDirty();
    }
  }

  /**
   * 
   */
  private void drawMap() {
    MapArea currentMap = game.getCurrentMapArea();
    Coordinate centerPosition = game.getCenterScreenPosition();
    Rectangle screenArea = currentMap.getVisibleAreaInTiles(WINDOW_WIDTH, WINDOW_HEIGHT,
        centerPosition);

    for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
      for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
        Tile tile = currentMap.getTileAt(x, y);
        int screenX = x - screenArea.x;
        int screenY = y - screenArea.y;

        if (tile.isVisible()) {
          SColor color, bgColor;
          SColor litColor = tile.getLightedColorValue();

          if (tile.getColor() == null) {
            throw new IllegalArgumentException("null tile color");
          }
          if (litColor == null) {
            throw new IllegalArgumentException("null lit color");
          }

          color = SColorFactory.lightWith(tile.getColor(), litColor);
          bgColor = SColorFactory.lightWith(tile.getBackground(), litColor);
          terminal.withColor(color, bgColor).put(screenX, screenY, tile.getSymbol());
        }
        else {
          terminal.withColor(tile.getColor(), tile.getBackground()).put(screenX, screenY,
              tile.getSymbol());
        }
      }
    }
  }

  /**
   * Calculates the Field of View and marks the maps spots seen appropriately.
   */
  private void doFOV() {
    MapArea currentMap = game.getCurrentMapArea();
    Coordinate centerPosition = game.getCenterScreenPosition();

    Rectangle screenArea = currentMap.getVisibleAreaInTiles(WINDOW_WIDTH, WINDOW_HEIGHT,
        centerPosition);

    doFOV(currentMap, screenArea, centerPosition);
  }

  /**
   * 
   * @param argCurrent
   * @param argScrnArea
   * @param argPlayerPos
   */
  private void doFOV(MapArea argCurrent, Rectangle argScrnArea, Coordinate argPlayerPos) {
    float[][] lighting = ArrayUtils.getSubArray(argCurrent.getLightValues(), argScrnArea);
    float lightForce = game.getPlayer().getVisionRadius();
    float[][] incomingLight = fov.calculateFOV(lighting, argPlayerPos.x - argScrnArea.x,
        argPlayerPos.y - argScrnArea.y, 1f, 1 / lightForce, radiusStrategy);

    for (int x = argScrnArea.x; x < argScrnArea.getMaxX(); x++) {
      for (int y = argScrnArea.y; y < argScrnArea.getMaxY(); y++) {
        int cX = x - argScrnArea.x;
        int cY = y - argScrnArea.y;

        Tile tile = argCurrent.getTileAt(x, y);
        tile.setVisible(fov.isLit(cX, cY));

        if (incomingLight[cX][cY] > 0) {
          float bright = 1 - incomingLight[cX][cY];
          tile.setLightedColorValue(SColorFactory.fromPallet("light", bright));
        }
        else if (!tile.getLightedColorValue().equals(SColor.BLACK)) {
          tile.setLightedColorValue(SColor.BLACK);
        }
      }
    }
  }

  /**
   * 
   * @param argRun
   */
  private void drawEvents(TurnResult argRun) {
    if (argRun == null) {
      return;
    }

    Rectangle screenArea = game.getCurrentMapArea().getVisibleAreaInTiles(WINDOW_WIDTH,
        WINDOW_HEIGHT, game.getCenterScreenPosition());

    for (TurnEvent event : argRun.getEvents()) {
      Actor initiator = event.getInitiator();
      Actor target = event.getTarget();
      Coordinate initiatorPos = initiator.getPosition();
      Coordinate targetPos, diff;
      DirectionIntercardinal direction;

      switch (event.getType()) {
        case TurnEvent.ATTACKED:
        case TurnEvent.ATTACK_MISSED:
          targetPos = target.getPosition();
          diff = initiator.getPosition().createOffsetPosition(-targetPos.x, -targetPos.y);
          direction = DirectionIntercardinal.getDirection(-diff.x, -diff.y);

          Log.debug(initiator.getName() + " attacks " + target.getName() + " in direction "
              + direction.symbol);

          if (shouldDisplayAnimation(initiatorPos, targetPos, screenArea, true)) {
            animationManager.addAnimation(event.getAnimation());
          }

          break;

        case TurnEvent.RANGED_ATTACKED:
          targetPos = target.getPosition();

          // only target needs to be visible here
          if (shouldDisplayAnimation(initiatorPos, targetPos, screenArea, false)) {
            animationManager.addAnimation(event.getAnimation());
            Log.debug("Added attack animation");
          }

          break;
      }
    }

    // prevent processing multiple times
    argRun.getEvents().clear();
  }

  /**
   * 
   * @param argRun
   */
  private void drawMessages(TurnResult argRun) {
    messageDisplay.draw();
  }

  /**
   * 
   */
  private void drawStats() {
    statsDisplay.draw();
  }

  /**
   * 
   * @param argRun
   */
  private void drawLookDisplay(TurnResult argRun) {
    // TODO: make this based on the player's position instead of using a global
    // property on the Game object
    Pair<Point, Boolean> p = argRun.getCurrentLook();

    if (p == null || p.getFirst() == null) {
      return;
    }

    int quadrantIdx = 0;
    Rectangle quadrant = screenQuadrants[0];
    Point player = game.getPlayer().getPosition();
    Point lookingAt = p.getFirst();

    while (quadrant.contains(player) || quadrant.contains(lookingAt)) {
      quadrantIdx++;
      quadrant = screenQuadrants[quadrantIdx];
    }

    TerminalBase term = this.terminal.getWindow(quadrant.x + 1, quadrant.y + 0, quadrant.width - 2,
        quadrant.height - 0);
    int height = lookDisplay.setTerminal(term).getHeight(game.getCurrentMapArea(), p.getFirst().x,
        p.getFirst().y, p.getSecond(), p.getSecond() ? "Looking at" : "On ground");
    lookDisplay.draw(height);
  }

  /**
   * 
   * @param argInitPos
   * @param argTgtPos
   * @param argScrnArea
   * @param isInitiatorVisible
   * @return
   */
  private boolean shouldDisplayAnimation(Point argInitPos, Point argTgtPos, Rectangle argScrnArea,
      boolean isInitiatorVisible) {
    MapArea map = game.getCurrentMapArea();

    if (argScrnArea.contains(argInitPos) && argScrnArea.contains(argTgtPos)) {
      return (map.isVisible(argInitPos) || !isInitiatorVisible) && map.isVisible(argTgtPos);
    }

    return false;
  }
}
