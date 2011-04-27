package ro.ulbsibiu.acaps.viewer.ctg;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
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
 * {@link JGraphModelAdapter} for the Communication Task Graph
 * 
 * @see CommunicationTaskGraph
 * 
 * @author cipi
 * 
 */
public class CtgJGraphModelAdapter extends JGraphModelAdapter<TaskVertex, CommunicationEdge> {

	/** auto generated serial version UID */
	private static final long serialVersionUID = 7290423034625888925L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(CtgJGraphModelAdapter.class);

	public CtgJGraphModelAdapter(Graph<TaskVertex, CommunicationEdge> jGraphTGraph) throws JAXBException {
		super(jGraphTGraph);
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
//		GraphConstants.setFont(map,
//				GraphConstants.DEFAULTFONT.deriveFont(Font.PLAIN, 4));
		GraphConstants.setBorderColor(map, Color.LIGHT_GRAY);
		GraphConstants.setHorizontalAlignment(map, JLabel.CENTER);
		GraphConstants.setVerticalAlignment(map, JLabel.CENTER);

		// we want to have the nodes represented as circles
		// but, the auto size property overrides this and
		// ellipses are created
		CellConstants.setVertexShape(map,
				MultiLineVertexRenderer.SHAPE_CIRCLE);

		return map;
	}

}
