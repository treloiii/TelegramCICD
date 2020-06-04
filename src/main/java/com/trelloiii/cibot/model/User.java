package com.trelloiii.cibot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="usr")
public class User {
    @Id
    private Integer id;
    private String name;
    private String nickname;
    private String locale;
}
