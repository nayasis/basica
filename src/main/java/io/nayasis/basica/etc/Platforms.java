package io.nayasis.basica.etc;

public class Platforms {

    /** O/S name */
    public static final String  osName          = System.getProperty( "os.name" );
    /** O/S architect */
    public static final String  osArchitecture  = System.getProperty( "os.arch" );
    /** O/S version */
    public static final String  osVersion       = System.getProperty( "os.version" );
    /** O/S character set */
    public static final String  osCharset       = System.getProperty( "sun.jnu.encoding" );
    /** Java Virtual Machine architect */
    public static final String  jvmArchitecture = System.getProperty( "sun.arch.data.model" );
    /** is WINDOWS O/S */
    public static final boolean isWindows       = osName.toLowerCase().contains( "win" );
    /** is LINUX O/S */
    public static final boolean isLinux         = osName.toLowerCase().contains( "linux" );
    /** is UNIX O/S */
    public static final boolean isUnix          = osName.toLowerCase().contains( "unix" );
    /** is SOLARIS O/S */
    public static final boolean isSolaris       = osName.toLowerCase().contains( "solaris" ) || osName.toLowerCase().contains( "sunos" );
    /** is MAC O/S */
    public static final boolean isMac           = osName.toLowerCase().contains( "mac" );

}
