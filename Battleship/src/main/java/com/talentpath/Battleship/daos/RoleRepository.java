package com.talentpath.Battleship.daos;

import com.talentpath.Battleship.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName( Role.RoleName name );

}
