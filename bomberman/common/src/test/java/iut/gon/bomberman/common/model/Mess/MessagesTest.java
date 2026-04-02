package iut.gon.bomberman.common.model.Mess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * Tests unitaires pour les messages du package {@link iut.gon.bomberman.common.model.Mess}
 *
 * Cette classe de test vérifie :
 * - L'instanciation des différents types de requêtes et de réponses.
 * - Le bon retour du type de message défini dans l'énumération MessageType.
 * - La récupération correcte des attributs par les getters.
 */
@DisplayName("Tests des messages réseau")
public class MessagesTest {

    @Nested
    @DisplayName("Tests pour ChatMessage")
    class ChatMessageTests {
        @Test
        @DisplayName("Le type de message doit être CHAT_MESSAGE")
        void testType() {
            ChatMessage msg = new ChatMessage("Player1", "Hello", 10);
            assertEquals(MessageType.CHAT_MESSAGE, msg.getType());
        }

        @Test
        @DisplayName("Les attributs doivent être correctement assignés")
        void testAttributes() {
            ChatMessage msg = new ChatMessage("Player1", "Hello", 10);
            assertEquals("Player1", msg.getSenderName());
            assertEquals("Hello", msg.getContent());
            assertEquals(10, msg.getLobbyId());
        }
    }

    @Nested
    @DisplayName("Tests pour CreateLobbyRequest")
    class CreateLobbyRequestTests {
        @Test
        @DisplayName("Le type de message doit être CREATE_LOBBY_REQUEST")
        void testType() {
            CreateLobbyRequest req = new CreateLobbyRequest("MyLobby", "Player1", 4, TypeLab.DEEPSEARCH, 15, 13);
            assertEquals(MessageType.CREATE_LOBBY_REQUEST, req.getType());
        }

        @Test
        @DisplayName("Les attributs de configuration du lobby doivent être correctement assignés")
        void testAttributes() {
            CreateLobbyRequest req = new CreateLobbyRequest("MyLobby", "Player1", 4, TypeLab.DEEPSEARCH, 15, 13);
            assertEquals("MyLobby", req.getLobbyName());
            assertEquals("Player1", req.getPlayerName());
            assertEquals(4, req.getMaxPlayers());
            assertEquals(TypeLab.DEEPSEARCH, req.getLabyrintheType());
            assertEquals(15, req.getSizeX());
            assertEquals(13, req.getSizeY());
        }
    }

    @Nested
    @DisplayName("Tests pour CreateLobbyResponse")
    class CreateLobbyResponseTests {
        @Test
        @DisplayName("Le type de message doit être CREATE_LOBBY_RESPONSE")
        void testType() {
            CreateLobbyResponse res = new CreateLobbyResponse(true, "Created", 123);
            assertEquals(MessageType.CREATE_LOBBY_RESPONSE, res.getType());
        }

        @Test
        @DisplayName("Les attributs de la réponse doivent être correctement assignés")
        void testAttributes() {
            CreateLobbyResponse res = new CreateLobbyResponse(true, "Created", 123);
            assertTrue(res.isSuccess());
            assertEquals("Created", res.getMessage());
            assertEquals(123, res.getLobbyId());
        }
    }

    @Nested
    @DisplayName("Tests pour GameUpdate")
    class GameUpdateTests {
        @Test
        @DisplayName("Le type de message doit être GAME_UPDATE")
        void testType() {
            GameUpdate update = new GameUpdate(new HashMap<>());
            assertEquals(MessageType.GAME_UPDATE, update.getType());
        }

        @Test
        @DisplayName("Les positions des joueurs doivent être correctement stockées")
        void testAttributes() {
            Map<Integer, GameUpdate.PlayerPositionDTO> positions = new HashMap<>();
            positions.put(1, new GameUpdate.PlayerPositionDTO(5, 5, "UP"));
            GameUpdate update = new GameUpdate(positions);
            assertEquals(1, update.getPlayerPositions().size());
            assertEquals(5, update.getPlayerPositions().get(1).x);
            assertEquals(5, update.getPlayerPositions().get(1).y);
            assertEquals("UP", update.getPlayerPositions().get(1).direction);
        }
    }

    @Nested
    @DisplayName("Tests pour JoinLobbyRequest & JoinLobbyResponse")
    class JoinLobbyTests {
        @Test
        @DisplayName("JoinLobbyRequest doit avoir les bons attributs et le type JOIN_LOBBY_REQUEST")
        void testJoinLobbyRequest() {
            JoinLobbyRequest req = new JoinLobbyRequest(42, "Player2");
            assertEquals(MessageType.JOIN_LOBBY_REQUEST, req.getType());
            assertEquals(42, req.getLobbyId());
            assertEquals("Player2", req.getPlayerName());
        }

        @Test
        @DisplayName("JoinLobbyResponse doit avoir les bons attributs et le type JOIN_LOBBY_RESPONSE")
        void testJoinLobbyResponse() {
            JoinLobbyResponse res = new JoinLobbyResponse(false, "Full", 42);
            assertEquals(MessageType.JOIN_LOBBY_RESPONSE, res.getType());
            assertFalse(res.isSuccess());
            assertEquals("Full", res.getMessage());
            assertEquals(42, res.getLobbyId());
        }
    }

    @Nested
    @DisplayName("Tests pour LobbyDetailsRequest & LobbyDetailsResponse")
    class LobbyDetailsTests {
        @Test
        @DisplayName("LobbyDetailsRequest doit avoir les bons attributs")
        void testLobbyDetailsRequest() {
            LobbyDetailsRequest req = new LobbyDetailsRequest(12);
            assertEquals(MessageType.LOBBY_DETAILS_REQUEST, req.getType());
            assertEquals(12, req.getLobbyId());
        }

        @Test
        @DisplayName("LobbyDetailsResponse doit encapsuler correctement le propriétaire et les joueurs")
        void testLobbyDetailsResponse() {
            LobbyDetailsResponse.PlayerDTO owner = new LobbyDetailsResponse.PlayerDTO(1, "Owner", true, true);
            List<LobbyDetailsResponse.PlayerDTO> players = new ArrayList<>();
            players.add(owner);
            LobbyDetailsResponse res = new LobbyDetailsResponse(12, "Lobby12", 4, owner, players);
            
            assertEquals(MessageType.LOBBY_DETAILS_RESPONSE, res.getType());
            assertEquals(12, res.getLobbyId());
            assertEquals("Lobby12", res.getLobbyName());
            assertEquals(4, res.getMaxPlayers());
            assertEquals(owner, res.getOwner());
            assertEquals(1, res.getPlayers().size());
            assertEquals("Owner", res.getPlayers().getFirst().name);
        }
    }

    @Nested
    @DisplayName("Tests pour LobbyListRequest & LobbyListResponse")
    class LobbyListTests {
        @Test
        @DisplayName("LobbyListRequest doit retourner le bon type")
        void testLobbyListRequest() {
            LobbyListRequest req = new LobbyListRequest();
            assertEquals(MessageType.LOBBY_LIST_REQUEST, req.getType());
        }

        @Test
        @DisplayName("LobbyListResponse doit conserver correctement la liste de lobbys")
        void testLobbyListResponse() {
            List<LobbyListResponse.LobbyDTO> lobbies = new ArrayList<>();
            lobbies.add(new LobbyListResponse.LobbyDTO(1, "Lobby1", 1, 4));
            LobbyListResponse res = new LobbyListResponse(lobbies);
            
            assertEquals(MessageType.LOBBY_LIST_RESPONSE, res.getType());
            assertEquals(1, res.getLobbies().size());
            assertEquals("Lobby1", res.getLobbies().getFirst().name);
        }
    }

    @Nested
    @DisplayName("Tests pour les autres requêtes (Move, Ready, StartGame)")
    class OtherRequestsTests {
        @Test
        @DisplayName("MoveRequest doit conserver les bonnes coordonnées et la direction")
        void testMoveRequest() {
            MoveRequest req = new MoveRequest(10, 20, "LEFT");
            assertEquals(MessageType.MOVE_REQUEST, req.getType());
            assertEquals(10, req.getX());
            assertEquals(20, req.getY());
            assertEquals("LEFT", req.getDirection());
        }

        @Test
        @DisplayName("ReadyStatus doit conserver le statut de préparation")
        void testReadyStatus() {
            ReadyStatus status = new ReadyStatus(true, 7);
            assertEquals(MessageType.READY_STATUS, status.getType());
            assertTrue(status.isReady());
            assertEquals(7, status.getLobbyId());
        }

        @Test
        @DisplayName("StartGameRequest doit retourner le bon type et l'identifiant du lobby")
        void testStartGameRequest() {
            StartGameRequest req = new StartGameRequest(45);
            assertEquals(MessageType.START_GAME_REQUEST, req.getType());
            assertEquals(45, req.getLobbyId());
        }
    }
}
