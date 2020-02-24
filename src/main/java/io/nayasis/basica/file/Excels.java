package io.nayasis.basica.file;

import io.nayasis.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.basica.file.handler.ExcelHandler;
import io.nayasis.basica.file.handler.implement.ExcelHandlerApachePoi;
import io.nayasis.basica.file.handler.implement.ExcelHandlerJxl;
import io.nayasis.basica.model.NList;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel Utility to read or write
 *
 * @author nayasis@gmail.com
 *
 */
@Slf4j
@UtilityClass
public class Excels {

	private ExcelHandler excelHandler = null;

	private ExcelHandler getHandler() {

		if( excelHandler != null ) return excelHandler;

		StringBuilder error = new StringBuilder();

		try {

			excelHandler = new ExcelHandlerApachePoi();
			log.info( "Excels use [Apache Poi Library]" );
			return excelHandler;

		} catch( Throwable e ) {
			error.append(
				"Excels can not use [Apache Poi Library] because it is not imported or re-imported.\n" +
				"\t- Maven dependency is like below.\n" +
				"\t\t<dependency>\n" +
				"\t\t  <groupId>org.apache.poi</groupId>\n" +
				"\t\t  <artifactId>poi-ooxml</artifactId>\n" +
				"\t\t  <version>4.1.2</version>\n" +
				"\t\t</dependency>\n"
			);
		}

		try {

			excelHandler = new ExcelHandlerJxl();
			log.info( "Excels use [JExcel Library]" );
			return excelHandler;

		} catch( Throwable e ) {
			error.append(
				"Excels can not use [JExcel Library] because it is not imported or re-imported.\n" +
				"\t- Maven dependency is like below.\n" +
				"\t\t<dependency>\n" +
				"\t\t  <groupId>net.sourceforge.jexcelapi</groupId>\n" +
				"\t\t  <artifactId>jxl</artifactId>\n" +
				"\t\t  <version>2.6.12</version>\n" +
				"\t\t</dependency>\n"
			);
		}

		if( excelHandler == null ) {
			NoClassDefFoundError throwable = new NoClassDefFoundError( "There is no excel libaray like ApachePoi or Jxl." );
			throwable.initCause( new LinkageError( error.toString() ) );
			throw throwable;
		}

		return excelHandler;

	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param sheets    key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excelFile, Map<String, ?> sheets ) throws UncheckedIOException {
		getHandler().writeTo( excelFile, sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( File excelFile, String sheetName, NList sheet ) throws UncheckedIOException {
		getHandler().writeTo( excelFile, sheetName, sheet );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( File excelFile, String sheetName, List<?> sheet ) throws UncheckedIOException {
		getHandler().writeTo( excelFile, sheetName, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excelFile, NList sheet ) throws UncheckedIOException {
		getHandler().writeTo( excelFile, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excelFile, List sheet ) throws UncheckedIOException {
		getHandler().writeTo( excelFile, sheet );
	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param sheets	key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( String excelFile, Map<String, ?> sheets ) throws UncheckedIOException {
	    writeTo( new File( excelFile ), sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( String excelFile, String sheetName, NList sheet ) throws UncheckedIOException {
		writeTo( new File( excelFile ), sheetName, sheet );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( String excelFile, String sheetName, List sheet ) throws UncheckedIOException {
		writeTo( new File( excelFile ), sheetName, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( String excelFile, NList sheet ) throws UncheckedIOException {
		writeTo( new File( excelFile ), sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( String excelFile, List sheet ) throws UncheckedIOException {
		writeTo( new File( excelFile ), sheet );
	}

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
    public NList readFrom( File excelFile, String sheetName ) throws UncheckedIOException {
    	return getHandler().readFrom( excelFile, sheetName );
    }

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFrom( File excelFile, String sheetName, Class<T> toClass ) throws UncheckedIOException {
		return getHandler().readFrom( excelFile, sheetName, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param excelFile excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
    public Map<String, NList> readFrom( File excelFile ) throws UncheckedIOException {
    	return getHandler().readFrom( excelFile );
    }

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( File excelFile, Class<T> toClass ) throws UncheckedIOException {
		return getHandler().readFrom( excelFile, toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile excel file to read
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readFirstSheetFrom( File excelFile ) throws UncheckedIOException {
		return getHandler().readFirstSheetFrom( excelFile );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFirstSheetFrom( File excelFile, Class<T> toClass ) throws UncheckedIOException {
		return getHandler().readFirstSheetFrom( excelFile, toClass );
	}

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
    public NList readFrom( String excelFile, String sheetName ) throws UncheckedIOException {
        return readFrom( new File(excelFile), sheetName );
    }

	/**
	 * Read sheet from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFrom( String excelFile, String sheetName, Class<T> toClass ) throws UncheckedIOException {
		return readFrom( new File(excelFile), sheetName, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param excelFile excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
    public Map<String, NList> readFrom( String excelFile ) throws UncheckedIOException {
    	return readFrom( new File(excelFile) );
    }

	/**
	 * Read all sheet from excel file
	 *
	 * @param excelFile excel file to read.
	 * @param toClass	generic type of list's class
	 * @param <T>		expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( String excelFile, Class<T> toClass ) throws UncheckedIOException {
		return readFrom( new File(excelFile), toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile excel file to read
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readFirstSheetFrom( String excelFile ) throws UncheckedIOException {
		return readFirstSheetFrom( new File( excelFile ) );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile 	excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFirstSheetFrom( String excelFile, Class<T> toClass ) throws UncheckedIOException {
		return readFirstSheetFrom( new File(excelFile), toClass );
	}

	/**
	 * Write data to excel file
	 *
	 * @param outputStream output stream to write data
	 * @param sheets      	key is sheetName and value is grid data.<br>
	 *                      value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream outputStream, Map<String, ?> sheets ) throws UncheckedIOException {
		getHandler().writeTo( outputStream, sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param outputStream 	output stream to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( OutputStream outputStream, String sheetName, NList sheet ) throws UncheckedIOException {
		getHandler().writeTo( outputStream, sheetName, sheet );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param outputStream 	output stream to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( OutputStream outputStream, String sheetName, List<?> sheet ) throws UncheckedIOException {
		getHandler().writeTo( outputStream, sheetName, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param outputStream	output stream to write data
	 * @param sheet		grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream outputStream, NList sheet ) throws UncheckedIOException {
		getHandler().writeTo( outputStream, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param outputStream	output stream to write data
	 * @param sheet			grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream outputStream, List<?> sheet ) throws UncheckedIOException {
		getHandler().writeTo( outputStream, sheet );
	}

	/**
	 * Read data from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public NList readFrom( InputStream inputStream, String sheetName ) throws UncheckedIOException {
		return getHandler().readFrom( inputStream, sheetName );
	}

	/**
	 * Read sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param sheetName		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T>			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFrom( InputStream inputStream, String sheetName, Class<T> toClass ) throws UncheckedIOException {
		return getHandler().readFrom( inputStream, sheetName, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
	public Map<String, NList> readFrom( InputStream inputStream ) throws UncheckedIOException {
		return getHandler().readFrom( inputStream );
	}

	/**
	 * Read all sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param toClass		generic type of list's class
	 * @param <T>			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( InputStream inputStream, Class<T> toClass ) throws UncheckedIOException {
		return getHandler().readFrom( inputStream, toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readFirstSheetFrom( InputStream inputStream ) throws UncheckedIOException {
		return getHandler().readFirstSheetFrom( inputStream );
	}

	/**
	 * Read sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFirstSheetFrom( InputStream inputStream, Class<T> toClass ) throws UncheckedIOException {
		return getHandler().readFirstSheetFrom( inputStream, toClass );
	}

	/**
	 * Convert data to NList
	 *
	 * @param data data for excel
	 * @return data as NList type
	 */
	public Map<String, NList> toNList( Map<String, ?> data ) {
		return getHandler().toNList( data );
	}

	/**
	 * Convert data to bean list
	 *
	 * @param data		data for excel
	 * @param toClass	generic type of list
	 * @param <T>		expected class of return
	 * @return data as toClass generic type
	 */
	public <T> Map<String, List<T>> toBeanList( Map<String, NList> data, Class<T> toClass ) {
		return getHandler().toBeanList( data, toClass );
	}

}
