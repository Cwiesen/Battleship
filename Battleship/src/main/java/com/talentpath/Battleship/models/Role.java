package com.talentpath.Battleship.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="roles")
public class Role {

    @Id //this tells Hibernate this field is involved in the primary key
    @GeneratedValue( strategy= GenerationType.IDENTITY) //tell Hibernate to create this column as a serial
    private Integer id;

    @Enumerated( EnumType.STRING )  //tell Hibernate that the set of allowable values should be constrained
    @Column( length = 20 ) //tell Hibernate to set up the column to be 20 characters wide
    private RoleName name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role role = (Role) o;

        if (id != null ? !id.equals(role.id) : role.id != null) return false;
        return name == role.name;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public enum RoleName {
    ROLE_USER,
    ROLE_ADMIN
    }
}
