package playground.logic.Entities.Activity;

import java.io.Serializable;

public class ActivityId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected String playground;
	protected int id;
	
	public ActivityId(String playground, int id) {
		this.playground = playground;
		this.id = id;
	}
	
	public ActivityId() {}

	
	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
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
		ActivityId other = (ActivityId) obj;
		if (id != other.id)
			return false;
		if (playground == null) {
			if (other.playground != null)
				return false;
		} else if (!playground.equals(other.playground))
			return false;
		return true;
	}
	
	
	
	
	
}
