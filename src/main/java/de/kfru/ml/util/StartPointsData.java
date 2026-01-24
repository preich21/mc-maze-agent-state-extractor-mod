package de.kfru.ml.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
          Codec.FLOAT.fieldOf("pitch").forGetter(StartPoint::pitch),
          Codec.INT.fieldOf("goal_x").forGetter(StartPoint::goalX),
          Codec.INT.fieldOf("goal_y").forGetter(StartPoint::goalY),
          Codec.INT.fieldOf("goal_z").forGetter(StartPoint::goalZ)
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

  public record StartPoint(UUID id, float weight, int x, int y, int z, float yaw, float pitch, int goalX, int goalY, int goalZ) {

    public StartPoint(float weight, BlockPos startPoint, float yaw, float pitch, BlockPos goalPoint) {
      this(UUID.randomUUID(), weight, startPoint.getX(), startPoint.getY(), startPoint.getZ(), yaw, pitch, goalPoint.getX(), goalPoint.getY(), goalPoint.getZ());
    }

    public StartPoint(Float weight, Integer x, Integer y, Integer z, Float yaw, Float pitch, Integer goalX, Integer goalY, Integer goalZ) {
      this(UUID.randomUUID(), weight, x, y, z, yaw, pitch, goalX, goalY, goalZ);
    }

    public BlockPos toBlockPos() {
      return new BlockPos(x, y, z);
    }

    @Override
    public @NotNull String toString() {
      return "x=" + x + ", y=" + y + ", z=" + z + "(weight " + weight + ")";
    }
  }

  public void addStartPoint(StartPoint point) {
    startPoints.add(point);
    setDirty(true);
  }

  public void updateStartPoint(StartPoint point) {
    Optional<StartPoint> existingPoint = startPoints.stream().filter(p -> p.x == point.x && p.y == point.y && p.z == point.z).findFirst();
    if (existingPoint.isEmpty()) {
      throw new IllegalArgumentException("No start point found at position x=" + point.x + ", y=" + point.y + ", z=" + point.z);
    }
    startPoints.set(startPoints.indexOf(existingPoint.get()), point);
    setDirty(true);
  }

  public void clear() {
    startPoints.clear();
    setDirty(true);
  }

  public List<StartPoint> getStartPoints() {
    return Collections.unmodifiableList(startPoints);
  }

  public boolean containsStartPoint(int x, int y, int z) {
    return startPoints.stream().anyMatch(p -> p.x == x && p.y == y && p.z == z);
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
