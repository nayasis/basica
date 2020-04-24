package io.nayasis.basica.file;

import io.nayasis.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.basica.file.handler.ExcelHandler;
import io.nayasis.basica.file.handler.implement.ExcelHandlerApachePoi;
import io.nayasis.basica.model.NList;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
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

	private static final String ERROR_MESSAGE = "There is no dependency to Apache POI [org.apache.poi.poi-ooxml:4.1.2]";

	private ExcelHandler excelHandler = null;

	private boolean hasApachePoi = true;

	private ExcelHandler getHandler() throws LinkageError {

		if( excelHandler != null ) return excelHandler;

		if( hasApachePoi ) {
			try {
				excelHandler = new ExcelHandlerApachePoi();
				return excelHandler;
			} catch( Throwable e ) {
				hasApachePoi = false;
				throw new LinkageError( ERROR_MESSAGE, e );
			}
		} else {
			throw new LinkageError( ERROR_MESSAGE );
		}

	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param sheets    key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excelFile, Map<String,?> sheets ) throws UncheckedIOException {
		getHandler().write( excelFile, sheets );
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
		getHandler().write( excelFile, sheetName, sheet );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excel		excel file to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( File excel, String sheet, Collection<?> data ) throws UncheckedIOException {
		getHandler().write( excel, sheet, data );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excel excel file to write
	 * @param sheet grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excel, NList sheet ) throws UncheckedIOException {
		getHandler().write( excel, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excel excel file to write
	 * @param sheet grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excel, Collection sheet ) throws UncheckedIOException {
		getHandler().write( excel, sheet );
	}

	/**
	 * Write data to excel file
	 *
	 * @param file excel file to write data
	 * @param sheets	key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( String file, Map<String,?> sheets ) throws UncheckedIOException {
	    writeTo( new File( file ), sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param file		excel file to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( String file, String sheet, NList data ) throws UncheckedIOException {
		writeTo( new File( file ), sheet, data );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param file		excel file to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( String file, String sheet, List data ) throws UncheckedIOException {
		writeTo( new File(file), sheet, data );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param file excel file to write
	 * @param data grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( String file, NList data ) throws UncheckedIOException {
		writeTo( new File( file ), data );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param file excel file to write
	 * @param data grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( String file, Collection data ) throws UncheckedIOException {
		writeTo( new File( file ), data );
	}

	/**
	 * Read data from excel file
	 *
	 * @param file		excel file to read
	 * @param sheet		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
    public NList readFrom( File file, String sheet ) throws UncheckedIOException {
    	return getHandler().read( file, sheet );
    }

	/**
	 * Read data from excel file
	 *
	 * @param file		excel file to read
	 * @param sheet		sheet name of excel file to read
	 * @param type		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFrom( File file, String sheet, Class<T> type ) throws UncheckedIOException {
		return getHandler().read( file, sheet, type );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param file excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
    public Map<String, NList> readFrom( File file ) throws UncheckedIOException {
    	return getHandler().read( file );
    }

	/**
	 * Read data from excel file
	 *
	 * @param file		excel file to read
	 * @param type		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( File file, Class<T> type ) throws UncheckedIOException {
		return getHandler().read( file, type );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param file excel file to read
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readFirstSheetFrom( File file ) throws UncheckedIOException {
		return getHandler().readSheet( file );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param file		excel file to read
	 * @param type		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFirstSheetFrom( File file, Class<T> type ) throws UncheckedIOException {
		return getHandler().readSheet( file, type );
	}

	/**
	 * Read data from excel file
	 *
	 * @param file		excel file to read
	 * @param sheet		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
    public NList readFrom( String file, String sheet ) throws UncheckedIOException {
        return readFrom( new File(file), sheet );
    }

	/**
	 * Read sheet from excel file
	 *
	 * @param file		excel file to read
	 * @param sheet		sheet name of excel file to read
	 * @param type		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFrom( String file, String sheet, Class<T> type ) throws UncheckedIOException {
		return readFrom( new File(file), sheet, type );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param file excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
    public Map<String, NList> readFrom( String file ) throws UncheckedIOException {
    	return readFrom( new File(file) );
    }

	/**
	 * Read all sheet from excel file
	 *
	 * @param file excel file to read.
	 * @param type	generic type of list's class
	 * @param <T>		expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( String file, Class<T> type ) throws UncheckedIOException {
		return readFrom( new File(file), type );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param file excel file to read
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readFirstSheetFrom( String file ) throws UncheckedIOException {
		return readFirstSheetFrom( new File( file ) );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param file 	excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFirstSheetFrom( String file, Class<T> toClass ) throws UncheckedIOException {
		return readFirstSheetFrom( new File(file), toClass );
	}

	/**
	 * Write data to excel file
	 *
	 * @param stream output stream to write data
	 * @param sheets      	key is sheetName and value is grid data.<br>
	 *                      value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream stream, Map<String,?> sheets ) throws UncheckedIOException {
		getHandler().write( stream, sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param stream 	output stream to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( OutputStream stream, String sheet, NList data ) throws UncheckedIOException {
		getHandler().write( stream, sheet, data );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param stream 	output stream to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( OutputStream stream, String sheet, List<?> data ) throws UncheckedIOException {
		getHandler().write( stream, sheet, data );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param stream	output stream to write data
	 * @param data		grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream stream, NList data ) throws UncheckedIOException {
		getHandler().write( stream, data );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param stream	output stream to write data
	 * @param data			grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream stream, Collection<?> data ) throws UncheckedIOException {
		getHandler().write( stream, data );
	}

	/**
	 * Read data from excel file
	 *
	 * @param stream	input stream to read data
	 * @param sheet		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public NList readFrom( InputStream stream, String sheet ) throws UncheckedIOException {
		return getHandler().read( stream, sheet );
	}

	/**
	 * Read sheet from input stream
	 *
	 * @param stream	input stream to read data
	 * @param sheet		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T>			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFrom( InputStream stream, String sheet, Class<T> toClass ) throws UncheckedIOException {
		return getHandler().read( stream, sheet, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param stream	input stream to read data
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
	public Map<String, NList> readFrom( InputStream stream ) throws UncheckedIOException {
		return getHandler().read( stream );
	}

	/**
	 * Read all sheet from input stream
	 *
	 * @param stream	input stream to read data
	 * @param type		generic type of list's class
	 * @param <T>			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( InputStream stream, Class<T> type ) throws UncheckedIOException {
		return getHandler().read( stream, type );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param stream	input stream to read data
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readSheetFrom( InputStream stream ) throws UncheckedIOException {
		return getHandler().readSheet( stream );
	}

	/**
	 * Read sheet from input stream
	 *
	 * @param stream	input stream to read data
	 * @param type		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readSheetFrom( InputStream stream, Class<T> type ) throws UncheckedIOException {
		return getHandler().readSheet( stream, type );
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
	public <T> Map<String, List<T>> toMap( Map<String, NList> data, Class<T> toClass ) {
		return getHandler().toMap( data, toClass );
	}

}
