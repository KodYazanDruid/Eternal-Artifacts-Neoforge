package com.sonamorningstar.eternalartifacts.client.render.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

public final class GhostVertexConsumer extends VertexConsumerWrapper {
    private final int alpha;

    public GhostVertexConsumer(VertexConsumer parent, int alpha) {
        super(parent);
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        return parent.color(r, g, b, (a * alpha) / 0xFF);
    }
}
