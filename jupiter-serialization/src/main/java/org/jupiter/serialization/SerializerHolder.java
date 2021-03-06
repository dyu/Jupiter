/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jupiter.serialization;

import org.jupiter.common.util.JServiceLoader;
import org.jupiter.common.util.collection.ByteObjectHashMap;
import org.jupiter.common.util.collection.ByteObjectMap;
import org.jupiter.common.util.internal.logging.InternalLogger;
import org.jupiter.common.util.internal.logging.InternalLoggerFactory;

import java.util.List;

import static org.jupiter.serialization.SerializerType.*;

/**
 * Holds a singleton serializer.
 *
 * jupiter
 * org.jupiter.serialization
 *
 * @author jiachun.fjc
 */
public final class SerializerHolder {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SerializerHolder.class);

    private static final ByteObjectMap<Serializer> serializerMapping = new ByteObjectHashMap<>();
    private static final Serializer defaultSerializer;

    static {
        List<Serializer> serializerList = JServiceLoader.loadAll(Serializer.class);

        logger.info("Support serializers: {}.", serializerList);

        for (Serializer s : serializerList) {
            serializerMapping.put(s.code(), s);
        }

        // protoStuff is expected
        Serializer defaultImpl = serializerMapping.get(PROTO_STUFF.value());
        if (defaultImpl == null && !serializerList.isEmpty()) {
            defaultImpl = serializerList.get(0);
        }
        defaultSerializer = defaultImpl;

        logger.info("Default serializer: {}.", defaultSerializer);
    }

    public static byte defaultSerializerCode() {
        return defaultSerializer.code();
    }

    public static Serializer defaultSerializerImpl() {
        return defaultSerializer;
    }

    public static Serializer serializerImpl(byte code) {
        Serializer serializer =  serializerMapping.get(code);

        if (serializer == null) {
            throw new NullPointerException("unsupported serializerImpl with code: " + code);
        }

        return serializer;
    }
}
