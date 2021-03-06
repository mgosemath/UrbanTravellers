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

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @author Arivazhagan Ambigapathi <arivu86@gmail.com>
 */
public class GameData {

    private JSONObject json;
    private Logger logger = Logger.getLogger(this.getClass());
   //  private String[] UserData = new String[50];

    public GameData() {
        
    }
    public GameData(JSONObject json) {
        this.json = json;
        logger.info("In GameData" + json);
        logger.info("GameData Length" + json.length());
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    @Override
    public String toString() {
        try {
            return json.toString(2);
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }
}
