/*
 * Copyright 2006-2009, 2017, 2020 United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 * 
 * The NASA World Wind Java (WWJ) platform is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * NASA World Wind Java (WWJ) also contains the following 3rd party Open Source
 * software:
 * 
 *     Jackson Parser – Licensed under Apache 2.0
 *     GDAL – Licensed under MIT
 *     JOGL – Licensed under  Berkeley Software Distribution (BSD)
 *     Gluegen – Licensed under Berkeley Software Distribution (BSD)
 * 
 * A complete listing of 3rd Party software notices and licenses included in
 * NASA World Wind Java (WWJ)  can be found in the WorldWindJava-v2.2 3rd-party
 * notices and licenses PDF found in code directory.
 */
package fr.isae.mae.ss.y2024;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fr.cnes.sirius.patrius.bodies.BodyShape;
import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.bodies.OneAxisEllipsoid;
import fr.cnes.sirius.patrius.frames.FactoryManagedFrame;
import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.math.ode.FirstOrderIntegrator;
import fr.cnes.sirius.patrius.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
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
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Box;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwindx.applications.worldwindow.util.Util;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

/**
 * This example demonstrates the use of multiple WMS layers, as displayed in a
 * WMSLayersPanel.
 *
 * @author tag
 * @version $Id: WMSLayerManager.java 2109 2014-06-30 16:52:38Z tgaskins $
 */
public class WorldWindOrbitViewer extends ApplicationTemplate {

	/**
	 * The ContextMenu class implements the context menu.
	 */
	protected static class ContextMenu {

		public static final String CONTEXT_MENU_INFO = "ContextMenuInfo";

		protected ContextMenuInfo ctxMenuInfo;
		protected Component sourceComponent;
		protected JMenuItem menuTitleItem;
		protected ArrayList<JMenuItem> menuItems = new ArrayList<>();

		public ContextMenu(Component sourceComponent, ContextMenuInfo contextMenuInfo) {
			this.sourceComponent = sourceComponent;
			this.ctxMenuInfo = contextMenuInfo;

			this.makeMenuTitle();
			this.makeMenuItems();
		}

		protected void makeMenuTitle() {
			this.menuTitleItem = new JMenuItem(this.ctxMenuInfo.menuTitle);
		}

		protected void makeMenuItems() {
			for (ContextMenuItemInfo itemInfo : this.ctxMenuInfo.menuItems) {
				this.menuItems.add(new JMenuItem(new ContextMenuItemAction(itemInfo)));
			}
		}

		public void show(final Point screenPt) {
			JPopupMenu popup = new JPopupMenu();

			popup.add(this.menuTitleItem);

			popup.addSeparator();

			for (JMenuItem subMenu : this.menuItems) {
				popup.add(subMenu);
			}

			popup.show(sourceComponent, (int) screenPt.getX(), (int) screenPt.getY());
		}
	}

	/**
	 * The ContextMenuInfo class specifies the contents of the context menu.
	 */
	protected static class ContextMenuInfo {

		protected String menuTitle;
		protected ContextMenuItemInfo[] menuItems;

		public ContextMenuInfo(String title, ContextMenuItemInfo[] menuItems) {
			this.menuTitle = title;
			this.menuItems = menuItems;
		}
	}

	/**
	 * The ContextMenuItemInfo class specifies the contents of one entry in the
	 * context menu.
	 */
	protected static class ContextMenuItemInfo {

		protected String displayString;

		public ContextMenuItemInfo(String displayString) {
			this.displayString = displayString;
		}
	}

	/**
	 * The ContextMenuItemAction responds to user selection of a context menu item.
	 */
	public static class ContextMenuItemAction extends AbstractAction {

		protected ContextMenuItemInfo itemInfo;

		public ContextMenuItemAction(ContextMenuItemInfo itemInfo) {
			super(itemInfo.displayString);

			this.itemInfo = itemInfo;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			System.out.println(this.itemInfo.displayString); // Replace with application's menu-item response.
		}
	}

	/**
	 * The Controller listens for selection events and either highlights a selected
	 * item or shows its context menu.
	 */
	protected static class ContextMenuController implements SelectListener {

		protected PointPlacemark lastPickedPlacemark = null;
		private final WorldWindow ww;

		public ContextMenuController(WorldWindow ww) {
			this.ww = ww;
		}

		@Override
		public void selected(SelectEvent event) {
			try {
				if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)) // Could do RIGHT_CLICK instead
				{
					showContextMenu(event);
				}
			} catch (Exception e) {
				Util.getLogger().warning(e.getMessage() != null ? e.getMessage() : e.toString());
			}
		}

		protected void showContextMenu(SelectEvent event) {
			if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)) {
				Object topObject = event.getTopObject();

				// Check if the clicked object is a Path
				if (topObject instanceof Path) {
					Path clickedPath = (Path) topObject;

					// Get the clicked position from the path
					Position clickedPosition = getClickedPositionOnPath(event, clickedPath);

					if (clickedPosition != null) {
						System.out.printf("Clicked Position - Lat: %.6f, Lon: %.6f, Alt: %.2f%n",
								clickedPosition.getLatitude().degrees, clickedPosition.getLongitude().degrees,
								clickedPosition.getElevation());

						ContextMenuItemInfo[] infos = {
								new ContextMenuItemInfo(clickedPosition.getLatitude().toString()),
								new ContextMenuItemInfo(clickedPosition.getLongitude().toString()),
								new ContextMenuItemInfo("" + clickedPosition.getAltitude())};
						ContextMenuInfo ctxInfo = new ContextMenuInfo("Position", infos);
						ContextMenu menu = new ContextMenu((Component) event.getSource(), ctxInfo);
						menu.show(event.getPickPoint());
					} else {
						System.out.println("No position found on the path for the click.");
					}
				}
			}
		}

		/**
		 * Computes the position along the Path that was clicked.
		 *
		 * @param event the SelectEvent containing the pick ray.
		 * @param path  the Path being clicked.
		 * @return the Position of the click on the path, or null if no intersection is
		 *         found.
		 */
		private Position getClickedPositionOnPath(SelectEvent event, Path path) {
			Line pickRay = this.ww.getView().computeRayFromScreenPoint(event.getPickPoint().getX(),
					event.getPickPoint().getY());

			Globe globe = this.ww.getModel().getGlobe();
			Iterable<? extends Position> positions = path.getPositions();

			Position closestPosition = null;
			double closestDistance = Double.MAX_VALUE;

			for (Position position : positions) {
				Vec4 pathPoint = globe.computePointFromPosition(position);
				Vec4 intersectionPoint = pickRay.nearestPointTo(pathPoint);

				double distance = pathPoint.distanceTo3(intersectionPoint);

				if (distance < closestDistance) {
					closestDistance = distance;
					closestPosition = position;
				}
			}

			// Return the closest position if it is within a reasonable threshold
			return closestDistance < 1e5 ? closestPosition : null;
		}
	}

	public static class CustomOrbitView extends BasicOrbitView {

		@Override
		protected double computeFarDistance(Position eyePosition) {
			// TODO Auto-generated method stub
			return 2 * super.computeFarDistance(eyePosition);
		}

	}

	protected static class AppFrame extends ApplicationTemplate.AppFrame {

		public AppFrame() throws PatriusException {
			super(false, false, false);

			OrbitSidePanel orbitSidePanel = new OrbitSidePanel();

			RenderableLayer layer = new RenderableLayer();

			this.getWwd().setView(new CustomOrbitView());

			ContextMenuItemInfo[] itemActionNames = new ContextMenuItemInfo[] { new ContextMenuItemInfo("Do This"),
					new ContextMenuItemInfo("Do That"), new ContextMenuItemInfo("Do the Other Thing"), };

			layer.setName("Orbit");

			// Method to create the orbit and add the path to the layer
			Runnable updateOrbitPath = () -> {
				try {
					Orbit updatedOrbit = new KeplerianOrbit(
							Constants.WGS84_EARTH_EQUATORIAL_RADIUS * orbitSidePanel.getSliderAValue(),
							orbitSidePanel.getSliderEValue(), Math.toRadians(orbitSidePanel.getSliderIValue()),
							Math.toRadians(orbitSidePanel.getSliderOmegaValue()),
							Math.toRadians(orbitSidePanel.getSliderUpperOmegaValue()),
							Math.toRadians(orbitSidePanel.getSliderVValue()), PositionAngle.MEAN,
							FramesFactory.getGCRF(), new AbsoluteDate(), Constants.WGS84_EARTH_MU);

					// Create and set an attribute bundle.
					ShapeAttributes boxAttrs = new BasicShapeAttributes();
					boxAttrs.setInteriorMaterial(Material.RED);
					boxAttrs.setInteriorOpacity(1);
					boxAttrs.setEnableLighting(true);
					boxAttrs.setOutlineMaterial(Material.RED);
					boxAttrs.setOutlineWidth(2d);
					boxAttrs.setDrawInterior(true);
					boxAttrs.setDrawOutline(false);

					// Propagate and update path
					List<GeodeticPoint> points = propagateMyWonderfulOrbit(updatedOrbit);
					List<Position> positions = glueBetweenPatriusAndWorldwind(points);

//					for (Position position: positions) {
//
//					}

					Box satellite2 = new Box(positions.get(0), 150000, 150000, 150000);
					satellite2.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
					satellite2.setAttributes(boxAttrs);
					satellite2.setVisible(true);
					satellite2.setValue(AVKey.DISPLAY_NAME, "Satellite");

					// Clear the previous path and add the new one
					layer.removeAllRenderables();
					ShapeAttributes attrs = new BasicShapeAttributes();
					attrs.setOutlineMaterial(new Material(Color.YELLOW));
					attrs.setOutlineWidth(5.0);
					attrs.setEnableAntialiasing(true);

					Path path = new Path(positions);
					path.setAttributes(attrs);
					path.setVisible(true);
					path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
					path.setPathType(AVKey.GREAT_CIRCLE);
					path.setValue(ContextMenu.CONTEXT_MENU_INFO, new ContextMenuInfo("Placemark A", itemActionNames));

					layer.addRenderable(path);
					layer.addRenderable(satellite2);

					// Force a redraw of the WorldWind canvas
					getWwd().redraw();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};

			// Add listeners to each slider
			orbitSidePanel.addSliderAListener(e -> updateOrbitPath.run());
			orbitSidePanel.addSliderEListener(e -> updateOrbitPath.run());
			orbitSidePanel.addSliderIListener(e -> updateOrbitPath.run());
			orbitSidePanel.addSliderUpperOmegaListener(e -> updateOrbitPath.run());
			orbitSidePanel.addSliderOmegaListener(e -> updateOrbitPath.run());
			orbitSidePanel.addSliderVListener(e -> updateOrbitPath.run());

			// Initial path setup
			updateOrbitPath.run();

			// Add the layer to the model
			insertBeforeCompass(getWwd(), layer);
			ContextMenuController contextMenuController = new ContextMenuController(this.getWwd());
			getWwd().addSelectListener(contextMenuController);

			this.getContentPane().add(orbitSidePanel, BorderLayout.WEST);
		}

		/** The orbit propagator */
		public static List<GeodeticPoint> propagateMyWonderfulOrbit(Orbit iniOrbit) throws PatriusException {

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

		/** This method maps the points of Patrius to positions of WorldWind */
		public static List<Position> glueBetweenPatriusAndWorldwind(List<GeodeticPoint> points) {
			List<Position> positions = new ArrayList<Position>(points.size());
			for (GeodeticPoint point : points) {
				positions.add(Position.fromRadians(point.getLatitude(), point.getLongitude(), point.getAltitude()));
			}
			return positions;

		}

	}

	public static void main(String[] args) {
		WorldWind.setOfflineMode(true);
		ApplicationTemplate.start("Mona's Wonderful Orbit Displayer", AppFrame.class);
	}
}