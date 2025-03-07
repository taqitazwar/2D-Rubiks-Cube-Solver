import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class RubiksCubeGUI extends JFrame {
    private static final int CELL_SIZE = 50;
    private static final int SPACING = 15; // Added spacing between panels
    private static final Color[] COLORS = {
        Color.WHITE,    // Up
        Color.YELLOW,   // Down
        Color.GREEN,    // Front
        Color.BLUE,     // Back
        Color.ORANGE,   // Left
        Color.RED       // Right
    };
    
    private static final String[] FACE_NAMES = {"Up", "Down", "Front", "Back", "Left", "Right"};
    private static final char[] COLOR_CHARS = {'W', 'Y', 'G', 'B', 'O', 'R'};
    
    private JPanel mainPanel;
    private JPanel[] facePanels;
    private CubeCell[][][] cubeCells;
    private JButton solveButton;
    private JButton resetButton;
    private JTextArea solutionArea;
    private JComboBox<String> solverChoice;
    private JButton applyMoveButton;
    private JComboBox<String> moveChoice;
    private JButton animateSolutionButton;
    private Timer animationTimer;
    private List<String> solutionMoves;
    private int currentMoveIndex;
    
    public RubiksCubeGUI() {
        super("Rubik's Cube Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750); 
        setLocationRelativeTo(null);
        
        initComponents();
        layoutComponents();
        initCube();
        
        setVisible(true);
    }
    
    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(SPACING, SPACING));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));
        
        // Initialize cube cells
        cubeCells = new CubeCell[6][3][3];
        facePanels = new JPanel[6];
        
        for (int face = 0; face < 6; face++) {
            facePanels[face] = new JPanel();
            facePanels[face].setLayout(new GridLayout(3, 3, 2, 2)); // Added cell spacing
            facePanels[face].setBorder(BorderFactory.createTitledBorder(FACE_NAMES[face]));
            
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cubeCells[face][i][j] = new CubeCell(face);
                    facePanels[face].add(cubeCells[face][i][j]);
                }
            }
        }
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(SPACING, SPACING));
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, SPACING, SPACING));
        solveButton = new JButton("Solve Cube");
        resetButton = new JButton("Reset Cube");
        solverChoice = new JComboBox<>(new String[]{"Brute Force", "Optimal Solution (IDA*)"});
        
        // Make buttons bigger
        solveButton.setFont(solveButton.getFont().deriveFont(Font.BOLD, 14f));
        resetButton.setFont(resetButton.getFont().deriveFont(Font.BOLD, 14f));
        
        buttonsPanel.add(solverChoice);
        buttonsPanel.add(solveButton);
        buttonsPanel.add(resetButton);
        
        JPanel movePanel = new JPanel();
        movePanel.setLayout(new FlowLayout(FlowLayout.CENTER, SPACING, SPACING));
        moveChoice = new JComboBox<>(RubiksCube.MOVES);
        applyMoveButton = new JButton("Apply Move");
        animateSolutionButton = new JButton("Animate Solution");
        animateSolutionButton.setEnabled(false);
        
        // Make move buttons bigger
        applyMoveButton.setFont(applyMoveButton.getFont().deriveFont(Font.BOLD, 14f));
        animateSolutionButton.setFont(animateSolutionButton.getFont().deriveFont(Font.BOLD, 14f));
        
        movePanel.add(new JLabel("Move: "));
        movePanel.add(moveChoice);
        movePanel.add(applyMoveButton);
        movePanel.add(animateSolutionButton);
        
        // Solution area
        solutionArea = new JTextArea(5, 40);
        solutionArea.setEditable(false);
        solutionArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Larger font
        JScrollPane scrollPane = new JScrollPane(solutionArea);
        
        controlPanel.add(buttonsPanel, BorderLayout.NORTH);
        controlPanel.add(movePanel, BorderLayout.CENTER);
        controlPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Add action listeners
        solveButton.addActionListener(e -> solveCube());
        resetButton.addActionListener(e -> resetCube());
        applyMoveButton.addActionListener(e -> applyMove());
        animateSolutionButton.addActionListener(e -> animateSolution());
        
        // Initialize animation timer
        animationTimer = new Timer(500, e -> {
            if (currentMoveIndex < solutionMoves.size()) {
                String move = solutionMoves.get(currentMoveIndex++);
                applyMoveToGUI(move);
                solutionArea.setText("Animating: " + move + " (" + currentMoveIndex + "/" + solutionMoves.size() + ")");
            } else {
                animationTimer.stop();
                solutionArea.setText("Animation complete!\n" + String.join(" ", solutionMoves));
                animateSolutionButton.setEnabled(true);
            }
        });
    }
    
    private void layoutComponents() {
        JPanel cubePanel = new JPanel();
        cubePanel.setLayout(new GridBagLayout());
        cubePanel.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING, SPACING, SPACING, SPACING); // Add padding
        
        // Create 3x4 grid for the cube faces
        // Back face
        gbc.gridx = 1;
        gbc.gridy = 0;
        cubePanel.add(facePanels[3], gbc);
        
        // Left, Up, Right, Down faces in middle row
        gbc.gridy = 1;
        gbc.gridx = 0;
        cubePanel.add(facePanels[4], gbc);
        
        gbc.gridx = 1;
        cubePanel.add(facePanels[0], gbc);
        
        gbc.gridx = 2;
        cubePanel.add(facePanels[5], gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        cubePanel.add(facePanels[1], gbc);
        
        // Front face
        gbc.gridx = 1;
        gbc.gridy = 3;
        cubePanel.add(facePanels[2], gbc);
        
        mainPanel.add(cubePanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(SPACING, SPACING));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));
        
        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING, SPACING));
        topControls.add(solverChoice);
        topControls.add(solveButton);
        topControls.add(resetButton);
        
        JPanel moveControls = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING, SPACING));
        moveControls.add(new JLabel("Move: "));
        moveControls.add(moveChoice);
        moveControls.add(applyMoveButton);
        moveControls.add(animateSolutionButton);
        
        controlPanel.add(topControls, BorderLayout.NORTH);
        controlPanel.add(moveControls, BorderLayout.CENTER);
        controlPanel.add(new JScrollPane(solutionArea), BorderLayout.SOUTH);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void initCube() {
        // Initialize a solved cube
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cubeCells[face][i][j].setColor(face);
                }
            }
        }
    }
    
    private void resetCube() {
        initCube();
        solutionArea.setText("");
        animateSolutionButton.setEnabled(false);
    }
    
    private void applyMove() {
        String move = (String) moveChoice.getSelectedItem();
        applyMoveToGUI(move);
    }
    
    private void applyMoveToGUI(String move) {
        RubiksCube cube = getCurrentCubeState();
        cube.applyMove(move);
        updateGUIFromCube(cube);
    }
    
    private RubiksCube getCurrentCubeState() {
        char[][][] state = new char[6][3][3];
        
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    state[face][i][j] = COLOR_CHARS[cubeCells[face][i][j].getColorIndex()];
                }
            }
        }
        
        return new RubiksCube(state);
    }
    
    private void updateGUIFromCube(RubiksCube cube) {
        char[][][] state = cube.getState();
        
        for (int face = 0; face < 6; face++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    char c = state[face][i][j];
                    int colorIndex = 0;
                    
                    for (int k = 0; k < COLOR_CHARS.length; k++) {
                        if (COLOR_CHARS[k] == c) {
                            colorIndex = k;
                            break;
                        }
                    }
                    
                    cubeCells[face][i][j].setColor(colorIndex);
                }
            }
        }
    }
    
    private void solveCube() {
        RubiksCube cube = getCurrentCubeState();
        
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            solveButton.setEnabled(false);
            solutionArea.setText("Solving... This may take a while for complex cube states.");
            
            // Run solver in background thread
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    if (solverChoice.getSelectedIndex() == 0) {
                        BruteForceSolver solver = new BruteForceSolver();
                        return solver.solve(cube);
                    } else {
                        OptimalSolver solver = new OptimalSolver();
                        return solver.solve(cube);
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        String solution = get();
                        solutionArea.setText("Solution: " + solution);
                        
                        if (!solution.contains("No solution") && !solution.contains("already solved")) {
                            solutionMoves = new ArrayList<>();
                            for (String move : solution.split(" ")) {
                                solutionMoves.add(move);
                            }
                            animateSolutionButton.setEnabled(true);
                        } else {
                            animateSolutionButton.setEnabled(false);
                        }
                    } catch (Exception e) {
                        solutionArea.setText("Error: " + e.getMessage());
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        solveButton.setEnabled(true);
                    }
                }
            };
            
            worker.execute();
        } catch (Exception e) {
            solutionArea.setText("Error: " + e.getMessage());
            setCursor(Cursor.getDefaultCursor());
            solveButton.setEnabled(true);
        }
    }
    
    private void animateSolution() {
        if (solutionMoves == null || solutionMoves.isEmpty()) {
            return;
        }
        
        // Reset to initial state
        updateGUIFromCube(getCurrentCubeState());
        
        currentMoveIndex = 0;
        animationTimer.start();
        animateSolutionButton.setEnabled(false);
    }
    
    // Inner class for a single cube cell
    private class CubeCell extends JPanel {
        private int colorIndex;
        
        public CubeCell(int initialColorIndex) {
            setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Thicker border
            colorIndex = initialColorIndex;
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Cycle through colors on click
                    colorIndex = (colorIndex + 1) % COLORS.length;
                    repaint();
                }
            });
        }
        
        public void setColor(int index) {
            colorIndex = index;
            repaint();
        }
        
        public int getColorIndex() {
            return colorIndex;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g.setColor(COLORS[colorIndex]);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Add a slight 3D effect
            g.setColor(COLORS[colorIndex].brighter());
            g.fillRect(0, 0, getWidth() - 3, 3);
            g.fillRect(0, 0, 3, getHeight() - 3);
            
            g.setColor(COLORS[colorIndex].darker());
            g.fillRect(getWidth() - 3, 0, 3, getHeight());
            g.fillRect(0, getHeight() - 3, getWidth(), 3);
        }
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new RubiksCubeGUI());
    }
}