package com.talentpath.Battleship.daos;

import com.talentpath.Battleship.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    //we're defining this as a repository that serves up User objects
    //with an Integer @Id

    Optional<User> findByUsername(String username );

    Boolean existsByUsername( String username );

    Boolean existsByEmail( String email );

}
