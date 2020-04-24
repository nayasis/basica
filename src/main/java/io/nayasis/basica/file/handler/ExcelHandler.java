package io.nayasis.basica.file.handler;

import io.nayasis.basica.base.Types;
import io.nayasis.basica.exception.unchecked.JsonMappingException;
import io.nayasis.basica.file.Files;
import io.nayasis.basica.model.NList;
import io.nayasis.basica.model.NMap;
import io.nayasis.basica.reflection.Reflector;
import io.nayasis.basica.validation.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Excel Writer
 *
 * @author nayasis@gmail.com
 */
public abstract class ExcelHandler {

	private static final String DEFAULT_SHEET_NAME = "Sheet1";
	private static final int    MAX_TEXT_LENGTH = 32_707;

	/**
	 * Write excel data to output stream
	 *
	 * @param stream	output stream to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void write( OutputStream stream, String sheet, NList data ) throws UncheckedIOException {
		write( stream, sheet, data, true );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param stream	output stream to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data		grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void write( OutputStream stream, String sheet, Collection<?> data ) throws UncheckedIOException {
		write( stream, sheet, new NList(data) );
	}

	/**
	 * Write excel data to output stream in sheet named 'Sheet1'
	 *
	 * @param stream	output stream to write data
	 * @param data      grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void write( OutputStream stream, NList data ) throws UncheckedIOException {
		write( stream, DEFAULT_SHEET_NAME, data, true );
	}

	/**
	 * Write excel data to output stream in sheet named 'Sheet1'
	 *
	 * @param stream	output stream to write data
	 * @param data      grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void write( OutputStream stream, Collection data ) throws UncheckedIOException {
		write( stream, DEFAULT_SHEET_NAME, new NList(data), true );
	}

	private void write( OutputStream output, String sheetName, NList data, boolean isXlsx ) throws UncheckedIOException {
		if( output == null ) return;
		Map<String, NList> worksheets = new HashMap<>();
		worksheets.put( sheetName, data );
		writeNListTo( output, worksheets, isXlsx );
	}

	/**
	 * Write excel data to output stream
	 *
	 * @param stream	output stream to write data
	 * @param data      	key is sheetName and value is grid data.<br>
	 *                      value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void write( OutputStream stream, Map<String,?> data ) throws UncheckedIOException {
		writeNListTo( stream, toNList(data), true );
	}

	/**
	 *
	 * Write excel data to output stream
	 *
	 * @param outputStream	output stream to write data
	 * @param data      	key is sheetName and value is grid data.
	 * @param isXlsx		excel file type ( true : xlsx, false : xls )
	 * @throws UncheckedIOException file I/O exception
	 */
	protected abstract void writeNListTo( OutputStream outputStream, Map<String, NList> data, boolean isXlsx ) throws UncheckedIOException;

	private boolean isXlsx( File file ) {
		return "xlsx".equalsIgnoreCase( Files.getExtension(file) );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param file		excel file to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data		grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void write( File file, String sheet, NList data ) throws UncheckedIOException {
		write( file, outputStream -> write( outputStream, sheet, data, isXlsx(file) ) );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param file		excel file to write data
	 * @param sheet		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void write( File file, String sheet, Collection<?> data )  throws UncheckedIOException {
		write( file, sheet, new NList(data) );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param file excel file to write
	 * @param data  grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void write( File file, NList data ) throws UncheckedIOException {
		write( file, DEFAULT_SHEET_NAME, data );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param file excel file to write
	 * @param data  grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void write( File file, Collection<?> data ) throws UncheckedIOException {
		write( file, new NList(data) );
	}

	/**
	 * Write data to excel file
	 *
	 * @param file excel file to write data
	 * @param data      key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void write( File file, Map<String,?> data ) throws UncheckedIOException {
		write( file, outputStream -> writeNListTo( outputStream, toNList( data ), isXlsx( file ) ) );
	}

	/**
	 * Read sheet from excel file
	 *
	 * @param file		excel file to read
	 * @param sheet		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public NList read( File file, String sheet ) throws UncheckedIOException {
		return (NList) read( file, inputStream -> read( inputStream, sheet ) );
	}

	/**
	 * Read sheet from excel file
	 *
	 * @param file		excel file to read
	 * @param sheet		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> read( File file, String sheet, Class<T> toClass ) throws UncheckedIOException {
		NList list = read( file, sheet );
		return toBean( list, toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param file	excel file to read
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readSheet( File file ) throws UncheckedIOException {
		return (NList) read( file, inputStream -> readSheet( inputStream ) );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param file 	excel file to read
	 * @param type	generic type of list's class
	 * @param <T> 		expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readSheet( File file, Class<T> type ) throws UncheckedIOException {
		NList list = readSheet( file );
		return toBean( list, type );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param file excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
	public Map<String,NList> read( File file ) throws UncheckedIOException {
		return (Map<String,NList>) read( file, inputStream -> read( inputStream ) );
	}

	/**
	 * Read all sheet from excel file
	 *
	 * @param file 	excel file to read.
	 * @param toClass	generic type of list's class
	 * @param <T> 		expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> read( File file, Class<T> toClass ) throws UncheckedIOException {
		Map<String,NList> sheets = read( file );
		return toMap( sheets, toClass );
	}

	/**
	 * Read sheet from excel file
	 *
	 * @param stream	input stream to read data
	 * @param sheet	sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public abstract NList read( InputStream stream, String sheet ) throws UncheckedIOException;

	/**
	 * Read sheet from input stream
	 *
	 * @param stream		input stream to read data
	 * @param sheet		sheet name of excel file to read
	 * @param toClass	generic type of list's class
	 * @param <T> 		expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> read( InputStream stream, String sheet, Class<T> toClass ) throws UncheckedIOException {
		NList list = read( stream, sheet );
		return toBean( list, toClass );
	}

	/**
	 * Read first sheet from input stream
	 *
	 * @param stream	input stream to read data
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public abstract NList readSheet( InputStream stream ) throws UncheckedIOException;

	/**
	 * Read sheet from input stream
	 *
	 * @param stream	input stream to read data
	 * @param type	generic type of list's class
	 * @param <T> 		expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readSheet( InputStream stream, Class<T> type ) throws UncheckedIOException {
		NList list = readSheet( stream );
		return toBean( list, type );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param stream	input stream to read data
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
	public abstract Map<String,NList> read( InputStream stream ) throws UncheckedIOException;

	/**
	 * Read all sheet from input stream
	 *
	 * @param stream	input stream to read data
	 * @param type	generic type of list's class
	 * @param <T> 		expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> read( InputStream stream, Class<T> type ) throws UncheckedIOException {
		Map<String, NList> sheets = read( stream );
		return toMap( sheets, type );
	}

    protected String toExcelText( Object object ) {
        if( object == null ) return "";
        String txt = object.toString();
        if( txt.length() > MAX_TEXT_LENGTH ) {
            txt = txt.substring( 0, MAX_TEXT_LENGTH );
        }
        return txt;
    }

	private FileInputStream toInputStream( File file ) {
		try {
			return new FileInputStream( file );
		} catch( FileNotFoundException e ) {
			throw new UncheckedIOException( String.format("No file(%s) found to read.", file), e );
		}
	}

	private FileOutputStream toOutputStream( File file ) {
		try {
			Files.makeFile( file );
			return new FileOutputStream( file );
		} catch( FileNotFoundException e ) {
			throw new UncheckedIOException( String.format("No file(%s) found to write.", file), e );
		}
	}


	protected <T> List<T> toBean( NList nlist, Class<T> type ) throws JsonMappingException {
		List<T> list = new ArrayList<>();
		if( Validator.isNotEmpty(nlist) ) {
			for( NMap map : nlist ) {
				list.add( Reflector.toBeanFrom(map, type) );
			}
		}
		return list;
	}

	/**
	 * Convert data to NList
	 *
	 * @param data data for excel
	 * @return data as NList type
	 */
	public Map<String, NList> toNList( Map<String,?> data ) {

		Map<String, NList> sheets = new LinkedHashMap<>();

		if( Validator.isNotEmpty(data) ) {
			for( String sheetName : data.keySet() ) {
				Object sheet = data.get( sheetName );
				if( sheet == null ) continue;
				if( sheet instanceof NList ) {
					sheets.put( sheetName, (NList) sheet );
				} else if( sheet instanceof Collection ) {
					sheets.put( sheetName, new NList( (List<?>) sheet ) );
				} else if( Types.isArray( sheet ) || Types.isCollection( sheet ) ) {
					sheets.put( sheetName, new NList( Types.toList(sheet) ) );
				}
			}
		}

		return sheets;

	}

	/**
	 * Convert data to bean list
	 *
	 * @param data		data for excel
	 * @param toClass	generic type of list
	 * @param <T> 		expected class of return
	 * @return data as toClass generic type
	 */
	public <T> Map<String, List<T>> toMap( Map<String, NList> data, Class<T> toClass ) {
		Map<String, List<T>> sheets = new LinkedHashMap<>();
		if( Validator.isNotEmpty(data) ) {
			for( String sheet : data.keySet() ) {
				sheets.put( sheet, toBean(data.get( sheet ), toClass ) );
			}
		}
		return sheets;
	}


	private interface Reader {
		Object read( InputStream inputStream );
	}
	private interface Writer {
		void write( OutputStream outputStream );
	}

	private void write( File excel, Writer writer ) {
		FileOutputStream stream = toOutputStream( excel );
		try {
			writer.write( stream );
		} catch( UncheckedIOException e ) {
			Files.delete( excel );
			throw new UncheckedIOException( String.format("Error on writing excel file(%s)", excel),  e.getCause() );
		}

	}

	private Object read( File excelFile, Reader reader ) {
		FileInputStream stream = toInputStream( excelFile );
		try {
			return reader.read( stream );
		} catch( UncheckedIOException e ) {
			throw new UncheckedIOException( String.format("Error on reading excel file(%s)", excelFile),  e.getCause() );
		}
	}

}
