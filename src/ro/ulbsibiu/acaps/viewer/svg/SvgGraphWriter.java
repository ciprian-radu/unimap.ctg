package ro.ulbsibiu.acaps.viewer.svg;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;

/**
 * Adapts the {@link com.jgraph.io.svg.SVGGraphWriter} for representing CTGs and
 * APCGs
 * 
 * @author cipi
 * 
 */
public class SvgGraphWriter extends com.jgraph.io.svg.SVGGraphWriter {

	public SvgGraphWriter() {
		super();
		edgeFactory = new SvgEdgeWriter();
		vertexFactory = new SvgVertexWriter();
	}

	@Override
	public Object[] getLabels(CellView view) {
		Object[] objectArray = new Object[0];
		AttributeMap attributes = view.getAllAttributes();
		if (attributes != null) {
			Object labelEnabled = attributes.get(GraphConstants.LABELENABLED);
			if (labelEnabled != null && labelEnabled.equals(Boolean.TRUE)) {
				objectArray = new Object[] { ((DefaultMutableTreeNode) view
						.getCell()).getUserObject() };
			}
		}
		return objectArray;
	}

}
