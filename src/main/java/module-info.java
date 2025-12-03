module com.tankwar.tankwargame {
    requires javafx.controls;
    requires javafx.graphics;
    
    // Export all packages
    exports com.tankwar.tankwargame.core;
    exports com.tankwar.tankwargame.util;
    exports com.tankwar.tankwargame.entities.base;
    exports com.tankwar.tankwargame.entities.tanks;
    exports com.tankwar.tankwargame.entities.projectiles;
    exports com.tankwar.tankwargame.entities.environment;
    exports com.tankwar.tankwargame.entities.pickups;
    exports com.tankwar.tankwargame.entities.effects;
    exports com.tankwar.tankwargame.ai;
    exports com.tankwar.tankwargame.events;
    exports com.tankwar.tankwargame.factory;
    exports com.tankwar.tankwargame.map;
}
