package org.pixielib.content;// automatically generated, do not modify

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class FBEvent extends Table {
  public static FBEvent getRootAsFBEvent(ByteBuffer _bb) { return getRootAsFBEvent(_bb, new FBEvent()); }
  public static FBEvent getRootAsFBEvent(ByteBuffer _bb, FBEvent obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public FBEvent __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String name() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer nameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public long sequence() { int o = __offset(6); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public long initialSequence() { int o = __offset(8); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public String source() { int o = __offset(10); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer sourceAsByteBuffer() { return __vector_as_bytebuffer(10, 1); }
  public String operationId() { int o = __offset(12); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer operationIdAsByteBuffer() { return __vector_as_bytebuffer(12, 1); }
  public long processId() { int o = __offset(14); return o != 0 ? bb.getLong(o + bb_pos) : 0; }
  public String sessionId() { int o = __offset(16); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer sessionIdAsByteBuffer() { return __vector_as_bytebuffer(16, 1); }
  public String timeStamp() { int o = __offset(18); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer timeStampAsByteBuffer() { return __vector_as_bytebuffer(18, 1); }
  public String timeZoneName() { int o = __offset(20); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer timeZoneNameAsByteBuffer() { return __vector_as_bytebuffer(20, 1); }
  public String userId() { int o = __offset(22); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer userIdAsByteBuffer() { return __vector_as_bytebuffer(22, 1); }
  public String treeChildren(int j) { int o = __offset(24); return o != 0 ? __string(__vector(o) + j * 4) : null; }
  public int treeChildrenLength() { int o = __offset(24); return o != 0 ? __vector_len(o) : 0; }
  public FBMeta metadata(int j) { return metadata(new FBMeta(), j); }
  public FBMeta metadata(FBMeta obj, int j) { int o = __offset(26); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int metadataLength() { int o = __offset(26); return o != 0 ? __vector_len(o) : 0; }

  public static int createFBEvent(FlatBufferBuilder builder,
      int nameOffset,
      long sequence,
      long initial_sequence,
      int sourceOffset,
      int operation_idOffset,
      long process_id,
      int session_idOffset,
      int time_stampOffset,
      int time_zone_nameOffset,
      int user_idOffset,
      int tree_childrenOffset,
      int metadataOffset) {
    builder.startObject(12);
    FBEvent.addProcessId(builder, process_id);
    FBEvent.addInitialSequence(builder, initial_sequence);
    FBEvent.addSequence(builder, sequence);
    FBEvent.addMetadata(builder, metadataOffset);
    FBEvent.addTreeChildren(builder, tree_childrenOffset);
    FBEvent.addUserId(builder, user_idOffset);
    FBEvent.addTimeZoneName(builder, time_zone_nameOffset);
    FBEvent.addTimeStamp(builder, time_stampOffset);
    FBEvent.addSessionId(builder, session_idOffset);
    FBEvent.addOperationId(builder, operation_idOffset);
    FBEvent.addSource(builder, sourceOffset);
    FBEvent.addName(builder, nameOffset);
    return FBEvent.endFBEvent(builder);
  }

  public static void startFBEvent(FlatBufferBuilder builder) { builder.startObject(12); }
  public static void addName(FlatBufferBuilder builder, int nameOffset) { builder.addOffset(0, nameOffset, 0); }
  public static void addSequence(FlatBufferBuilder builder, long sequence) { builder.addLong(1, sequence, 0); }
  public static void addInitialSequence(FlatBufferBuilder builder, long initialSequence) { builder.addLong(2, initialSequence, 0); }
  public static void addSource(FlatBufferBuilder builder, int sourceOffset) { builder.addOffset(3, sourceOffset, 0); }
  public static void addOperationId(FlatBufferBuilder builder, int operationIdOffset) { builder.addOffset(4, operationIdOffset, 0); }
  public static void addProcessId(FlatBufferBuilder builder, long processId) { builder.addLong(5, processId, 0); }
  public static void addSessionId(FlatBufferBuilder builder, int sessionIdOffset) { builder.addOffset(6, sessionIdOffset, 0); }
  public static void addTimeStamp(FlatBufferBuilder builder, int timeStampOffset) { builder.addOffset(7, timeStampOffset, 0); }
  public static void addTimeZoneName(FlatBufferBuilder builder, int timeZoneNameOffset) { builder.addOffset(8, timeZoneNameOffset, 0); }
  public static void addUserId(FlatBufferBuilder builder, int userIdOffset) { builder.addOffset(9, userIdOffset, 0); }
  public static void addTreeChildren(FlatBufferBuilder builder, int treeChildrenOffset) { builder.addOffset(10, treeChildrenOffset, 0); }
  public static int createTreeChildrenVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startTreeChildrenVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addMetadata(FlatBufferBuilder builder, int metadataOffset) { builder.addOffset(11, metadataOffset, 0); }
  public static int createMetadataVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startMetadataVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endFBEvent(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishFBEventBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
};

