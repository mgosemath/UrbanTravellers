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
import in.jugchennai.urbantravellers.game.Player;
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
 * @author prasannakumar
 */
@ServerEndpoint(value = "/UTGameBootSocket",
        encoders = {DataEncoder.class},
        decoders = {DataDecoder.class})
public class GameBootSocket extends UTSocket {

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
        System.out.println("JSON RECEIVED");
        String gameID = gd.getJson().get("gameId").toString();
        String playername = gd.getJson().getString("playerName");
        
        // we may not need these lines 
        GameBoard board = cache.getBoard();
        board.addPlayerToBoard(new Player(playername));
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("players", board.getPlayersOnBoard());
        jSONObject.put("gameId", gameID);
        jSONObject.put("type", "notify");
        jSONObject.put("playerName", playername);
        if (board.getPlayersOnBoard().size() == 3) {
            jSONObject.put("startGame", true);
        }
        gd.setJson(jSONObject);
        System.out.println("JSON : "+gd.getJson());
        for (Session currPeer : peers) {
            currPeer.getBasicRemote().sendObject(gd);
        }
        System.out.println("JSON SENT");
    }

    @OnOpen
    public void onOpen(Session peer) throws Exception {
        peers.add(peer);
        System.out.println("player added");
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }
}
