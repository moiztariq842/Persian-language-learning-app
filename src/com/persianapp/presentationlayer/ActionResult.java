package com.persianapp.presentationlayer;

/**
 * Outcome of a user action, with a message the screen can show.
 * Keeps business-layer exceptions away from the Swing code.
 */
public final class ActionResult {

    private final boolean success;
    private final String  message;

    private ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ActionResult ok(String message)    { return new ActionResult(true,  message); }
    public static ActionResult error(String message) { return new ActionResult(false, message); }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
