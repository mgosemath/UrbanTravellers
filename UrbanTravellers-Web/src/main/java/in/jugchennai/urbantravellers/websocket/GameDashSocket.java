/*
 * Copyright 2013 JUGChennai.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.jugchennai.urbantravellers.websocket;

import in.jugchennai.urbantravellers.game.GameBoard;
import in.jugchennai.urbantravellers.game.GameCache;
import in.jugchennai.urbantravellers.game.GameStatus;
import static in.jugchennai.urbantravellers.websocket.UTSocket.cache;
import java.io.IOException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author prasannakumar
 */
@ServerEndpoint(value = "/UTGameDashSocket",
        encoders = {DataEncoder.class},
        decoders = {DataDecoder.class})
public class GameDashSocket extends UTSocket {

    /**
     *
     * @param gd
     * @param peer
     * @throws IOException
     * @throws EncodeException
     * @throws JSONException
     */
    @OnMessage
    public void broadCastMessage(GameData gd, Session peer)
            throws IOException, EncodeException, JSONException, Exception {
        gd.setJson(prepareGameDash(gd));
        peer.getBasicRemote().sendObject(gd);
    }

    /**
     *
     * @param gd
     * @return
     * @throws JSONException
     */
    private JSONObject prepareGameDash(GameData gd)
            throws JSONException, Exception {
        JSONObject jSONObject = gd.getJson();
        GameBoard board = cache.getBoard();
        if (!board.getGameStatus().equals(GameStatus.HAPPENING)) {
            jSONObject.put("gameId", GameCache.GAME_ID);
            jSONObject.put("players", board.getCurrentPlayersOnBoard());
        }
        jSONObject.put("gameStatus", board.getGameStatus().name());
        jSONObject.put("type", "loadGames");
        return jSONObject;
    }

    @OnOpen
    public void onOpen(Session peer) throws Exception {
        peers.add(peer);
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }
}
