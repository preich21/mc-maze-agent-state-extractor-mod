package de.kfru.ml.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * See: https://docs.fabricmc.net/develop/saved-data
 * (this is named differently via yarn but has the same functionality)
 */
@Getter
public class StartPointsData extends PersistentState {

  public static final Codec<StartPoint> START_POINT_CODEC =
      RecordCodecBuilder.create(instance -> instance.group(
//          Codec.STRING.fieldOf("id").forGetter(startPoint -> startPoint.id.toString()),
          Codec.FLOAT.fieldOf("weight").forGetter(StartPoint::weight),
          Codec.INT.fieldOf("x").forGetter(StartPoint::x),
          Codec.INT.fieldOf("y").forGetter(StartPoint::y),
          Codec.INT.fieldOf("z").forGetter(StartPoint::z),
          Codec.FLOAT.fieldOf("yaw").forGetter(StartPoint::yaw),
          Codec.FLOAT.fieldOf("pitch").forGetter(StartPoint::pitch)
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
    this.startPoints = new ArrayList<>();
  }

  public StartPointsData(List<StartPoint> startPoints) {
    this.startPoints = new ArrayList<>(startPoints);
  }

  public record StartPoint(UUID id, float weight, int x, int y, int z, float yaw, float pitch) {

    public StartPoint(float weight, int x, int y, int z, float yaw, float pitch) {
      this(UUID.randomUUID(), weight, x, y, z, yaw, pitch);
    }

    @Override
    public @NotNull String toString() {
      return "x=" + x + ", y=" + y + ", z=" + z;
    }
  }

  public void addStartPoint(StartPoint point) {
    startPoints.add(point);
    setDirty(true);
  }

  public void clear() {
    startPoints.clear();
    setDirty(true);
  }

  public List<StartPoint> getStartPoints() {
    return Collections.unmodifiableList(startPoints);
  }

  public void setStartPoints(List<StartPoint> points) {
    startPoints.clear();
    startPoints.addAll(points);
    setDirty(true);
  }

  public static StartPointsData getSavedBlockData(MinecraftServer server) {
    if (server == null || server.getOverworld() == null) return null;
    PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
    return manager.getOrCreate(TYPE);
  }

  @Override
  public String toString() {
    return this.startPoints.stream().map(StartPoint::toString).collect(Collectors.joining("\n"));
  }
}
