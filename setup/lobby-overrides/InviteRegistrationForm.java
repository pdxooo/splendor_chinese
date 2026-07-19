package eu.kartoffelquadrat.ls.accountmanager.controller;

/** Public player registration request protected by an invitation code. */
public class InviteRegistrationForm {

    private String name;
    private String password;
    private String preferredColour;
    private String inviteCode;

    /** Required by the JSON deserializer. */
    public InviteRegistrationForm() {
    }

    /** Creates a complete invitation registration request. */
    public InviteRegistrationForm(String name, String password, String preferredColour,
                                  String inviteCode) {
        this.name = name;
        this.password = password;
        this.preferredColour = preferredColour;
        this.inviteCode = inviteCode;
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

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
