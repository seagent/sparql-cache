package test;

import java.io.Serializable;

import com.hp.hpl.jena.query.ResultSet;

public class SerializableResultset implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3824408403025399540L;
	private ResultSet resultSet;

	public SerializableResultset(ResultSet resultSet) {
		super();
		this.setResultSet(resultSet);
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

}
