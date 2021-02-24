/**
 * Abstract class defined as an interface to encapsulate the methods
 * which enable the broadcasting of messages
 */
interface Broadcasting {
    void broadcastToAllMembers(String message);

    void broadcastToAllOtherMembers(String message, ClientThread clientThread);

    void broadcastToParticularMember(String message, ClientThread clientThread);

    void broadcastToBot(String message);
}