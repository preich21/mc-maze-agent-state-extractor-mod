package de.kfru.ml.ws.messages;

import de.kfru.ml.util.StartPointsData;
import lombok.Builder;

import java.util.List;

@Builder(builderClassName = "HelloMessageBuilder")
public class HelloMessage extends OutgoingMessage {

  private final MessageType type = MessageType.HELLO;
  private List<StartPointsData.StartPoint> startPoints;

  public HelloMessage(final List<StartPointsData.StartPoint> startPoints) {
    this.startPoints = startPoints;
  }

}
