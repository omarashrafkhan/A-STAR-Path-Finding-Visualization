import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class PathPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private final PriorityQueue<Node> openSet;
    private final Set<Node> closedSet;
    private final Map<Node, Node> cameFrom;
    int size;
    ArrayList<Node> wall;
    Node startNode, endNode;
    char currentKey = (char) 0;
    JButton start, reset;
    List<Node> shortestPath;
    JButton stopContinue; // New button for stopping/continuing the process
    private boolean algorithmRunning = false;
    private boolean paused = false;


    public PathPanel() {
        size = 32;
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);
        start = new JButton("Start");
        reset = new JButton("Reset");
        stopContinue = new JButton("Stop");


        add(start);
        add(reset);
        add(stopContinue);

        stopContinue.addActionListener(e -> {
            togglePause(); // Toggle the paused state on button click

            if (paused) {
                stopContinue.setText("Continue");
            } else {
                stopContinue.setText("Stop");
            }
        });

        openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        closedSet = new HashSet<>();
        cameFrom = new HashMap<>();
        wall = new ArrayList<>();


        start.addActionListener(e -> {
            startAlgorithm();
        });

        reset.addActionListener(e -> {

            reset();

        });


    }

    public void reset() {
        startNode = null;
        endNode = null;
        wall.clear();
        openSet.clear();
        closedSet.clear();
        cameFrom.clear();
        shortestPath = null;

        paused = false;
        repaint();
        requestFocus();
    }


    private void togglePause() {
        paused = !paused;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.lightGray);
        for (int j = 0; j < this.getHeight(); j += size) {
            for (int i = 0; i < this.getWidth(); i += size) {
                g.drawRect(i, j, size, size);
            }
        }

        g.setColor(Color.black);
        for (Node value : wall) {
            g.fillRect(value.getX() + 1, value.getY() + 1,
                    size - 1, size - 1);
        }

        // Draw the start node in blue


        // Draw nodes in the openSet in green
        if (algorithmRunning) {
            g.setColor(Color.green);
            for (Node node : openSet) {
                g.fillRect(node.getX() + 1, node.getY() + 1, size - 1, size - 1);

            }

            // Draw nodes in the closedSet in yellow
            g.setColor(Color.yellow);
            for (Node node : closedSet) {
                g.fillRect(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
            }
        }


        // Draw the end node in red


        if (shortestPath != null) {
            g.setColor(Color.cyan);
            for (Node node : shortestPath) {
                g.fillOval(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
            }
        }
        if (endNode != null) {
            g.setColor(Color.red);
            g.fillRect(endNode.getX() + 1, endNode.getY() + 1, size - 1, size - 1);
        }
        if (startNode != null) {
            g.setColor(Color.blue);
            g.fillRect(startNode.getX() + 1, startNode.getY() + 1, size - 1, size - 1);
        }

        Set<Node> copyOfOpenSet = new HashSet<>(openSet);

        for (Node node : copyOfOpenSet) {
            drawCost(node, g); // Assuming you have a Graphics object
        }

        copyOfOpenSet = new HashSet<>(closedSet);
        for (Node node : copyOfOpenSet) {
            drawCost(node, g);
        }


    }


    public void drawCost(Node node, Graphics g) {
        g.setColor(Color.black);
        //set text size
        g.setFont(new Font("TimesRoman", Font.BOLD, 10));
        g.drawString(String.valueOf(node.getF()), node.getX() + 10, node.getY() + 20);

    }

    private void updateGUI() {
        repaint();
    }


    private void startAlgorithm() {
        SwingWorker<Void, Void> pathfindingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                algorithmRunning = true;
                try {
                    pathFind();
                    reconstructPath();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            }

            @Override
            protected void done() {
//                algorithmRunning = false;
//                start.setText("Reset");
                updateGUI();
            }
        };

        pathfindingWorker.execute();
    }

    private void reconstructPath() {


        shortestPath = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            shortestPath.add(current);

            current = cameFrom.get(current);

        }
        Collections.reverse(shortestPath);
    }


    public int dist(Node a, Node b) {
        return (int) Point.distance(a.getX(), a.getY(), b.getX(), b.getY());
    }


    public void pathFind() throws InterruptedException {
        if (endNode == null) {
            System.out.println("End node is not set.");
            return;
        }

        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node curr = openSet.poll();
            closedSet.add(curr);

            if (curr.equals(endNode)) {
                System.out.println("Path found");
//                cameFrom.put(endNode, curr);
                reconstructPath(); // Implement path reconstruction
                return;
            }

            List<Node> neighbors = findNodes(curr);

            for (Node neighbor : neighbors) {
                if (isInClosed(neighbor) || isInWall(neighbor) || isAtDiagonal(neighbor, curr) || !inBounds(neighbor)) {
                    continue;
                }


                int tentativeG = (int) Point.distance(curr.getX(), curr.getY(), neighbor.getX(), neighbor.getY());

                if (!openSet.contains(neighbor) || tentativeG < neighbor.getG()) {
                    neighbor.setG(tentativeG);
                    neighbor.setH(dist(neighbor, endNode));
                    neighbor.setF(Math.abs(neighbor.getG()) + Math.abs(neighbor.getH()));
                    cameFrom.put(neighbor, curr);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }

            repaint();

            while (paused) {
                Thread.sleep(100);
            }

            Thread.sleep(300); // Adjust the delay as needed
        }
    }


    public boolean inBounds(Node node) {
        return node.getX() + size <= Toolkit.getDefaultToolkit().getScreenSize().width && node.getX() >= 0 && node.getY() + size <= Toolkit.getDefaultToolkit().getScreenSize().height && node.getY() >= 0;
    }

    public boolean isAtDiagonal(Node node, Node curr) {
        if (isInWall(new Node(node.getX() - size, node.getY())) && isInWall(new Node(node.getX(), node.getY() + size))) {
            closedSet.add(node);
            return true;
        } else if (isInWall(new Node(node.getX() - size, node.getY())) && isInWall(new Node(node.getX(), node.getY() - size))) {
            closedSet.add(node);
            return true;
        } else if (!(node.getX()>curr.getX()&&node.getY()>curr.getY())&&isInWall(new Node(node.getX() + size, node.getY())) && isInWall(new Node(node.getX(), node.getY() + size))) {
            closedSet.add(node);
            return true;
        } else if (isInWall(new Node(node.getX() + size, node.getY())) && isInWall(new Node(node.getX(), node.getY() - size))) {
            closedSet.add(node);
            return true;
        }
        return false;
    }

    public boolean isInClosed(Node node) {
        for (Node n : closedSet) {
            if (n.compareTo(node) == 0) {
                return true;
            }
        }
        return false;
    }


    public boolean isInWall(Node node) {
        for (Node n : wall) {
            if (n.compareTo(node) == 0) {
                return true;
            }
        }
        return false;
    }

    public List<Node> findNodes(Node center) {
        List<Node> neighbors = new ArrayList<>();

        neighbors.add(new Node(center.getX() + size, center.getY()));
        neighbors.add(new Node(center.getX() - size, center.getY()));
        neighbors.add(new Node(center.getX(), center.getY() + size));
        neighbors.add(new Node(center.getX(), center.getY() - size));
        neighbors.add(new Node(center.getX() + size, center.getY() + size));
        neighbors.add(new Node(center.getX() - size, center.getY() - size));
        neighbors.add(new Node(center.getX() + size, center.getY() - size));
        neighbors.add(new Node(center.getX() - size, center.getY() + size));

        return neighbors;
    }

    public Node getNode(int x, int y) {
        for (Node node : openSet) {
            if (node.getX() == x && node.getY() == y) {
                return node;
            }
        }
        for (Node node : closedSet) {
            if (node.getX() == x && node.getY() == y) {
                return node;
            }
        }
        return new Node(x, y);
    }


    public void createWall(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && currentKey != 's' && currentKey != 'e') {
            int xBorder = e.getX() - (e.getX() % size);
            int yBorder = e.getY() - (e.getY() % size);

            Node newBorder = new Node(xBorder, yBorder);
            wall.add(newBorder);

            repaint();
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            createWall(e);
        }


        if (currentKey == 's') {

            int xRollover = e.getX() % size;
            int yRollover = e.getY() % size;

            if (startNode == null) {
                startNode = new Node(e.getX() - xRollover, e.getY() - yRollover);


            } else {
                startNode.setXY(e.getX() - xRollover, e.getY() - yRollover);
            }
            repaint();
        }
        // If 'e' is pressed create end node
        else if (currentKey == 'e') {
            int xRollover = e.getX() % size;
            int yRollover = e.getY() % size;

            if (endNode == null) {
                endNode = new Node(e.getX() - xRollover, e.getY() - yRollover);
            } else {
                endNode.setXY(e.getX() - xRollover, e.getY() - yRollover);
            }
            repaint();
        }


    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            createWall(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }


    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub


        currentKey = e.getKeyChar();

    }


    @Override
    public void keyReleased(KeyEvent e) {
        currentKey = (char) 0;
    }


}
