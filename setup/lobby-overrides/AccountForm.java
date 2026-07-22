package eu.kartoffelquadrat.ls.accountmanager.controller;

import eu.kartoffelquadrat.ls.accountmanager.model.Role;
import java.util.regex.Pattern;

/** Request data and validation for administrator-created accounts. */
public class AccountForm {

    String name;
    String password;
    String preferredColour;
    Role role;

    public AccountForm(String name, String password, String preferredColour, Role role) {
        this.name = name;
        this.password = password;
        this.preferredColour = preferredColour;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPreferredColour() {
        return preferredColour;
    }

    public void setPreferredColour(String preferredColour) {
        this.preferredColour = preferredColour;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /** Validate required account fields. */
    public void validate() throws AccountException {
        StringBuilder problems = new StringBuilder();
        if (name == null || name.trim().isEmpty()) {
            problems.append("Name must not be only whitespaces. ");
        }
        if (!validatePasswordString(password)) {
            problems.append("Password must not be empty. ");
        }
        if (!validateColourString(preferredColour)) {
            problems.append("Colour is not a valid hex-rgb string, e.g. 3A6C42. ");
        }
        if (problems.length() > 0) {
            throw new AccountException(problems.toString());
        }
    }

    /** Accept every password except a missing or empty string. */
    public static boolean validatePasswordString(String password) {
        return password != null && !password.isEmpty();
    }

    /** Validate a six-character uppercase hexadecimal colour. */
    public static boolean validateColourString(String colourString) {
        return colourString != null
                && Pattern.compile("(?:[0-9A-F]{6})").matcher(colourString).matches();
    }
}
