package eu.kartoffelquadrat.ls.lobby.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.kartoffelquadrat.asyncrestlib.BroadcastContentManager;
import eu.kartoffelquadrat.ls.accountmanager.controller.TokenController;
import eu.kartoffelquadrat.ls.gameregistry.controller.RegistryException;
import eu.kartoffelquadrat.ls.gameregistry.model.GameServerParameters;
import eu.kartoffelquadrat.ls.gameregistry.model.GameServers;
import eu.kartoffelquadrat.ls.lobby.model.Session;
import eu.kartoffelquadrat.ls.lobby.model.Sessions;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/** Authorization tests for deleting lobby sessions. */
public class SessionControllerTest {

    /** A creator can delete a room even after it has launched. */
    @Test
    public void creatorCanDeleteLaunchedSession() throws Exception {
        Sessions sessions = new Sessions();
        Session session = launchedSession("lxh");
        sessions.addSession(731204L, session);
        SessionController controller = controllerFor(sessions, session);

        ResponseEntity response = controller.removeSession(731204L, principal("lxh"));

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(sessions.isExistent(731204L));
    }

    /** A regular player cannot delete a room created by another player. */
    @Test
    public void nonCreatorCannotDeleteLaunchedSession() throws Exception {
        Sessions sessions = new Sessions();
        Session session = launchedSession("lxh");
        sessions.addSession(731204L, session);
        SessionController controller = controllerFor(sessions, session);

        ResponseEntity response = controller.removeSession(731204L, principal("xyj"));

        assertEquals(403, response.getStatusCodeValue());
        assertTrue(sessions.isExistent(731204L));
    }

    private SessionController controllerFor(Sessions sessions, Session session) throws Exception {
        SessionController controller = new SessionController(sessions);
        controller.tokenController = mock(TokenController.class);
        when(controller.tokenController.currentUserRole()).thenReturn(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_PLAYER")));
        controller.gameServers = mock(GameServers.class);
        when(controller.gameServers.getGameServerParameters(session.getGameName()))
                .thenReturn(session.getGameParameters());

        Field field = SessionController.class.getDeclaredField("sessionSpecificBroadcastManagers");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, BroadcastContentManager<Session>> managers =
                (Map<Long, BroadcastContentManager<Session>>) field.get(controller);
        managers.put(731204L, new BroadcastContentManager<>(session));
        return controller;
    }

    private Session launchedSession(String creator) throws RegistryException {
        GameServerParameters parameters = new GameServerParameters(
                "splendor_base", "Splendor Classic", "", 2, 4, "true");
        Session session = new Session(creator, parameters, "");
        session.markAsLaunched();
        return session;
    }

    private Principal principal(String name) {
        return () -> name;
    }
}


