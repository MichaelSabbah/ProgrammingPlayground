package playground.logic.Entities.Element;

import java.io.Serializable;

//@Embeddable
public class ElementId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//@Column(name="playground", nullable=false)
	protected String playground;
	
	//@Column(name="id", nullable=false)
	protected int id;
	
	public ElementId() {}
	
	public ElementId(String playground, int id) {
		this.playground = playground;
		this.id = id;
	}

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
		ElementId other = (ElementId) obj;
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
