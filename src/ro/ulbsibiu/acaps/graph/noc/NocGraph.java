package ro.ulbsibiu.acaps.graph.noc;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleWeightedGraph;

import ro.ulbsibiu.acaps.noc.xml.link.LinkType;
import ro.ulbsibiu.acaps.noc.xml.node.NodeType;

/**
 * The Network-on-Chip topology graph
 * 
 * @author cipi
 * 
 */
public class NocGraph extends SimpleWeightedGraph<Object, Object> {

	/** auto generated serial version UID */
	private static final long serialVersionUID = -8077128461035688989L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(NocGraph.class);

	/** the path to the file that contains the NoC topology */
	private String nocTopologyFilePath;
	
	private int[] nodes;

	/**
	 * Constructor
	 * 
	 * @param nocTopologyFilePath
	 *            the path to the file that contains the NoC topology (cannot be
	 *            empty)
	 */
	public NocGraph(String nocTopologyFilePath) {
		super(Object.class);
		logger.assertLog(
				nocTopologyFilePath != null && !nocTopologyFilePath.isEmpty(),
				null);
		this.nocTopologyFilePath = nocTopologyFilePath;
		try {
			buildGraph();
		} catch (JAXBException e) {
			logger.error(e);
		}
	}

	private void buildGraph() throws JAXBException {
		// initialize nodes
		File nodesDir = new File(nocTopologyFilePath, "nodes");
		logger.assertLog(nodesDir.isDirectory(), nodesDir.getName()
				+ " is not a directory!");
		File[] nodeXmls = nodesDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".xml");
			}
		});
		logger.debug("Found " + nodeXmls.length + " nodes");
		nodes = new int[nodeXmls.length];
		for (int i = 0; i < nodeXmls.length; i++) {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("ro.ulbsibiu.acaps.noc.xml.node");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			@SuppressWarnings("unchecked")
			NodeType node = ((JAXBElement<NodeType>) unmarshaller
					.unmarshal(nodeXmls[i])).getValue();

			nodes[i] = Integer.valueOf(node.getId());
		}
		Arrays.sort(nodes);
		for (int i = 0; i < nodes.length; i++) {
			addVertex(Integer.toString(nodes[i]));
		}
		
		// initialize links
		File linksDir = new File(nocTopologyFilePath, "links");
		logger.assertLog(linksDir.isDirectory(), linksDir.getName()
				+ " is not a directory!");
		File[] linkXmls = linksDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".xml");
			}
		});
		logger.debug("Found " + linkXmls.length + " links");
		for (int i = 0; i < linkXmls.length; i++) {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("ro.ulbsibiu.acaps.noc.xml.link");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			@SuppressWarnings("unchecked")
			LinkType link = ((JAXBElement<LinkType>) unmarshaller
					.unmarshal(linkXmls[i])).getValue();
			addEdge(link.getFirstNode(), link.getSecondNode(), link.getId());
		}

	}
}
