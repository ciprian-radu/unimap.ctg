package ro.ulbsibiu.acaps.viewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.tree.TreeNode;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;

import ro.ulbsibiu.acaps.ctg.xml.apcg.ApcgType;
import ro.ulbsibiu.acaps.ctg.xml.apcg.CoreType;
import ro.ulbsibiu.acaps.ctg.xml.apcg.TaskType;
import ro.ulbsibiu.acaps.ctg.xml.mapping.MapType;
import ro.ulbsibiu.acaps.ctg.xml.mapping.MappingType;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;

/**
 * {@link JGraphModelAdapter} for the Network-on-Chip topology
 * 
 * @author cipi
 * 
 */
public class NocJGraphModelAdapter extends JGraphModelAdapter<Object, Object> {

	/** autogenerated serial version UID */
	private static final long serialVersionUID = -7429662661307611647L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(NocJGraphModelAdapter.class);

	private File mappingXmlFile;

	public NocJGraphModelAdapter(Graph<Object, Object> jGraphTGraph,
			File mappingXmlFile) throws JAXBException {
		super(jGraphTGraph);
		this.mappingXmlFile = mappingXmlFile;
		logger.assertLog(mappingXmlFile != null, "Invalid mapping XML file!");

		addCoresToNodes();
	}

	private void addCoresToNodes() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext
				.newInstance("ro.ulbsibiu.acaps.ctg.xml.mapping");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		MappingType mappingType = ((JAXBElement<MappingType>) unmarshaller
				.unmarshal(mappingXmlFile)).getValue();
		logger.assertLog(mappingType != null, "No mapping provided!");
		List<MapType> mapList = mappingType.getMap();
		for (int i = 0; i < mapList.size(); i++) {
			MapType mapType = mapList.get(i);
			// add IP core to NoC node
			DefaultGraphCell cell = new DefaultGraphCell(mapType.getCore());
			getVertexCell(mapType.getNode()).add(cell);
			// add tasks to core
//			addTasksToCore(mapType, cell);
		}
	}

	private void addTasksToCore(MapType mapType, DefaultGraphCell cell)
			throws JAXBException {
		String apcgId = mapType.getApcg();
		logger.assertLog(apcgId != null && !apcgId.isEmpty(),
				"The APCG ID cannot be empty!");
		logger.assertLog(apcgId.contains("_"), "Invalid APCG ID: " + apcgId
				+ " doen't contain the '_' character");
		logger.debug("APCG ID: " + apcgId);
		String ctgId = apcgId.substring(0, apcgId.lastIndexOf("_"));
		logger.debug("CTG ID: " + ctgId);
		File ctgFile = new File(mappingXmlFile.getParent(), "ctg-" + ctgId
				+ ".xml");
		File apcgFile = new File(mappingXmlFile.getParent(), "apcg-" + apcgId
				+ ".xml");

		JAXBContext jaxbContext = JAXBContext
				.newInstance("ro.ulbsibiu.acaps.ctg.xml.apcg");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		ApcgType apcgType = ((JAXBElement<ApcgType>) unmarshaller
				.unmarshal(apcgFile)).getValue();
		List<CoreType> coreList = apcgType.getCore();
		for (int i = 0; i < coreList.size(); i++) {
			if (mapType.getCore().equals(coreList.get(i).getUid())) {
				List<TaskType> taskList = coreList.get(i).getTask();
				for (int j = 0; j < taskList.size(); j++) {
					File taskFile = new File(mappingXmlFile.getParent()
							+ File.separator + "tasks" + File.separator,
							"task-" + taskList.get(j).getId() + ".xml");

					jaxbContext = JAXBContext
							.newInstance("ro.ulbsibiu.acaps.ctg.xml.task");
					unmarshaller = jaxbContext.createUnmarshaller();
					@SuppressWarnings("unchecked")
					ro.ulbsibiu.acaps.ctg.xml.task.TaskType taskType = ((JAXBElement<ro.ulbsibiu.acaps.ctg.xml.task.TaskType>) unmarshaller
							.unmarshal(taskFile)).getValue();

					DefaultGraphCell taskCell = new DefaultGraphCell(
							taskType.getName());
					DefaultGraphCell nodeCell = getVertexCell(mapType
							.getNode());
					logger.assertLog(nodeCell.getChildCount() == 2, "Node " + mapType
							.getNode() + " doesn't have a core mapped to it!");
					// nodeCell.getChildAt(0) returns the default port
					DefaultGraphCell coreCell = (DefaultGraphCell) nodeCell.getChildAt(1);

					coreCell.add(taskCell);
					// FIXME add task communications
				}
			}
		}
	}

	@Override
	public AttributeMap getDefaultEdgeAttributes() {
		AttributeMap map = new AttributeMap();

		GraphConstants.setEndFill(map, true);
		GraphConstants.setLabelEnabled(map, false);

		GraphConstants.setForeground(map, Color.decode("#25507C"));
		// GraphConstants.setFont(map,
		// GraphConstants.DEFAULTFONT.deriveFont(Font.BOLD, 12));
		GraphConstants.setLineColor(map, Color.decode("#7AA1E6"));

		return map;
	}

	@Override
	public AttributeMap getDefaultVertexAttributes() {
		AttributeMap map = new AttributeMap();

		// we set the bounds only to force the layout manager to use minimum
		// size shapes
		// this way the auto sizing mechanism will immediately generate the
		// optimum sized shapes
		// TODO I am not sure how exactly setBounds(...) works with
		// setAutoSize(...)
		GraphConstants.setBounds(map, new Rectangle2D.Double(0, 0, 100, 100));
		GraphConstants.setAutoSize(map, true);
		GraphConstants.setBackground(map, Color.LIGHT_GRAY);
		GraphConstants.setForeground(map, Color.WHITE);
		GraphConstants.setOpaque(map, true);
		GraphConstants.setGroupOpaque(map, true);
		GraphConstants.setFont(map,
				GraphConstants.DEFAULTFONT.deriveFont(Font.PLAIN, 4));
		GraphConstants.setBorderColor(map, Color.LIGHT_GRAY);
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		GraphConstants.setVerticalAlignment(map, JLabel.BOTTOM);

		// we want to have the nodes represented as circles
		// but, the auto size property overrides this and
		// ellipses are created
		CellConstants.setVertexShape(map,
				MultiLineVertexRenderer.SHAPE_RECTANGLE);

		return map;
	}

}
