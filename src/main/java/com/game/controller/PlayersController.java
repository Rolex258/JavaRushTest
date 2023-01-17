package com.game.controller;

import com.game.dto.PlayerDTO;
import com.game.entity.Player;
import com.game.util.IdException;
import com.game.util.NotFoundException;
import com.game.util.PlayerErrorResponse;
import com.game.util.PlayerValidator;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/players")
public class PlayersController {
    private final PlayersService playersService;
    private final PlayerValidator playerValidator;

    @Autowired
    public PlayersController(PlayersService playersService, PlayerValidator playerValidator) {
        this.playersService = playersService;
        this.playerValidator = playerValidator;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePlayer(@PathVariable String id) {
        Long playerId = validateId(id);
        Player player = playersService.findById(playerId);

        if (player == null) {
            throw new NotFoundException();
        }

        playersService.delete(player);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public PlayerDTO updatePlayer(@PathVariable String id,
                                  @RequestBody PlayerDTO playerDTO,
                                  BindingResult bindingResult) {

        Long playerId = validateId(id);
        Player player = playersService.findById(playerId);
        if (player == null) {
            throw new NotFoundException();
        }

        if (playerDTO.getName() != null) {
            player.setName(playerDTO.getName());
        }
        if (playerDTO.getTitle() != null) {
            player.setTitle(playerDTO.getTitle());
        }
        if (playerDTO.getRace() != null) {
            player.setRace(playerDTO.getRace());
        }
        if (playerDTO.getProfession() != null) {
            player.setProfession(playerDTO.getProfession());
        }
        if (playerDTO.getBirthday() != null) {
            player.setBirthday(new Date(playerDTO.getBirthday()));
        }
        if (playerDTO.getBanned() != null) {
            player.setBanned(playerDTO.getBanned());
        }
        if (playerDTO.getExperience() != null) {
            player.setExperience(playerDTO.getExperience());
        }

        playerValidator.validate(player, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new PlayerErrorResponse();
        }

        playersService.save(player);
        return convertToPlayerDTO(player);
    }

    @GetMapping("/{id}")
    public PlayerDTO getPlayer(@PathVariable String id) {
        Long playerId = validateId(id);
        Player player = playersService.findById(playerId);

        if (player == null) {
            throw new NotFoundException();
        }


        return convertToPlayerDTO(player);
    }

    @PostMapping
    public PlayerDTO createPlayer(@RequestBody PlayerDTO playerDTO,
                                  BindingResult bindingResult) {

        Player player = convertToPlayer(playerDTO);

        playerValidator.validate(player, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new PlayerErrorResponse();
        }

        playersService.save(player);

        return convertToPlayerDTO(player);
    }

    @GetMapping
    public List<PlayerDTO> getPlayers(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                   @RequestParam(value = "order", required = false) PlayerOrder order,
                                   @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (order == null) {
            order = PlayerOrder.ID;
        }
        if (pageNumber == null) {
            pageNumber = 0;
        }
        if (pageSize == null) {
            pageSize = 3;
        }

        List<PlayerDTO> result = playersService.getAll(order.getFieldName()).stream()
                .filter(player -> name == null || player.getName().matches(String.format(".*%s.*", name)))
                .filter(player -> title == null || player.getTitle().matches(String.format(".*%s.*", title)))
                .filter(player -> race == null || player.getRace() == race)
                .filter(player -> profession == null || player.getProfession() == profession)
                .filter(player -> after == null || player.getBirthday().getTime() > after)
                .filter(player -> before == null || player.getBirthday().getTime() < before)
                .filter(player -> banned == null || player.getBanned() == banned)
                .filter(player -> minExperience == null || player.getExperience() >= minExperience)
                .filter(player -> maxExperience == null || player.getExperience() <= maxExperience)
                .filter(player -> minLevel == null || player.getLevel() >= minLevel)
                .filter(player -> maxLevel == null || player.getLevel() <= maxLevel)
                .map(PlayersController::convertToPlayerDTO)
                .collect(Collectors.toList());

        return paging(result, pageNumber, pageSize);
    }

    @GetMapping("/count")
    public Integer count(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return (int) playersService.getAll().stream()
                .filter(player -> name == null || player.getName().matches(String.format(".*%s.*", name)))
                .filter(player -> title == null || player.getTitle().matches(String.format(".*%s.*", title)))
                .filter(player -> race == null || player.getRace() == race)
                .filter(player -> profession == null || player.getProfession() == profession)
                .filter(player -> after == null || player.getBirthday().getTime() > after)
                .filter(player -> before == null || player.getBirthday().getTime() < before)
                .filter(player -> banned == null || player.getBanned() == banned)
                .filter(player -> minExperience == null || player.getExperience() >= minExperience)
                .filter(player -> maxExperience == null || player.getExperience() <= maxExperience)
                .filter(player -> minLevel == null || player.getLevel() >= minLevel)
                .filter(player -> maxLevel == null || player.getLevel() <= maxLevel)
                .count();
    }

    @ExceptionHandler
    private ResponseEntity<HttpStatus> handleException(PlayerErrorResponse ignored) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<HttpStatus> handleException(IdException ignored) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<HttpStatus> handleException(NotFoundException ignored) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private static List<PlayerDTO> paging(List<PlayerDTO> list, Integer pageNumber, Integer pageSize) {
        int size = list.size();
        if (pageNumber * pageSize + 1 > size) {
            return Collections.emptyList();
        }
        if (size >= (pageNumber + 1) * pageSize) {
            return list.subList(pageNumber * pageSize, (pageNumber + 1) * pageSize);
        } else {
            return list.subList(pageNumber * pageSize, size);
        }
    }

    private static Player convertToPlayer(PlayerDTO playerDTO) {
        Player player = new Player();

        player.setName(playerDTO.getName());
        player.setTitle(playerDTO.getTitle());
        player.setRace(playerDTO.getRace());
        player.setProfession(playerDTO.getProfession());
        player.setBanned(playerDTO.getBanned());
        if (playerDTO.getBirthday() != null) {
            player.setBirthday(new Date(playerDTO.getBirthday()));
        }
        player.setExperience(playerDTO.getExperience());

        return player;
    }
    private static PlayerDTO convertToPlayerDTO(Player player) {
        PlayerDTO playerDTO = new PlayerDTO();

        playerDTO.setName(player.getName());
        playerDTO.setTitle(player.getTitle());
        playerDTO.setRace(player.getRace());
        playerDTO.setProfession(player.getProfession());
        playerDTO.setBanned(player.getBanned());
        playerDTO.setBirthday(player.getBirthday().getTime());
        playerDTO.setExperience(player.getExperience());
        playerDTO.setId(player.getId());
        playerDTO.setLevel(player.getLevel());
        playerDTO.setUntilNextLevel(player.getUntilNextLevel());

        return playerDTO;
    }

    private static Long validateId(String id) {
        long playerId;
        try {
            playerId = Long.parseLong(id);
            if (playerId <= 0) {
                throw new RuntimeException();
            }
        } catch (Exception ignored) {
            throw new IdException();
        }
        return playerId;
    }

}
