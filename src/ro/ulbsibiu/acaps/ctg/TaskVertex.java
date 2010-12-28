package ro.ulbsibiu.acaps.ctg;

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

	public TaskVertex(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		// return name + "(" + id + ")";
		return name;
	}

}