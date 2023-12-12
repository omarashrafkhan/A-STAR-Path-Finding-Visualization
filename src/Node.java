import java.awt.*;

public class Node implements Comparable<Node> {
    private int x, y, g, h, f;



    public Node(int x, int y) {
        this.x = x;
        this.y = y;
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



    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distance(Node n) {
        int dx = Math.abs(this.x - n.x);
        int dy = Math.abs(this.y - n.y);
        return (int) Math.sqrt(Math.pow(dx,2) + Math.pow(dy, 2));
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
