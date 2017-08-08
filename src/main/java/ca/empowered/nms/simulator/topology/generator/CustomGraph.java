package ca.empowered.nms.simulator.topology.generator;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.node.NodeElement;

public class CustomGraph extends AdjacencyListGraph {

	private static final Logger log = LogManager.getLogger(CustomGraph.class.getName());
	
	public CustomGraph(String id) {
		super(id);
		
		this.setStrict(true);
		this.setAutoCreate( false );
		this.addAttribute("ui.title", Settings.getAppName());
		
		// improve visual quality
		if (Settings.isUiAntiAlias())
			this.addAttribute("ui.quality");
		if (Settings.isUiQuality())
			this.addAttribute("ui.antialias");
				
		// css stylesheet
		this.addAttribute("ui.stylesheet", "url('"+Settings.getCssStyleSheet()+"')");
		
		// use NodeElement instead of default Node class
		this.setNodeFactory(new NodeFactory<NodeElement>() {
			public NodeElement newInstance(String id, Graph graph) {
				return new NodeElement((AbstractGraph) graph, id);
			}
		});
		
		// graph -> viewer -> view
		//				   -> renderer -> camera

		// show GUI ?
		if (Settings.isDisplayGUI()) {
			Viewer viewer = this.display();
			DefaultView view = ((DefaultView)viewer.getView(Viewer.DEFAULT_VIEW_ID));
			//view.getCamera().setViewCenter(0, 0, 0);

			if (!Settings.isGuiClosesApp()) {
				viewer.setCloseFramePolicy(CloseFramePolicy.CLOSE_VIEWER);
			}
			
			final Graph thisGraph = this;
			
			// show connected node names when clicked on
			view.addMouseListener(new MouseListener() {
				@Override public void mouseReleased(MouseEvent e) {}
				@Override public void mousePressed(MouseEvent e) {}				
				@Override public void mouseExited(MouseEvent e) {}				
				@Override public void mouseEntered(MouseEvent e) {}
				@Override public void mouseClicked(MouseEvent e) {
					//view.moveElementAtPx(element, event.getX(), event.getY());
					GraphicElement curElement = view.findNodeOrSpriteAt(e.getX(), e.getY());
					if (curElement == null)
						return;
					
					NodeElement node = thisGraph.getNode(curElement.getLabel());
					String nodes = node.getId();
					
					for (Edge edge : node.getEdgeSet()) {
						NodeElement otherNode = edge.getOpposite(node);
						nodes += "\n  -  "+otherNode.getId();
					}
					if (Settings.isUiShowRelatedNodesOnClick())
						JOptionPane.showMessageDialog(null, nodes);
					log.debug("\n"+nodes);
				}
			});
			
			view.addMouseMotionListener(new MouseMotionListener() {				
				@Override public void mouseMoved(MouseEvent e) {
					JOptionPane.getRootFrame().dispose();
				}
				@Override public void mouseDragged(MouseEvent e) {}
			});
			
			// zoom with mouse
			view.addMouseWheelListener(new MouseWheelListener() {				
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					//log.debug("mouse wheel event: "+e.getWheelRotation()+" "+e.getScrollAmount());
					if (!Settings.isUiZoomWithMouse())
						return;
					
					// zoom 1x to nx
					double currentViewPercent = view.getCamera().getViewPercent();
					double newViewPercent = currentViewPercent;
					double temp = currentViewPercent + (e.getWheelRotation() * Settings.getUiZoomFactor());
					if ( temp > 0 && temp < 2 ) {
						newViewPercent = temp;
						//log.debug("current: "+currentViewPercent+" new: "+newViewPercent);
					}
					view.getCamera().setViewPercent(newViewPercent);
					
					// change camera view center
					double width = view.getSize().getWidth();
					double height = view.getSize().getHeight();
					double xCenter = width / 2;
					double yCenter = height  / 2;
					double x = e.getX();						
					double y = e.getY();
					double xFromCenter = x - xCenter;						
					double yFromCenter = -y + yCenter;
					
					double cameraWidth = view.getCamera().getMetrics().getSize().x();
					double cameraHeight = view.getCamera().getMetrics().getSize().y();
					double cameraRatioX = width / cameraWidth;
					double cameraRatioY = width / cameraHeight;
					/*log.debug(width+","+height);
					log.debug(Arrays.toString(view.getCamera().getMetrics().viewport));
					log.debug(xCenter+" x "+yCenter);
					log.debug(x+","+y);
					log.debug(xFromCenter+","+yFromCenter);
					log.debug(cameraWidth+","+cameraHeight);
					log.debug(cameraRatioX+","+cameraRatioY);*/
					
					view.getCamera().setViewCenter(xFromCenter/cameraRatioX, yFromCenter/cameraRatioY, 0);
				}
			});
		}
	}
}
