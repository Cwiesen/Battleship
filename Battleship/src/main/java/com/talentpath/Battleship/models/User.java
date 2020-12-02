package com.talentpath.Battleship.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name="users")
public class User {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank       //indicates that an empty string or just spaces or tabs is not valid
    @Size(max = 20)
    @Column(unique=true)
    private String username;

    @NotBlank
    @Size(max=50)
    @Email      //validates this field holds a string that conforms to a valid email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    //@ManyToMany( fetch = FetchType.LAZY )       //don't load the roles from the db unless requested
    //annoying for now, we'll kill this
    @ManyToMany( fetch = FetchType.EAGER )
    @JoinTable( name = "user_roles",
            joinColumns = @JoinColumn( name = "user_id"),
            //tells Hibernate a that foreign key called user_id should link to id
            inverseJoinColumns = @JoinColumn( name = "role_id" )
            //tells Hibernate that a foreign key called role_id should link to the @Id field of the Role entity
    )
    private Set<Role> roles = new HashSet<>();

    public User( @NotBlank @Size(max=20) String username, @NotBlank @Size(max=50) String email, @NotBlank @Size(max=120) String password ){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        return roles != null ? roles.equals(user.roles) : user.roles == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }
}