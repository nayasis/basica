package io.nayasis.common.file.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.nayasis.common.base.Types;
import io.nayasis.common.base.Validator;
import io.nayasis.common.exception.unchecked.JsonMappingException;
import io.nayasis.common.exception.unchecked.ParseException;
import io.nayasis.common.file.Files;
import io.nayasis.common.model.NList;
import io.nayasis.common.model.NMap;
import io.nayasis.common.reflection.mapper.NObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Abstract Excel Writer
 *
 * @author nayasis@gmail.com
 */
public abstract class ExcelHandler {

	private static final String DEFAULT_SHEET_NAME = "Sheet1";

	private static NObjectMapper excelMapper  = new NObjectMapper();

	/**
	 * Write excel data to output stream
	 *
	 * @param outputStream	output stream to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( OutputStream outputStream, String sheetName, NList data ) throws UncheckedIOException {
		writeTo( outputStream, sheetName, data, true );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param outputStream	output stream to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( OutputStream outputStream, String sheetName, List<?> data ) throws UncheckedIOException {
		writeTo( outputStream, sheetName, toExcelNListFromBean(data) );
	}

	/**
	 * Write excel data to output stream in sheet named 'Sheet1'
	 *
	 * @param outputStream	output stream to write data
	 * @param data      	grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream outputStream, NList data ) throws UncheckedIOException {
		writeTo( outputStream, DEFAULT_SHEET_NAME, data, true );
	}

	/**
	 * Write excel data to output stream in sheet named 'Sheet1'
	 *
	 * @param outputStream	output stream to write data
	 * @param data      	grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream outputStream, List<?> data ) throws UncheckedIOException {
		writeTo( outputStream, DEFAULT_SHEET_NAME, toExcelNListFromBean(data), true );
	}

	private void writeTo( OutputStream outputStream, String sheetName, NList data, boolean isXlsx ) throws UncheckedIOException {
		if( outputStream == null ) return;
		Map<String, NList> worksheets = new HashMap<>();
		worksheets.put( sheetName, data );
		writeNListTo( outputStream, worksheets, isXlsx );
	}

	/**
	 * Write excel data to output stream
	 *
	 * @param outputStream	output stream to write data
	 * @param data      	key is sheetName and value is grid data.<br>
	 *                      value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( OutputStream outputStream, Map<String, ?> data ) throws UncheckedIOException {
		writeNListTo( outputStream, toNList(data), true );
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
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( File excelFile, String sheetName, NList data ) throws UncheckedIOException {
		write( excelFile, outputStream -> writeTo( outputStream, sheetName, data, isXlsx(excelFile) ) );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param data			grid data
	 * @throws UncheckedIOException    File I/O Exception
	 */
	public void writeTo( File excelFile, String sheetName, List<?> data )  throws UncheckedIOException {
		writeTo( excelFile, sheetName, toExcelNListFromBean(data) );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param data      grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excelFile, NList data ) throws UncheckedIOException {
		writeTo( excelFile, DEFAULT_SHEET_NAME, data );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param data      grid data
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excelFile, List<?> data ) throws UncheckedIOException {
		writeTo( excelFile, toExcelNListFromBean(data) );
	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param data      key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws UncheckedIOException file I/O exception
	 */
	public void writeTo( File excelFile, Map<String, ?> data ) throws UncheckedIOException {
		write( excelFile, outputStream -> writeNListTo( outputStream, toNList( data ), isXlsx( excelFile ) ) );
	}

	/**
	 * Read sheet from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public NList readFrom( File excelFile, String sheetName ) throws UncheckedIOException {
		return (NList) read( excelFile, inputStream -> readFrom( inputStream, sheetName ) );
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
	public <T> List<T> readFrom( File excelFile, String sheetName, Class<T> toClass ) throws UncheckedIOException {
		NList list = readFrom( excelFile, sheetName );
		return toBeanFromExcelNList( list, toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile 	excel file to read
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public NList readFirstSheetFrom( File excelFile ) throws UncheckedIOException {
		return (NList) read( excelFile, inputStream -> readFirstSheetFrom( inputStream ) );
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
	public <T> List<T> readFirstSheetFrom( File excelFile, Class<T> toClass ) throws UncheckedIOException {
		NList list = readFirstSheetFrom( excelFile );
		return toBeanFromExcelNList( list, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param excelFile excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
	public Map<String, NList> readFrom( File excelFile ) throws UncheckedIOException {
		return (Map<String, NList>) read( excelFile, inputStream -> readFrom( inputStream ) );
	}

	/**
	 * Read all sheet from excel file
	 *
	 * @param excelFile 	excel file to read.
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( File excelFile, Class<T> toClass ) throws UncheckedIOException {
		Map<String, NList> sheets = readFrom( excelFile );
		return toBeanList( sheets, toClass );
	}

	/**
	 * Read sheet from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public abstract NList readFrom( InputStream inputStream, String sheetName ) throws UncheckedIOException;

	/**
	 * Read sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param sheetName		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> List<T> readFrom( InputStream inputStream, String sheetName, Class<T> toClass ) throws UncheckedIOException {
		NList list = readFrom( inputStream, sheetName );
		return toBeanFromExcelNList( list, toClass );
	}

	/**
	 * Read first sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @return grid data from first sheet
	 * @throws UncheckedIOException file I/O exception
	 */
	public abstract NList readFirstSheetFrom( InputStream inputStream ) throws UncheckedIOException;

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
		NList list = readFirstSheetFrom( inputStream );
		return toBeanFromExcelNList( list, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @return key is sheetName and value is grid data.
	 * @throws UncheckedIOException file I/O exception
	 */
	public abstract Map<String, NList> readFrom( InputStream inputStream ) throws UncheckedIOException;

	/**
	 * Read all sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param toClass		generic type of list's class
	 * @param <T> 			expected class of return
	 * @return grid data
	 * @throws UncheckedIOException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( InputStream inputStream, Class<T> toClass ) throws UncheckedIOException {
		Map<String, NList> sheets = readFrom( inputStream );
		return toBeanList( sheets, toClass );
	}

    protected String toExcelText( Object object ) {

        if( object == null ) return "";

        String txt = object.toString();

        if( txt.length() > 32_707 ) {
            txt = txt.substring( 0, 32_707 );
        }

        return txt;

    }

	private FileInputStream getInputStream( File excelFile ) {
		try {
			return new FileInputStream( excelFile );
		} catch( FileNotFoundException e ) {
			throw new UncheckedIOException( String.format("No file(%s) found to read.", excelFile), e );
		}
	}

	private FileOutputStream getOutputStream( File excelFile ) {
		try {
			Files.makeFile( excelFile );
			return new FileOutputStream( excelFile );
		} catch( FileNotFoundException e ) {
			throw new UncheckedIOException( String.format("No file(%s) found to write.", excelFile), e );
		}
	}


	protected NList toExcelNListFromBean( List<?> fromList ) throws JsonMappingException {

		NList result = new NList();

		if( hasRow(fromList) ) {
			ObjectWriter writer = excelMapper.writer();
			try {
				for( Object bean : fromList ) {
					result.addRow( writer.writeValueAsString(bean) );
				}
			} catch( JsonProcessingException e ) {
				throw new JsonMappingException( e );
			}
		}

		return result;

	}

	protected <T> List<T> toBeanFromExcelNList( NList fromList, Class<T> toClass ) throws JsonMappingException {

		List<T> list = new ArrayList<>();

		if( fromList == null || fromList.size() == 0 ) return list;

		for( NMap map : fromList ) {

			String json = map.toJson();

			try {

				T bean = excelMapper.readValue( json, toClass );
				list.add( bean );

			} catch( IOException e ) {
				throw new ParseException( e, "JsonParseException : {}\n\t- json string :\n{}\n\t- target class : {}", e.getMessage(), json, toClass );
			}

		}

		return list;

	}

	private boolean hasRow( List<?> list ) {
		return Validator.isNotEmpty( list ) && Types.isNotPrimitive( list.get(0) );
	}

	/**
	 * Convert data to NList
	 *
	 * @param data data for excel
	 * @return data as NList type
	 */
	public Map<String, NList> toNList( Map<String, ?> data ) {

		Map<String, NList> sheets = new LinkedHashMap<>();

		if( Validator.isNotEmpty(data) ) {
			for( String sheetName : data.keySet() ) {

				Object sheet = data.get( sheetName );

				if( sheet == null ) continue;

				if( sheet instanceof NList ) {
					sheets.put( sheetName, (NList) sheet );
				} else if( sheet instanceof List ) {
					sheets.put( sheetName, toExcelNListFromBean( (List<?>) sheet ) );
				} else if( Types.isArray( sheet ) || Types.isCollection( sheet ) ) {
					sheets.put( sheetName, toExcelNListFromBean( Types.toList(sheet) ) );
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
	public <T> Map<String, List<T>> toBeanList( Map<String, NList> data, Class<T> toClass ) {

		Map<String, List<T>> sheets = new LinkedHashMap<>();

		if( Validator.isNotEmpty(data) ) {
			for( String sheet : data.keySet() ) {
				sheets.put( sheet, toBeanFromExcelNList(data.get( sheet ), toClass ) );
			}
		}

		return sheets;

	}

	//----------- annonymous interface

	private Object read( File excelFile, Reader reader ) {

		FileInputStream inputStream = getInputStream( excelFile );

		try {
			return reader.read( inputStream );
		} catch( UncheckedIOException e ) {
			throw new UncheckedIOException( String.format("Error on reading excel file(%s)", excelFile),  e.getCause() );
		}

	}

	private interface Reader {
		Object read( InputStream inputStream );
	}

	private void write( File excelFile, Writer reader ) {

		FileOutputStream outputStream = getOutputStream( excelFile );

		try {
			reader.write( outputStream );
		} catch( UncheckedIOException e ) {
			Files.delete( excelFile );
			throw new UncheckedIOException( String.format("Error on writing excel file(%s)", excelFile),  e.getCause() );
		}

	}

	private interface Writer {
		void write( OutputStream outputStream );
	}


}
