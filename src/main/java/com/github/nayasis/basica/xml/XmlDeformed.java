package com.github.nayasis.basica.xml;

import com.github.nayasis.basica.exception.unchecked.ParseException;
import com.github.nayasis.basica.exception.unchecked.UncheckedFileNotFoundException;
import com.github.nayasis.basica.exception.unchecked.UncheckedIOException;
import com.github.nayasis.basica.xml.reader.DeformedXmlReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public class XmlDeformed extends Xml {

	public XmlDeformed() {
		super();
	}

	public XmlDeformed( String xml ) throws ParseException, UncheckedIOException {
		super( xml, true );
	}

	public XmlDeformed( File file ) throws ParseException, UncheckedFileNotFoundException, UncheckedIOException {
		super( file, true );
	}

	public XmlDeformed( Path path ) throws ParseException, UncheckedFileNotFoundException, UncheckedIOException {
		super( path, true );
	}

	public XmlDeformed( URL url ) throws ParseException, UncheckedFileNotFoundException, UncheckedIOException {
		super( url, true );
	}

	public XmlDeformed( String xml, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
		super( xml, ignoreDtd );
	}

	public XmlDeformed( File file, boolean ignoreDtd ) throws ParseException, UncheckedFileNotFoundException, UncheckedIOException {
		super( file, ignoreDtd );
	}

	public XmlDeformed( Path path, boolean ignoreDtd ) throws ParseException, UncheckedFileNotFoundException, UncheckedIOException {
		super( path, ignoreDtd );
	}

	public XmlDeformed( URL url, boolean ignoreDtd ) throws ParseException, UncheckedFileNotFoundException, UncheckedIOException {
		super( url, ignoreDtd );
	}

    public XmlDeformed readFrom( String xml, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
  	    readXml( new DeformedXmlReader().readFrom(xml), ignoreDtd );
  	    return this;
    }

	public XmlDeformed readFrom( URL url, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
		readXml( new DeformedXmlReader().readFrom(url), ignoreDtd );
		return this;
	}

    public XmlDeformed readFrom( File file, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
    	readXml( new DeformedXmlReader().readFrom(file), ignoreDtd );
    	return this;
    }

	private void readXml( String xml, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
	    readFrom( new ByteArrayInputStream(xml.getBytes()), ignoreDtd );
    }

}
