import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    // These belong to the InputHandler now, and they are public
    private boolean forward = false;
    private boolean backward = false;
    private boolean left = false;
    private boolean right = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)
            forward = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
            backward = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
            left = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
            right = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)
            forward = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
            backward = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
            left = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
            right = false;
    }

    public boolean getForward() {
        return this.forward;
    }

    public boolean getBackward() {
        return this.backward;
    }

    public boolean getLeft() {
        return this.left;
    }

    public boolean getRight() {
        return this.right;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Required by interface
    }
}
