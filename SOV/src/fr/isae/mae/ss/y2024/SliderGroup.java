package fr.isae.mae.ss.y2024;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * The SliderGroup class represents a group of sliders for orbit parameters along with a delete button.
 * It provides controls for six orbital parameters and allows the user to modify them using sliders and a delete button.
 */
public class SliderGroup extends JPanel {
    private final CustomSlider sliderA;
    private final CustomSlider sliderE;
    private final CustomSlider sliderI;
    private final CustomSlider sliderOmega;
    private final CustomSlider sliderUpperOmega;
    private final CustomSlider sliderV;
    private final JButton deleteButton;

    
    /**
     * Constructs a SliderGroup with specified default values for each parameter.
     * @param groupName The name of the slider group.
     * @param defaultA Default value for semi-major axis.
     * @param defaultE Default value for eccentricity.
     * @param defaultI Default value for inclination.
     * @param defaultOmega Default value for argument of periapsis.
     * @param defaultUpperOmega Default value for longitude of ascending node.
     * @param defaultV Default value for true anomaly.
     */
    public SliderGroup(String groupName, double defaultA, double defaultE, double defaultI, double defaultOmega,
                       double defaultUpperOmega, double defaultV) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false); // Make the background transparent

        // Initialize sliders
        sliderA = new CustomSlider(1.0, 6.0, defaultA, 10, "a");
        sliderE = new CustomSlider(0.0, 1.0, defaultE, 100, "e");
        sliderI = new CustomSlider(0.0, 180.0, defaultI, 1, "i");
        sliderOmega = new CustomSlider(0.0, 360.0, defaultOmega, 1, "ω");
        sliderUpperOmega = new CustomSlider(0.0, 360.0, defaultUpperOmega, 1, "Ω");
        sliderV = new CustomSlider(0.0, 360.0, defaultV, 1, "v");

        // Add sliders and spacers to the group
        JLabel label = new JLabel(groupName);
        label.setForeground(Color.WHITE);
        this.add(label);
        
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(200, 50, 50)); // Red background for delete
        deleteButton.setForeground(Color.WHITE); // White text
        deleteButton.setFocusPainted(false);
        this.add(deleteButton);
        
        this.add(Box.createVerticalStrut(10));
        this.add(sliderA);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderE);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderI);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderOmega);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderUpperOmega);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderV);
        this.add(Box.createVerticalStrut(40));
    }

    /**
     * @return The current value of the semi-major axis slider.
     */
    public double getSliderAValue() {
        return sliderA.getValue();
    }

    /**
     * @return The current value of the eccentricity slider.
     */
    public double getSliderEValue() {
        return sliderE.getValue();
    }

    /**
     * @return The current value of the inclination slider.
     */
    public double getSliderIValue() {
        return sliderI.getValue();
    }

    /**
     * @return The current value of the argument of periapsis slider.
     */
    public double getSliderOmegaValue() {
        return sliderOmega.getValue();
    }

    /**
     * @return The current value of the longitude of ascending node slider.
     */
    public double getSliderUpperOmegaValue() {
        return sliderUpperOmega.getValue();
    }

    /**
     * @return The current value of the true anomaly slider.
     */
    public double getSliderVValue() {
        return sliderV.getValue();
    }

    /**
     * Adds a listener for the semi-major axis slider.
     * @param listener The listener to be added.
     */
    public void addSliderAListener(CustomSlider.SliderValueChangeListener listener) {
        sliderA.addSliderValueChangeListener(listener);
    }

    /**
     * Adds a listener for the eccentricity slider.
     * @param listener The listener to be added.
     */
    public void addSliderEListener(CustomSlider.SliderValueChangeListener listener) {
        sliderE.addSliderValueChangeListener(listener);
    }

    /**
     * Adds a listener for the inclination slider.
     * @param listener The listener to be added.
     */
    public void addSliderIListener(CustomSlider.SliderValueChangeListener listener) {
        sliderI.addSliderValueChangeListener(listener);
    }

    /**
     * Adds a listener for the argument of periapsis slider.
     * @param listener The listener to be added.
     */
    public void addSliderOmegaListener(CustomSlider.SliderValueChangeListener listener) {
        sliderOmega.addSliderValueChangeListener(listener);
    }

    /**
     * Adds a listener for the longitude of ascending node slider.
     * @param listener The listener to be added.
     */
    public void addSliderUpperOmegaListener(CustomSlider.SliderValueChangeListener listener) {
        sliderUpperOmega.addSliderValueChangeListener(listener);
    }

    /**
     * Adds a listener for the true anomaly slider.
     * @param listener The listener to be added.
     */
    public void addSliderVListener(CustomSlider.SliderValueChangeListener listener) {
        sliderV.addSliderValueChangeListener(listener);
    }

    /**
     * Adds a listener for the delete button.
     * @param listener The listener to be added.
     */
    public void addDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }
}
