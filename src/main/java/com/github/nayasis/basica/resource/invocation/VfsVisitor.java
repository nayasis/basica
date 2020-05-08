package com.github.nayasis.basica.resource.invocation;

import com.github.nayasis.basica.resource.type.VfsResource;
import com.github.nayasis.basica.resource.type.interfaces.Resource;
import com.github.nayasis.basica.resource.matcher.PathMatcher;
import com.github.nayasis.basica.resource.util.VfsUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

public class VfsVisitor implements InvocationHandler {

    private final String        subPattern;
    private final PathMatcher   pathMatcher;
    private final String        rootPath;
    private final Set<Resource> resources = new LinkedHashSet<>();

    public VfsVisitor( String rootPath, String subPattern, PathMatcher pathMatcher) {
        this.subPattern  = subPattern;
        this.pathMatcher = pathMatcher;
        this.rootPath    = (rootPath.isEmpty() || rootPath.endsWith("/")) ? rootPath : rootPath + "/";
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        String methodName = method.getName();
        if( Object.class == method.getDeclaringClass() ) {
            if (methodName.equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (methodName.equals("hashCode")) {
                return System.identityHashCode(proxy);
            }
        } else if ("getAttributes".equals(methodName)) {
            return getAttributes();
        } else if ("visit".equals(methodName)) {
            visit(args[0]);
            return null;
        } else if ("toString".equals(methodName)) {
            return toString();
        }

        throw new IllegalStateException("Unexpected method invocation: " + method);
    }

    public void visit( Object vfsResource ) {
        if( pathMatcher.match(subPattern,
            VfsUtils.getPath(vfsResource).substring(rootPath.length()))) {
            resources.add(new VfsResource(vfsResource));
        }
    }

    public Object getAttributes() {
        return VfsUtils.getVisitorAttributes();
    }

    public Set<Resource> getResources() {
        return this.resources;
    }

    public int size() {
        return this.resources.size();
    }

    @Override
    public String toString() {
        return "sub-pattern: " + this.subPattern + ", resources: " + this.resources;
    }

}
