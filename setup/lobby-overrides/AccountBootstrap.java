package eu.kartoffelquadrat.ls.accountmanager.config;

import eu.kartoffelquadrat.ls.accountmanager.model.Player;
import eu.kartoffelquadrat.ls.accountmanager.model.PlayerRepository;
import eu.kartoffelquadrat.ls.accountmanager.model.Role;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Keeps only the accounts required by this deployment. */
@Component
public class AccountBootstrap implements CommandLineRunner {

    private static final Set<String> ALLOWED_ACCOUNTS = Set.of("lxh", "maex", "qhc", "xox", "xyj");
    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountBootstrap(PlayerRepository playerRepository, BCryptPasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        playerRepository.findAll().stream()
                .filter(player -> !ALLOWED_ACCOUNTS.contains(player.getName()))
                .forEach(playerRepository::delete);

        savePlayerWithSimplePassword("lxh", "00DD44");
        savePlayerWithSimplePassword("qhc", "2288EE");
        savePlayerWithSimplePassword("xyj", "EE2222");
    }

    private void savePlayerWithSimplePassword(String name, String colour) {
        Player player = playerRepository.findById(name)
                .orElseGet(() -> new Player(name, colour, "", Role.ROLE_PLAYER));
        player.setPassword(passwordEncoder.encode("123456"));
        player.setRole(Role.ROLE_PLAYER);
        playerRepository.save(player);
    }
}
