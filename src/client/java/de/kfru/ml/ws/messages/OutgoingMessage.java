package de.kfru.ml.ws.messages;


import com.google.gson.Gson;

public abstract class OutgoingMessage {

  private static final Gson GSON = new Gson();

  public String toJson() {
    return GSON.toJson(this);
  }

}
