package ro.ulbsibiu.acaps.viewer.apcg;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.VertexView;
import org.jgrapht.graph.AbstractGraph;

import ro.ulbsibiu.acaps.graph.apcg.ApplicationCharacterizationGraph;
import ro.ulbsibiu.acaps.graph.ctg.CommunicationEdge;
import ro.ulbsibiu.acaps.viewer.layout.JGraphLayoutPanel;

import com.jgraph.components.labels.MultiLineVertexRenderer;

/**
 * APplication Characterization Graph (APCG) viewer
 * 
 * @author cipi
 * 
 */
public class ApcgViewer {

	/** auto generated serial version UID */
	private static final long serialVersionUID = -6804689760104726630L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ApcgViewer.class);

	private ApcgJGraphModelAdapter jgAdapter;

	private AbstractGraph<Object, CommunicationEdge> graph;

	/** the APCG XMl file */
	private File apcgXmlFile;

	/**
	 * Constructor
	 * 
	 * @param graph
	 * @param apcgXmlFile
	 */
	public ApcgViewer(AbstractGraph<Object, CommunicationEdge> graph,
			File apcgXmlFile) {
		logger.assertLog(graph != null, null);
		logger.assertLog(apcgXmlFile != null, null);

		this.graph = graph;
		this.apcgXmlFile = apcgXmlFile;
	}

	public JGraphLayoutPanel initialize() throws JAXBException {
		// create a visualization using JGraph, via an adapter
		jgAdapter = new ApcgJGraphModelAdapter(graph, apcgXmlFile);
		JGraph jgraph = new JGraph(jgAdapter);

		// Overrides the global vertex renderer
		VertexView.renderer = new MultiLineVertexRenderer();
		// we hide the icon that indicates grouping (we create core - tasks groups)
		jgraph.putClientProperty(MultiLineVertexRenderer.CLIENTPROPERTY_SHOWFOLDINGICONS, Boolean.FALSE);

		JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(jgraph);

		return layoutPanel;
	}

	public static void main(String[] args) throws FileNotFoundException,
			JAXBException {
		if (args == null || args.length < 2) {
			System.err
					.println("usage:   java CtgViewer.class {CTG XML} {APCG XML}");
			System.err
					.println("example: java CtgViewer.class ../CTG-XML/xml/VOPD/ctg-0/ctg-0.xml ../CTG-XML/xml/VOPD/ctg-0/apcg-0_m.xml");
		} else {
			ApplicationCharacterizationGraph apcgGraph = new ApplicationCharacterizationGraph(args[1],
					args[0]);
			ApcgViewer app = new ApcgViewer(apcgGraph, new File(args[1]));

			// Switch off D3D because of Sun XOR painting bug
			// See http://www.jgraph.com/forum/viewtopic.php?t=4066
			System.setProperty("sun.java2d.d3d", "false");
			JFrame frame = new JFrame(
					"Application Characterization Graph Viewer (" + args[0]
							+ " | " + args[1] + ")");
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
