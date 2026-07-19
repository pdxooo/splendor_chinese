package eu.kartoffelquadrat.ls.accountmanager.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.kartoffelquadrat.ls.accountmanager.model.Player;
import eu.kartoffelquadrat.ls.accountmanager.model.PlayerRepository;
import eu.kartoffelquadrat.ls.accountmanager.model.Role;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Tests for public player registration protected by an invitation code. */
public class InviteRegistrationControllerTest {

    private PlayerRepository repository;
    private InviteRegistrationController controller;

    /** Create isolated collaborators for every test. */
    @Before
    public void setUp() {
        repository = mock(PlayerRepository.class);
        when(repository.findById("newplayer")).thenReturn(Optional.empty());
        controller = new InviteRegistrationController(
                repository, new BCryptPasswordEncoder(), "TEST-INVITE-CODE");
    }

    /** A matching invitation code creates a player account. */
    @Test
    public void validInviteCreatesPlayerAccount() {
        InviteRegistrationForm form = new InviteRegistrationForm(
                "newplayer", "123456", "2684FF", "TEST-INVITE-CODE");

        ResponseEntity response = controller.register(form);

        assertEquals(200, response.getStatusCodeValue());
        ArgumentCaptor<Player> player = ArgumentCaptor.forClass(Player.class);
        verify(repository).save(player.capture());
        assertEquals("newplayer", player.getValue().getName());
        assertEquals(Role.ROLE_PLAYER, player.getValue().getRole());
        assertTrue(new BCryptPasswordEncoder().matches(
                "123456", player.getValue().getPassword()));
    }

    /** An invalid invitation code never creates an account. */
    @Test
    public void invalidInviteIsRejected() {
        InviteRegistrationForm form = new InviteRegistrationForm(
                "newplayer", "123456", "2684FF", "WRONG-CODE");

        ResponseEntity response = controller.register(form);

        assertEquals(403, response.getStatusCodeValue());
        verify(repository, never()).save(any(Player.class));
    }

    /** A missing invitation code never creates an account. */
    @Test
    public void missingInviteIsRejected() {
        InviteRegistrationForm form = new InviteRegistrationForm(
                "newplayer", "123456", "2684FF", null);

        ResponseEntity response = controller.register(form);

        assertEquals(403, response.getStatusCodeValue());
        verify(repository, never()).save(any(Player.class));
    }

    /** A blank password is rejected even with a valid invitation. */
    @Test
    public void emptyPasswordIsRejected() {
        InviteRegistrationForm form = new InviteRegistrationForm(
                "newplayer", "", "2684FF", "TEST-INVITE-CODE");

        ResponseEntity response = controller.register(form);

        assertEquals(400, response.getStatusCodeValue());
        verify(repository, never()).save(any(Player.class));
    }

    /** Public registration cannot be used to create an administrator. */
    @Test
    public void publicRegistrationAlwaysCreatesPlayerRole() {
        InviteRegistrationForm form = new InviteRegistrationForm(
                "newplayer", "123456", "2684FF", "TEST-INVITE-CODE");

        controller.register(form);

        ArgumentCaptor<Player> player = ArgumentCaptor.forClass(Player.class);
        verify(repository).save(player.capture());
        assertFalse(player.getValue().getRole() == Role.ROLE_ADMIN);
        assertFalse(player.getValue().getRole() == Role.ROLE_SERVICE);
    }
}
