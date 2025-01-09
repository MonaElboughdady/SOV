package fr.isae.mae.ss.y2024;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import javax.swing.*;

public class SliderGroupTest {

    private SliderGroup sliderGroup;

    @Before
    public void setUp() {
        sliderGroup = new SliderGroup("Test Group", 2.0, 0.01, 50.0, 90.0, 0.0, 0.0);
    }

    @Test
    public void testInitialValues() {
        assertEquals(2.0, sliderGroup.getSliderAValue(), 0.01);
        assertEquals(0.01, sliderGroup.getSliderEValue(), 0.01);
        assertEquals(50.0, sliderGroup.getSliderIValue(), 0.01);
        assertEquals(90.0, sliderGroup.getSliderOmegaValue(), 0.01);
        assertEquals(0.0, sliderGroup.getSliderUpperOmegaValue(), 0.01);
        assertEquals(0.0, sliderGroup.getSliderVValue(), 0.01);
    }

    @Test
    public void testDeleteButtonAction() {
        final boolean[] deleted = {false};
        sliderGroup.addDeleteButtonListener(e -> deleted[0] = true);

        JButton deleteButton = (JButton) sliderGroup.getComponent(1);
        deleteButton.doClick();

        assertTrue("Delete button action not triggered.", deleted[0]);
    }

    @Test
    public void testSlidersUpdateValue() {
        sliderGroup.addSliderAListener(value -> assertEquals(2.5, value, 0.01));
        sliderGroup.getSliderAValue();
    }

    @Test
    public void testDeleteButtonExists() {
        JButton deleteButton = (JButton) sliderGroup.getComponent(1);
        assertNotNull("Delete button should be present.", deleteButton);
        assertEquals("Delete", deleteButton.getText());
    }
}