package fr.isae.mae.ss.y2024;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.cnes.sirius.patrius.bodies.BodyShape;
import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.bodies.OneAxisEllipsoid;
import fr.cnes.sirius.patrius.frames.FactoryManagedFrame;
import fr.cnes.sirius.patrius.frames.Frame;
import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.math.ode.FirstOrderIntegrator;
import fr.cnes.sirius.patrius.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Box;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import fr.cnes.sirius.patrius.orbits.KeplerianOrbit;
import fr.cnes.sirius.patrius.orbits.Orbit;
import fr.cnes.sirius.patrius.orbits.OrbitType;
import fr.cnes.sirius.patrius.orbits.PositionAngle;
import fr.cnes.sirius.patrius.propagation.SpacecraftState;
import fr.cnes.sirius.patrius.propagation.numerical.NumericalPropagator;
import fr.cnes.sirius.patrius.propagation.sampling.PatriusFixedStepHandler;
import fr.cnes.sirius.patrius.time.AbsoluteDate;
import fr.cnes.sirius.patrius.utils.Constants;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import fr.cnes.sirius.patrius.utils.exception.PropagationException;


/**
 * The CustomOrbit class represents an orbital object that can be visualized using the WorldWind and Patrius libraries.
 * It provides methods for creating, propagating, and rendering an orbit, including paths and satellite representations.
 */
public class CustomOrbit {

    /** List of geodetic points representing the orbit trajectory. */
    private List<GeodeticPoint> points;

    /** List of WorldWind positions used for rendering the orbit path. */
    private List<Position> positions;

    /** The Keplerian orbit representing the orbital parameters of the object. */
    private KeplerianOrbit orbit;

    /** The graphical path representing the orbit trajectory in the WorldWind environment. */
    private Path path;

    /** The satellite representation as a 3D box rendered in WorldWind. */
    private Box satellite;

    /** Attributes for styling the orbit path, including color and line width. */
    private ShapeAttributes pathAttrs;

    /** Attributes for styling the satellite box, including color, lighting, and visibility. */
    private ShapeAttributes boxAttrs;

    /** The display name of the orbit, used for identification in the WorldWind UI. */
    private String displayName;

	/**
	 * Constructs a CustomOrbit instance with the specified orbital parameters.
	 *
	 * @param a             The semi-major axis of the orbit, in meters.
	 * @param e             The eccentricity of the orbit (0 for circular, closer to 1 for elliptical).
	 * @param i             The inclination of the orbit, in radians.
	 * @param pa            The argument of perigee, in radians.
	 * @param raan          The right ascension of ascending node (RAAN), in radians.
	 * @param anomaly       The true anomaly or mean anomaly of the orbit, in radians.
	 * @param type          The type of position angle (e.g., true, mean, eccentric).
	 * @param frame         The reference frame for the orbit (e.g., GCRF, ITRF).
	 * @param date          The epoch date for the orbital elements.
	 * @param mu            The standard gravitational parameter for the central body, in m^3/s^2.
	 * @param displayName   A display name for the orbit, used for visualization.
	 */
	CustomOrbit(final double a, final double e, final double i, final double pa, final double raan,
			final double anomaly, final PositionAngle type, final Frame frame, final AbsoluteDate date,
			final double mu, final String displayName) {
		
		this.pathAttrs = new BasicShapeAttributes();
		this.pathAttrs.setOutlineMaterial(new Material(generateRandomColor()));
		this.pathAttrs.setOutlineWidth(5.0);
		this.pathAttrs.setEnableAntialiasing(true);
		
		// Create and set an attribute bundle.
		this.boxAttrs = new BasicShapeAttributes();
		this.boxAttrs.setInteriorMaterial(Material.RED);
		this.boxAttrs.setInteriorOpacity(1);
		this.boxAttrs.setEnableLighting(true);
		this.boxAttrs.setOutlineMaterial(Material.RED);
		this.boxAttrs.setOutlineWidth(2d);
		this.boxAttrs.setDrawInterior(true);
		this.boxAttrs.setDrawOutline(false);
		
		this.displayName = displayName;
		
		updateOrbit(a, e, i, pa, raan, anomaly, type, frame, date, mu, displayName);


	}

	/**
	 * Propagates the orbit and returns a list of geodetic points representing the trajectory.
	 *
	 * @param iniOrbit The initial orbit to be propagated.
	 * @return A list of geodetic points representing the propagated orbit's trajectory.
	 * @throws PatriusException If an error occurs during the orbit propagation or coordinate transformation.
	 */
	private static List<GeodeticPoint> propagateOrbit(Orbit iniOrbit) throws PatriusException {

		// We create a spacecratftstate
		final SpacecraftState iniState = new SpacecraftState(iniOrbit);

		// Initialization of the Runge Kutta integrator with a 2 s step
		final double pasRk = 0.05;
		final FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(pasRk);

		// Initialization of the propagator
		final NumericalPropagator propagator = new NumericalPropagator(integrator);
		propagator.resetInitialState(iniState);

		// Forcing integration using cartesian equations
		propagator.setOrbitType(OrbitType.CARTESIAN);

		final FactoryManagedFrame ITRF = FramesFactory.getITRF();

		final BodyShape EARTH = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
				Constants.WGS84_EARTH_FLATTENING, ITRF);
		// SPECIFIC
		// Creation of a fixed step handler
		final ArrayList<GeodeticPoint> listOfStates = new ArrayList<>();
		PatriusFixedStepHandler myStepHandler = new PatriusFixedStepHandler() {
			private static final long serialVersionUID = 1L;

			public void init(SpacecraftState s0, AbsoluteDate t) {
				// Nothing to do ...
			}

			/** The step handler used to store every point */
			public void handleStep(SpacecraftState currentState, boolean isLast) throws PropagationException {

				GeodeticPoint geodeticPoint;
				try {
					geodeticPoint = EARTH.transform(currentState.getPVCoordinates().getPosition(), ITRF,
							currentState.getDate());
				} catch (PatriusException e) {
					throw new PropagationException(e);
				}
				// Adding S/C to the list
				listOfStates.add(geodeticPoint);
			}
		};
		// The handler frequency is set to 10S
		propagator.setMasterMode(10., myStepHandler);
		// SPECIFIC

		// Propagating 100s
		final double dt = iniOrbit.getKeplerianPeriod();
		final AbsoluteDate finalDate = iniOrbit.getDate().shiftedBy(dt);
		final SpacecraftState finalState = propagator.propagate(finalDate);

		return listOfStates;

	}

	/**
	 * Converts a list of geodetic points from the Patrius library into WorldWind Position objects.
	 *
	 * @param points The list of geodetic points representing the satellite's trajectory.
	 * @return A list of WorldWind Position objects corresponding to the provided geodetic points.
	 */
	private static List<Position> glueBetweenPatriusAndWorldwind(List<GeodeticPoint> points) {
		List<Position> positions = new ArrayList<Position>(points.size());
		for (GeodeticPoint point : points) {
			positions.add(Position.fromRadians(point.getLatitude(), point.getLongitude(), point.getAltitude()));
		}
		return positions;

	}

	/**
	 * Updates the orbital parameters and redraws the orbit visualization using the provided values.
	 *
	 * @param a             Semi-major axis of the orbit in meters.
	 * @param e             Eccentricity of the orbit, a unitless value (0 for circular orbits).
	 * @param i             Inclination of the orbit in radians (angle between the orbital plane and the equatorial plane).
	 * @param pa            Argument of perigee in radians (angle from ascending node to perigee).
	 * @param raan          Right Ascension of Ascending Node (RAAN) in radians (angle from reference direction to ascending node).
	 * @param anomaly       True anomaly or mean anomaly in radians (current position of the satellite in the orbit).
	 * @param type          The type of anomaly used (e.g., Mean, True, Eccentric).
	 * @param frame         The reference frame used for the orbit (e.g., GCRF).
	 * @param date          The reference date of the orbital parameters.
	 * @param mu            Standard gravitational parameter of the central body in m^3/s^2 (e.g., Earth's gravitational constant).
	 * @param displayName   The name to be displayed on the orbit visualization.
	 */
	public void updateOrbit(final double a, final double e, final double i, final double pa, final double raan,
			final double anomaly, final PositionAngle type, final Frame frame, final AbsoluteDate date,
			final double mu, String displayName) {
		this.orbit = new KeplerianOrbit(a, e, i, pa, raan, anomaly, type, frame, date, mu);

		// Propagate and update path
		try {
			this.points = propagateOrbit(this.orbit);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		this.positions = glueBetweenPatriusAndWorldwind(points);

		this.path = new Path(this.positions);
		this.path.setAttributes(pathAttrs);
		this.path.setVisible(true);
		this.path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		this.path.setPathType(AVKey.GREAT_CIRCLE);

		this.satellite = new Box(positions.get(0), 300000, 300000, 300000);
		this.satellite.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		this.satellite.setAttributes(boxAttrs);
		this.satellite.setVisible(true);
		this.satellite.setValue(AVKey.DISPLAY_NAME, displayName);
	}
	
	/**
	 * Creates a runnable task that updates the orbit and redraws the visualization
	 * based on the current values of the provided slider group.
	 *
	 * The task updates the orbital parameters using the current values of the sliders
	 * and refreshes the graphical representation of the orbit on the specified layer and
	 * WorldWindow instance.
	 *
	 * @param sliderGroup  The {@link SliderGroup} containing the orbital parameter sliders.
	 * @param layer        The {@link RenderableLayer} where the orbit visualization will be updated.
	 * @param wwd          The {@link WorldWindow} instance responsible for rendering the graphical representation.
	 * @return A {@link Runnable} task that updates the orbit visualization when executed.
	 */
	public Runnable createUpdateRunnable(SliderGroup sliderGroup, RenderableLayer layer, WorldWindow wwd) {
	    return () -> {
	        updateOrbit(
	            Constants.WGS84_EARTH_EQUATORIAL_RADIUS * sliderGroup.getSliderAValue(),
	            sliderGroup.getSliderEValue(),
	            Math.toRadians(sliderGroup.getSliderIValue()),
	            Math.toRadians(sliderGroup.getSliderOmegaValue()),
	            Math.toRadians(sliderGroup.getSliderUpperOmegaValue()),
	            Math.toRadians(sliderGroup.getSliderVValue()),
	            PositionAngle.MEAN,
	            FramesFactory.getGCRF(),
	            new AbsoluteDate(),
	            Constants.WGS84_EARTH_MU,
	            displayName
	        );

	        // Update the layer and redraw the canvas
	        layer.removeAllRenderables();
	        layer.addRenderable(getPath());
	        layer.addRenderable(getSatellite());
	        wwd.redraw();
	    };
	}

	/**
	 * Generates a random color for the orbit path.
	 * The color is generated using random RGB values.
	 *
	 * @return A randomly generated {@link Color} object.
	 */
	private Color generateRandomColor() {
	    Random random = new Random();
	    return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

    /**
     * Returns the path representation of the orbit.
     * @return the path object.
     */
	public Path getPath() {
		return path;
	}

    /**
     * Returns the satellite representation of the orbit.
     * @return the satellite box object.
     */
	public Box getSatellite() {
		return satellite;
	}

	public List<GeodeticPoint> getPoints() {
		return points;
	}
}
