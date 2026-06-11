import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;

public class Engine extends Canvas implements Runnable {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private boolean running = false;
    private int mapWidth;
    private int mapHeight;

    private BufferedImage frameBuffer;

    private int[][] map;

    public Engine() {
        // Setup the Window
        frameBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        JFrame frame = new JFrame("Java Raycaster");
        frame.setSize(WIDTH, HEIGHT);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Main loop
    public void run() {
        // Setup Double Buffering (eliminates screen tearing/flickering)
        createBufferStrategy(2);
        BufferStrategy bs = getBufferStrategy();

        while (running) {
            // Player logic
            updatePlayerState();

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

    private void updatePlayerState() {
        // Read keyboard input and update playerX, playerY, and playerAngle
    }

    private void renderToBuffer() {
        // Clear screen (draw floor and ceiling)
        Graphics bufferGraphics = frameBuffer.getGraphics();
        bufferGraphics.setColor(Color.DARK_GRAY); // Ceiling
        bufferGraphics.fillRect(0, 0, WIDTH, HEIGHT / 2);
        bufferGraphics.setColor(Color.GRAY); // Floor
        bufferGraphics.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

        // Raycasting logic
        // For each vertical stripe (x) on the screen:
        // 1. Cast a ray
        // 2. Find distance to wall
        // 3. Calculate vertical wall height
        // 4. Draw the vertical wall line using bufferGraphics.drawLine(...)
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
        } catch (Exception e) {
            System.out.printf("Error: No file found at %s", filePath);
            e.printStackTrace();
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
        try {
            map = getMapFromTextFile("map.txt");
            printArray();
        } catch (IllegalArgumentException e) {
            System.err.printf(e.getMessage());
        }

        running = true;
        new Thread(this).start(); // Runs the game loop in a separate thread
    }

    public static void main(String[] args) {
        new Engine().start();
    }
}
