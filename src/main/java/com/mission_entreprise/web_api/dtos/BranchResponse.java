package com.mission_entreprise.web_api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchResponse {
    private String name;
    private boolean _protected;

    public boolean isProtected() {
        return _protected;
    }

    public void setProtected(boolean isProtected) {
        this._protected = isProtected;
    }
}
