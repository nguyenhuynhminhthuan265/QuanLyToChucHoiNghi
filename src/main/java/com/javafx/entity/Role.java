package com.javafx.entity;


import javafx.scene.Node;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Table(name = "roles")
@Entity
@Data
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Vui lòng nhập tên!")
    private String name;

    @NotBlank(message = "Vui lòng nhập mô tả!")
    private String description;

    @OneToMany(mappedBy = "role",
            fetch = FetchType.EAGER)
    private List<Account> accounts;

    @Column(name = "is_delete")
    private boolean isDelete;


    public Role(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Role(String name, String description) {

        this.name = name;
        this.description = description;
    }

    public Role() {
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", accounts=" + "\n" + accounts.toString() +
                '}';
    }
}


