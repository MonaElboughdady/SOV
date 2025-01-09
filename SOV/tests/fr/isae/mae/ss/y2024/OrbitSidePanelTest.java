package fr.isae.mae.ss.y2024;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import javax.swing.*;

public class OrbitSidePanelTest {

    private OrbitSidePanel orbitSidePanel;

    @Before
    public void setUp() {
        orbitSidePanel = new OrbitSidePanel();
    }

    @Test
    public void testAddNewGroup() {
        int initialSize = orbitSidePanel.getSliderGroups().size();
        orbitSidePanel.addNewGroup();
        assertEquals(initialSize + 1, orbitSidePanel.getSliderGroups().size());
    }

    @Test
    public void testRemoveGroup() {
        orbitSidePanel.addNewGroup();
        SliderGroup groupToRemove = orbitSidePanel.getSliderGroups().get(0);
        orbitSidePanel.removeGroup(groupToRemove);
        assertFalse(orbitSidePanel.getSliderGroups().contains(groupToRemove));
    }

    @Test
    public void testNilesatButtonToggle() {
        final boolean[] toggled = {false};
        orbitSidePanel.setNilesatButtonListener(() -> toggled[0] = true, () -> toggled[0] = false);
        orbitSidePanel.getSliderGroups().size();

        JPanel quickAddPanel = (JPanel) orbitSidePanel.getComponent(0);
        JToggleButton nilesatButton = (JToggleButton) quickAddPanel.getComponent(0);
        nilesatButton.doClick();
        assertTrue(toggled[0]);

        nilesatButton.doClick();
        assertFalse(toggled[0]);
    }

    @Test
    public void testIssButtonToggle() {
        final boolean[] toggled = {false};
        orbitSidePanel.setIssButtonListener(() -> toggled[0] = true, () -> toggled[0] = false);

        JPanel quickAddPanel = (JPanel) orbitSidePanel.getComponent(0);
        JToggleButton issButton = (JToggleButton) quickAddPanel.getComponent(2);
        issButton.doClick();
        assertTrue(toggled[0]);

        issButton.doClick();
        assertFalse(toggled[0]);
    }

    @Test
    public void testAddGroupButtonAction() {
        JButton addButton = orbitSidePanel.getAddGroupButton();
        addButton.addActionListener(e -> {
        	orbitSidePanel.addNewGroup();
        });
        int initialSize = orbitSidePanel.getSliderGroups().size();

        addButton.doClick();
        assertEquals(initialSize + 1, orbitSidePanel.getSliderGroups().size());
    }
}
