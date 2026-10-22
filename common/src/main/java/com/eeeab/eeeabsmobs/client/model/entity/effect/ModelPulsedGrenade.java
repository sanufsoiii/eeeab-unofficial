package com.eeeab.eeeabsmobs.client.model.entity.effect;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Unit;
import net.minecraft.client.renderer.RenderType;

public class ModelPulsedGrenade extends Model<Unit> {
    public final ModelPart ball;

    public ModelPulsedGrenade(ModelPart root) {
        super(root.getChild("ball"), RenderType::entitySolid);
        this.ball = root.getChild("ball");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("ball", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}