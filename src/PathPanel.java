import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class PathPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private static final char START_NODE_KEY = 's';
    private static final char END_NODE_KEY = 'e';
    private static final Color GRID_COLOR = Color.lightGray;
    private static final Color WALL_COLOR = Color.black;
    private static final Color OPEN_SET_COLOR = Color.green;
    private static final Color CLOSED_SET_COLOR = Color.yellow;
    private static final Color PATH_COLOR = Color.cyan;
    private static final Color END_NODE_COLOR = Color.red;
    private static final Color START_NODE_COLOR = Color.blue;
    private static final Color COST_COLOR = Color.black;
    private final PriorityQueue<Node> openSet;
    private final Set<Node> closedSet;
    private final Map<Node, Node> cameFrom;
    int size; //size of the squares being printed
    ArrayList<Node> wall; //wall for obstacles
    Node startNode, endNode;
    char currentKey = (char) 0; //used to define start and end nodes
    JButton start, reset;
    List<Node> shortestPath; //final shortest path being drawn
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

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //grid being drawn
        drawGrid(g);

        //wall being drawn
        drawWalls(g);


        drawNodes(g);

        Set<Node> copyOfOpenSet = new HashSet<>(openSet);

        for (Node node : copyOfOpenSet) {
            drawNodeDetails(node, g); // Assuming you have a Graphics object
        }

        copyOfOpenSet = new HashSet<>(closedSet);
        for (Node node : copyOfOpenSet) {
            drawNodeDetails(node, g);
        }


    }


    private void drawGrid(Graphics g) {
        g.setColor(GRID_COLOR);
        for (int j = 0; j < this.getHeight(); j += size) {
            for (int i = 0; i < this.getWidth(); i += size) {
                g.drawRect(i, j, size, size);
            }
        }
    }

    private void drawWalls(Graphics g) {
        g.setColor(WALL_COLOR);
        for (Node value : wall) {
            g.fillRect(value.getX() + 1, value.getY() + 1, size - 1, size - 1);
        }
    }

    private void drawNodes(Graphics g) {
        if (algorithmRunning) {
            drawOpenSet(g);
            drawClosedSet(g);
        }
        drawShortestPath(g);
        drawEndNode(g);
        drawStartNode(g);
    }

    private void drawOpenSet(Graphics g) {
        g.setColor(OPEN_SET_COLOR);
        for (Node node : openSet) {
            g.fillRect(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
        }
    }

    private void drawClosedSet(Graphics g) {
        g.setColor(CLOSED_SET_COLOR);
        for (Node node : closedSet) {
            g.fillRect(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
        }
    }

    private void drawShortestPath(Graphics g) {
        if (shortestPath != null) {
            g.setColor(PATH_COLOR);
            for (Node node : shortestPath) {
                g.fillOval(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
            }
        }
    }

    private void drawEndNode(Graphics g) {
        if (endNode != null) {
            g.setColor(END_NODE_COLOR);
            g.fillRect(endNode.getX() + 1, endNode.getY() + 1, size - 1, size - 1);
        }
    }

    private void drawStartNode(Graphics g) {
        if (startNode != null) {
            g.setColor(START_NODE_COLOR);
            g.fillRect(startNode.getX() + 1, startNode.getY() + 1, size - 1, size - 1);
        }
    }

    private void drawNodeDetails(Node node, Graphics g) {
        g.setColor(COST_COLOR);
        g.setFont(new Font("TimesRoman", Font.BOLD, 10));
        g.drawString(String.valueOf(node.getF()), node.getX() + 10, node.getY() + 25);
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
                reconstructPath(); // Implement path reconstruction
                return;
            }

            List<Node> neighbors = findNodes(curr);

            for (Node neighbor : neighbors) {
                if (isInClosed(neighbor) || isInWall(neighbor) || isAtDiagonal(neighbor, curr) || !inBounds(neighbor)) {
                    continue;
                }


                int GxMoveCost = neighbor.getX() - curr.getX();
                int GyMoveCost = neighbor.getY() - curr.getY();
                int gCost = curr.getG();

                if (GxMoveCost != 0 && GyMoveCost != 0) {
                    gCost += (int) (Math.sqrt(2 * (Math.pow(size, 2))));
                } else {
                    gCost += size;
                }

                if (!openSet.contains(neighbor) || gCost < neighbor.getG()) {
                    neighbor.setG(gCost);

                    int HxDiff = Math.abs(endNode.getX() - neighbor.getX());
                    int HyDiff = Math.abs(endNode.getY() - neighbor.getY());
                    int hCost = HxDiff + HyDiff;

                    neighbor.setH(hCost);
                    neighbor.setF(neighbor.getG() + neighbor.getH());  // Update F cost based on G and H costs
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

            Thread.sleep(0); // Adjust the delay as needed for animation
        }


        System.out.println("No path");
    }


    public boolean inBounds(Node node) {
        return node.getX() + size <= getWidth() && node.getX() >= 0 && node.getY() + size <= getHeight() && node.getY() >= 0;
    }

    public boolean isAtDiagonal(Node node, Node curr) {
        if (isInWall(new Node(node.getX() - size, node.getY())) && isInWall(new Node(node.getX(), node.getY() + size))) {
            closedSet.add(node);
            return true;
        } else if (isInWall(new Node(node.getX() - size, node.getY())) && isInWall(new Node(node.getX(), node.getY() - size))) {
            closedSet.add(node);
            return true;
        } else if (!(node.getX() > curr.getX() && node.getY() > curr.getY()) && isInWall(new Node(node.getX() + size, node.getY())) && isInWall(new Node(node.getX(), node.getY() + size))) {
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


    private void createWall(MouseEvent e) {
        int xBorder = e.getX() - (e.getX() % size);
        int yBorder = e.getY() - (e.getY() % size);
        Node newBorder = new Node(xBorder, yBorder);

        if (SwingUtilities.isLeftMouseButton(e) && currentKey != START_NODE_KEY && currentKey != END_NODE_KEY) {
            wall.add(newBorder);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            wall.remove(newBorder);
        }

        repaint();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            createWall(e);


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
        } else if (SwingUtilities.isRightMouseButton(e)) {
            int mouseBoxX = e.getX() - (e.getX() % size);
            int mouseBoxY = e.getY() - (e.getY() % size);

            // If 's' is pressed remove start node
            if (currentKey == 's') {
                if (startNode != null && mouseBoxX == startNode.getX() && startNode.getY() == mouseBoxY) {
                    startNode = null;
                    repaint();
                }
            }
            // If 'e' is pressed remove end node
            else if (currentKey == 'e') {
                if (endNode != null && mouseBoxX == endNode.getX() && endNode.getY() == mouseBoxY) {
                    endNode = null;
                    repaint();
                }
            }
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
        createWall(e);

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
