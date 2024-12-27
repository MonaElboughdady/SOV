package fr.isae.mae.ss.y2024;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * A custom slider component that displays a slider with a label showing its
 * current value. The slider supports floating-point ranges by scaling integer
 * values.
 * 
 * This component is styled with a transparent background and includes a dynamic
 * label that updates as the slider value changes.
 */
public class CustomSlider extends JPanel {
	
    private final JSlider slider;
    private final JLabel label;
    private final double scale;

	/**
	 * Constructs a CustomSlider with the specified range, initial value, scale, and
	 * label text.
	 * 
	 * @param min          the minimum value of the slider (floating-point).
	 * @param max          the maximum value of the slider (floating-point).
	 * @param initialValue the initial value of the slider (floating-point).
	 * @param scale        the scaling factor to simulate floating-point values
	 *                     (e.g., 10 or 100).
	 * @param labelText    the text displayed next to the slider, describing its
	 *                     purpose.
	 */
	public CustomSlider(double min, double max, double initialValue, int scale, String labelText) {
		this.scale = scale;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setOpaque(false); // Transparent background

		// Label for the slider
		label = new JLabel(labelText + ": " + String.format("%.2f", initialValue));
		label.setForeground(Color.WHITE); // White text for the label
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Create slider
		slider = new JSlider((int) (min * scale), (int) (max * scale), (int) (initialValue * scale));
		slider.setOpaque(false); // Transparent background for the slider
		slider.setUI(new CustomSliderUI(slider));
		slider.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Update the label dynamically when the slider changes
		slider.addChangeListener(e -> {
			double value = slider.getValue() / (double) scale;
			label.setText(labelText + ": " + String.format("%.2f", value));
			// Here you can trigger any updates for orbits or other logic
		});

		// Add the label and slider to the panel
		this.add(label);
		this.add(slider);
	}
	
    // Get the slider value
    public double getValue() {
        return slider.getValue() / scale;
    }

    // Add listener for slider value changes
    public void addSliderValueChangeListener(SliderValueChangeListener listener) {
        slider.addChangeListener(e -> listener.onValueChanged(getValue()));
    }

    // Listener interface
    public interface SliderValueChangeListener {
        void onValueChanged(double newValue);
    }

}
