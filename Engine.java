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

    private BufferedImage frameBuffer;

    private List<List<Integer>> map = new ArrayList<List<Integer>>();

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

    private List<List<Integer>> getMapFromTextFile(String filePath) {
        File file = new File(filePath);

        map.add(new ArrayList<Integer>());
        int firstIndex = 0;
        int secondIndex = 0;
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("");
            while (scanner.hasNext()) {
                char character = scanner.next().charAt(0);

                if (Character.isDigit(character)) {
                    map.get(firstIndex).set(secondIndex, (int) character);
                } else if (character == '\n') {
                    secondIndex = 0;
                    firstIndex++;
                    scanner.nextLine();
                    map.add(new ArrayList<Integer>());
                } else {
                    throw new IllegalArgumentException("Map file contains invalid characters!");
                }

            }

        } catch (Exception e) {
            System.out.printf("Error: No file found at %s", filePath);
            e.printStackTrace();
        }

        return List.of(List.of(0));
    }

    public synchronized void start() {
        try {
            map = getMapFromTextFile("map.txt");
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
