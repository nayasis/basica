package com.github.nayasis.basica.base.format;

import com.github.nayasis.basica.base.format.function.Replacer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

@Data
@Accessors(fluent=true)
public class ExtractPattern {

    private Pattern       pattern;
    private Character     escapeChar;
    private Replacer replacer;

    public ExtractPattern( String pattern ) {
        pattern( pattern );
    }

    public ExtractPattern pattern( String pattern ) {
        this.pattern = Pattern.compile( pattern );
        return this;
    }

    public boolean isEscapable( String prefix ) {
        return escapeChar != null && ! prefix.isEmpty() && prefix.charAt( prefix.length() - 1 ) == escapeChar;
    }

}
