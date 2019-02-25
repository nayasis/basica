package io.nayasis.common.xml;

import io.nayasis.common.file.Files;
import io.nayasis.common.xml.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

@Slf4j
public class DeformedXmlTest {

	@Test
	public void read() {

		Xml xml = new XmlDeformed( new File(Files.getRootPath() + "/xml/Deformed.xml") );

		Assert.assertTrue( ! xml.toString().isEmpty() );

		Node sqlNode = xml.getRoot().getChildElement("sql");

		Assert.assertTrue( sqlNode.hasAttr( "pooled" ) );
		Assert.assertFalse( sqlNode.hasAttr( "pooledN" ) );

	}

}
