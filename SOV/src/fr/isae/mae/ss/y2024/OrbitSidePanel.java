package fr.isae.mae.ss.y2024;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;


/**
 * The OrbitSidePanel class provides a graphical panel containing orbit control components.
 * It includes buttons for quick access to pre-defined orbits (ISS, Nilesat) and custom orbit groups
 * that can be dynamically added and removed.
 */
public class OrbitSidePanel extends JPanel {
    private final List<SliderGroup> sliderGroups;
    private final JPanel groupContainer;
    private final JToggleButton nilesatButton;
    private final JToggleButton issButton;
    private final JButton addGroupButton;
    private int groupId = 0;

    
    /**
     * Constructs an OrbitSidePanel with UI components for orbit control.
     * The panel contains pre-defined buttons for ISS and Nilesat orbits and a section for adding custom orbit groups.
     */
    public OrbitSidePanel() {
        // Configure panel layout and appearance
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(50, 50, 50, 200)); // Semi-transparent dark gray
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding

        sliderGroups = new ArrayList<>();

        // Quick Add Section
        JPanel quickAddPanel = new JPanel();
        quickAddPanel.setLayout(new BoxLayout(quickAddPanel, BoxLayout.Y_AXIS));
        quickAddPanel.setOpaque(false);
        quickAddPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Quick Add", TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));

        nilesatButton = createStyledButton("NILESAT");
        issButton = createStyledButton("ISS");

        quickAddPanel.add(nilesatButton);
        quickAddPanel.add(Box.createVerticalStrut(10));
        quickAddPanel.add(issButton);

        this.add(quickAddPanel);
        this.add(Box.createVerticalStrut(20));

        // Custom Orbit Section
        JPanel customOrbitPanel = new JPanel();
        customOrbitPanel.setLayout(new BoxLayout(customOrbitPanel, BoxLayout.Y_AXIS));
        customOrbitPanel.setOpaque(false);
        customOrbitPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Custom Orbit", TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));

        groupContainer = new JPanel();
        groupContainer.setLayout(new BoxLayout(groupContainer, BoxLayout.Y_AXIS));
        groupContainer.setOpaque(false);

        // Add scroll pane for slider groups
        JScrollPane scrollPane = new JScrollPane(groupContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(300, 400)); // Adjust size as needed

        // Add button to add new slider groups
        addGroupButton = new JButton("Add Orbit Group");
        addGroupButton.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent dark gray
        addGroupButton.setForeground(Color.BLACK);

        customOrbitPanel.add(addGroupButton);
        customOrbitPanel.add(Box.createVerticalStrut(20));
        customOrbitPanel.add(scrollPane);

        this.add(customOrbitPanel);
    }

    
    /**
     * Customizes the panel's appearance with anti-aliased rounded corners.
     * @param g The graphics object for rendering.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded corners with radius 20
    }

    
    /**
     * Adds a new group of sliders to the panel and returns the group's identifier.
     * @return The unique identifier of the newly added slider group.
     */
    public int addNewGroup() {
        groupId += 1;
        SliderGroup newGroup = new SliderGroup("Group " + groupId, 2.0, 0.01, 0.2, 90.0, 0.0, 0.0);
        sliderGroups.add(newGroup);
        groupContainer.add(newGroup);
        groupContainer.revalidate();
        groupContainer.repaint();
        return groupId;
    }

    /**
     * Sets the listener for the Nilesat button, specifying actions for toggle on and off.
     * @param toggleAction The action to perform when the button is toggled on.
     * @param untoggleAction The action to perform when the button is toggled off.
     */
    public void setNilesatButtonListener(Runnable toggleAction, Runnable untoggleAction) {
        nilesatButton.addActionListener(e -> {
            if (nilesatButton.isSelected()) {
                toggleAction.run();
            } else {
                untoggleAction.run();
            }
        });
    }

    /**
     * Sets the listener for the ISS button, specifying actions for toggle on and off.
     * @param toggleAction The action to perform when the button is toggled on.
     * @param untoggleAction The action to perform when the button is toggled off.
     */
    public void setIssButtonListener(Runnable toggleAction, Runnable untoggleAction) {
        issButton.addActionListener(e -> {
            if (issButton.isSelected()) {
                toggleAction.run();
            } else {
                untoggleAction.run();
            }
        });
    }
    
    private JToggleButton createStyledButton(String text) {
        JToggleButton button = new JToggleButton(text);
        button.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent dark gray
        button.setForeground(Color.BLACK);
        return button;
    }

    /**
     * @return The list of slider groups currently added to the panel.
     */
    public List<SliderGroup> getSliderGroups() {
        return sliderGroups;
    }

    /**
     * @return The button used to add new orbit groups.
     */
    public JButton getAddGroupButton() {
        return addGroupButton;
    }
    
    /**
     * Removes a specified slider group from the panel.
     * @param group The slider group to remove.
     */
    public void removeGroup(SliderGroup group) {
    	sliderGroups.remove(group);
        groupContainer.remove(group);
        groupContainer.revalidate();
        groupContainer.repaint();
    }
}
