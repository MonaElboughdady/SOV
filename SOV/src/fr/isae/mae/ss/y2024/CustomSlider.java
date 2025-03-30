package fr.isae.mae.ss.y2024;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * A custom slider component that displays a slider with a text field showing its
 * current value. The slider supports floating-point ranges by scaling integer
 * values.
 * 
 * This component is styled with a transparent background and includes a dynamic
 * text field that updates as the slider value changes.
 */
public class CustomSlider extends JPanel {
	
    /** The slider component for adjusting the value. */
    private final JSlider slider;

    /** The text field for displaying and manually entering the slider value. */
    private final JTextField textField;

    /** The scaling factor used to convert the slider's integer values to floating-point values. */
    private final double scale;

    /** The label describing the purpose of the slider. */
    private JLabel label;

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
		label = new JLabel(labelText);
		label.setForeground(Color.WHITE); // White text for the label
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// Create slider
		slider = new JSlider((int) (min * scale), (int) (max * scale), (int) (initialValue * scale));
		slider.setOpaque(false); // Transparent background for the slider
		slider.setUI(new CustomSliderUI(slider));
		slider.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Create text field for displaying and editing slider value
		textField = new JTextField(String.format("%.2f", initialValue), 10);
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		textField.setMaximumSize(textField.getPreferredSize());
		textField.setBackground(new Color(50, 50, 50)); // Dark background for text field
		textField.setForeground(Color.WHITE); // White text for text field

		// Update the text field dynamically when the slider changes
		slider.addChangeListener(e -> {
			double value = slider.getValue() / (double) scale;
			textField.setText(String.format("%.2f", value));
			// Here you can trigger any updates for orbits or other logic
		});

		// Update the slider dynamically when the text field value changes
		textField.addActionListener(e -> {
			try {
				double value = Double.parseDouble(textField.getText());
				slider.setValue((int) (value * scale));
			} catch (NumberFormatException ex) {
				textField.setText(String.format("%.2f", slider.getValue() / scale)); // Reset to current slider value on error
			}
		});

		// Add the text field and slider to the panel
		this.add(label);
		this.add(textField);
		this.add(slider);
	}
	
    /**
     * Returns the current value of the slider.
     * @return the current value as a double.
     */
    public double getValue() {
        return slider.getValue() / scale;
    }

    /**
     * Adds a listener that triggers when the slider value changes.
     * @param listener the listener to be triggered on value change.
     *                 The {@code onValueChanged} method of the listener will be called with the updated value 
     *                 whenever the slider value is adjusted.
     */
    public void addSliderValueChangeListener(SliderValueChangeListener listener) {
        slider.addChangeListener(e -> listener.onValueChanged(getValue()));
    }

    /**
     * Interface for listening to slider value changes.
     */
    public interface SliderValueChangeListener {
        void onValueChanged(double newValue);
    }

}
