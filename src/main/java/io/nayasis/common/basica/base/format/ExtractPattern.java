package io.nayasis.common.basica.base.format;

import io.nayasis.common.basica.base.Types;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.regex.Pattern;

@Data
@Accessors(chain=true)
public class ExtractPattern {

    private Pattern       pattern;
    private List<Integer> targetGroups;
    private Pattern       recoveryPattern;
    private String        recoveryReplacer;

    public ExtractPattern( String pattern, int[] targetGroups, String recoveryPattern, String recoveryReplacer ) {
        setPattern( pattern );
        setRecoveryPattern( recoveryPattern );
        setTargetGroups( targetGroups );
        setRecoveryReplacer( recoveryReplacer );
    }

    public ExtractPattern setTargetGroups( int[] targetGroups ) {
        this.targetGroups = Types.toList( targetGroups );
        return this;
    }

    public ExtractPattern setPattern( String pattern ) {
        this.pattern = Pattern.compile( pattern );
        return this;
    }

    public ExtractPattern setRecoveryPattern( String recoveryPattern ) {
        this.recoveryPattern = Pattern.compile( recoveryPattern );
        return this;
    }
}
