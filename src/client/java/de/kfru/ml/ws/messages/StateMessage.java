package de.kfru.ml.ws.messages;

import de.kfru.ml.action.PlayerActions;
import de.kfru.ml.state.FieldOfView;
import de.kfru.ml.state.PlayerState;
import lombok.Builder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

@Builder(builderClassName = "StateMessageBuilder")
public class StateMessage extends OutgoingMessage {

    private final MessageType type = MessageType.OBSERVATION;
    private long tick;

    private Long actionStartedTick;
//    private ActionMessage activeActionRequest;

    private int x;
    private int y;
    private int z;

    private double yaw; // horizontal rotation - 360° to left and right possible
    private double pitch; // vertical rotation - max 90° up and down

    private boolean died;
    private boolean hasGroundBelow;
    private int standingOn;

    private List<Double> fovDistances;
    private List<Integer> fovBlocks;

    @SuppressWarnings("unused")
    public static class StateMessageBuilder {
        public StateMessageBuilder playerState(final PlayerState state) {
            this.x = (int) state.position().x();
            this.y = (int) state.position().y();
            this.z = (int) state.position().z();
            this.yaw = state.facing().yaw();
            this.pitch = state.facing().pitch();
            this.standingOn = state.standingOn().id;
            final FieldOfView fov = state.fieldOfView();
            this.fovDistances = fov.getDistances();
            this.fovBlocks = fov.getBlocks();
//            System.out.println("FOV blocks contains goal: " + this.fovBlocks.contains(BlockType.GOAL_BLOCK.id));
            this.died = state.died().died();
            this.hasGroundBelow = state.hasGroundBelow().hasGroundBelow();
            return this;
        }

        public StateMessageBuilder activeActions(final PlayerActions activeActions) {
            this.actionStartedTick = activeActions.getActionStartTick();
//            this.activeActionRequest = activeActions.getActiveActionRequest();
            return this;
        }
    }

    public byte[] toBytes() {
        int size =
            Long.BYTES * 2 +   // tick + actionStartedTick
                Integer.BYTES * 3 + // x, y, z
                Float.BYTES * 2 + // yaw, pitch
                Byte.BYTES * 3 + // died, hasGroundBelow, standingOn
                fovBlocks.size() * Short.BYTES + // fovDistances as float16
                fovBlocks.size() * Byte.BYTES; // fovBlocks as byte
        ByteBuffer buf = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);

        buf.putLong(tick);
        buf.putLong(actionStartedTick != null ? actionStartedTick : 0L);

        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(z);

        buf.putFloat((float) yaw);
        buf.putFloat((float) pitch);

        buf.put((byte) (died ? 1 : 0));
        buf.put((byte) (hasGroundBelow ? 1 : 0));
        buf.put((byte) standingOn);

        // fovDistances -> float16
        for (Double d : fovDistances) {
            float f = (d != null) ? d.floatValue() : -1f;
            short h = floatToHalf(f);
            buf.putShort(h);
        }

        // fovBlocks -> byte
        for (Integer b : fovBlocks) {
            buf.put((byte) (b != null ? b : 0));
        }

        return buf.array();
    }

    public static short floatToHalf(float f) {
        int bits = Float.floatToIntBits(f);
        int sign = (bits >>> 16) & 0x8000;
        int exp = ((bits >>> 23) & 0xff) - 127 + 15;
        int mant = bits & 0x7fffff;

        if (exp <= 0) return (short) sign;
        if (exp >= 31) return (short) (sign | 0x7c00);

        return (short) (sign | (exp << 10) | (mant >> 13));
    }
}
