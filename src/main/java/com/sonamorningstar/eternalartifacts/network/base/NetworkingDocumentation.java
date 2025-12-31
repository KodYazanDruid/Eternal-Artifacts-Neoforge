package com.sonamorningstar.eternalartifacts.network.base;

/**
 * Network Base Package
 * ====================
 * <p>
 * Bu paket, network paketleri oluşturmayı kolaylaştıran base interface'ler ve helper sınıflar içerir.
 * <p>
 * ## Ana Bileşenler:
 *
 * ### 1. ServerPayload
 * Server'a gönderilen paketler için interface.
 * Otomatik olarak player extraction ve async handling sağlar.
 *
 * Örnek kullanım:
 * ```java
 * public record MyPacket(...) implements ServerPayload {
 *     @Override
 *     public void handleOnServer(ServerPlayer player) {
 *         // Server-side işlemler
 *     }
 * }
 * ```
 *
 * ### 2. ClientPayload
 * Client'a gönderilen paketler için interface.
 * Otomatik olarak client-side execution sağlar.
 *
 * Örnek kullanım:
 * ```java
 * public record MyPacket(...) implements ClientPayload {
 *     @Override
 *     @OnlyIn(Dist.CLIENT)
 *     public void handleOnClient(Minecraft minecraft) {
 *         // Client-side işlemler
 *     }
 * }
 * ```
 *
 * ### 3. PacketHelper
 * Ortak paket okuma/yazma işlemleri için utility sınıfı.
 *
 * Metodlar:
 * - writeContainerId/readContainerId: Menu container ID
 * - writePos/readPos: BlockPos
 * - writeEntityId/readEntityId: Entity ID
 * - writeIndex/readIndex: Genel index değerleri
 *
 * ### 4. MenuHelper
 * Menu validation ve handling için helper.
 *
 * Örnek kullanım:
 * ```java
 * handleMenu(player, containerId, MyMenu.class,
 *     menu -> menu.doSomething());
 * ```
 *
 * ### 5. BlockEntityHelper
 * BlockEntity validation ve handling için helper.
 *
 * Örnek kullanım:
 * ```java
 * handleBlockEntity(player, pos, MyBlockEntity.class,
 *     blockEntity -> blockEntity.doSomething());
 * ```
 *
 * ## Avantajlar:
 *
 * 1. **Daha Az Kod Tekrarı**: Ortak işlemler helper'larda merkezi hale getirildi
 * 2. **Tip Güvenliği**: Generic tipler ile compile-time kontrol
 * 3. **Okunabilirlik**: Paket sınıfları artık çok daha kısa ve anlaşılır
 * 4. **Bakım Kolaylığı**: Değişiklikler tek yerden yapılabilir
 * 5. **Tutarlılık**: Tüm paketler aynı pattern'i kullanır
 *
 * ## Yeni Paket Oluşturma:
 *
 * ### Server Paketi (Menu):
 * ```java
 * public record MyMenuPacket(int containerId, ...) implements ServerPayload {
 *     public static final ResourceLocation ID = new ResourceLocation(MODID, "my_packet");
 *
 *     public static MyMenuPacket create(FriendlyByteBuf buf) {
 *         return new MyMenuPacket(readContainerId(buf), ...);
 *     }
 *
 *     @Override
 *     public void write(FriendlyByteBuf buff) {
 *         writeContainerId(buff, containerId);
 *         // Diğer veriler...
 *     }
 *
 *     @Override
 *     public ResourceLocation id() {
 *         return ID;
 *     }
 *
 *     @Override
 *     public void handleOnServer(ServerPlayer player) {
 *         handleMenu(player, containerId, MyMenu.class,
 *             menu -> menu.handlePacket(this));
 *     }
 * }
 * ```
 *
 * ### Server Paketi (BlockEntity):
 * ```java
 * public record MyBlockEntityPacket(BlockPos pos, ...) implements ServerPayload {
 *     public static final ResourceLocation ID = new ResourceLocation(MODID, "my_packet");
 *
 *     public static MyBlockEntityPacket create(FriendlyByteBuf buf) {
 *         return new MyBlockEntityPacket(readPos(buf), ...);
 *     }
 *
 *     @Override
 *     public void write(FriendlyByteBuf buff) {
 *         writePos(buff, pos);
 *         // Diğer veriler...
 *     }
 *
 *     @Override
 *     public ResourceLocation id() {
 *         return ID;
 *     }
 *
 *     @Override
 *     public void handleOnServer(ServerPlayer player) {
 *         handleBlockEntity(player, pos, MyBlockEntity.class,
 *             blockEntity -> blockEntity.handlePacket(this));
 *     }
 * }
 * ```
 *
 * ### Client Paketi:
 * ```java
 * public record MyClientPacket(...) implements ClientPayload {
 *     public static final ResourceLocation ID = new ResourceLocation(MODID, "my_packet");
 *
 *     public static MyClientPacket create(FriendlyByteBuf buf) {
 *         return new MyClientPacket(...);
 *     }
 *
 *     @Override
 *     public void write(FriendlyByteBuf buff) {
 *         // Veriler...
 *     }
 *
 *     @Override
 *     public ResourceLocation id() {
 *         return ID;
 *     }
 *
 *     @Override
 *     @OnlyIn(Dist.CLIENT)
 *     public void handleOnClient(Minecraft minecraft) {
 *         // Client işlemleri...
 *     }
 * }
 * ```
 */
public final class NetworkingDocumentation {
    private NetworkingDocumentation() {}
}

