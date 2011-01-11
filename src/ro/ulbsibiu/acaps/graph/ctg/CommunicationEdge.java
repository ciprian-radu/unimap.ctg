package ro.ulbsibiu.acaps.graph.ctg;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * {@link DefaultWeightedEdge} which has the communication volume as weight.
 * 
 * @author cipi
 * 
 */
public class CommunicationEdge extends DefaultWeightedEdge {

	/** auto generated serial version UID */
	private static final long serialVersionUID = -1828082141276503376L;
	
	private double volume;

	public CommunicationEdge() {
		super();
	}

	public void setWeight(double volume) {
		this.volume = volume;
	}

	public double getEdgeWeight() {
		return volume;
	}
	
	@Override
	protected double getWeight() {
		return volume;
	}

	@Override
	public String toString() {
		return Long.toString((long)volume);
	}

}
