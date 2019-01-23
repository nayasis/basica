package io.nayasis.common.file.handler.implement;

import io.nayasis.common.base.Strings;
import io.nayasis.common.base.Types;
import io.nayasis.common.exception.unchecked.UncheckedIOException;
import io.nayasis.common.file.handler.ExcelHandler;
import io.nayasis.common.model.NList;
import io.nayasis.common.model.NMap;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.GREY_40_PERCENT;
import static org.apache.poi.ss.usermodel.CellType.*;

public class ExcelHandlerApachePoi extends ExcelHandler {

	@Override
	protected void writeNListTo(OutputStream outputStream, Map<String, NList> data, boolean isXlsx ) throws UncheckedIOException {

		Workbook workbook = isXlsx ? new XSSFWorkbook() : new HSSFWorkbook();

		try {
			for( String sheetName : data.keySet() ) {
				writeTo( workbook, sheetName, data.get( sheetName ) );
			}
			workbook.write( outputStream );
		} catch( IOException e ) {
			throw new UncheckedIOException( e );
		} finally {
			try { workbook.close(); } catch( IOException e ) {}
			try { if( outputStream != null ) outputStream.close(); } catch( IOException e ) {}
		}

	}

	private void writeTo( Workbook workbook, String sheetName, NList data ) {

		int idxColumn = 0, idxRow = 0;

		Sheet sheet = workbook.createSheet( sheetName );
		Row   row   = sheet.createRow( idxRow++ );

		CellStyle headerStyle = getHeaderStyle( workbook );

		for( String alias : data.getAliases() ) {
			Cell cell = row.createCell( idxColumn++ );
			cell.setCellValue( alias );
			cell.setCellStyle( headerStyle );
		}

		for( NMap nrow : data ) {

			row = sheet.createRow( idxRow++ );

			idxColumn = 0;

			for( Object key : data.keySet() ) {

				Object val = nrow.get( key );

				if( val == null ) {
					row.createCell( idxColumn++, CellType.STRING );
				} else if( Types.isNumeric( val ) ) {
					row.createCell( idxColumn++, NUMERIC ).setCellValue( Types.toDouble(nrow.get(key)) );
				} else if( Types.isBoolean( val) ) {
					row.createCell( idxColumn++, BOOLEAN ).setCellValue( (boolean) nrow.get( key ) );
				} else {
					row.createCell( idxColumn++, CellType.STRING ).setCellValue( toExcelText(nrow.get(key)) );
				}

			}

		}

	}

	private CellStyle getHeaderStyle( Workbook workbook ) {

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont  = workbook.createFont();

		headerFont.setBold( true );
		headerStyle.setFillBackgroundColor( GREY_40_PERCENT.getIndex() );
		headerStyle.setFont( headerFont );
		return headerStyle;

	}

	@Override
	public NList readFrom( InputStream inputStream, String sheetName ) throws UncheckedIOException {
		return readFrom( inputStream, ( workbook, result ) -> {
			result.put( sheetName, readFrom( workbook, workbook.getSheetIndex( sheetName ) ) );
		} ).get( sheetName );
	}

	@Override
	public NList readFirstSheetFrom( InputStream inputStream ) throws UncheckedIOException {
		Map<String, NList> sheets = readFrom( inputStream, ( workbook, result ) -> {
			Sheet sheet = workbook.getSheetAt( 0 );
			if( sheet != null ) {
				result.put( "FirstSheet", readFrom( workbook, 0 ) );
			}
		} );
		return sheets.get( "FirstSheet" );
	}

	@Override
	public Map<String, NList> readFrom( InputStream inputStream ) throws UncheckedIOException {
		return readFrom( inputStream, ( workbook, result ) -> {
			for( int sheetIndex = 0, limit = workbook.getNumberOfSheets(); sheetIndex < limit; sheetIndex++ ) {
				result.put( workbook.getSheetName( sheetIndex ), readFrom(workbook, sheetIndex) );
			}
		} );
	}

	private interface Reader {
		void read( Workbook workbook, Map<String, NList> result );
	}

	public Map<String, NList> readFrom( InputStream inputStream, Reader reader ) throws UncheckedIOException {

		Map<String, NList> result   = new LinkedHashMap<>();
		Workbook           workbook = null;

		try {
			workbook = WorkbookFactory.create( inputStream );
			reader.read( workbook, result );
		} catch( IOException e ) {
			throw new UncheckedIOException( e, "error on reading excel data." );
		} catch( IllegalArgumentException e ) {
			throw new UncheckedIOException( e, "invalid excel format." );
		} finally {
			if( workbook != null ) {
				try { workbook.close(); } catch( IOException e ) {}
			} else {
				try { inputStream.close(); } catch( IOException e ) {}
			}
		}

		return result;

	}

	private NList readFrom( Workbook workbook, int sheetIndex ) {

		NList result = new NList();

		Sheet sheet = workbook.getSheetAt( sheetIndex );

		if( sheet == null ) return result;

		NMap header = getExcelColumnHeader( sheet );

		if( header.size() == 0 ) return result;

		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		for( int idxRow = 1, maxRowCnt = sheet.getPhysicalNumberOfRows(); idxRow < maxRowCnt; idxRow++ ) {

			Row  row  = sheet.getRow( idxRow );
			NMap data = new NMap();

			for( int idxColumn = 0, maxColumnCnt = header.size(); idxColumn < maxColumnCnt; idxColumn++ ) {

				Cell cell = row.getCell( idxColumn );
				if( cell == null ) continue;

				String key = Strings.nvl( header.get(idxColumn) );
				data.put( key, getValue(cell, evaluator) );

			}

			result.addRow( data );

		}

		return result;

	}

	private Object getValue( Cell cell, FormulaEvaluator evaluator ) {

		switch( cell.getCellType() ) {
			case FORMULA :
				if( Strings.isNotEmpty( cell ) ) {
					switch( evaluator.evaluateFormulaCell(cell) ) {
						case NUMERIC :
							return getNumericCellValue( cell );
						case BOOLEAN :
							return cell.getBooleanCellValue();
						case STRING :
							return cell.getStringCellValue();
					}
				}
				return cell.getStringCellValue();
			case NUMERIC :
				return getNumericCellValue( cell );
			case BOOLEAN :
				return cell.getBooleanCellValue();
			default :
				return cell.getStringCellValue();

		}

	}

	private NMap getExcelColumnHeader( Sheet sheet ) {

    	NMap result = new NMap();

    	if( sheet == null || sheet.getPhysicalNumberOfRows() == 0 ) return result;

    	Row row = sheet.getRow( 0 );

		if( row == null ) {
			throw new IllegalArgumentException( String.format("No header in sheet(%s).", sheet.getSheetName()) );
		}

    	for( int i = 0, iCnt = row.getPhysicalNumberOfCells(); i < iCnt; i++ ) {
    		Cell cell = row.getCell( i );
    		result.put( i, cell.getStringCellValue() );
    	}

    	return result;

    }

    private Object getNumericCellValue( Cell cell ) {

		double val = cell.getNumericCellValue();

		if( isCellDateFormatted(cell) ) {
			String dateFormat = cell.getCellStyle().getDataFormatString();
			return new CellDateFormatter(dateFormat).format( HSSFDateUtil.getJavaDate(val) );

		} else {

			long fixedVal = (long) val;
			if( val - fixedVal == 0 ) {
				if( fixedVal < Integer.MAX_VALUE ) {
					return (int) fixedVal;
				} else {
					return fixedVal;
				}
			} else {
				return cell.getNumericCellValue();
			}
		}

    }

    private boolean isCellDateFormatted( Cell cell ) {

    	if (cell == null) return false;

        if ( ! DateUtil.isValidExcelDate(cell.getNumericCellValue()) ) return false;

        CellStyle style = cell.getCellStyle();
        if( style == null ) return false;

        int    formatIndex = style.getDataFormat();
        String format      = style.getDataFormatString();

        // Apache poi's missing logic
        format = format.replaceAll( "([^\\\\])\".*?[^\\\\]\"", "$1" );

        return DateUtil.isADateFormat( formatIndex, format) ;

    }

}
