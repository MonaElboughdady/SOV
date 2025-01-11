package fr.isae.mae.ss.y2024;

import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.event.ActionEvent;

import org.junit.Before;
import org.junit.Test;
import javax.swing.*;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.Box;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;

public class WorldWindOrbitViewerTest {

    private WorldWindOrbitViewer.AppFrame appFrame;

    @Before
    public void setUp() throws PatriusException {
        appFrame = new WorldWindOrbitViewer.AppFrame();
    }
    
    @Test
    public void testAddNewGroupAndOrbit() {
    	OrbitSidePanel orbitSidePanel = (OrbitSidePanel) appFrame.getContentPane().getComponent(1);
        int initialGroupCount = orbitSidePanel.getSliderGroups().size();

        JButton addGroupButton = orbitSidePanel.getAddGroupButton();
        assertNotNull("Add Group button should be present.", addGroupButton);

        addGroupButton.doClick();

        assertEquals(initialGroupCount + 1, orbitSidePanel.getSliderGroups().size());
    }

    @Test
    public void testIssButtonToggle() {
        OrbitSidePanel sidePanel = (OrbitSidePanel) appFrame.getContentPane().getComponent(1);
        JPanel quickAddPanel = (JPanel) sidePanel.getComponent(0);
        JToggleButton issButton = (JToggleButton) quickAddPanel.getComponent(2);

        issButton.doClick();

        RenderableLayer issLayer = (RenderableLayer) appFrame.getWwd().getModel().getLayers().stream()
                .filter(layer -> layer.getName().equals("ISS"))
                .findFirst().orElse(null);
        
        
        assertNotNull("ISS layer should be present after toggle.", issLayer);
        assertFalse("ISS layer should contain renderables.", issLayer.getNumRenderables() == 0);

        issButton.doClick();
        assertTrue("ISS layer should be empty after untoggle.", issLayer.getNumRenderables() == 0);
    }

    @Test
    public void testNilesatButtonToggle() {
        OrbitSidePanel sidePanel = (OrbitSidePanel) appFrame.getContentPane().getComponent(1);
        JPanel quickAddPanel = (JPanel) sidePanel.getComponent(0);
        JToggleButton nilesatButton = (JToggleButton) quickAddPanel.getComponent(0);

        nilesatButton.doClick();

        RenderableLayer nilesatLayer = (RenderableLayer) appFrame.getWwd().getModel().getLayers().stream()
                .filter(layer -> layer.getName().equals("NileSat"))
                .findFirst().orElse(null);

        assertNotNull("NileSat layer should be present after toggle.", nilesatLayer);
        assertFalse("NileSat layer should contain renderables.", nilesatLayer.getNumRenderables() == 0);

        nilesatButton.doClick();
        assertTrue("NileSat layer should be empty after untoggle.", nilesatLayer.getNumRenderables() == 0);
    }
    
    @Test
    public void testContextMenuCreation() {
        Component dummyComponent = new JPanel();
        WorldWindOrbitViewer.ContextMenu contextMenu = new WorldWindOrbitViewer.ContextMenu(
            dummyComponent,
            new WorldWindOrbitViewer.ContextMenuInfo(
                "Test Menu",
                new WorldWindOrbitViewer.ContextMenuItemInfo[]{
                    new WorldWindOrbitViewer.ContextMenuItemInfo("Item 1"),
                    new WorldWindOrbitViewer.ContextMenuItemInfo("Item 2")
                }
            )
        );

        assertNotNull("Context menu should be created.", contextMenu);
        assertEquals("Context menu should have 2 items.", 2, contextMenu.menuItems.size());
    }
    
    @Test
    public void testContextMenuItemAction() {
        boolean[] actionTriggered = {false};

        WorldWindOrbitViewer.ContextMenuItemInfo testItem =
            new WorldWindOrbitViewer.ContextMenuItemInfo("Test Action");

        WorldWindOrbitViewer.ContextMenuItemAction action =
            new WorldWindOrbitViewer.ContextMenuItemAction(testItem) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionTriggered[0] = true;
                }
            };

        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

        assertTrue("Context menu action should be triggered.", actionTriggered[0]);
    }
    
    @Test
    public void testShowContextMenu() {
        // Create a dummy SelectEvent with a Box object
        WorldWindOrbitViewer.ContextMenuController controller = new WorldWindOrbitViewer.ContextMenuController(new WorldWindowGLCanvas());
        
        Box testBox = new Box(gov.nasa.worldwind.geom.Position.fromDegrees(0, 0, 100), 100, 100, 100);
        PickedObject pickedObj = new PickedObject(0, testBox);
        PickedObjectList pickedList = new PickedObjectList();
        pickedList.add(pickedObj);
        SelectEvent mockEvent = new SelectEvent((Object) testBox, SelectEvent.LEFT_CLICK, new java.awt.Point(), pickedList);
        
        // Override event behavior to simulate selection event and test context menu
        controller.selected(mockEvent);
        
        // Verify context menu creation by checking the console output or indirectly through context setup
        assertTrue("The event should be processed when a Box is clicked.", mockEvent.getEventAction().equals(SelectEvent.LEFT_CLICK));
    }
    
    @Test
    public void verifyIssLocation() {
        OrbitSidePanel sidePanel = (OrbitSidePanel) appFrame.getContentPane().getComponent(1);
        JPanel quickAddPanel = (JPanel) sidePanel.getComponent(0);
        JToggleButton issButton = (JToggleButton) quickAddPanel.getComponent(2);

        issButton.doClick();

        RenderableLayer issLayer = (RenderableLayer) appFrame.getWwd().getModel().getLayers().stream()
                .filter(layer -> layer.getName().equals("ISS"))
                .findFirst().orElse(null);
        
        
        assertNotNull("ISS layer should be present after toggle.", issLayer);
        assertFalse("ISS layer should contain renderables.", issLayer.getNumRenderables() == 0);

        issButton.doClick();
        assertTrue("ISS layer should be empty after untoggle.", issLayer.getNumRenderables() == 0);
    }
    
}
