package io.nayasis.basica.file.handler.implement;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.base.Types;
import io.nayasis.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.basica.model.NList;
import io.nayasis.basica.model.NMap;
import io.nayasis.basica.validation.Validator;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.GREY_40_PERCENT;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

@Slf4j
public class ApachePoiWriter {

	private static final int    MAX_TEXT_LENGTH    = 32_707;

	public void write( OutputStream stream, Map<String,?> map, String type, boolean writeHeader ) throws UncheckedIOException {
		try {
			@Cleanup Workbook workbook = createWorkbook( type );
			toNListMap(map).forEach( (name,data) -> {
				write( workbook, name, data, writeHeader );
			});
			workbook.write( stream );
		} catch( IOException e ) {
			throw new UncheckedIOException( e );
		} finally {
			try { if( stream != null ) stream.close(); } catch( IOException e ) {}
		}
	}

	public void writeSheet( OutputStream stream, Object data, String sheetName, String type, boolean writeHeader ) throws UncheckedIOException {
		try {
			@Cleanup Workbook workbook = createWorkbook( type );
			write( workbook, sheetName, toNList(data), writeHeader );
			workbook.write( stream );
		} catch( IOException e ) {
			throw new UncheckedIOException( e );
		} finally {
			try { if( stream != null ) stream.close(); } catch( IOException e ) {}
		}
	}

	private Workbook createWorkbook( String type ) {
		switch ( Strings.toLowerCase(type) ) {
			case "xlsx" :
				return new XSSFWorkbook();
			case "xls" :
				return new HSSFWorkbook();
			default :
				return new XSSFWorkbook();
		}
	}

	private NList toNList( Object data ) {
		if( data == null ) return new NList();
		if( data instanceof NList ) {
			return (NList) data;
		} else if( data instanceof Collection ) {
			return new NList( (Collection) data );
		} else if( Types.isArray(data) ) {
			return new NList( Types.toList(data) );
		}
		return new NList();
	}

	private Map<String,NList> toNListMap( Map<String,?> data ) {
		Map<String, NList> sheets = new LinkedHashMap<>();
		if( Validator.isNotEmpty(data) ) {
			data.forEach( (key,val) -> {
				sheets.put( key, toNList(val) );
			});
		}
		return sheets;
	}

	private void write( Workbook workbook, String sheetName, NList data, boolean writeHeader ) {

		int r = 0, c = 0;

		Sheet sheet = workbook.createSheet( sheetName );

		if( writeHeader ) {
			Row row = sheet.createRow( r++ );
			CellStyle style = getHeaderStyle( workbook );
			for( String alias : data.getAliases().values() ) {
				Cell cell = row.createCell( c++ );
				cell.setCellValue( alias );
				cell.setCellStyle( style );
			}
		}

		for( NMap map : data ) { c = 0;
			Row row = sheet.createRow( r++ );
			for( Object key : data.keySet() ) {
				Object val = map.get( key );
				if( val == null ) {
					row.createCell( c++ );
				} else if( Types.isNumeric( val ) ) {
					row.createCell( c++, NUMERIC ).setCellValue( Types.toDouble(map.get(key)) );
				} else if( Types.isBoolean( val) ) {
					row.createCell( c++, BOOLEAN ).setCellValue( (boolean) map.get( key ) );
				} else {
					row.createCell( c++, STRING ).setCellValue( toExcelText(map.get(key)) );
				}
			}
		}

	}

	protected String toExcelText( Object object ) {
		if( object == null ) return "";
		String txt = object.toString();
		if( txt.length() > MAX_TEXT_LENGTH ) {
			txt = txt.substring( 0, MAX_TEXT_LENGTH );
		}
		return txt;
	}

	private CellStyle getHeaderStyle( Workbook workbook ) {

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();

		headerFont.setBold( true );
		headerStyle.setFillBackgroundColor( GREY_40_PERCENT.getIndex() );
		headerStyle.setFont( headerFont );
		return headerStyle;

	}

}