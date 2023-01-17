package com.game.util;

import com.game.entity.Player;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PlayerValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Player.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Player player = (Player) o;
        if (player.getName() == null || player.getName().length() > 12 || "".equals(player.getName())) {
            errors.rejectValue("name", "", "Incorrect name");
        }
        if (player.getTitle() == null || player.getTitle().length() > 30) {
            errors.rejectValue("title", "", "Incorrect title");
        }
        if (player.getRace() == null) {
            errors.rejectValue("race", "", "Race is null");
        }
        if (player.getProfession() == null) {
            errors.rejectValue("profession", "", "Profession is null");
        }
        if (player.getBirthday() == null || player.getBirthday().getTime() < 0 ||
                player.getBirthday().getYear() + 1970 > 3000 || player.getBirthday().getYear() + 1970 < 2000) {
            errors.rejectValue("birthday", "", "Birthday is incorrect");
        }
        if (player.getExperience() == null || player.getExperience() < 0 || player.getExperience() > 10000000L) {
            errors.rejectValue("experience", "", "Experience is incorrect");
        }
    }
}
