package io.nayasis.basica.file.handler.implement;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.exception.unchecked.BaseRuntimeException;
import io.nayasis.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.basica.model.NList;
import io.nayasis.basica.model.NMap;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ApachePoiReader {

	public NList readSheet( InputStream stream, boolean readHeader ) throws UncheckedIOException {
		NList[] list = new NList[1];
		read( stream, workbook -> {
			list[0] = read( workbook, firstSheetIndex(workbook), readHeader );
		});
		return list[0];
	}

	public NList readSheet( InputStream stream, String sheetName, boolean readHeader ) throws UncheckedIOException {
		NList[] list = new NList[1];
		read( stream, workbook -> {
			list[0] = read( workbook, getSheetNames(workbook).get(sheetName), readHeader );
		});
		return list[0];
	}

	private Integer firstSheetIndex( Workbook workbook ) {
		for( int i = 0, iCnt = workbook.getNumberOfSheets(); i < iCnt; i++ ) {
			Sheet sheet = workbook.getSheetAt(i);
			if( sheet != null ) return i;
		}
		return null;
	}

	public Map<String,NList> readAll( InputStream stream, boolean readHeader ) throws UncheckedIOException {
		Map<String,NList> result = new LinkedHashMap<>();
		read( stream, workbook -> {
			getSheetNames( workbook ).forEach( (name,index) -> {
				result.put( name, read(workbook,index,readHeader ) );
			});
		});
		return result;
	}

	private Map<String,Integer> getSheetNames( Workbook workbook ) {
		Map<String,Integer> names = new LinkedHashMap();
		for( int i = 0, iCnt = workbook.getNumberOfSheets(); i < iCnt; i++ ) {
			names.put( workbook.getSheetName(i), i );
		}
		return names;
	}

	private interface Reader {
		void read( Workbook workbook ) throws Exception;
	}

	private void read( InputStream stream, Reader reader ) throws BaseRuntimeException {
		try {
			@Cleanup Workbook workbook = WorkbookFactory.create( stream );
			reader.read( workbook );
		} catch( IOException e ) {
			throw new UncheckedIOException( e, "error on reading excel data." );
		} catch( IllegalArgumentException e ) {
			throw new UncheckedIOException( e, "invalid excel format." );
		} catch ( Exception e ) {
			throw new BaseRuntimeException( e );
		} finally {
			try { stream.close(); } catch( IOException e ) {}
		}
	}

	private NList read( Workbook workbook, Integer index, boolean readHeader ) {

		NList result = new NList();
		if( index == null ) return result;

		Sheet sheet = workbook.getSheetAt( index );
		if( sheet == null ) return result;

		Header header = getHeader( sheet, readHeader );
		result.addKey( header.body.values() );

		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		for( int i = header.has ? 1 : 0, iCnt = sheet.getPhysicalNumberOfRows(); i < iCnt; i++ ) {

			NMap data = new NMap();
			Row row = sheet.getRow( i );

			for( int c = 0; c < header.body.size(); c++ ) {

				Cell cell = row.getCell( c );
				if( cell == null ) continue;

				String key = Strings.nvl( header.body.get(c), c );
				data.put( key, getValue(cell,evaluator) );

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

	private Header getHeader( Sheet sheet, boolean readHeader ) {

		Header header = new Header();

		int count = getColumnCount( sheet );
		if( count == 0 ) return header;

    	Row row = sheet.getRow( 0 );

		if( row != null ) {
			try {
				for( int i = 0; i < count; i++ ) {
					Cell cell = row.getCell( i );
					header.body.put( i, readHeader ? cell.getStringCellValue() : Strings.nvl(i) );
				}
				header.has = true;
			} catch ( NullPointerException e ) {
				header.body.clear();
				for( int i = 0; i < count; i++ ) {
					header.body.put( i, Strings.nvl(i) );
				}
			}
		}

		return header;

    }

    private int getColumnCount( Sheet sheet ) {
		int max = 0;
		if( sheet != null ) {
			for( int i = 0; i < sheet.getPhysicalNumberOfRows(); i++ ) {
				Row row = sheet.getRow( i );
				max = Math.max( max, row.getLastCellNum() );
			}
		}
		return max;
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

    	if ( cell == null) return false;

        if ( ! DateUtil.isValidExcelDate(cell.getNumericCellValue()) ) return false;

        CellStyle style = cell.getCellStyle();
        if( style == null ) return false;

        int    formatIndex = style.getDataFormat();
        String format      = style.getDataFormatString();

        // Apache poi's missing logic
        format = format.replaceAll( "([^\\\\])\".*?[^\\\\]\"", "$1" );

        return DateUtil.isADateFormat( formatIndex, format) ;

    }

    private class Header {
		Map<Integer,String> body = new LinkedHashMap<>();
		boolean has = false;
	}

}