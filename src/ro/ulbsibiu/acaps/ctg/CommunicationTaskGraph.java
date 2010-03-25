package ro.ulbsibiu.acaps.ctg;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

/**
 * @author Ciprian Radu
 *
 */
public class CommunicationTaskGraph extends DefaultDirectedWeightedGraph<Object, Object> {

	public CommunicationTaskGraph() {
		super(Object.class);
	}

}
