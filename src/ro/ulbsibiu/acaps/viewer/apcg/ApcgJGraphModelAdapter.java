package ro.ulbsibiu.acaps.viewer.apcg;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.undo.UndoableEdit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;

import ro.ulbsibiu.acaps.ctg.xml.apcg.ApcgType;
import ro.ulbsibiu.acaps.ctg.xml.apcg.CoreType;
import ro.ulbsibiu.acaps.ctg.xml.apcg.TaskType;
import ro.ulbsibiu.acaps.ctg.xml.mapping.MapType;
import ro.ulbsibiu.acaps.ctg.xml.mapping.MappingType;
import ro.ulbsibiu.acaps.graph.ctg.CommunicationEdge;
import ro.ulbsibiu.acaps.graph.ctg.CommunicationTaskGraph;
import ro.ulbsibiu.acaps.graph.ctg.TaskVertex;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;

/**
 * {@link JGraphModelAdapter} for the APplication Characterization Graph
 * 
 * @see CommunicationTaskGraph
 * 
 * @author cipi
 * 
 */
public class ApcgJGraphModelAdapter extends
		JGraphModelAdapter<Object, CommunicationEdge> {

	/** auto generated serial version UID */
	private static final long serialVersionUID = 6771141909077600280L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(ApcgJGraphModelAdapter.class);

	/** the APCG XMl file */
	private File apcgXmlFile;

	/**
	 * Constructor
	 * 
	 * @param jGraphTGraph
	 * @param apcgXmlFile
	 * @throws JAXBException
	 */
	public ApcgJGraphModelAdapter(
			Graph<Object, CommunicationEdge> jGraphTGraph, File apcgXmlFile)
			throws JAXBException {
		super(jGraphTGraph);

		this.apcgXmlFile = apcgXmlFile;
		logger.assertLog(apcgXmlFile != null, "Invalid APCG XML file!");

		addTasksToCores(jGraphTGraph);
	}

	private void addTasksToCores(Graph<Object, CommunicationEdge> jGraphTGraph)
			throws JAXBException {
		JAXBContext jaxbContext = JAXBContext
				.newInstance("ro.ulbsibiu.acaps.ctg.xml.apcg");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		ApcgType apcgType = ((JAXBElement<ApcgType>) unmarshaller
				.unmarshal(apcgXmlFile)).getValue();
		logger.assertLog(apcgType != null, "No APCG provided!");

		List<CoreType> coreList = apcgType.getCore();
		for (int i = 0; i < coreList.size(); i++) {
			CoreType coreType = coreList.get(i);
			List<TaskType> taskList = coreType.getTask();
			DefaultGraphCell coreVertexCell = getVertexCell(coreType.getUid());
			for (int j = 0; j < taskList.size(); j++) {
				TaskType taskType = taskList.get(j);
				TaskVertex taskVertex = new TaskVertex(taskType.getId());
				DefaultGraphCell taskVertexCell = getVertexCell(taskVertex);
				coreVertexCell.add(taskVertexCell);
			}
		}
	}

	@Override
	public void insert(Object[] roots, Map attributes, ConnectionSet cs,
			ParentMap pm, UndoableEdit[] edits) {
		if (roots != null && roots.length == 1
				&& roots[0] instanceof DefaultGraphCell) {
			DefaultGraphCell cell = (DefaultGraphCell) roots[0];
			if (cell.getUserObject() instanceof TaskVertex) {
				attributes = new AttributeMap();
				attributes.put(cell, getDefaultTaskVertexAttributes());
			}
		}
		super.insert(roots, attributes, cs, pm, edits);
	}

	@Override
	public AttributeMap getDefaultEdgeAttributes() {
		AttributeMap map = new AttributeMap();

		GraphConstants.setEndFill(map, true);
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
		GraphConstants.setLabelEnabled(map, true);

		GraphConstants.setForeground(map, Color.decode("#25507C"));
		// GraphConstants.setFont(map,
		// GraphConstants.DEFAULTFONT.deriveFont(Font.BOLD, 12));
		GraphConstants.setLineColor(map, Color.decode("#7AA1E6"));

		GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);

		return map;
	}

	@Override
	public AttributeMap getDefaultVertexAttributes() {
		AttributeMap map = new AttributeMap();

		GraphConstants.setLabelEnabled(map, true);
		// we set the bounds only to force the layout manager to use minimum
		// size shapes
		// this way the auto sizing mechanism will immediately generate the
		// optimum sized shapes
		// TODO I am not sure how exactly setBounds(...) works with
		// setAutoSize(...)
		GraphConstants.setBounds(map, new Rectangle2D.Double(0, 0, 100, 100));
		GraphConstants.setAutoSize(map, true);
		GraphConstants.setBackground(map, Color.LIGHT_GRAY);
		GraphConstants.setForeground(map, Color.BLACK);
		GraphConstants.setOpaque(map, true);
		GraphConstants.setGroupOpaque(map, true);
		GraphConstants.setFont(map,
				GraphConstants.DEFAULTFONT.deriveFont(Font.BOLD, 30));
		GraphConstants.setBorderColor(map, Color.LIGHT_GRAY);
		GraphConstants.setHorizontalAlignment(map, JLabel.CENTER);
		GraphConstants.setVerticalAlignment(map, JLabel.CENTER);

		// we want to have the nodes represented as circles
		// but, the auto size property overrides this and
		// ellipses are created
		CellConstants.setVertexShape(map,
				MultiLineVertexRenderer.SHAPE_RECTANGLE);

		return map;
	}

	public AttributeMap getDefaultTaskVertexAttributes() {
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
		GraphConstants.setOpaque(map, false);
		GraphConstants.setGroupOpaque(map, false);
		GraphConstants.setFont(map,
				GraphConstants.DEFAULTFONT.deriveFont(Font.PLAIN, 20));
		GraphConstants.setBorderColor(map, Color.LIGHT_GRAY);
		GraphConstants.setHorizontalAlignment(map, JLabel.CENTER);
		GraphConstants.setVerticalAlignment(map, JLabel.BOTTOM);

		// we want to have the nodes represented as circles
		// but, the auto size property overrides this and
		// ellipses are created
		CellConstants.setVertexShape(map, MultiLineVertexRenderer.SHAPE_RECTANGLE);

		return map;
	}

}
