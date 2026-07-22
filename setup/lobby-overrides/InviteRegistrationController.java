package eu.kartoffelquadrat.ls.accountmanager.controller;

import eu.kartoffelquadrat.ls.accountmanager.model.Player;
import eu.kartoffelquadrat.ls.accountmanager.model.PlayerRepository;
import eu.kartoffelquadrat.ls.accountmanager.model.Role;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Allows invited users to create ordinary player accounts without administrator access. */
@RestController
public class InviteRegistrationController {

    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String expectedInviteCode;

    /** Creates the controller with the server-side invitation secret. */
    @Autowired
    public InviteRegistrationController(PlayerRepository playerRepository,
                                        BCryptPasswordEncoder passwordEncoder,
                                        @Value("${SPLENDOR_INVITE_CODE:}")
                                        String expectedInviteCode) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.expectedInviteCode = expectedInviteCode;
    }

    /** Registers a normal player when the supplied invitation code is valid. */
    @PostMapping(value = "/api/registration", consumes = "application/json")
    public ResponseEntity register(@RequestBody InviteRegistrationForm form) {
        if (expectedInviteCode == null || expectedInviteCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("自助注册暂未启用。");
        }
        if (form == null || !inviteMatches(form.getInviteCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("邀请码不正确。");
        }

        AccountForm account = new AccountForm(form.getName(), form.getPassword(),
                form.getPreferredColour(), Role.ROLE_PLAYER);
        try {
            account.validate();
            if (playerRepository.findById(account.getName()).isPresent()) {
                throw new AccountException("用户名已被使用。");
            }
            Player player = new Player(account.getName(), account.getPreferredColour(),
                    passwordEncoder.encode(account.getPassword()), Role.ROLE_PLAYER);
            playerRepository.save(player);
            return ResponseEntity.ok("注册成功，请登录。");
        } catch (AccountException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名已被使用。");
        }
    }

    private boolean inviteMatches(String suppliedInviteCode) {
        if (suppliedInviteCode == null) {
            return false;
        }
        byte[] expected = expectedInviteCode.getBytes(StandardCharsets.UTF_8);
        byte[] supplied = suppliedInviteCode.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, supplied);
    }
}
