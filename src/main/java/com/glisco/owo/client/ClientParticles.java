package com.glisco.owo.client;

import com.glisco.owo.VectorRandomUtils;
import com.glisco.conjuringforgery.mixin.ParticleManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class ClientParticles {

    private static int particleCount = 1;
    private static boolean persist = false;
    private static Vector3d velocity = new Vector3d(0, 0, 0);

    /**
     * Marks the values set by {@link ClientParticles#setParticleCount(int)} and {@link ClientParticles#setVelocity(Vector3d)} to be persistent
     */
    public static void persist() {
        ClientParticles.persist = true;
    }

    /**
     * How many particles to spawn per operation
     * <br><b>
     * Volatile unless {@link ClientParticles#persist()} is called before the next operation
     * </b>
     */
    public static void setParticleCount(int particleCount) {
        ClientParticles.particleCount = particleCount;
    }

    /**
     * The velocity added to each spawned particle
     * <br><b>
     * Volatile unless {@link ClientParticles#persist()} is called before the next operation
     * </b>
     */
    public static void setVelocity(Vector3d velocity) {
        ClientParticles.velocity = velocity;
    }

    /**
     * Forces a reset of velocity and particleCount
     */
    public static void reset() {
        persist = false;
        clearState();
    }

    private static void clearState() {
        if (!persist) {
            particleCount = 1;
            velocity = new Vector3d(0, 0, 0);
        }
    }

    /**
     * Spawns particles with a maximum offset of {@code deviation} from the center of {@code pos}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link ClientWorld}
     * @param pos       The block to center on
     * @param deviation The maximum deviation from the center of pos
     */
    public static void spawnCenteredOnBlock(IParticleData particle, World world, BlockPos pos, double deviation) {
        Vector3d location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomCenteredOnBlock(world, pos, deviation);
            world.addParticle(particle, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z);
        }

        clearState();
    }

    /**
     * Spawns particles randomly distributed within {@code pos}
     *
     * @param particle The particle to spawn
     * @param world    The world to spawn the particles in, must be {@link ClientWorld}
     * @param pos      The block to spawn particles in
     */
    public static void spawnWithinBlock(IParticleData particle, World world, BlockPos pos) {
        Vector3d location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomWithinBlock(world, pos);
            world.addParticle(particle, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z);
        }

        clearState();
    }

    /**
     * Spawns particles with a maximum offset of {@code deviation} from {@code pos + offset}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link ClientWorld}
     * @param pos       The base position
     * @param offset    The offset from {@code pos}
     * @param deviation The scalar for random distribution
     */
    public static void spawnWithOffsetFromBlock(IParticleData particle, World world, BlockPos pos, Vector3d offset, double deviation) {
        Vector3d location;
        offset = offset.add(Vector3d.copy(pos));

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffset(world, offset, deviation);

            world.addParticle(particle, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z);
        }

        clearState();
    }

    /**
     * Spawns particles at the given location with a maximum offset of {@code deviation}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link ClientWorld}
     * @param pos       The base position
     * @param deviation The scalar from random distribution
     */
    public static void spawn(IParticleData particle, World world, Vector3d pos, double deviation) {
        Vector3d location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffset(world, pos, deviation);
            world.addParticle(particle, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z);
        }

        clearState();
    }

    /**
     * Spawns particles at the given location with a maximum offset of {@code deviation}
     *
     * @param particle   The particle to spawn
     * @param world      The world to spawn the particles in, must be {@link ClientWorld}
     * @param pos        The base position
     * @param deviationX The scalar from random distribution on x
     * @param deviationY The scalar from random distribution on y
     * @param deviationZ The scalar from random distribution on z
     */
    public static void spawnPrecise(IParticleData particle, World world, Vector3d pos, double deviationX, double deviationY, double deviationZ) {
        Vector3d location;

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffsetSpecific(world, pos, deviationX, deviationY, deviationZ);
            world.addParticle(particle, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z);
        }

        clearState();
    }

    /**
     * Spawns enchant particles travelling from origin to destination
     *
     * @param world       The world to spawn the particles in, must be {@link ClientWorld}
     * @param origin      The origin of the particle stream
     * @param destination The destination of the particle stream
     * @param deviation   The scalar for random distribution around {@code origin}
     */
    public static void spawnEnchantParticles(World world, Vector3d origin, Vector3d destination, float deviation) {

        Vector3d location;
        Vector3d particleVector = origin.subtract(destination);

        for (int i = 0; i < particleCount; i++) {
            location = VectorRandomUtils.getRandomOffset(world, particleVector, deviation);
            world.addParticle(ParticleTypes.ENCHANT, destination.x, destination.y, destination.z, location.x, location.y, location.z);
        }

        clearState();
    }

    /**
     * Spawns a particle at the given location with a lifetime of {@code maxAge}
     *
     * @param particleType The type of the particle to spawn
     * @param world        The world to spawn the particles in, must be {@link ClientWorld}
     * @param pos          The position to spawn at
     * @param maxAge       The maxAge to set for the spawned particle
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T extends IParticleData> void spawnWithMaxAge(T particleType, World world, Vector3d pos, int maxAge) {

        IParticleFactory<T> particleFactory = (IParticleFactory<T>) ((ParticleManagerAccessor) Minecraft.getInstance().particles).getFactories().get(ForgeRegistries.PARTICLE_TYPES.getKey(particleType.getType()));

        Particle particle = particleFactory.makeParticle(particleType, (ClientWorld) world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        particle.setMaxAge(maxAge);
        Minecraft.getInstance().particles.addEffect(particle);

        clearState();
    }

    /**
     * Spawns a line of particles going from {@code start} to {@code end}
     *
     * @param particle  The particle to spawn
     * @param world     The world to spawn the particles in, must be {@link ClientWorld}
     * @param start     The line's origin
     * @param end       The line's end point
     * @param deviation A random offset from the line that particles can have
     */
    public static void spawnLine(IParticleData particle, World world, Vector3d start, Vector3d end, float deviation) {
        spawnLineInner(particle, world, start, end, deviation);
        clearState();
    }

    private static void spawnLineInner(IParticleData particle, World world, Vector3d start, Vector3d end, float deviation) {
        Vector3d increment = end.subtract(start).scale(1f / (float) particleCount);

        for (int i = 0; i < particleCount; i++) {
            start = VectorRandomUtils.getRandomOffset(world, start, deviation);
            world.addParticle(particle, start.x, start.y, start.z, 0, 0, 0);
            start = start.add(increment);
        }
    }

    public static void spawnCubeOutline(IParticleData particle, World world, Vector3d origin, float size, float deviation) {

        spawnLineInner(particle, world, origin, origin.add(size, 0, 0), deviation);
        spawnLineInner(particle, world, origin.add(size, 0, 0), origin.add(size, 0, size), deviation);

        spawnLineInner(particle, world, origin, origin.add(0, 0, size), deviation);
        spawnLineInner(particle, world, origin.add(0, 0, size), origin.add(size, 0, size), deviation);

        origin = origin.add(0, size, 0);

        spawnLineInner(particle, world, origin, origin.add(size, 0, 0), deviation);
        spawnLineInner(particle, world, origin.add(size, 0, 0), origin.add(size, 0, size), deviation);

        spawnLineInner(particle, world, origin, origin.add(0, 0, size), deviation);
        spawnLineInner(particle, world, origin.add(0, 0, size), origin.add(size, 0, size), deviation);

        spawnLineInner(particle, world, origin, origin.add(0, -size, 0), deviation);
        spawnLineInner(particle, world, origin.add(size, 0, 0), origin.add(size, -size, 0), deviation);
        spawnLineInner(particle, world, origin.add(0, 0, size), origin.add(0, -size, size), deviation);
        spawnLineInner(particle, world, origin.add(size, 0, size), origin.add(size, -size, size), deviation);

        clearState();
    }

}
