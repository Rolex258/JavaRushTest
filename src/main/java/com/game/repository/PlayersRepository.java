package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayersRepository extends JpaRepository<Player, Integer> {
    Optional<Player> findById(Long id);
}
