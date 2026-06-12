public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void rotate(double angle) {
        double oldX = this.x;
        this.x = oldX * Math.cos(angle) - this.y * Math.sin(angle);
        this.y = oldX * Math.sin(angle) + this.y * Math.cos(angle);
    }
}
