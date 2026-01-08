package de.kfru.ml.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;

/**
 * See: https://docs.fabricmc.net/develop/saved-data
 * (this is named differently via yarn but has the same functionality)
 */
@Getter
public class StartPointsData extends PersistentState {

  // TODO: this data needs to be initialized in the world.
  // best option would be to add a player command with which the player in the world can add start points:
  // similar to /spawnpoint but for our custom data structure e.g. "/startpoint"

  public static final Codec<StartPoint> START_POINT_CODEC =
      RecordCodecBuilder.create(instance -> instance.group(
          Codec.INT.fieldOf("x").forGetter(StartPoint::x),
          Codec.INT.fieldOf("y").forGetter(StartPoint::y),
          Codec.INT.fieldOf("z").forGetter(StartPoint::z)
      ).apply(instance, StartPoint::new));

  public static final Codec<StartPointsData> CODEC =
      START_POINT_CODEC.listOf().xmap(StartPointsData::new, StartPointsData::getStartPoints);


  private static final PersistentStateType<StartPointsData> TYPE = new PersistentStateType<>(
      "start_points",
      StartPointsData::new,
      CODEC,
      null
  );

  private final List<StartPoint> startPoints;


  public StartPointsData() {
    startPoints = new ArrayList<>();
  }

  public StartPointsData(List<StartPoint> startPoints) {
    this.startPoints = startPoints;
  }


  public record StartPoint(int x, int y, int z) {
  }

  public void addStartPoint(StartPoint point) {
    startPoints.add(point);
    setDirty(true);
  }

  public void clear() {
    startPoints.clear();
    setDirty(true);
  }

  public void setStartPoints(List<StartPoint> points) {
    startPoints.clear();
    startPoints.addAll(points);
    setDirty(true);
  }

  public static StartPointsData getSavedBlockData(MinecraftServer server) {
    StartPointsData startPointsData = server.getOverworld().getPersistentStateManager().get(TYPE);
    return startPointsData;
  }

}


