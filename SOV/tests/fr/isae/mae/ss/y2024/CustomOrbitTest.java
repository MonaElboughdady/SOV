package fr.isae.mae.ss.y2024;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.orbits.PositionAngle;
import fr.cnes.sirius.patrius.time.AbsoluteDate;
import fr.cnes.sirius.patrius.utils.Constants;
import fr.cnes.sirius.patrius.utils.exception.PropagationException;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

public class CustomOrbitTest {

    private CustomOrbit customOrbit;
    private static final double TOLERANCE = 1e-5;

    @Before
    public void setup() {
        customOrbit = new CustomOrbit(
            Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 500000,
            0.01,
            Math.toRadians(45),
            Math.toRadians(120),
            Math.toRadians(75),
            Math.toRadians(30),
            PositionAngle.MEAN,
            FramesFactory.getGCRF(),
            new AbsoluteDate(),
            Constants.WGS84_EARTH_MU,
            "TestOrbit"
        );
    }

    @Test
    public void testInitialValues() {
        assertNotNull(customOrbit.getPath());
        assertNotNull(customOrbit.getSatellite());
    }

    @Test
    public void testUpdateOrbit() {
        customOrbit.updateOrbit(
            Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 700000,
            0.02,
            Math.toRadians(30),
            Math.toRadians(100),
            Math.toRadians(50),
            Math.toRadians(20),
            PositionAngle.MEAN,
            FramesFactory.getGCRF(),
            new AbsoluteDate(),
            Constants.WGS84_EARTH_MU,
            "UpdatedOrbit"
        );

        assertNotNull(customOrbit.getPath());
        assertNotNull(customOrbit.getSatellite());
        assertEquals("UpdatedOrbit", customOrbit.getSatellite().getValue(AVKey.DISPLAY_NAME));
    }

    @Test
    public void testCreateUpdateRunnable() {
        SliderGroup sliderGroup = new SliderGroup("TestGroup", 1.0, 0.01, 30.0, 120.0, 75.0, 30.0);
        RenderableLayer layer = new RenderableLayer();
        WorldWindow wwd = new WorldWindowGLCanvas();

        Runnable updateRunnable = customOrbit.createUpdateRunnable(sliderGroup, layer, wwd);
        assertNotNull(updateRunnable);

        // Trigger the runnable and check for updates
        updateRunnable.run();
        assertFalse(layer.getNumRenderables() == 0);
    }

    @Test
    public void testRandomColorGeneration() {
        CustomOrbit orbit1 = new CustomOrbit(
            Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 500000,
            0.01,
            Math.toRadians(45),
            Math.toRadians(120),
            Math.toRadians(75),
            Math.toRadians(30),
            PositionAngle.MEAN,
            FramesFactory.getGCRF(),
            new AbsoluteDate(),
            Constants.WGS84_EARTH_MU,
            "TestOrbit"
        );

        CustomOrbit orbit2 = new CustomOrbit(
            Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 500000,
            0.01,
            Math.toRadians(45),
            Math.toRadians(120),
            Math.toRadians(75),
            Math.toRadians(30),
            PositionAngle.MEAN,
            FramesFactory.getGCRF(),
            new AbsoluteDate(),
            Constants.WGS84_EARTH_MU,
            "TestOrbit"
        );

        assertFalse(orbit1.getPath().getAttributes().getOutlineMaterial().equals(
                orbit2.getPath().getAttributes().getOutlineMaterial()));
    }
    

}
