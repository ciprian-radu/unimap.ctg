package ro.ulbsibiu.acaps.graph.apcg;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import ro.ulbsibiu.acaps.ctg.xml.apcg.ApcgType;
import ro.ulbsibiu.acaps.ctg.xml.apcg.CoreType;
import ro.ulbsibiu.acaps.ctg.xml.apcg.TaskType;
import ro.ulbsibiu.acaps.ctg.xml.ctg.CommunicatingTaskType;
import ro.ulbsibiu.acaps.ctg.xml.ctg.CommunicationType;
import ro.ulbsibiu.acaps.ctg.xml.ctg.CtgType;
import ro.ulbsibiu.acaps.graph.ctg.CommunicationEdge;
import ro.ulbsibiu.acaps.graph.ctg.TaskVertex;

/**
 * {@link DefaultDirectedWeightedGraph} that represents the APplication
 * Characterization Graph (APCG)
 * 
 * @author cipi
 * 
 */
public class ApplicationCharacterizationGraph extends
		DefaultDirectedWeightedGraph<Object, CommunicationEdge> {

	/** auto generated serial version UID */
	private static final long serialVersionUID = 2541643584175328917L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(ApplicationCharacterizationGraph.class);

	/** the path to the file that contains the APCG */
	private String apcgFilePath;

	/** the path to the file that contains the CTG */
	private String ctgFilePath;

	/**
	 * Constructor
	 * 
	 * @param apcgFilePath
	 *            the path to the file that contains the APCG (cannot be empty)
	 * @param ctgFilePath
	 *            the path to the file that contains the CTG (cannot be empty)
	 */
	public ApplicationCharacterizationGraph(String apcgFilePath,
			String ctgFilePath) {
		super(CommunicationEdge.class);
		logger.assertLog(apcgFilePath != null && !apcgFilePath.isEmpty(), null);
		this.apcgFilePath = apcgFilePath;
		logger.assertLog(ctgFilePath != null && !apcgFilePath.isEmpty(), null);
		this.ctgFilePath = ctgFilePath;
		try {
			buildGraph();
		} catch (JAXBException e) {
			logger.error(e);
		}
	}

	private void buildGraph() throws JAXBException {
		File apcgFile = new File(apcgFilePath);

		JAXBContext jaxbContext = JAXBContext
				.newInstance("ro.ulbsibiu.acaps.ctg.xml.apcg");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		ApcgType apcg = ((JAXBElement<ApcgType>) unmarshaller
				.unmarshal(apcgFile)).getValue();

		File ctgFile = new File(ctgFilePath);

		jaxbContext = JAXBContext.newInstance("ro.ulbsibiu.acaps.ctg.xml.ctg");
		unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		CtgType ctg = ((JAXBElement<CtgType>) unmarshaller.unmarshal(ctgFile))
				.getValue();

		List<CoreType> coreList = apcg.getCore();
		for (int i = 0; i < coreList.size(); i++) {
			CoreType coreType = coreList.get(i);
			List<TaskType> taskList = coreType.getTask();
			addVertex(coreType.getUid());
			for (int j = 0; j < taskList.size(); j++) {
				TaskType taskType = taskList.get(j);
				addTaskVertex(new File(ctgFilePath), taskType.getId());
			}
		}

		List<CommunicationType> communicationList = ctg.getCommunication();
		for (int i = 0; i < communicationList.size(); i++) {
			CommunicationType communicationType = communicationList.get(i);
			CommunicatingTaskType source = communicationType.getSource();
			CommunicatingTaskType destination = communicationType
					.getDestination();
			double volume = communicationType.getVolume();
			String sourceCore = getTaskCore(coreList, source.getId());
			String destinationCore = getTaskCore(coreList, destination.getId());
			// omit intra-core communications
			if (!sourceCore.equals(destinationCore)) {
				CommunicationEdge edge = addEdge(sourceCore, destinationCore);
				if (edge == null) {
					// there is already an edge from source core to destination core
					edge = getEdge(sourceCore, destinationCore);
					edge.setWeight(edge.getEdgeWeight() + volume);
				} else {
					edge.setWeight(volume);
				}
			}
		}

	}

	private String getTaskCore(List<CoreType> coreList, String taskId) {
		String uid = null;

		done: for (int i = 0; i < coreList.size(); i++) {
			CoreType coreType = coreList.get(i);
			List<TaskType> taskList = coreType.getTask();
			addVertex(coreType.getUid());
			for (int j = 0; j < taskList.size(); j++) {
				TaskType taskType = taskList.get(j);
				if (taskId.equals(taskType.getId())) {
					uid = coreType.getUid();
					break done;
				}
			}
		}

		return uid;
	}

	private TaskVertex addTaskVertex(File ctgFile, String taskId)
			throws JAXBException {
		File sourceTaskFile = new File(ctgFile.getParent(), "tasks"
				+ File.separator + "task-" + taskId + ".xml");
		JAXBContext jaxbContext = JAXBContext
				.newInstance("ro.ulbsibiu.acaps.ctg.xml.task");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		ro.ulbsibiu.acaps.ctg.xml.task.TaskType task = ((JAXBElement<ro.ulbsibiu.acaps.ctg.xml.task.TaskType>) unmarshaller
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
