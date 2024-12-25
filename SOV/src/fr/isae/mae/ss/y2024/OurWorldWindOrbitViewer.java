package fr.isae.mae.ss.y2024;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.render.markers.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.Color;
import java.awt.Dimension;
import java.util.*;

import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.frames.FactoryManagedFrame;
import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.orbits.KeplerianOrbit;
import fr.cnes.sirius.patrius.orbits.Orbit;
import fr.cnes.sirius.patrius.orbits.OrbitType;
import fr.cnes.sirius.patrius.orbits.PositionAngle;
import fr.cnes.sirius.patrius.propagation.SpacecraftState;
import fr.cnes.sirius.patrius.propagation.events.AltitudeDetector;
import fr.cnes.sirius.patrius.propagation.numerical.NumericalPropagator;
import fr.cnes.sirius.patrius.propagation.sampling.PatriusFixedStepHandler;
import fr.cnes.sirius.patrius.time.AbsoluteDate;
import fr.cnes.sirius.patrius.utils.Constants;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import fr.cnes.sirius.patrius.utils.exception.PropagationException;
import fr.cnes.sirius.patrius.math.ode.*;
import fr.cnes.sirius.patrius.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import fr.cnes.sirius.patrius.bodies.*;

/**
 * Example of {@link Path} usage. A Path is a line or curve between positions.
 * The path may follow terrain, and may be turned into a curtain by extruding
 * the path to the ground.
 *
 * @author tag
 * @version $Id: Paths.java 2292 2014-09-02 21:13:05Z tgaskins $
 */
public class OurWorldWindOrbitViewer extends ApplicationTemplate {

	public static class AppFrame extends ApplicationTemplate.AppFrame {

		public AppFrame() throws PatriusException {
			RenderableLayer layer = new RenderableLayer();

			for (int i = 1; i <= 5; i++) {
				// Add a dragger to enable shape dragging
				this.getWwd().addSelectListener(new BasicDragger(this.getWwd()));
				Orbit theOrbit1 = new KeplerianOrbit(Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 400e3, 0.01, 0.2 * i, 0,
						0.1 * i, 0, PositionAngle.MEAN, FramesFactory.getGCRF(), new AbsoluteDate(),
						Constants.WGS84_EARTH_MU);
				List<GeodeticPoint> points1 = orbitCreation(theOrbit1);
				List<Position> positions1 = glueBetweenPatriusAndWorldWind(points1);

				// Create and set an attribute bundle.
				ShapeAttributes attrs = new BasicShapeAttributes();
				attrs.setOutlineMaterial(new Material(Color.YELLOW));
				attrs.setOutlineWidth(2d);

				// Create a path, set some of its properties and set its attributes.
				Path path = new Path(positions1);
				path.setAttributes(attrs);
				path.setVisible(true);
				path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
				path.setPathType(AVKey.GREAT_CIRCLE);
				layer.addRenderable(path);
			}
			// Add the layer to the model.
			insertBeforeCompass(getWwd(), layer);

			List<Marker> markers = new ArrayList<>(1);
			markers.add(new BasicMarker(Position.fromDegrees(90, 0), new BasicMarkerAttributes()));
			MarkerLayer markerLayer = new MarkerLayer();
			markerLayer.setMarkers(markers);
			insertBeforeCompass(getWwd(), markerLayer);

		}
	}

	public static List<GeodeticPoint> orbitCreation(Orbit iniOrbit) throws PatriusException {
		// We create a spacecratftstate
		final SpacecraftState iniState = new SpacecraftState(iniOrbit);

		// Initialization of the Runge Kutta integrator with a 2 s step
		final double pasRk = 2.;
		final FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(pasRk);

		// Initialization of the propagator
		final NumericalPropagator propagator = new NumericalPropagator(integrator);
		propagator.resetInitialState(iniState);

		// Forcing integration using cartesian equations
		propagator.setOrbitType(OrbitType.CARTESIAN);
		final FactoryManagedFrame ITRF = FramesFactory.getITRF();
		final double AE = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
		final BodyShape EARTH = new OneAxisEllipsoid(AE, Constants.WGS84_EARTH_FLATTENING, ITRF);
//SPECIFIC
		// Creation of a fixed step handler
		final ArrayList<GeodeticPoint> listOfStates = new ArrayList<GeodeticPoint>();
		PatriusFixedStepHandler myStepHandler = new PatriusFixedStepHandler() {
			private static final long serialVersionUID = 1L;

			public void init(SpacecraftState s0, AbsoluteDate t) {
				// Nothing to do ...
			}

			/* The step handler used to store every point */
			public void handleStep(SpacecraftState currentState, boolean isLast) throws PropagationException {
				GeodeticPoint geodeticPoint = null;
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
//SPECIFIC

		// Propagating 100s
		final double dt = iniOrbit.getKeplerianPeriod();
		final AbsoluteDate finalDate = iniOrbit.getDate().shiftedBy(dt);
		final SpacecraftState finalState = propagator.propagate(finalDate);

		return listOfStates;
	}

	public static List<Position> glueBetweenPatriusAndWorldWind(List<GeodeticPoint> points) {
		List<Position> positions = new ArrayList<Position>(points.size());
		for (GeodeticPoint point : points) {
			positions.add(Position.fromRadians(point.getLatitude(), point.getLongitude(), point.getAltitude()));
		}

		return positions;
	}

	public static void main(String[] args) {
		ApplicationTemplate.start("WorldWind Paths", AppFrame.class);
	}
}