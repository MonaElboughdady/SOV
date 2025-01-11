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
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.orbits.PositionAngle;
import fr.cnes.sirius.patrius.time.AbsoluteDate;
import fr.cnes.sirius.patrius.utils.Constants;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Box;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwindx.applications.worldwindow.util.Util;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

/**
 * This is a satellite orbit viewer that utilizes worldwind and patrius
 *
 * @author Mona Elboughdady
 */
public class WorldWindOrbitViewer extends ApplicationTemplate {

	/**
	 * Represents a context menu for interacting with satellite objects.
	 */
	protected static class ContextMenu {

		public static final String CONTEXT_MENU_INFO = "ContextMenuInfo";

		protected ContextMenuInfo ctxMenuInfo;
		protected Component sourceComponent;
		protected JMenuItem menuTitleItem;
		protected ArrayList<JMenuItem> menuItems = new ArrayList<>();

		/**
		 * Constructs a context menu with the specified component and menu items.
		 * 
		 * @param sourceComponent The component to which the menu is attached.
		 * @param contextMenuInfo The context menu information including title and
		 *                        items.
		 */
		public ContextMenu(Component sourceComponent, ContextMenuInfo contextMenuInfo) {
			this.sourceComponent = sourceComponent;
			this.ctxMenuInfo = contextMenuInfo;

			this.makeMenuTitle();
			this.makeMenuItems();
		}

		/**
		 * Creates the title item for the context menu.
		 */
		protected void makeMenuTitle() {
			this.menuTitleItem = new JMenuItem(this.ctxMenuInfo.menuTitle);
		}

		/**
		 * Populates the context menu with items.
		 */
		protected void makeMenuItems() {
			for (ContextMenuItemInfo itemInfo : this.ctxMenuInfo.menuItems) {
				this.menuItems.add(new JMenuItem(new ContextMenuItemAction(itemInfo)));
			}
		}

		/**
		 * Displays the context menu at the specified screen position.
		 * 
		 * @param screenPt The screen position where the menu should be shown.
		 */
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
	 * Represents context menu information such as title and items.
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

		/**
		 * Displays a context menu when a satellite is clicked.
		 * 
		 * @param event The selection event triggering the menu.
		 */
		protected void showContextMenu(SelectEvent event) {
			if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)) {
				Object topObject = event.getTopObject();

				// Check if the clicked object is a Path
				if (topObject instanceof Path) {
					Path clickedPath = (Path) topObject;

					// Get the clicked position from the path
					Position clickedPathPosition = getClickedPositionOnPath(event, clickedPath);

					if (clickedPathPosition != null) {
						ContextMenuItemInfo[] infosPath = {
								new ContextMenuItemInfo(clickedPathPosition.getLatitude().toString()),
								new ContextMenuItemInfo(clickedPathPosition.getLongitude().toString()),
								new ContextMenuItemInfo("" + clickedPathPosition.getAltitude()) };
						ContextMenuInfo ctxInfoPath = new ContextMenuInfo("Position", infosPath);
						ContextMenu menuPath = new ContextMenu((Component) event.getSource(), ctxInfoPath);
						menuPath.show(event.getPickPoint());
					}

					// Check if the clicked object is a Path
					if (topObject instanceof Box) {
						Box clickedSatellite = (Box) topObject;

						// Get the clicked position from the path
						Position clickedPosition = clickedSatellite.getCenterPosition();

						ContextMenuItemInfo[] infos = {
								new ContextMenuItemInfo("Latitude: " + clickedPosition.getLatitude().toString()),
								new ContextMenuItemInfo("Longitude: " + clickedPosition.getLongitude().toString()),
								new ContextMenuItemInfo("Altitude: " + clickedPosition.getAltitude()) };
						ContextMenuInfo ctxInfo = new ContextMenuInfo("Position", infos);
						ContextMenu menu = new ContextMenu((Component) event.getSource(), ctxInfo);
						menu.show(event.getPickPoint());
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

	/**
	 * Custom view for orbit visualization multiplies far distance to allow showing
	 * complete orbits without being incorrectly clipped a the "horizon".
	 */
	public static class CustomOrbitView extends BasicOrbitView {

		@Override
		protected double computeFarDistance(Position eyePosition) {
			// TODO Auto-generated method stub
			return 2 * super.computeFarDistance(eyePosition);
		}

	}

	/**
	 * The main application frame for managing and visualizing multiple orbit
	 * layers.
	 */
	protected static class AppFrame extends ApplicationTemplate.AppFrame {

		public AppFrame() throws PatriusException {
			super(false, false, false);

			// Initialize slider groups and layers
			OrbitSidePanel orbitSidePanel = new OrbitSidePanel();

			this.getWwd().setView(new CustomOrbitView());
			RenderableLayer issLayer = new RenderableLayer();
			issLayer.setName("ISS");

			CustomOrbit ISS = new CustomOrbit(Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 415e3, 0.0005931,
					Math.toRadians(51.6403), Math.toRadians(28.9604), Math.toRadians(57.3420), Math.toRadians(122.7049),
					PositionAngle.MEAN, FramesFactory.getGCRF(), new AbsoluteDate(), Constants.WGS84_EARTH_MU, "ISS");
			this.getWwd().getModel().getLayers().add(issLayer);
			insertBeforeCompass(getWwd(), issLayer);

			RenderableLayer nilesatLayer = new RenderableLayer();
			nilesatLayer.setName("NileSat");

			CustomOrbit nileSat = new CustomOrbit(Constants.WGS84_EARTH_EQUATORIAL_RADIUS + 35786.5e3, 0.0004911,
					Math.toRadians(0.0440), Math.toRadians(310.3249), Math.toRadians(359.1397),
					Math.toRadians(300.3377), PositionAngle.MEAN, FramesFactory.getGCRF(), new AbsoluteDate(),
					Constants.WGS84_EARTH_MU, "NileSat");
			this.getWwd().getModel().getLayers().add(nilesatLayer);
			insertBeforeCompass(getWwd(), nilesatLayer);

			orbitSidePanel.setIssButtonListener(() -> {
				issLayer.addRenderable(ISS.getPath());
				issLayer.addRenderable(ISS.getSatellite());
				this.getWwd().redraw();
			}, () -> {
				issLayer.removeAllRenderables();
			});

			orbitSidePanel.setNilesatButtonListener(() -> {
				nilesatLayer.addRenderable(nileSat.getPath());
				nilesatLayer.addRenderable(nileSat.getSatellite());
				this.getWwd().redraw();
			}, () -> {
				nilesatLayer.removeAllRenderables();
				this.getWwd().redraw();
			});

			// Configure add group button to dynamically create orbits and layers
			orbitSidePanel.getAddGroupButton().addActionListener(e -> {
				int groupNumber = orbitSidePanel.getSliderGroups().size() + 1;

				// Create a new layer for the orbit
				RenderableLayer newLayer = new RenderableLayer();
				newLayer.setName("Orbit " + groupNumber);
				this.getWwd().getModel().getLayers().add(newLayer);

				// Create a new slider group
				int groupId = orbitSidePanel.addNewGroup();
				SliderGroup newGroup = orbitSidePanel.getSliderGroups().get(groupNumber - 1);

				// Create a new orbit controlled by the new slider group
				CustomOrbit newOrbit = new CustomOrbit(
						Constants.WGS84_EARTH_EQUATORIAL_RADIUS * newGroup.getSliderAValue(),
						newGroup.getSliderEValue(), Math.toRadians(newGroup.getSliderIValue()),
						Math.toRadians(newGroup.getSliderOmegaValue()),
						Math.toRadians(newGroup.getSliderUpperOmegaValue()), Math.toRadians(newGroup.getSliderVValue()),
						PositionAngle.MEAN, FramesFactory.getGCRF(), new AbsoluteDate(), Constants.WGS84_EARTH_MU,
						"Group " + groupId);

				// Create a runnable to update the orbit and link to slider listeners
				Runnable updateOrbitRunnable = newOrbit.createUpdateRunnable(newGroup, newLayer, getWwd());
				addRunnables(newGroup, updateOrbitRunnable);

				// Add delete functionality
				newGroup.addDeleteButtonListener(event -> {
					orbitSidePanel.removeGroup(newGroup);
					newLayer.removeAllRenderables();
					this.getWwd().getModel().getLayers().remove(newLayer);
					this.getWwd().redraw();
				});

				insertBeforeCompass(getWwd(), newLayer);
			});

			this.getContentPane().add(orbitSidePanel, BorderLayout.WEST);

			ContextMenuController contextMenuController = new ContextMenuController(this.getWwd());
			getWwd().addSelectListener(contextMenuController);
		}

		private void addRunnables(SliderGroup group, Runnable runnable) {
			group.addSliderAListener(event -> runnable.run());
			group.addSliderEListener(event -> runnable.run());
			group.addSliderIListener(event -> runnable.run());
			group.addSliderOmegaListener(event -> runnable.run());
			group.addSliderUpperOmegaListener(event -> runnable.run());
			group.addSliderVListener(event -> runnable.run());
		}

	}

	/**
	 * The main method initializes the WorldWind application.
	 * 
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		System.setProperty("sun.java2d.uiScale", "1.0");
		System.setProperty("prism.allowhidpi", "false");
		System.setProperty("jogamp.gluegen.system.dont.use.native.awt", "false");
		WorldWind.setOfflineMode(true);
		ApplicationTemplate.start("Mona's Wonderful Orbit Displayer", AppFrame.class);
	}
}