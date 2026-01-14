package com.sonamorningstar.eternalartifacts;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;

public abstract class MinecraftBootstrapTest {
    
    private static boolean bootstrapped = false;
    
    @BeforeAll
    public static void bootstrapMinecraft() {
        if (!bootstrapped) {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            bootstrapped = true;
        }
    }
}

