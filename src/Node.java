import java.awt.*;

public class Node implements Comparable<Node> {
    private int x, y, g, h, f;

    private Point point;
    private Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        point = new Point(x, y);
    }

    public static boolean isEqual(Node s, Node e) {
        if (s.getX() == e.getX() && s.getY() == e.getY()) {
            return true;
        }
        return false;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public Node getNode() {
        return parent;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

                /*
+         * Check if o is an instance of Complex or not
+         * "null instanceof [type]" also returns false
+         */
        if (!(o instanceof Node)) {
            return false;
        }
        // typecast o to Complex so that we can compare data members
        Node c = (Node) o;

        // Compare the data members and return accordingly
        return Integer.compare(x, c.x) == 0
                && Integer.compare(y, c.y) == 0;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public int compareTo(Node o) {
        if (this.getX() == o.getX()) {
            return Integer.compare(this.getY(), o.getY());
        }
        return Integer.compare(this.getX(), o.getX());
    }

}
