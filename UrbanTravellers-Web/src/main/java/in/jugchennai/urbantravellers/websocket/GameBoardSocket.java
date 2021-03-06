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
import javax.net.websocket.annotations.WebSocketMessage;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * GameBoard web-socket
 *
 * @author Prasanna Kumar <prassee.sathian@gmail.com>
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@ServerEndpoint(value = "/UTGameSocket",
encoders = {DataEncoder.class},
decoders = {DataDecoder.class})
public class GameBoardSocket extends UTSocket {

    private static final Logger logger = Logger.getLogger(GameBoardSocket.class);
    // the getInstance method does some bootstrap activities

    /**
     * the following block of code might be moved to a JSF handler and it has to
     * be removed from here
     *
     * @param peer
     * @throws java.lang.Exception
     * @Deprecated
     *
     * static { try { // replace the GameCache.GAME_ID with id obtained from DB
     * cache.addBoard(GameCache.GAME_ID,
     * GameBoardFactory.createGameBoard("brandNewGame", 50, 2, 6)); GameBoard
     * board = cache.getBoard(GameCache.GAME_ID);
     *
     * // except the following lines board.addPlayerToBoard(new Player("Pras"));
     * board.addPlayerToBoard(new Player("Raj")); board.addPlayerToBoard(new
     * Player("Shiv"));
     *
     * } catch (Exception ex) { logger.error("exception while configuring game "
     * + ex); } }
     *
     */
    @OnOpen
    public void onOpen(Session peer) throws Exception {
        logger.info("added player to session ");
        peers.add(peer);
    }

    @OnClose
    public void onClose(Session peer) {
        logger.info("removing player from session");
        peers.remove(peer);
    }

    /**
     * method to send the result of rolling the dice to all players
     *
     * @param gd
     * @param peer
     * @throws IOException
     * @throws EncodeException
     */
    @OnMessage
    public void broadCastMessage(GameData gd, Session peer)
            throws IOException, EncodeException {
        try {
            logger.info("Broadcast Game Data:" + gd);
            String playerName = gd.getJson().getString("playerName");
            String diceValue = gd.getJson().getString("diceValue");
            String type = gd.getJson().getString("type");

            GameBoard board = cache.getBoard();
            Player player = board.movePlayerPosition(playerName,
                    Integer.parseInt(diceValue));
            GameData gamedata = new GameData();
            JSONObject json = new JSONObject();
            json.put("players", board.getPlayersOnBoard());
            json.put("player", player.getName());
            json.put("position", player.getPosition());
            json.put("diceValue", diceValue);
            //json.put("currentplayer", board.getNextPlayer(playerName));
            json.put("type", type);
            gamedata.setJson(json);
            System.out.println("JSON "+json);
            if(type.equals("getPlayers"))
            {
                peer.getBasicRemote().sendObject(gamedata);
            }
            else
            {
                for (Session currPeer : peers) {
                    currPeer.getBasicRemote().sendObject(gamedata);
                }
            }
        } catch (JSONException ex) {
            logger.error("json exception has occured" + ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (NumberFormatException ex) {
            logger.error(ex.getMessage());
        } catch (EncodeException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * send signal change msg to connected browsers
     *
     * @param name
     * @throws JSONException
     * @throws IOException
     * @throws EncodeException
     */
    @WebSocketMessage
    public void sendSignalChange(String name) throws
            JSONException, IOException, EncodeException {
        GameData gamedata = new GameData();
        JSONObject json = new JSONObject();
        json.put("notification", name);
        for (Session currPeer : peers) {
            currPeer.getBasicRemote().sendObject(gamedata);
        }
    }
}

/**
 * A game board endpoint factory
 *
 * @author prasannakumar
 */
//class GameBoardEndpointFactory implements EndpointFactory {
//
//    private Logger logger = Logger.getLogger(this.getClass());
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public Object createEndpoint() {
//        logger.info("creating new end point");
//        return null;
//    }
//}
