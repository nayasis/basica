package io.nayasis.common.etc;

public class Platform {

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
    public static final boolean isWindows       = osName.startsWith( "Windows" );
    /** is LINUX O/S */
    public static final boolean isLinux         = osName.startsWith( "Linux" );
    /** is MAC O/S */
    public static final boolean isMac           = osName.startsWith( "Mac OS" );

}
