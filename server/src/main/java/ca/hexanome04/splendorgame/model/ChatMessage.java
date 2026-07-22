package ca.hexanome04.splendorgame.model;

/** A short message sent by a player in a game room. */
public record ChatMessage(long id, String sender, String text, long timestampEpochMillis) {
}
