package com.github.nayasis.basica.xml;

import com.github.nayasis.basica.file.Files;
import com.github.nayasis.basica.xml.Xml;
import com.github.nayasis.basica.xml.XmlDeformed;
import com.github.nayasis.basica.xml.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
public class DeformedXmlTest {

	@Test
	public void read() {

		Xml xml = new XmlDeformed( new File(Files.getRootPath(getClass()) + "/xml/Deformed.xml") );

		Assertions.assertTrue( ! xml.toString().isEmpty() );

		Node sqlNode = xml.getRoot().getChildElement("sql");

		Assertions.assertTrue( sqlNode.hasAttr( "pooled" ) );
		Assertions.assertFalse( sqlNode.hasAttr( "pooledN" ) );

	}

}
