package com.zhonghcc.ltrpc.protocal.serializer;

import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;

public interface LtRpcSerializer {

    public <T> byte[] serialize(T obj, Class<T> clazz);

    public <T> T deserialize(byte[] data, Class<T> clazz);


}
