package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public class ExampleMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

//    @SubscribeEvent
//    public void onJumpEvent(LivingEvent.LivingJumpEvent event) {
//        Entity entity = event.getEntity();
//        if (!(entity instanceof PlayerEntity)) {
//            return;
//        }
//        LOGGER.info("player Jumped");
//        return;
//    }

    @SubscribeEvent
    public void onJumpEvent(LivingEvent.LivingJumpEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        Random random = new Random(System.currentTimeMillis());
        Integer randomValue = random.nextInt(3);
        LOGGER.info("random value: " + randomValue + " -> " + (randomValue == 0 ? "exit" : "Item Drop"));
        if (randomValue == 0) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity) entity;

        randomValue = random.nextInt(4);
        LOGGER.info("random value: " + randomValue + " -> " + (randomValue == 0 ? "armorInventory" : "mainInventory"));
        ItemStack itemStack = null;
        if (randomValue == 0) {
            LOGGER.info("armor inventory size: " + playerEntity.inventory.armorInventory.size());
            randomValue = random.nextInt(playerEntity.inventory.armorInventory.size());
            itemStack = playerEntity.inventory.armorInventory.get(randomValue);
            LOGGER.info("random value: " + randomValue + " -> " + itemStack);
            playerEntity.dropItem(itemStack, true, true);
            playerEntity.inventory.armorInventory.set(randomValue, ItemStack.EMPTY);
        } else {
            LOGGER.info("armor inventory size: " + playerEntity.inventory.mainInventory.size());
            randomValue = random.nextInt(playerEntity.inventory.mainInventory.size());
            itemStack = playerEntity.inventory.mainInventory.get(randomValue);
            LOGGER.info("random value: " + randomValue + " -> " + itemStack);
            playerEntity.dropItem(itemStack, true, true);
            playerEntity.inventory.mainInventory.set(randomValue, ItemStack.EMPTY);
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
