package com.trelloiii.cibot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Root {
    @Id
    private String id;
    private String password;
    private boolean isActivated;
}
