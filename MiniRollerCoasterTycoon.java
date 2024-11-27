import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class MiniRollerCoasterTycoon extends JPanel implements MouseListener, ActionListener {
    private final int gridSize = 20; // Grid size
    private final int cellSize = 30; // Cell size (in pixels)
    private String[][] park = new String[gridSize][gridSize]; // Game state
    private ArrayList<Guest> guests = new ArrayList<>(); // Guests
    private String selectedTool = "path"; // Current tool
    private Timer timer;

    public MiniRollerCoasterTycoon() {
        this.setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        this.addMouseListener(this);

        // Initialize game state
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                park[y][x] = null;
            }
        }

        // Add some initial paths for testing
        for (int i = 5; i < 15; i++) {
            park[10][i] = "path";
        }

        // Add guests
        spawnGuest();
        spawnGuest();

        // Timer for game loop
        timer = new Timer(100, this); // Update every 100ms
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);

                if ("path".equals(park[y][x])) {
                    g.setColor(Color.GRAY);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else if ("ride".equals(park[y][x])) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else if ("ferris".equals(park[y][x])) {
                    g.setColor(Color.GREEN); // Green for Ferris Wheel
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }

        // Draw guests
        g.setColor(Color.RED);
        for (Guest guest : guests) {
            g.fillOval(guest.x * cellSize + cellSize / 4, guest.y * cellSize + cellSize / 4,
                    cellSize / 2, cellSize / 2);
        }
    }

    // Handle mouse clicks
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / cellSize;
        int y = e.getY() / cellSize;

        if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
            if ("clear".equals(selectedTool)) {
                park[y][x] = null;
            } else {
                park[y][x] = selectedTool;
            }
            repaint();
        }
    }

    // Spawn guests
    private void spawnGuest() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(gridSize);
            y = random.nextInt(gridSize);
        } while (!"path".equals(park[y][x])); // Spawn on a path
        guests.add(new Guest(x, y));
    }

    // Move guests
    private void moveGuests() {
        Random random = new Random();
        for (Guest guest : guests) {
            int[][] directions = {
                    {0, -1}, {0, 1}, {-1, 0}, {1, 0}
            };
            int[] direction = directions[random.nextInt(directions.length)];

            int newX = guest.x + direction[0];
            int newY = guest.y + direction[1];

            if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize &&
                    "path".equals(park[newY][newX])) {
                guest.x = newX;
                guest.y = newY;
            }
        }
    }

    // Game loop (triggered by Timer)
    @Override
    public void actionPerformed(ActionEvent e) {
        moveGuests();
        repaint();
    }

    // Tool selection
    public void setTool(String tool) {
        selectedTool = tool;
    }

    // Clear all tiles
    public void clearAllTiles() {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                park[y][x] = null;
            }
        }
        repaint(); // Repaint the grid after clearing
    }

    // Empty implementations for other MouseListener methods
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // Guest class
    private static class Guest {
        int x, y;

        Guest(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Main method to run the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Mini RollerCoaster Tycoon");
        MiniRollerCoasterTycoon game = new MiniRollerCoasterTycoon();

        JPanel buttons = new JPanel();
        JButton pathButton = new JButton("Build Path");
        JButton rideButton = new JButton("Build Ride");
        JButton ferrisButton = new JButton("Build Ferris Wheel");
        JButton clearButton = new JButton("Clear Tile");
        JButton clearAllButton = new JButton("Clear All Tiles");

        pathButton.addActionListener(e -> game.setTool("path"));
        rideButton.addActionListener(e -> game.setTool("ride"));
        ferrisButton.addActionListener(e -> game.setTool("ferris"));
        clearButton.addActionListener(e -> game.setTool("clear"));
        clearAllButton.addActionListener(e -> game.clearAllTiles()); // Clear all tiles

        buttons.add(pathButton);
        buttons.add(rideButton);
        buttons.add(ferrisButton); // Add Ferris Wheel button
        buttons.add(clearButton);
        buttons.add(clearAllButton); // Add Clear All Tiles button

        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
