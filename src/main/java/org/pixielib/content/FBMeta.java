package org.pixielib.content;// automatically generated, do not modify

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class FBMeta extends Table {
  public static FBMeta getRootAsFBMeta(ByteBuffer _bb) { return getRootAsFBMeta(_bb, new FBMeta()); }
  public static FBMeta getRootAsFBMeta(ByteBuffer _bb, FBMeta obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public FBMeta __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String name() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer nameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public String value() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }

  public static int createFBMeta(FlatBufferBuilder builder,
      int nameOffset,
      int valueOffset) {
    builder.startObject(2);
    FBMeta.addValue(builder, valueOffset);
    FBMeta.addName(builder, nameOffset);
    return FBMeta.endFBMeta(builder);
  }

  public static void startFBMeta(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addName(FlatBufferBuilder builder, int nameOffset) { builder.addOffset(0, nameOffset, 0); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(1, valueOffset, 0); }
  public static int endFBMeta(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

