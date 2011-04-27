package ro.ulbsibiu.acaps.viewer.ctg;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.VertexView;
import org.jgrapht.graph.AbstractGraph;

import ro.ulbsibiu.acaps.graph.ctg.CommunicationEdge;
import ro.ulbsibiu.acaps.graph.ctg.CommunicationTaskGraph;
import ro.ulbsibiu.acaps.graph.ctg.TaskVertex;
import ro.ulbsibiu.acaps.viewer.layout.JGraphLayoutPanel;

import com.jgraph.components.labels.MultiLineVertexRenderer;

/**
 * Communication Task Graph viewer
 * 
 * @author cipi
 * 
 */
public class CtgViewer {

	/** auto generated serial version UID */
	private static final long serialVersionUID = -984434070656763839L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CtgViewer.class);

	private CtgJGraphModelAdapter jgAdapter;

	private AbstractGraph<TaskVertex, CommunicationEdge> graph;

	/**
	 * Constructor
	 * 
	 * @param graph
	 */
	public CtgViewer(AbstractGraph<TaskVertex, CommunicationEdge> graph) {
		logger.assertLog(graph != null, null);

		this.graph = graph;
	}

	public JGraphLayoutPanel initialize() throws JAXBException {
		// create a visualization using JGraph, via an adapter
		jgAdapter = new CtgJGraphModelAdapter(graph);
		JGraph jgraph = new JGraph(jgAdapter);

		// Overrides the global vertex renderer
		VertexView.renderer = new MultiLineVertexRenderer();

		JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(jgraph,
				((CommunicationTaskGraph) graph).getCtgName());

		return layoutPanel;
	}

	public static void main(String[] args) throws FileNotFoundException,
			JAXBException {
		if (args == null || args.length == 0) {
			System.err.println("usage:   java CtgViewer.class {CTG XML}");
			System.err
					.println("example: java CtgViewer.class ../CTG-XML/xml/VOPD/ctg-0/ctg-0.xml");
		} else {
			CommunicationTaskGraph ctgGraph = new CommunicationTaskGraph(
					args[0]);
			CtgViewer app = new CtgViewer(ctgGraph);

			// Switch off D3D because of Sun XOR painting bug
			// See http://www.jgraph.com/forum/viewtopic.php?t=4066
			System.setProperty("sun.java2d.d3d", "false");
			JFrame frame = new JFrame("Communication Task Graph Viewer ("
					+ args[0] + ")");
			final JGraphLayoutPanel layoutPanel = app.initialize();
			frame.getContentPane().add(layoutPanel);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					layoutPanel.reset();
					super.componentResized(e);
				}
				
			});
			frame.pack();
			frame.setSize(800, 600);
			frame.setVisible(true);
		}
	}

}
