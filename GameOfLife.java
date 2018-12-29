import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * This is an implementation of Conway's Game of Life. This is designed
 * so that a student could write the logic of the program and have it work
 * without having to learn about graphics programming, but a motivated 
 * student could modify the graphics if they wanted to.
 * 
 * @author Dave Avis
 * @version 12.19.2018
 */
public class GameOfLife extends ApplicationAdapter
{
    // Required Constants and Variables
    private static final int ROWS = 100; // The number of rows in the world grid
    private static final int COLS = 100; // The number of columns in the world grid
    private static final String WINDOW_TITLE = "Game of Life"; // The text in the window titlebar
    private static final int WINDOW_WIDTH = 400; // width of the window in pixels.
    private static final int WINDOW_HEIGHT = 400; // height of the window in pixels.
    private static float timeStep = 0.2f; // the number of seconds between screen refreshes.
    private String[][] cells; // This is the grid of the world.
    // These are the possible values for each grid location, along with the 
    // associated color. Each row is a Value, Color pair. A complete list of
    // colors is below in the draw() method.
    private String[][] valuesAndColors = { 
        { "DEAD", "WHITE" },  // A single Value, Color pair.
        { "ALIVE", "BLACK" }
    };
    
    // Game Specific Constants and Variables
    private int[][] liveNeighbors;  // how many living cells neighbor this one?
    private double initialPct = 0.2; // 20% of cells are alive to start

    /**
     * Constructor. Initializes the world grid and populates it.
     */
    public GameOfLife()
    {
        cells = new String[ROWS][COLS];
        liveNeighbors = new int[ROWS][COLS];
        populate();
    }
    
    /**
     * Populate the initial world grid randomly.
     */
    private void populate()
    {
        for( int row = 0; row < ROWS; row++ )
        {
            for( int col = 0; col < COLS; col++ )
            {
                if( Math.random() < initialPct )
                {
                    cells[row][col] = "ALIVE";
                } else {
                    cells[row][col] = "DEAD";
                }
            }
        }
    }
    
    /**
     * Count the number of living neighbors surrounding each cell.
     * Wraps around the edges of the world.
     */
    private void countNeighbors()
    {
        int rowUp, rowDown, colLeft, colRight;
        
        for( int row = 0; row < ROWS; row++ )
        {
            for( int col = 0; col < COLS; col++ )
            {
                int neighbors = 0;
                
                rowUp = row - 1;
                if( rowUp < 0 ) rowUp += ROWS; // wrap around the top
                rowDown = (row + 1) % (ROWS - 1); // wrap around the bottom
                
                colLeft = col - 1;
                if( colLeft < 0 ) colLeft += COLS; // wrap around the right
                colRight = (col + 1) % (COLS - 1); // wrap around the left
                
                if( cells[rowUp][colLeft] == "ALIVE" ) neighbors++;
                if( cells[rowUp][col] == "ALIVE" ) neighbors++;
                if( cells[rowUp][colRight] == "ALIVE" ) neighbors++;
                if( cells[row][colLeft] == "ALIVE" ) neighbors++;
                if( cells[row][colRight] == "ALIVE" ) neighbors++;
                if( cells[rowDown][colLeft] == "ALIVE" ) neighbors++;
                if( cells[rowDown][col] == "ALIVE" ) neighbors++;
                if( cells[rowDown][colRight] == "ALIVE" ) neighbors++;

                liveNeighbors[row][col] = neighbors;
            }
        }
    }
    
    /**
     * Compute what the next generation will look like.
     */
    private void nextGen()
    {
        for (int row = 0; row < ROWS; row++) 
        {
            for (int col = 0; col < COLS; col++) 
            {
                if ( cells[row][col].equals("ALIVE") ) 
                {
                    if (liveNeighbors[row][col] < 2)
                        cells[row][col] = "DEAD";
                    else if (liveNeighbors[row][col] == 2 || liveNeighbors[row][col] == 3)
                        cells[row][col] = "ALIVE";
                    else if (liveNeighbors[row][col] > 3)
                        cells[row][col] = "DEAD";
                } else {
                    if (liveNeighbors[row][col] == 3)
                        cells[row][col] = "ALIVE";
                }
            }
        }
    }
    
    /**
     * This method is called every time the screen is refreshed (based on your
     * timeStep). This should call any methods needed to update the screen for the
     * next time it is drawn.
     */
    public void doUpdates()
    {
        countNeighbors();
        nextGen();
    }
    
/////////////////////////////////////////////////////////
/////////// DO NOT EDIT BELOW THIS LINE /////////////////
///////////////////////////////////////////////////////// 
   
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private float timeSinceLastFrame = 0f;
    
    /**
     * Used to launch the application.
     * 
     * @param args not used.
     */
    public static void main( String[] args )
    {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

        cfg.title = WINDOW_TITLE;
        cfg.width = WINDOW_WIDTH;
        cfg.height = WINDOW_HEIGHT;

        new LwjglApplication(new GameOfLife(), cfg);
    }
    
    /**
     * Sets up the Camera and the ShapeRenderer.
     */
    @Override
    public void create()
    {
        // setup the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, COLS, ROWS);
        camera.update();
        
        shapeRenderer = new ShapeRenderer();
    }
    
    /**
     * Clean up.
     */
    @Override
    public void dispose()
    {
        shapeRenderer.dispose();
    }
    
    /**
     * Updates and draws the board after the specified time has passed.
     * The refresh interval is found in the variable timeStep.
     */
    @Override
    public void render()
    {
        timeSinceLastFrame += Gdx.graphics.getDeltaTime();
        if( timeSinceLastFrame > timeStep )
        {
            doUpdates();
            timeSinceLastFrame = 0f;
        }
        draw(); // don't put this in the if statement. render() must draw something everytime.
    }
    
    /**
     * Checks the value of each cell in the grid, updates it with the proper color,
     * and draws the cell.
     */
    public void draw()
    {
        Color color = Color.WHITE;
        
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        shapeRenderer.setProjectionMatrix( camera.combined );
        
        for( int row = 0; row < ROWS; row++ )
        {
            for( int col = 0; col < COLS; col++ )
            {
                for( int i = 0; i < valuesAndColors.length; i++ )
                {
                    if( cells[row][col] == valuesAndColors[i][0] )
                    {
                        switch( valuesAndColors[i][1] ) {
                            case "BLACK": color = Color.BLACK; break;
                            case "BLUE": color = Color.BLUE; break;
                            case "BROWN": color = Color.BROWN; break;
                            case "CHARTREUSE": color = Color.CHARTREUSE; break;
                            case "CLEAR": color = Color.CLEAR; break;
                            case "CORAL": color = Color.CORAL; break;
                            case "CYAN": color = Color.CYAN; break;
                            case "DARK_GRAY": color = Color.DARK_GRAY; break;
                            case "FIREBRICK": color = Color.FIREBRICK; break;
                            case "FOREST": color = Color.FOREST; break;
                            case "GOLD": color = Color.GOLD; break;
                            case "GOLDENROD": color = Color.GOLDENROD; break;
                            case "GRAY": color = Color.GRAY; break;
                            case "GREEN": color = Color.GREEN; break;
                            case "LIGHT_GRAY": color = Color.LIGHT_GRAY; break;
                            case "LIME": color = Color.LIME; break;
                            case "MAGENTA": color = Color.MAGENTA; break;
                            case "MAROON": color = Color.MAROON; break;
                            case "NAVY": color = Color.NAVY; break;
                            case "OLIVE": color = Color.OLIVE; break;
                            case "ORANGE": color = Color.ORANGE; break;
                            case "PINK": color = Color.PINK; break;
                            case "PURPLE": color = Color.PURPLE; break;
                            case "RED": color = Color.RED; break;
                            case "ROYAL": color = Color.ROYAL; break;
                            case "SALMON": color = Color.SALMON; break;
                            case "SCARLET": color = Color.SCARLET; break;
                            case "SKY": color = Color.SKY; break;
                            case "SLATE": color = Color.SLATE; break;
                            case "TAN": color = Color.TAN; break;
                            case "TEAL": color = Color.TEAL; break;
                            case "VIOLET": color = Color.VIOLET; break;
                            case "WHITE": color = Color.WHITE; break;
                            case "YELLOW": color = Color.YELLOW; break;
                            default: color = Color.WHITE; break;
                        }
                    }
                }
                drawBox( color, row, col );
            }
        }
    }
    
    /**
     * Draw a filled box of the proper color in the given cell.
     * 
     * @param color the color to fill the cell with.
     * @param row the row the cell is in.
     * @param col the column the cell is in.
     */
    public void drawBox( Color color, int row, int col )
    {
        shapeRenderer.begin( ShapeType.Filled );
        shapeRenderer.setColor( color );
        shapeRenderer.rect( col, row, 1, 1 );
        shapeRenderer.end();
    }
}