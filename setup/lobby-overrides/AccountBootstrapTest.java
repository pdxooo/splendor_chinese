package eu.kartoffelquadrat.ls.accountmanager.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.kartoffelquadrat.ls.accountmanager.model.Player;
import eu.kartoffelquadrat.ls.accountmanager.model.PlayerRepository;
import eu.kartoffelquadrat.ls.accountmanager.model.Role;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Tests that administrator-created accounts survive service restarts. */
public class AccountBootstrapTest {

    /** Startup must not remove an account outside the original fixed list. */
    @Test
    public void preservesAdministratorCreatedAccounts() {
        PlayerRepository repository = mock(PlayerRepository.class);
        BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
        Player customPlayer = new Player("jq", "123456", "112233", Role.ROLE_PLAYER);
        when(repository.findAll()).thenReturn(List.of(customPlayer));
        when(repository.findById(any())).thenReturn(Optional.empty());
        when(encoder.encode("123456")).thenReturn("encoded");

        new AccountBootstrap(repository, encoder).run();

        verify(repository, never()).delete(customPlayer);
    }
}
