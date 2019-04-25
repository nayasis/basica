package io.nayasis.common.basica.base.format;

import java.util.regex.Pattern;

public class ExtractPattern {

    private Pattern pattern;
    private int     targetGroup;
    private Pattern recoveryPattern;
    private String  recoveryReplacer;

    public ExtractPattern( String pattern, int targetGroup, String recoveryPattern, String recoveryReplacer ) {
        this( Pattern.compile(pattern), targetGroup, Pattern.compile(recoveryPattern), recoveryReplacer );
    }

    public ExtractPattern( Pattern pattern, int targetGroup, Pattern recoveryPattern, String recoveryReplacer ) {
        this.pattern          = pattern;
        this.targetGroup      = targetGroup;
        this.recoveryPattern  = recoveryPattern;
        this.recoveryReplacer = recoveryReplacer;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getTargetGroup() {
        return targetGroup;
    }

    public Pattern getRecoveryPattern() {
        return recoveryPattern;
    }

    public String getRecoveryReplacer() {
        return recoveryReplacer;
    }
}
