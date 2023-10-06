package com.leno.example.config.baseClasses.controllerBase;


/**
 * Idea of this class is to help with AOP. If all controller extend this class it is
 * easier to filter all the class that will be called when @Before is activated
 *
 *
 * This is  the reason    @Before("within(ai.atmc.hawkadoccollector.config.baseClasses.controllerBase.ControllerBase+)") -> we are saying call this Before method on each controller that is
 * extending the abstract class ControllerBase
 */
public abstract class ControllerBase {
}
