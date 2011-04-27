package ro.ulbsibiu.acaps.graph.ctg;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import ro.ulbsibiu.acaps.ctg.xml.ctg.CommunicatingTaskType;
import ro.ulbsibiu.acaps.ctg.xml.ctg.CommunicationType;
import ro.ulbsibiu.acaps.ctg.xml.ctg.CtgType;
import ro.ulbsibiu.acaps.ctg.xml.task.TaskType;

/**
 * {@link DefaultDirectedWeightedGraph} that represents the application
 * Communication Task Graph (CTG)
 * 
 * @author cipi
 * 
 */
public class CommunicationTaskGraph extends
		DefaultDirectedWeightedGraph<TaskVertex, CommunicationEdge> {

	/** auto generated serial version UID */
	private static final long serialVersionUID = 2541643584175328917L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(CommunicationTaskGraph.class);

	/** the path to the file that contains the CTG */
	private String ctgFilePath;

	/**
	 * Constructor
	 * 
	 * @param ctgFilePath
	 *            the path to the file that contains the CTG (cannot be empty)
	 */
	public CommunicationTaskGraph(String ctgFilePath) {
		super(CommunicationEdge.class);
		logger.assertLog(ctgFilePath != null && !ctgFilePath.isEmpty(), null);
		this.ctgFilePath = ctgFilePath;
		try {
			buildGraph();
		} catch (JAXBException e) {
			logger.error(e);
		}
	}
	
	public String getCtgName() {
		String ctgName = ctgFilePath.substring(0, ctgFilePath.lastIndexOf("/"));
		String second = ctgName.substring(ctgName.lastIndexOf("/") + 1);
		String first = ctgName.substring(0, ctgName.lastIndexOf("/"));
		first = first.substring(first.lastIndexOf("/") + 1);
		return first + "." + second;
	}

	private void buildGraph() throws JAXBException {
		File ctgFile = new File(ctgFilePath);

		JAXBContext jaxbContext = JAXBContext
				.newInstance("ro.ulbsibiu.acaps.ctg.xml.ctg");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		CtgType ctg = ((JAXBElement<CtgType>) unmarshaller.unmarshal(ctgFile))
				.getValue();

		List<CommunicationType> communicationList = ctg.getCommunication();
		for (int i = 0; i < communicationList.size(); i++) {
			CommunicationType communicationType = communicationList.get(i);
			CommunicatingTaskType source = communicationType.getSource();
			CommunicatingTaskType destination = communicationType
					.getDestination();
			double volume = communicationType.getVolume();

			TaskVertex sourceVertex = addVertex(ctgFile, source);
			TaskVertex destinationVertex = addVertex(ctgFile, destination);

			CommunicationEdge edge = addEdge(sourceVertex, destinationVertex);
			edge.setWeight(volume);
		}

	}

	public TaskVertex addVertex(File ctgFile, CommunicatingTaskType taskType)
			throws JAXBException {
		File sourceTaskFile = new File(ctgFile.getParent(), "tasks"
				+ File.separator + "task-" + taskType.getId() + ".xml");
		JAXBContext jaxbContext = JAXBContext
				.newInstance("ro.ulbsibiu.acaps.ctg.xml.task");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		TaskType task = ((JAXBElement<TaskType>) unmarshaller
				.unmarshal(sourceTaskFile)).getValue();
		TaskVertex vertex = new TaskVertex(task.getID());
		vertex.setName(task.getName());
		boolean success = addVertex(vertex);
		if (logger.isDebugEnabled()) {
			if (success) {
				logger.debug("Added vertex " + vertex);
			} else {
				logger.debug("Vertex " + vertex
						+ " already exists in the graph!");
			}
		}

		return vertex;
	}

}
