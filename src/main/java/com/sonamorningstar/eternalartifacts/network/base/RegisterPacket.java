package com.sonamorningstar.eternalartifacts.network.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Paket sınıflarını otomatik olarak kaydetmek için annotation.
 * 
 * Kullanım:
 * ```java
 * @AutoRegisterPacket(side = PacketSide.SERVER)
 * public record MyPacket(...) implements IServerPayload {
 *     // ...
 * }
 * ```
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterPacket {
    
    /**
     * Paketin hangi tarafta çalışacağı
     */
    PacketSide side();
    
    /**
     * Paket sınıfı türü
     */
    enum PacketSide {
        /** Server'a gönderilen paketler */
        SERVER,
        /** Client'a gönderilen paketler */
        CLIENT,
        /** Her iki tarafa da gönderilen paketler (nadiren kullanılır) */
        BOTH
    }
}

