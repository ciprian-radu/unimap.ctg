package ro.ulbsibiu.acaps.viewer;

import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import ro.ulbsibiu.acaps.graph.apcg.ApplicationCharacterizationGraph;
import ro.ulbsibiu.acaps.graph.ctg.CommunicationTaskGraph;
import ro.ulbsibiu.acaps.graph.noc.NocGraph;
import ro.ulbsibiu.acaps.viewer.apcg.ApcgViewer;
import ro.ulbsibiu.acaps.viewer.ctg.CtgViewer;
import ro.ulbsibiu.acaps.viewer.layout.JGraphLayoutPanel;
import ro.ulbsibiu.acaps.viewer.noc.NocViewer;

public class NocAppMapViewer {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(NocAppMapViewer.class);

	/**
	 * Constructor
	 * 
	 * @param graph
	 */
	public NocAppMapViewer() {
		;
	}

	public static void main(String[] args) throws FileNotFoundException,
			JAXBException {
		if (args == null || args.length < 4) {
			System.err
					.println("usage:   java NocAppMapViewer.class {CTG XML} {APCG XML} {mapping XML} {NoC topology}");
			System.err
					.println("example: java NocAppMapViewer.class ../CTG-XML/xml/VOPD/ctg-0/ctg-0.xml ../CTG-XML/xml/VOPD/ctg-0/apcg-0_m.xml ../CTG-XML/xml/VOPD/ctg-0/mapping-0_m_bb.xml ../NoC-XML/src/ro/ulbsibiu/acaps/noc/topology/mesh2D/4x4");
		} else {
			CommunicationTaskGraph ctgGraph = new CommunicationTaskGraph(
					args[0]);
			CtgViewer ctgViewer = new CtgViewer(ctgGraph);

			ApplicationCharacterizationGraph apcgGraph = new ApplicationCharacterizationGraph(
					args[1], args[0]);
			ApcgViewer apcgViewer = new ApcgViewer(apcgGraph, new File(args[1]));

			NocGraph nocGraph = new NocGraph(args[3]);
			int hSize = 0;
			try {
				String hSizeAsString = args[3].substring(0, args[3].lastIndexOf("x"));
				hSizeAsString = hSizeAsString.substring(hSizeAsString.lastIndexOf("/") + 1);
				hSize = Integer.valueOf(hSizeAsString);
			} catch (Exception e) {
				logger.fatal("Could not determine hSize! Stopping...", e);
				System.exit(0);
			}
			NocViewer nocViewer = new NocViewer(nocGraph, args[2], hSize);

			// Switch off D3D because of Sun XOR painting bug
			// See http://www.jgraph.com/forum/viewtopic.php?t=4066
			System.setProperty("sun.java2d.d3d", "false");
			JFrame frame = new JFrame(
					"Network-on-Chip Application Mapping Viewer (" + args[0]
							+ " | " + args[1] + " | " + args[2] + " | "
							+ args[3] + ")");

			final JGraphLayoutPanel ctgPanel = ctgViewer.initialize();
			final JGraphLayoutPanel apcgPanel = apcgViewer.initialize();
			final JGraphLayoutPanel nocPanel = nocViewer.initialize();

			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			JPanel topComponent = new JPanel(new GridLayout(1, 3));
			topComponent.add(new JLabel(args[0]));
			topComponent.add(new JLabel(args[1]));
			topComponent.add(
					new JLabel("<html><body>" + args[2] + "<br />" + args[3]
							+ "</body></html>"));
			splitPane.setTopComponent(topComponent);
			JPanel bottomComponent = new JPanel(new GridLayout(1, 3));
			bottomComponent.add(ctgPanel);
			bottomComponent.add(apcgPanel);
			bottomComponent.add(nocPanel);
			splitPane.setBottomComponent(bottomComponent);
			
			frame.getContentPane().add(splitPane);

			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					ctgPanel.reset();
					apcgPanel.reset();
					nocPanel.reset();
					super.componentResized(e);
				}

			});
			frame.pack();
			frame.setSize(800, 600);
			frame.setVisible(true);
		}
	}

}
