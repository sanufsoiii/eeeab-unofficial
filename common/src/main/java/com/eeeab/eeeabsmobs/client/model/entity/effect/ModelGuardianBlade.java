package com.eeeab.eeeabsmobs.client.model.entity.effect;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

public class ModelGuardianBlade extends Model<Unit> {
    public ModelGuardianBlade(ModelPart root) {
        super(root.getChild("root"), RenderTypes::entitySolid);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -24.0F, -10.0F, 6.0F, 48.0F, 56.0F, new CubeDeformation(0.0F))
                .texOffs(110, 136).addBox(-4.0F, -32.0F, -2.0F, 6.0F, 8.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(0, 206).addBox(-4.0F, -40.0F, 8.0F, 6.0F, 8.0F, 42.0F, new CubeDeformation(0.0F))
                .texOffs(110, 206).addBox(-4.0F, 32.0F, 8.0F, 6.0F, 8.0F, 42.0F, new CubeDeformation(0.0F))
                .texOffs(0, 142).addBox(-4.0F, 24.0F, -2.0F, 6.0F, 8.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.2182F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}