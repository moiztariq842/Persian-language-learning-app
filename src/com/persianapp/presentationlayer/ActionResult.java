package com.persianapp.presentationlayer;

// Result status and display message for an action.
public final class ActionResult {

    private final boolean success;
    private final String  message;

    private ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Creates a successful action result.
    public static ActionResult ok(String message) {
        return new ActionResult(true, message);
    }

    // Creates an error action result.
    public static ActionResult error(String message) {
        return new ActionResult(false, message);
    }

    // Returns true if the action succeeded.
    public boolean isSuccess() { return success; }

    // Returns the message.
    public String getMessage()  { return message; }
}
