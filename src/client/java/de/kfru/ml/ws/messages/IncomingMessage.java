package de.kfru.ml.ws.messages;

import lombok.Getter;

@Getter
public abstract class IncomingMessage {

    protected int episode;
    protected int step;
}
