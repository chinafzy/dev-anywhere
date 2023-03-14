package me.in1978.tools.anywhere.util;

import java.io.Serializable;

public interface CloneBySerializable<T extends CloneBySerializable<T>> extends Serializable {

    default T clone2() {
        return Utils.cloneBySer((T) this);
    }

}