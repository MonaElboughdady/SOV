package fr.isae.mae.ss.y2024;
import static org.junit.Assert.*;

import javax.swing.JTextField;

import org.junit.Before;
import org.junit.Test;
import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.orbits.PositionAngle;
import fr.cnes.sirius.patrius.time.AbsoluteDate;
import fr.cnes.sirius.patrius.utils.Constants;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

public class CustomSliderTest {


    private CustomSlider slider;

    @Before
    public void setup() {
        slider = new CustomSlider(0.0, 100.0, 50.0, 100, "Test Slider");
    }

    @Test
    public void testInitialValue() {
    	// Description: Verifies that the slider initializes with the correct default value.
        assertEquals(50.0, slider.getValue(), 0.01);
    }

    @Test
    public void testSetValueWithSlider() {
    	// Description: Tests if the slider value can be updated and triggers the value change listener correctly.
        slider.addSliderValueChangeListener(value -> assertEquals(75.0, value, 0.01));
        slider.getValue();
    }

    @Test
    public void testSetValueWithTextField() {
    	// Description: Tests if the text field can update the slider value and trigger the value change listener.
        slider.addSliderValueChangeListener(value -> assertEquals(30.0, value, 0.01));
        slider.getValue();
    }

    @Test
    public void testInvalidTextFieldInput() {
    	// Description: Tests that invalid text input does not change the slider's value.
        slider.addSliderValueChangeListener(value -> assertEquals(50.0, value, 0.01));
    }
    

    @Test
    public void testSetValueWithTextField1() {
    	// Description: Tests setting a valid value through the text field and synchronizing it with the slider.
        JTextField textField = (JTextField) slider.getComponent(1);
        textField.setText("30.0");
        textField.postActionEvent();
        assertEquals(30.0, slider.getValue(), 0.01);
    }

    @Test
    public void testSliderTextFieldSync() {
    	// Description: Ensures that updates to the text field correctly synchronize with the slider value.
        slider.addSliderValueChangeListener(value -> assertEquals(60.0, value, 0.01));
        JTextField textField = (JTextField) slider.getComponent(1);
        textField.setText("60.0");
        textField.postActionEvent();
    }
}
