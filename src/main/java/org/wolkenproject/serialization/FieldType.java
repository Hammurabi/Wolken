package org.wolkenproject.serialization;

public enum FieldType {
    none,
    base,

    int8,
    int16,
    int32,
    int64,
    int128,
    int256,

    uint8,
    uint16,
    uint32,
    uint64,
    uint128,
    uint256,

    var8ui,
    var16ui,
    var32ui,
    var64ui,
    var128ui,
    var256ui,

    hash256,
    hash160,

    bytes,

    serializable
}
