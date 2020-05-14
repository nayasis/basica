package com.github.nayasis.basica.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.nayasis.basica.reflection.Reflector;
import com.github.nayasis.basica.reflection.serializer.SimpleNListSerializer;
import com.github.nayasis.basica.validation.Assert;
import com.github.nayasis.basica.validation.Validator;
import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.base.Types;
import com.github.nayasis.basica.exception.unchecked.JsonMappingException;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Multiple data structure consisted with NMap
 *
 * @author nayasis@gmail.com
 */
@JsonSerialize( using = SimpleNListSerializer.class )
public class NList implements Serializable, Cloneable, Iterable<NMap> {

    private static final long serialVersionUID = 7907985681932632424L;

    protected Map<Object,Integer> header = new LinkedHashMap<>();
    protected Map<Object,String>  alias  = new LinkedHashMap<>();
    protected List<NMap>          body   = new ArrayList<>();

    /**
     * default constructor
     */
    public NList() {}

    /**
     * constructor
     *
     * @param json json text
     */
    public NList( String json ) {
        addRow( -1, json, false );
        refreshKey();
    }

    /**
     * constructor
     *
     * @param nlist  NList data
     */
    public NList( NList nlist ) {
        if( nlist == null || nlist.size() == 0 ) return;
    	body.addAll( nlist.body );
        header.putAll( nlist.header );
        alias.putAll( nlist.alias );
    }

    /**
     * constructor
     *
     * @param list   list
     */
    public NList( Collection list ) {
    	this( list, null );
    }

    /**
     * constructor
     *
     * @param list      list
     * @param header    specific header
     */
    public NList( Collection list, Set<?> header ) {

        boolean hasHeader = Validator.isNotEmpty( header );

        addRows( -1, list, ! hasHeader );

        if( hasHeader ) {
            for( Object key : header )
                this.header.put( key, list.size() );
        }

    }

    /**
     * set alias corresponding to header key
     * @param key       header key
     * @param alias     alias
     * @return self instance
     */
    public NList setAlias( String key, String alias ) {
        if( header.containsKey(key) ) {
            this.alias.put( key, alias );
        }
        return this;
    }

    /**
     * get alias corresponding key
     * @param key   column key
     * @return alias
     */
    public String getAlias( Object key ) {
        if( ! containsKey(key) ) return null;
        return Strings.nvl( alias.get(key), key );
    }

    /**
     * get all alias
     *
     * @return all aliases.
     */
    public List<String> getAliases() {
        List<String> list = new ArrayList<>();
    	for( Object key : header.keySet() ) {
    		list.add( getAlias(key) );
    	}
    	return list;
    }

    /**
     * check if data has any alias.
     *
     * @return true if data has any alias.
     */
    public boolean hasAlias() {
        return ! alias.isEmpty();
    }

    /**
     * add key in header
     * @param key key to add
     * @return self instance
     */
    public NList addKey( Object... key ) {
        for( Object val : key ) {
            if( val == null ) continue;
            if( val instanceof Collection ) {
                addKey( (Collection) val );
            } else if( Types.isArray(val) ) {
                addKey( Types.toList(val)  );
            } else {
                if( ! containsKey(val) ) {
                    this.header.put( val, 0 );
                }
            }
        }
        return this;
    }

    /**
     * add key in header
     *
     * @param keys  keys
     * @return  self instance
     */
    @SuppressWarnings("unchecked")
    public NList addKey( Collection keys ) {
        if( Validator.isNotEmpty(keys) ) {
            keys.forEach( key -> addKey(key) );
        }
        return this;
    }

    /**
     * get key by given column index
     *
     * @param column column index
     * @return key
     */
    public Object getKey( int column ) {
        if( 0 > column || column >= keySize() )
            throw new IndexOutOfBoundsException( String.format( "Header index[%d] is out of bounds from 0 to %d", column, keySize() ) );
        Iterator<Object> iterator = header.keySet().iterator();
        for( int i = 0; i < column; i++ )
            iterator.next();
        return iterator.next();
    }

    /**
     * get key size
     *
     * @return size of key
     */
    public int keySize() {
        return header.size();
    }

    /**
     * get key set
     * @return key set
     */
    public Set<Object> keySet() {
        return header.keySet();
    }

    /**
     * Refresh Header and Key information
     *
     * @return self instance
     */
    public NList refreshKey() {

        Map<Object,Integer> currHeader = new LinkedHashMap<>();

        // read last index from bottom
        for( int i = body.size() - 1; i >=0; i-- ) {
            for( Object key : body.get(i).keySet() ) {
                if( currHeader.containsKey(key) ) continue;
                currHeader.put( key, i + 1 );
            }
        }

        // fill in previous order (and remove it from current currHeader)
        Map<Object,Integer> buffer = new LinkedHashMap<>();
        for( Object key : header.keySet() ) {
            if( currHeader.containsKey(key) ) {
                buffer.put( key, currHeader.get(key) );
                currHeader.remove(key);
            } else {
                buffer.put( key, 0 );
            }
        }

        // fill the rest in current currHeader
        for( Object key : currHeader.keySet() ) {
            buffer.put( key, currHeader.get(key) );
        }

        // swap
        header.clear();
        header.putAll( buffer );

        return this;

    }

    /**
     * append data
     *
     * @param key   key
     * @param value value
     * @return self instance
     */
    @SuppressWarnings("unchecked")
    public NList addData( Object key, Object value ) {

    	int dataSize  = size( key );
    	int totalSize = size();

        if( totalSize == dataSize ) {
    		NMap row = new NMap();
    		row.put( key, value );
    		body.add( row );

        } else {
    		body.get( dataSize ).put( key, value );
        }

        header.put( key, ++dataSize );

        return this;

    }

    /**
     * add row
     *
     * <pre>
     * {@link NList} data = new {@link NList};
     *
     * data.add( "{key:'1', val:'AAA'}" );
     * </pre>
     *
     * @param value row data (Bean, Map, JSON text)
     * @return self instance
     */
    public NList addRow( Object value ) {
        addRow( -1, value, true );
        return this;
    }

    /**
     * add row
     *
     * <pre>
     * {@link NList} data = new {@link NList};
     *
     * data.add( 0, "{key:'1', val:'AAA'}" );
     * </pre>
     *
     * @param index index at which to insert the first element from collection
     * @param value row data (Bean, Map, JSON text)
     * @return self instance
     */
    public NList addRow( int index, Object value ) {
        addRow( index, value, true );
        return this;
    }

    private void addRow( int index, Object value, boolean synchronizeHeader ) {

        if( value == null ) return;

        if( value instanceof NMap ) {
            addRowFromNMap( index, (NMap) value, synchronizeHeader );

        } else if( value instanceof Map ) {
            addRowFromNMap( index, new NMap( value ), synchronizeHeader );

        } else if( value instanceof Collection ) {
            addRows( index, (Collection) value, false );
            if( synchronizeHeader )
                refreshKey();

        } else if( value instanceof NList ) {
            addRows( index, (NList) value );

        } else if( Types.isArray(value) ) {
            addRows( index, Types.toList( value ), false );
            if( synchronizeHeader )
                refreshKey();

        } else if( Types.isStringLike( value ) ) {
            String json = value.toString();
            try {
                addRows( index, Reflector.toListFrom(json), false );
                if( synchronizeHeader ) {
                    refreshKey();
                }
            } catch( JsonMappingException e ) {
                addRow( index, Reflector.toMapFrom(json), synchronizeHeader );
            }
        } else {
            addRow( index, new NMap( value ), synchronizeHeader );
        }

    }

    private void addRowFromNMap( int index, NMap data, boolean synchronizeHeader ) {

        if( index < 0 ) {
            body.add( data );
        } else {
            body.add( index, data );
        }

        if( synchronizeHeader ) {
            int size = body.size();
            for( Object key : data.keySet() ) {
                header.put( key, size );
            }
        }

    }

    /**
     * add rows from another NList data
     *
     * @param nlist NList data
     * @return self instance
     */
    public NList addRows( NList nlist ) {
        addRows( -1, nlist );
        return this;
    }

    /**
     * add rows from another NList data
     *
     * @param index index at which to insert the first element from collection
     * @param nlist data
     * @return self instance
     */
    public NList addRows( int index, NList nlist ) {

        if( nlist == null ) return this;

        if( index < 0 ) {
            body.addAll( nlist.body );
        } else {
            body.addAll( index, nlist.body );
        }

        for( Object key : nlist.header.keySet() ) {
            if( header.containsKey(key) ) {
                header.put( key, header.get(key) + nlist.header.get(key) );
            } else {
                header.put( key, nlist.header.get(key) );
            }
        }

        return this;

    }

    /**
     * add rows from another List contains Map or Bean or JSON text.
     *
     * @param list List data
     * @return self instance
     */
    public NList addRows( Collection list ) {
        addRows( -1, list, true );
        return this;
	}

    /**
     * add rows from another List contains Map or Bean or JSON text.
     *
     * @param index index at which to insert the first element from collection
     * @param list List data
     * @return self instance
     */
    public NList addRows( int index, Collection list ) {
        addRows( index, list, true );
        return this;
    }

    private void addRows( int index, Collection list, boolean synchronizeHeader ) {
        if( list != null ) {
            for( Object e : list ) {
                addRow( index, e, synchronizeHeader );
            }
        }
    }

    /**
     * get data size of header key
     *
     * @param key header key
     * @return data size
     */
    public int size( Object key ) {
    	return Validator.nvl( header.get(key), 0 );
    }

    /**
     * get total row size
     *
     * @return data size
     */
    public int size() {
    	return body.size();
    }

    /**
     * remove row
     *
     * @param index row index
     * @return self instance
     */
    public NList removeRow( int index ) {
        for( Object key :  getRow(index).keySet() ) {
            if( ! header.containsKey(key) ) continue;
            Integer size = header.get(key);
            if( size > index + 1 ) {
                header.put( key, size - 1 );
            }
        }
        body.remove( index );
        return this;
    }

    /**
     * remove row
     *
     * @param key column key
     * @return self instance
     */
    public NList removeKey( Object key ) {
        if( containsKey(key) ) {
            header.remove( key );
            for( NMap row : body ) {
                row.remove( key );
            }
        }
        return this;
    }

    /**
     * set row data
     * @param index          row index
     * @param value   data (Bean, map, json)
     * @return self instance
     */
    public NList setRow( int index, Object value ) throws IllegalArgumentException {

        Assert.notNull( value, "value must not be null" );
        Assert.notTrue( Types.isCollection(value) || Types.isArray(value), "value must not be collection or array" );

        if( value instanceof NMap ) {
            setRowFromNMap( index, (NMap) value );
        } else {
            setRowFromNMap( index, new NMap(value) );
        }

        return this;

    }

    private void setRowFromNMap( int rowIndex, NMap map ) {
        body.set( rowIndex, map );
        for( Object key : map.keySet() ) {
            if( ! containsKey(key) )
                header.put( key, rowIndex + 1 );
        }
    }

    /**
     * set data in row
     *
     * @param row       row index
     * @param key       key
     * @param value     value
     * @return self instance
     */
    @SuppressWarnings("unchecked")
    public NList setDataByKey( int row, Object key, Object value ) throws IndexOutOfBoundsException {
        Assert.beTrue( containsKey(key), "key({}) is not exist.", key );
        NMap data = body.get( row );
        if( data == null ) {
            data = new NMap();
            body.set( row, data );
        }
        data.put( key, value );
        header.put( key, Math.max(header.get(key), row + 1) );
        return this;
    }

    /**
     * set data
     *
     * @param row       row index
     * @param column    column index
     * @param value     value
     * @return self instance
     */
    public NList setData( int row, int column, Object value ) {
        Object key = getKey( column );
        Assert.notNull( key, "column(index:{}) is not exist.", column );
        return setDataByKey( row, key, value );
    }

    /**
     * Get row data
     * @param index row index
     * @return map data
     */
    public NMap getRow( int index ) {
        return body.get( index );
    }

    /**
     * get value
     *
     * @param row       row index
     * @param column    column index
     * @return value
     */
    public Object getData( int row, int column ) {
        NMap data = body.get( row );
        return data == null ? null : data.get( getKey(column) );
    }

    /**
     * get value
     *
     * @param row       row index
     * @param key       header key
     * @return value
     */
    public Object getDataByKey( int row, Object key ) {
        NMap data = body.get( row );
        return data == null ? null : data.get(key);
    }

    /**
     * check if header contains the specified key.
     *
     * @param key   key to inspect
     * @return true if header contains the specified key.
     */
    public boolean containsKey( Object key ) {
    	return header.containsKey( key );
    }

    /**
     * check if specified data contains.
     *
     * @param row   data to inspect
     * @return  true if this list contains the specified data.
     */
    public boolean contains( NMap row ) {
        return body.contains(row);
    }

    /**
     * clear all
     *
     * @return self instance
     */
    public NList clear() {
    	header.clear();
    	alias.clear();
        body.clear();
        return this;
    }

    /**
     * clear data only (without header)
     *
     * @return self instance
     */
    public NList clearData() {
        body.clear();
        return this;
    }

    /**
     * Get List consisted with NMap
     *
     * @return Data List
     */
    public List<NMap> toList() {
        return body;
    }

    /**
     * get values corresponding key
     *
     * @param key   column key
     * @param <T> 	expected class of return
     * @return values corresponding key
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> toList( String key, Class<T> type ) {

        List<T> result = new ArrayList<>();
        if( ! containsKey(key) ) return result;

        boolean toPrimitive = Types.isPrimitive( type );

        for( Map row : body ) {
            Object val = row.get( key );
            if( val == null ) {
                result.add( null );
            } else {
                try {
                    result.add( (T) val );
                } catch ( ClassCastException e ) {
                    if( toPrimitive ) {
                        result.add( Types.castPrimitive(val, type) );
                    } else {
                        result.add( Reflector.toBeanFrom(val,type) );
                    }
                }
            }
        }
        return result;

    }

    /**
     * convert to list
     *
     * @param type  return's generic type
     * @param <T> 	expected class of return
     * @return converted list
     */
    public <T> List<T> toList( Class<T> type ) {
        return toList( type, false );
    }

    /**
     * convert to list
     *
     * @param type               generic type class
     * @param ignoreException    if true, ignore casting exception
     * @param <T> 	             expected class of return
     * @return converted list
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> toList( Class<T> type, boolean ignoreException ) {

        List<T> result = new ArrayList<T>();

        for( NMap row : body ) {
            try {
                Object bean = row.toBean( type );
                result.add( bean == null ? null : (T) bean );
            } catch( Exception e ) {
                if( ! ignoreException ) {
                    throw e;
                }
            }
        }

        return result;

    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object other ) {

    	if( other == null || ! (other instanceof NList) ) return false;
        if( other == this ) return true;

    	NList list = (NList) other;

        if( size() != list.size() ) return false;
        if( ! header.equals( list.header ) ) return false;

    	for( int i = 0, iCnt = size(); i < iCnt; i++ ) {
    		if( ! Objects.equals(getRow(i), list.getRow(i)) ) return false;
    	}

    	return true;

    }

    /**
     * print data only first 500 rows
     *
     * @return grid data
     */
    public String toString() {
        return toString( true, false );
    }

    /**
     * Print data
     *
     * @param header    if true, print header.
     * @param all       if true, print all row.
     * @return debug string
     */
    public String toString( boolean header, boolean all ) {
        return toString( header, header, -1, all ? Integer.MAX_VALUE : -1 );
    }

    /**
     * convert to string
     *
     * @param header            if true, print header
     * @param alias             if true, print alias
     * @param maxColumnWidth    maximum column width ( minus : use default(255) )
     * @param showCounts        row counts to show ( minus : use default(500) )
     * @return  grid contents
     */
    public String toString( boolean header, boolean alias, int maxColumnWidth, int showCounts ) {
        refreshKey();
        return new NListPrinter( this )
            .maxColumnWidth( maxColumnWidth )
            .showCounts( showCounts )
            .toString( header, alias );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public NList clone() {

        NList clone = new NList();

        clone.header = new LinkedHashMap<>( header );
        clone.alias  = new LinkedHashMap<>( alias );

        body.forEach( row -> {
            clone.body.add( row.clone() );
        });

        return clone;
    }

    /**
     * Sort data
     *
     * @param comparator comparator to determine the order of the list.
     *                   A {@code null} value indicates that the elements' <i>natural ordering</i> should be used.
     *
     * @return self instance
     */
    public NList sort( Comparator<NMap> comparator ) {
        Collections.sort( body, comparator );
        return this;
    }

	@Override
	public Iterator<NMap> iterator() {

		final int size = size();

		return new Iterator<NMap>() {

			int index = 0;

			public boolean hasNext() {
				return index < size;
			}

			public NMap next() {
				return getRow( index++ );
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
            public void forEachRemaining( Consumer<? super NMap> action ) {
                throw new UnsupportedOperationException();
            }

		};

	}


	@Override
    public void forEach( Consumer<? super NMap> action ) {
		Objects.requireNonNull( action );
		for( NMap row : this ) {
			action.accept( row );
		}
    }

	@Override
    public Spliterator<NMap> spliterator() {
        throw new UnsupportedOperationException();
    }

}