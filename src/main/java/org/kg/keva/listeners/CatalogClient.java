/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kg.keva.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

/**
 *
 * @author Kapil
 */
@ClientEndpoint
public class CatalogClient {
    
    ObservableList<String> _idstore = FXCollections.observableArrayList();
    Map<String, Item> _itemStore = new HashMap<>();
    private final Gson gson = new Gson();
    
    RemoteEndpoint.Basic remoteEndpoint;
    
    @OnOpen
    public void onConnect(Session session) {
        remoteEndpoint = session.getBasicRemote();
    }
    
    public void saveCatalog(String name, String price) throws IOException {
        if (name != null) {
            Item i = new Item(name, price);
            remoteEndpoint.sendText(gson.toJson(i));
        } else {
            remoteEndpoint.sendText("");
        }
    }
    
    public ObservableList<String> getLocalCatalog() {
        return _idstore;
    }
    
    @OnMessage
    public void onMessage(String s) {
        JsonReader greader = new JsonReader(new StringReader(s));
        JsonParser gparser = new JsonParser();
        JsonArray garray = gparser.parse(greader).getAsJsonArray();
        Iterator<JsonElement> iterator = garray.iterator();
        while (iterator.hasNext()) {
            final Item i = gson.fromJson(iterator.next(), Item.class);
            if (!_itemStore.containsKey(i.getName())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        _idstore.add(i.getName());
                    }
                });
                
                _itemStore.put(i.getName(), i);
            }
        }
        
    }
    
    public class Item {
        
        String name;
        String price;
        
        public Item() {
        }
        
        public Item(String name, String price) {
            this.name = name;
            this.price = price;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getPrice() {
            return price;
        }
        
        public void setPrice(String price) {
            this.price = price;
        }
        
    }
    
}
