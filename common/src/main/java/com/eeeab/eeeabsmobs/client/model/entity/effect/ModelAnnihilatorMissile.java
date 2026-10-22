package com.eeeab.eeeabsmobs.client.model.entity.effect;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

public class ModelAnnihilatorMissile extends Model<Unit> {
    public final ModelPart root;
    public final ModelPart missile;

    public ModelAnnihilatorMissile(ModelPart root) {
        super(root.getChild("root"), RenderTypes::entitySolid);
        this.root = root.getChild("root");
        this.missile = this.root.getChild("missile");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));
        PartDefinition missile = root.addOrReplaceChild("missile", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -6.5F, 3.0F, 3.0F, 9.0F, new CubeDeformation(-0.5F))
                .texOffs(0, 0).addBox(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition cube_r1 = missile.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -5.0F, -1.0F, 0.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.5708F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}