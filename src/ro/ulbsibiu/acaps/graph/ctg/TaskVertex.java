package ro.ulbsibiu.acaps.graph.ctg;

/**
 * Models the vertex from a {@link CommunicationTaskGraph}. The vertex is
 * identified by task ID and name. However, only the task name is printed by
 * {@link #toString()}
 * 
 * @author cipi
 * 
 */
public class TaskVertex {

	private String id;

	private String name;

	public TaskVertex(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskVertex other = (TaskVertex) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		// return name + "(" + id + ")";
		return name == null ? "" : name;
	}

}