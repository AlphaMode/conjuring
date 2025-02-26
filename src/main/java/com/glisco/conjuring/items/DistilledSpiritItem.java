package com.glisco.conjuring.items;

import com.glisco.conjuring.Conjuring;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DistilledSpiritItem extends Item {
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("text.conjuring.distilled_spirit_tooltip").formatted(Formatting.GRAY));
    }

    public DistilledSpiritItem() {
        super(new Settings().group(Conjuring.CONJURING_GROUP).maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity player = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (player instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
        }

        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2000, 4));
        }

        if (player != null) {
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        if (player == null || !player.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        world.emitGameEvent(user, GameEvent.DRINKING_FINISH, user.getCameraBlockPos());
        return stack;
    }
}
