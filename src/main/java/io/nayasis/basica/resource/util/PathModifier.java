package io.nayasis.basica.resource.util;

import io.nayasis.basica.base.Strings;

import java.util.LinkedList;
import java.util.List;

import static io.nayasis.basica.file.Files.FOLDER_SEPARATOR;
import static io.nayasis.basica.file.Files.FOLDER_SEPARATOR_WINDOWS;

public class PathModifier {

    private static final String PATH_PARENT = "..";

    private static final String PATH_CURRENT = ".";

    public static String clean( String path ) {

        if ( Strings.isEmpty(path) ) return path;

        String pathToUse = path.replace( FOLDER_SEPARATOR_WINDOWS, FOLDER_SEPARATOR );

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(':');
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains( FOLDER_SEPARATOR )) {
                prefix = "";
            }
            else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }
        }
        if (pathToUse.startsWith( FOLDER_SEPARATOR )) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathArray = Strings.split( pathToUse, FOLDER_SEPARATOR );
        LinkedList<String> pathElements = new LinkedList<>();
        int tops = 0;

        for (int i = pathArray.size() - 1; i >= 0; i--) {
            String element = pathArray.get(i);
            if ( PATH_CURRENT.equals(element)) {
                // Points to current directory - drop it.
            }
            else if ( PATH_PARENT.equals(element)) {
                // Registering top path found.
                tops++;
            }
            else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                }
                else {
                    // Normal path element found.
                    pathElements.add(0, element);
                }
            }
        }

        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, PATH_PARENT );
        }
        // If nothing else left, at least explicitly point to current path.
        if (pathElements.size() == 1 && "".equals(pathElements.getLast()) && !prefix.endsWith( FOLDER_SEPARATOR )) {
            pathElements.add(0, PATH_CURRENT );
        }

        return prefix + Strings.join( pathElements, FOLDER_SEPARATOR );

    }

}
