package io.nayasis.common.reflection.mapper;

import org.nybatis.core.reflection.inspector.ColumnAnnotationInspector;


public class JsonSqlMapper extends JsonMapper {

	public JsonSqlMapper() {
		super();
		addCustomAnnotationIntrospectors();
	}

	private void addCustomAnnotationIntrospectors() {
		setAnnotationIntrospector(
			new ColumnAnnotationInspector()
		);
	}

}
