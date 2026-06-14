import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOError;
import java.util.Scanner;
import java.awt.Color;

public class Engine extends Canvas implements Runnable {
    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;
    private final double MOVE_SPEED = 1.0;
    private final double ROTATION_SPEED = 1.5;

    private boolean running = false;
    private int mapWidth;
    private int mapHeight;

    private BufferedImage frameBuffer;

    private int[][] map;

    // Player position vector
    private Vector2D pos = new Vector2D(1.5, 1.5);
    // Player direction vector
    private Vector2D dir = new Vector2D(1, 0);
    // Camera plane vector
    private Vector2D plane = new Vector2D(0, 1);

    private double time = 0; // time of current frame
    private double oldTime = 0; // time of previous frame
    private InputHandler inputHandler;

    public Engine() {
        // Setup the Window
        frameBuffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        JFrame frame = new JFrame("Java Raycaster");
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        inputHandler = new InputHandler();
        this.addKeyListener(inputHandler);
        this.requestFocus();
    }

    // Main loop
    public void run() {
        // Setup Double Buffering (eliminates screen tearing/flickering)
        createBufferStrategy(2);
        BufferStrategy bs = getBufferStrategy();

        dir.rotate(1);
        plane.rotate(1);

        while (running) {
            oldTime = time;
            time = System.nanoTime() / 1e9; // Convert nanoseconds to seconds
            double deltaTime = time - oldTime;

            // Player logic
            updatePlayerState(deltaTime);

            // Raycasting and rendering logic
            renderToBuffer();

            // Push the frame buffer to the screen
            Graphics g = bs.getDrawGraphics();
            g.drawImage(frameBuffer, 0, 0, getWidth(), getHeight(), null);
            g.dispose();
            bs.show(); // Flip the buffer being used to display the new frame

            // Optional Thread.sleep() to cap fame rate.
        }
    }

    private void updatePlayerState(double dt) {
        if (inputHandler.getForward()) {
            pos.x += dir.x * MOVE_SPEED * dt;
            pos.y += dir.y * MOVE_SPEED * dt;
        }
        if (inputHandler.getBackward()) {
            pos.x -= dir.x * MOVE_SPEED * dt;
            pos.y -= dir.y * MOVE_SPEED * dt;
        }
        if (inputHandler.getLeft()) {
            dir.rotate(-ROTATION_SPEED * dt);
            plane.rotate(-ROTATION_SPEED * dt);
        }
        if (inputHandler.getRight()) {
            dir.rotate(ROTATION_SPEED * dt);
            plane.rotate(ROTATION_SPEED * dt);
        }
    }

    private void renderToBuffer() {
        // Clear screen (draw floor and ceiling)
        Graphics2D bufferGraphics = (Graphics2D) frameBuffer.getGraphics();
        bufferGraphics.setColor(Color.DARK_GRAY);
        bufferGraphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT / 2);
        bufferGraphics.setColor(Color.GRAY);
        bufferGraphics.fillRect(0, SCREEN_HEIGHT / 2, SCREEN_WIDTH, SCREEN_HEIGHT / 2);
        bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int x = 0; x < SCREEN_WIDTH; x++) {
            // Calculate ray direction for each x in the screen
            double cameraX = 2 * (x / (double) SCREEN_WIDTH) - 1;
            Vector2D rayDir = new Vector2D(dir.x + plane.x * cameraX, dir.y + plane.y * cameraX);
            // Coordinate each grid square in the map
            Vector2D mapGrid = new Vector2D((int) pos.x, (int) pos.y);
            // Length of ray to the next boundary from current pos
            Vector2D sideDist = new Vector2D(0, 0);
            // Length of ray to the next boundary from currnet x and y
            Vector2D deltaDist = new Vector2D(Math.abs(1 / rayDir.x), Math.abs(1 / rayDir.y));
            // Step direction
            Vector2D step = new Vector2D(0, 0);

            boolean isHit = false; // If a wall was hit
            int side = 0; // x-side = 0, y-side = 1

            // Get step direction and initial distance to a boundary
            if (rayDir.x > 0) {
                step.x = 1;
                sideDist.x = ((mapGrid.x + 1.0) - pos.x) * deltaDist.x;
            } else {
                step.x = -1;
                sideDist.x = (pos.x - mapGrid.x) * deltaDist.x;
            }
            if (rayDir.y > 0) {
                step.y = 1;
                sideDist.y = ((mapGrid.y + 1.0) - pos.y) * deltaDist.y;
            } else {
                step.y = -1;
                sideDist.y = (pos.y - mapGrid.y) * deltaDist.y;
            }

            // Main DDA algorith,
            while (!isHit) {
                // Check which boundary was met first
                if (sideDist.x < sideDist.y) {
                    sideDist.x += deltaDist.x;
                    mapGrid.x += step.x;
                    side = 0;
                } else {
                    sideDist.y += deltaDist.y;
                    mapGrid.y += step.y;
                    side = 1;
                }

                if (mapGrid.x < 0 || mapGrid.x >= mapWidth || mapGrid.y < 0 || mapGrid.y >= mapHeight) {
                    break;
                }

                // Check if wall was hit
                isHit = map[(int) mapGrid.y][(int) mapGrid.x] > 0;
            }

            // Calulate perpendicular distance to wall from camera plane
            double dist;
            if (side == 0) {
                dist = sideDist.x - deltaDist.x;
                bufferGraphics.setColor(Color.WHITE); // Floor
            } else {
                dist = sideDist.y - deltaDist.y;
                bufferGraphics.setColor(Color.lightGray); // Floor
            }
            double lineHeight = SCREEN_HEIGHT / dist;

            // Clamp values
            double drawStart = SCREEN_HEIGHT / 2 - lineHeight / 2;
            double drawEnd = SCREEN_HEIGHT / 2 + lineHeight / 2;
            drawStart = Math.clamp(drawStart, 0, SCREEN_HEIGHT - 1);
            drawEnd = Math.clamp(drawEnd, 0, SCREEN_HEIGHT - 1);

            Rectangle2D.Double line = new Rectangle2D.Double(x, drawStart, 1, drawEnd - drawStart);
            bufferGraphics.fill(line);
        }
    }

    private int[][] getMapFromTextFile(String filePath) throws IllegalArgumentException {
        File file = new File(filePath);

        int firstIndex = 0;
        int secondIndex = 0;
        int[][] newMap = null;
        try (Scanner scanner = new Scanner(file)) {
            // Get width and height of map from file
            if (!scanner.next().equals("WIDTH"))
                throw new IllegalArgumentException("WIDTH not specified in map file!");
            mapWidth = scanner.nextInt();
            System.out.printf("Map width: %d\n", mapWidth);
            if (!scanner.next().equals("HEIGHT"))
                throw new IllegalArgumentException("HEIGHT not specified in map file!");
            mapHeight = scanner.nextInt();
            System.out.printf("Map height: %d\n", mapHeight);

            newMap = new int[mapHeight][mapWidth];

            // Loop through each line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty())
                    continue;

                // Loop through each character in the line
                for (char character : line.toCharArray()) {
                    if (Character.isDigit(character)) {
                        newMap[firstIndex][secondIndex] = Character.getNumericValue(character);
                    } else {
                        throw new IllegalArgumentException("Map file contains invalid character: " + character);
                    }
                    secondIndex++;
                }
                firstIndex++;
                secondIndex = 0;
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println("Check " + filePath + " for errors.");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return newMap;
    }

    private void printArray() {
        for (int[] x : map) {
            for (int y : x) {
                System.out.print(y);
            }
            System.out.println();
        }
    }

    public synchronized void start() {
        map = getMapFromTextFile("map.txt");
        printArray();

        running = true;
        new Thread(this).start(); // Runs the game loop in a separate thread
    }

    public static void main(String[] args) {
        new Engine().start();
    }
}
