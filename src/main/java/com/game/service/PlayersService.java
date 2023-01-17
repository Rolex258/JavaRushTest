package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PlayersService {
    private final PlayersRepository playersRepository;

    @Autowired
    public PlayersService(PlayersRepository playersRepository) {
        this.playersRepository = playersRepository;
    }

    public List<Player> getAll(String order) {
        return playersRepository.findAll(Sort.by(order));
    }

    public List<Player> getAll() {
        return playersRepository.findAll();
    }

    @Transactional
    public void save(Player player) {
        enrich(player);
        playersRepository.save(player);
    }

    @Transactional
    public void delete(Player player) {
        playersRepository.delete(player);
    }

    public Player findById(Long id) {
        return playersRepository.findById(id).orElse(null);
    }

    private static void enrich(Player player) {
        if (player.getBanned() == null) {
            player.setBanned(false);
        }

        int level = (int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        player.setLevel(level);

        int nextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
        player.setUntilNextLevel(nextLevel);
    }
}
