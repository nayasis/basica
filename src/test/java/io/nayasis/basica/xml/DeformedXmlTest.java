package io.nayasis.basica.xml;

import io.nayasis.basica.file.Files;
import io.nayasis.basica.xml.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
public class DeformedXmlTest {

	@Test
	public void read() {

		Xml xml = new XmlDeformed( new File(Files.getRootPath() + "/xml/Deformed.xml") );

		Assertions.assertTrue( ! xml.toString().isEmpty() );

		Node sqlNode = xml.getRoot().getChildElement("sql");

		Assertions.assertTrue( sqlNode.hasAttr( "pooled" ) );
		Assertions.assertFalse( sqlNode.hasAttr( "pooledN" ) );

	}

}
