package com.ssplugins.preedit.launcher;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Version implements Comparable<Version> {
    
    private Integer[] version;
    
    public Version(String version) {
        this.version = Arrays.stream(version.split("\\."))
                            .map(Integer::parseInt)
                            .toArray(Integer[]::new);
    }
    
    @Override
    public String toString() {
        return String.join(".", Arrays.stream(version)
                                      .map(String::valueOf)
                                      .collect(Collectors.toList()));
    }
    
    public int compareTo(Version o) {
        for (int i = 0; i < version.length; i++) {
            if (i >= o.version.length) {
                return 1;
            }
            int c = Integer.compare(version[i], o.version[i]);
            if (c != 0) {
                return c;
            }
        }
        if (version.length < o.version.length) {
            return -1;
        }
        return 0;
    }
    
}
