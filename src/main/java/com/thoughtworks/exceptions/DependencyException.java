package com.thoughtworks.exceptions;

public class DependencyException extends ServiceException {

    String dependencyType;
    String dependencyName;
    String dependencyURI;
    String dependencyError;

    public DependencyException(String dependencyType, String dependencyName, String dependencyURI, String dependencyError) {
        super(dependencyName, dependencyError);
        this.dependencyType = dependencyType;
        this.dependencyName = dependencyName;
        this.dependencyURI = dependencyURI;
        this.dependencyError = dependencyError;
    }

    public DependencyException(String dependencyType, String dependencyName, String dependencyURI, String dependencyError, Exception causedByException) {
        super(dependencyName, dependencyError, causedByException);
        this.dependencyType = dependencyType;
        this.dependencyName = dependencyName;
        this.dependencyURI = dependencyURI;
        this.dependencyError = dependencyError;
    }

    @Override
    public String getErrorCode() {
        return dependencyName;
    }

    @Override
    public String getErrorMessage() {
        return dependencyError;
    }
}
