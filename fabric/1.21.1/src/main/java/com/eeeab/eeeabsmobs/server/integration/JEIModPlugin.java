package com.eeeab.eeeabsmobs.server.integration;

/**
 * JEI integration, disabled on this Fabric build - flagged loudly per instructions rather than silently
 * dropped.
 * <p>
 * Unlike Curios, JEI *does* publish a Fabric build (mezz.jei:jei-1.21.1-fabric), so this integration could work
 * here. The blocker is this branch's build.gradle: it declares no JEI dependency at all (compare the NeoForge
 * branch, which adds {@code compileOnly "mezz.jei:jei-1.21.1-neoforge:19.51.0.417"}, and the Forge branch,
 * which adds both jei-1.21.1-forge-api and jei-1.21.1-common-api). Without mezz.jei.api.* on the compile
 * classpath, @JeiPlugin/IModPlugin/IRecipeRegistration cannot be referenced at all - not "compiles but does
 * nothing", but a hard compile error - so this class has been reduced to a no-op with zero JEI imports.
 * <p>
 * Per this task's instructions, build files are not to be touched from here. Restoring the real plugin needs
 * one line added to build.gradle's dependencies block, mirroring the other two branches, e.g.:
 * {@code compileOnly "mezz.jei:jei-1.21.1-fabric:19.51.0.417"}
 * (exact artifact/version to be confirmed against JEI's Fabric maven metadata), after which this class's
 * original body (registerRecipes -> IRecipeRegistration.addItemStackInfo for the two eye items) can be restored
 * verbatim from the NeoForge/Forge branches.
 */
public class JEIModPlugin {
}
