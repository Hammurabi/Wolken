package org.wolkenproject.rpc;

public class UrlPath {
    private String      path;
    private UrlPath[]   paths;
    public UrlPath(String path, UrlPath paths[]) {
        this.path = path;
        this.paths = paths;
    }

    public String getPath() {
        return path;
    }

    public UrlPath[] getPaths() {
        return paths;
    }
}
