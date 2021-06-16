package life;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

// Callback interface is to get the implementation
// of updating the Generation and Alive Labels in GUI
// after finding out next generation.
interface Callback {
    void action(int alive, int generation);
}

class MatrixPanel extends JPanel {

    // BufferedImage manipulates the image data
    public BufferedImage image;
    public GameOfLifeSetup gol;
    private Callback onUpdate;
    public int generation;
    // Timer is a utility class, in which we can specify the future
    // task to be executed
    public Timer timer;
    public int cellSize;

    public MatrixPanel() {
        this.gol = new GameOfLifeSetup();
        cellSize = gol.getCellSize();
        image = new BufferedImage(cellSize * gol.getLength(), cellSize * gol.getLength(), BufferedImage.TYPE_INT_RGB);
        timer = new Timer(0, ae -> createImage(image));
        timer.setDelay(500);
        timer.start();
        this.generation = 0;
    }

    public void setOnUpdateAction(Callback action) {
        this.onUpdate = action;
    }

    public void createImage(BufferedImage image) {
        // Graphics allow us to draw onto the components.
        Graphics g = image.getGraphics();
        char[][] arr = this.gol.getGrid();

        // Variables to traverse in the "arr" grid.
        int r = 0;
        int c = 0;

        onUpdate.action(this.gol.getAlive(), generation);
        this.generation += 1;

        int sqW = cellSize,
            sqH = cellSize;
        for (int i = 0; i < cellSize * gol.getLength(); i += sqW) {
            if (r == gol.getLength()) {
                r = 0;
            }
            for (int j = 0; j < cellSize * gol.getLength(); j += sqH) {
                if (c == gol.getLength()) {
                    c = 0;
                }
                if (arr[r][c] == 'O') {
                    g.setColor(new Color(116, 255, 0));
                } else {
                    g.setColor(Color.GRAY);
                }
                g.fillRect(i, j, sqW, sqH);
                g.setColor(Color.BLACK);
                g.drawRect(i, j, sqW, sqH);
                c += 1;
            }
            r += 1;
        }
        // Sending the old grid to set the next generation,
        // in the grid.
        this.gol.generateGenerations(arr);

        repaint();
    }


    // This method is overriden to add custom graphics and to render it.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // x and y coordinates are set to 0.
        // Image observer not required so it can be set to null in drawImage.
        g.drawImage(image, 0, 0, null);
    }
}

public class GameOfLife extends JFrame {
    private final JPanel leftPanel;
    private final JPanel rightPanel;
    private JLabel aliveLabel;
    private JLabel generationLabel;
    private final JButton restartButton;
    private final JToggleButton pauseButton;
    private final JSlider slider;

    public GameOfLife() {
        setTitle("Conway's Game Of Life");

        // Setting up ToolTip delay time
        ToolTipManager.sharedInstance().setDismissDelay(1500); 
        ToolTipManager.sharedInstance().setInitialDelay(100);

        // Panels
        leftPanel = new JPanel();
        rightPanel = new JPanel();

        // Buttons
        Icon restartIcon = new ImageIcon(".\\resources\\icons8-restart-30.png");
        restartButton = new JButton(restartIcon);
        restartButton.setName("ResetButton");
        restartButton.setToolTipText("Restart");

        Icon pauseIcon = new ImageIcon(".\\resources\\icons8-resume-button-30.png");
        pauseButton = new JToggleButton(pauseIcon);
        pauseButton.setName("PlayToggleButton");
        pauseButton.setToolTipText("Pause");

        // Slider
        slider = new JSlider(0, 2000, 500);
        slider.setMaximumSize(new Dimension(400, 50));
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(500);
        slider.setToolTipText("Change Speed of Animation");

        // Setting up Left Side of Frame
        // leftPanel set with Box Layout
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.setBackground(new Color(197, 203, 227));

        // createRigidArea creates a small vertical space between each component,
        // to get a clear spacing.
        leftPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        leftPanel.add(pauseButton);
        leftPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        leftPanel.add(restartButton);
        leftPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        leftPanel.add(slider);
        
        updateLabel(0);

        // Creating matrixPanel object, to add it in rightPanel
        MatrixPanel matrixPanel = new MatrixPanel();
        matrixPanel.setPreferredSize(new Dimension(matrixPanel.gol.getCellSize() * matrixPanel.gol.getLength(), matrixPanel.gol.getCellSize() * matrixPanel.gol.getLength()));

        // Setting up anonymous method using lambda because
        // there is only one method to handle in Interface.
        matrixPanel.setOnUpdateAction((alive, generation) -> {
            generationLabel.setText("Generation #" + generation);
            aliveLabel.setText("Alive: " + alive);
        });

        pauseButton.addActionListener(e -> {
            if (pauseButton.isSelected()) {
                matrixPanel.timer.stop();
                pauseButton.setToolTipText("Resume");
            } else {
                matrixPanel.timer.start();
                pauseButton.setToolTipText("Pause");
            }
        });

        restartButton.addActionListener(e -> {
            // If pause button is paused then, reset it.
            pauseButton.setSelected(false);
            pauseButton.setToolTipText("Pause");
            matrixPanel.timer.stop();
            matrixPanel.image = new BufferedImage(matrixPanel.gol.getCellSize() * matrixPanel.gol.getLength(), matrixPanel.gol.getCellSize() * matrixPanel.gol.getLength(), BufferedImage.TYPE_INT_RGB);
            // Resetting the generation to 0
            matrixPanel.generation = 0;
            // And again define the GameOfLifeSetup 
            matrixPanel.gol = new GameOfLifeSetup();
            matrixPanel.timer.start();
        });

        // To get the slider values, if changed applying it as Timer value.
        slider.addChangeListener(e -> {
            JSlider temp = (JSlider) e.getSource();
            matrixPanel.timer.setDelay(temp.getValue());
        });

        // Adding the matrixPanel to rightPanel. 
        // As matrixPanel is extended to JPanel, we can directly add it as component.
        rightPanel.add(matrixPanel);

        // Dividing the frame into 1 row and 2 columns
        setLayout(new GridLayout(1, 2));

        // Adding the panels into frames
        add(leftPanel);
        add(rightPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // It centers the frame location with respect to window
        // setLocationRelativeTo(null);

        // Pack method here sizes the frame and all its component
        // to at or above their preferred sizes.
        pack();
    }

    // Initialises the Label and add it to Panel.
    public void updateLabel(int number) {
        /* Generation Label */
        generationLabel = new JLabel();
        generationLabel.setName("GenerationLabel");
        generationLabel.setText("Generation #" + number);
        generationLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        generationLabel.setBounds(10, 5, 300, 20);

        /* Alive Label */
        aliveLabel = new JLabel();
        aliveLabel.setName("AliveLabel");
        aliveLabel.setText("Alive: " + number);
        aliveLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        aliveLabel.setBounds(10, 25, 300, 20);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(generationLabel);
        leftPanel.add(aliveLabel);
    }
}