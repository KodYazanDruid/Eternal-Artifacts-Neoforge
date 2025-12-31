package com.sonamorningstar.eternalartifacts.network.base;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class PacketRegistrar {
    
    private static final List<PacketInfo<?>> PACKETS = new ArrayList<>();
    private static final Type AUTO_REGISTER_ANNOTATION = Type.getType(RegisterPacket.class);
    private static boolean scanned = false;
    
    private PacketRegistrar() {}
    
    public static <T extends CustomPacketPayload> void register(Class<T> packetClass, RegisterPacket.PacketSide side) {
        PACKETS.add(new PacketInfo<>(packetClass, side));
    }
    
    @SuppressWarnings("unchecked")
    public static void scanAndRegisterAnnotatedPackets() {
        if (scanned) return;
        scanned = true;
        int count = 0;
        
        try {
            ModFileScanData scanData = ModList.get()
                .getModFileById(EternalArtifacts.MODID)
                .getFile()
                .getScanResult();
            
            for (ModFileScanData.AnnotationData annotation : scanData.getAnnotations()) {
                if (AUTO_REGISTER_ANNOTATION.equals(annotation.annotationType())) {
                    String className = annotation.memberName();
                    
                    try {
                        Class<?> clazz = Class.forName(className, false, PacketRegistrar.class.getClassLoader());
                        
                        if (CustomPacketPayload.class.isAssignableFrom(clazz)) {
                            RegisterPacket packetAnnotation = clazz.getAnnotation(RegisterPacket.class);
                            if (packetAnnotation != null) {
                                Class<? extends CustomPacketPayload> packetClass = (Class<? extends CustomPacketPayload>) clazz;
                                
                                boolean alreadyRegistered = PACKETS.stream()
                                    .anyMatch(p -> p.packetClass.equals(packetClass));
                                
                                if (!alreadyRegistered) {
                                    register(packetClass, packetAnnotation.side());
                                    count++;
                                    /*EternalArtifacts.LOGGER.debug("Auto-registered packet: {} with side: {}",
                                        clazz.getSimpleName(), packetAnnotation.side());*/
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        EternalArtifacts.LOGGER.warn("Could not load packet class: {}", className, e);
                    }
                }
            }
            
            if (count > 0) {
                EternalArtifacts.LOGGER.info("Auto-registered {} annotated packets", count);
            }
            
        } catch (Exception e) {
            EternalArtifacts.LOGGER.error("Failed to scan for annotated packets", e);
        }
    }
    
    public static void registerAll(IPayloadRegistrar registrar) {
        scanAndRegisterAnnotatedPackets();
        
        for (PacketInfo<?> info : PACKETS) {
            registerPacket(registrar, info);
        }
    }
    
    private static <T extends CustomPacketPayload> void registerPacket(IPayloadRegistrar registrar, PacketInfo<T> info) {
        try {
            Class<T> packetClass = info.packetClass;
            ResourceLocation id = (ResourceLocation) packetClass.getField("ID").get(null);
            FriendlyByteBuf.Reader<T> decoder = getReader(packetClass);
            Method handleMethod = findHandleMethod(packetClass);
            
            switch (info.side) {
                case SERVER -> registrar.play(
                    id,
                    decoder,
                    handler -> handler.server((packet, ctx) -> {
                        try {
                            handleMethod.invoke(packet, ctx);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to handle packet: " + packetClass.getName(), e);
                        }
                    })
                );
                
                case CLIENT -> registrar.play(
                    id,
                    decoder,
                    handler -> handler.client((packet, ctx) -> {
                        try {
                            handleMethod.invoke(packet, ctx);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to handle packet: " + packetClass.getName(), e);
                        }
                    })
                );
                
                case BOTH -> registrar.play(
                    id,
                    decoder,
                    handler -> handler
                        .server((packet, ctx) -> {
                            try {
                                handleMethod.invoke(packet, ctx);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to handle packet: " + packetClass.getName(), e);
                            }
                        })
                        .client((packet, ctx) -> {
                            try {
                                handleMethod.invoke(packet, ctx);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to handle packet: " + packetClass.getName(), e);
                            }
                        })
                );
            }
            
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                "Class " + info.packetClass.getName() + " must have a public static 'ID' field of type ResourceLocation", e
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                "Class " + info.packetClass.getName() + " must have a public static 'create(FriendlyByteBuf)' method", e
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to register packet: " + info.packetClass.getName(), e);
        }
    }
    
    private static <T extends CustomPacketPayload> FriendlyByteBuf.@NotNull Reader<T> getReader(Class<T> packetClass) throws NoSuchMethodException {
        Method createMethod = packetClass.getMethod("create", FriendlyByteBuf.class);
        
		return buf -> {
            try {
                return (T) createMethod.invoke(null, buf);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create packet: " + packetClass.getName(), e);
            }
        };
    }
    
    private static Method findHandleMethod(Class<?> packetClass) throws NoSuchMethodException {
        for (Method method : packetClass.getMethods()) {
            if (method.getName().equals("handle") &&
                method.getParameterCount() == 1 &&
                method.getParameterTypes()[0].equals(PlayPayloadContext.class)) {
                return method;
            }
        }
        throw new NoSuchMethodException("No handle(PlayPayloadContext) method found in " + packetClass.getName());
    }
    
    private record PacketInfo<T extends CustomPacketPayload>(
        Class<T> packetClass,
        RegisterPacket.PacketSide side
    ) {}
}
