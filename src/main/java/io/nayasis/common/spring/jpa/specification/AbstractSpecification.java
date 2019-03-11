package io.nayasis.common.spring.jpa.specification;

import io.nayasis.common.base.Strings;
import io.nayasis.common.validation.Validator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import java.util.List;

public class AbstractSpecification<T> {

    protected Specification<T> in( String key, List values ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Validator.isNotEmpty(values) ) {
                Path<Object> column = getPath( root, key );
                CriteriaBuilder.In<Object> in = cb.in( column );
                values.forEach( status -> {
                    in.value( status );
                });
                return in;
            }
            return cb.conjunction();
        };
    }

    protected Specification<T> like( String key, String value ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Strings.isNotEmpty(value) ) {
                return cb.like( getPath(root, key), "%" + value + "%" );
            }
            return cb.conjunction();
        };
    }

    protected Specification<T> notLike( String key, String value ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Strings.isNotEmpty(value) ) {
                return cb.notLike( getPath(root, key), "%" + value + "%" );
            }
            return cb.conjunction();
        };
    }

    protected Specification<T> equal( String key, Object value ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Strings.isNotEmpty(value) ) {
                return cb.equal( getPath(root, key), value );
            }
            return cb.conjunction();
        };
    }

    protected Specification<T> lessThan( String key, String value ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Strings.isNotEmpty(value) ) {
                return cb.lessThan( getPath(root, key), value );
            }
            return cb.conjunction();
        };
    }

    protected Specification<T> lessThanOrEqual( String key, String value ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Strings.isNotEmpty(value) ) {
                return cb.lessThanOrEqualTo( getPath(root, key), value );
            }
            return cb.conjunction();
        };
    }

    protected Specification<T> greaterThan( String key, String value ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Strings.isNotEmpty(value) ) {
                return cb.greaterThan( getPath(root, key), value );
            }
            return cb.conjunction();
        };
    }

    protected Specification<T> greaterThanOrEqual( String key, String value ) {
        return (Specification<T>) ( root, query, cb ) -> {
            if( Strings.isNotEmpty(value) ) {
                return cb.greaterThanOrEqualTo( getPath(root, key), value );
            }
            return cb.conjunction();
        };
    }

    private Path getPath( Path expression, String key ) {

        if( expression == null ) return null;

        int index = key.indexOf( "." );

        if( index < 0 ) {
            return expression.get( key );
        } else {

            String prevKey = key.substring( 0, index );
            String nextKey = key.substring( index + 1 );

            Path path = expression.get( prevKey );

            Path child = getPath( path, nextKey );

            if( child != null ) {
                return child;
            } else {
                return path;
            }

        }

    }

}