package com.glisco.conjuring.blocks.conjurer;

import com.glisco.conjuring.blocks.ConjuringBlocks;
import com.glisco.conjuring.items.ConjuringScepter;
import com.glisco.conjuring.items.SuperiorConjuringScepter;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConjurerBlock extends BlockWithEntity {

    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public ConjurerBlock(Settings settings) {
        super(settings);
    }

    public ConjurerBlock() {
        this(FabricBlockSettings.of(Material.METAL).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque().breakByTool(FabricToolTags.PICKAXES, 2).luminance(ConjurerBlock::getLuminance));
        setDefaultState(getDefaultState().with(ACTIVE, false));
    }

    private static int getLuminance(BlockState state) {
        return state.get(ACTIVE) ? 7 : 0;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConjurerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ConjuringBlocks.Entities.CONJURER, ConjurerBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!(player.getMainHandStack().getItem() instanceof ConjuringScepter) && !(player.getMainHandStack().getItem() instanceof SuperiorConjuringScepter))
            return ActionResult.PASS;

        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ConjurerBlockEntity) {
                ItemScatterer.spawn(world, pos, (ConjurerBlockEntity) blockEntity);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
