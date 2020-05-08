package com.github.nayasis.basica.resource.finder;

import com.github.nayasis.basica.resource.invocation.VfsVisitor;
import com.github.nayasis.basica.resource.matcher.PathMatcher;
import com.github.nayasis.basica.resource.type.interfaces.Resource;
import com.github.nayasis.basica.resource.util.VfsUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class VfsResourceFinder {

    private PathMatcher pathMatcher;

    public VfsResourceFinder( PathMatcher pathMatcher ) {
        this.pathMatcher = pathMatcher;
    }

    public void setPathMatcher( PathMatcher pathMatcher ) {
        this.pathMatcher = pathMatcher;
    }

    public Set<Resource> find( URL rootDir, String pattern ) throws IOException {
        Object root = VfsUtils.getRoot(rootDir);
        VfsVisitor visitor = new VfsVisitor(VfsUtils.getPath(root), pattern, pathMatcher);
        VfsUtils.visit(root, visitor);
        return visitor.getResources();
    }

}

