package io.github.zekerzhayard.uhcstatushud.config;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfig {
    public static ModConfig instance = new ModConfig();

    private Configuration config;

    public Configuration getConfig() {
        return this.config;
    }

    public void setConfig(File file) {
        this.config = new Configuration(file);
        this.config.load();
        this.saveConfig();
    }

    public void saveConfig() {
        Arrays.stream(EnumConfig.values()).forEachOrdered(c -> {
            try {
                c.setProperty((Property) MethodUtils.invokeExactMethod(this.config, "get", c.getArgs(), Arrays.stream(c.getArgs()).map(Object::getClass).map(cl -> ClassUtils.isPrimitiveWrapper(cl) ? ClassUtils.wrapperToPrimitive(cl) : cl).toArray(Class<?>[]::new)));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        this.config.save();
    }

    public void saveNormalConfig() {
        Arrays.stream(EnumConfig.values()).filter(c -> c.getProperty().getType().compareTo(Property.Type.STRING) != 0).forEachOrdered(c -> {
            try {
                MethodUtils.invokeExactMethod((Property) MethodUtils.invokeExactMethod(this.config, "get", c.getArgs(), Arrays.stream(c.getArgs()).map(Object::getClass).map(cl -> ClassUtils.isPrimitiveWrapper(cl) ? ClassUtils.wrapperToPrimitive(cl) : cl).toArray(Class<?>[]::new)), "set", new Object[] {MethodUtils.invokeExactMethod(c.getProperty(), "get" + c.getArgs()[2].getClass().getSimpleName().replace("[]", "").replace("eger", ""))}, new Class<?>[] {ClassUtils.isPrimitiveWrapper(c.getArgs()[2].getClass()) ? ClassUtils.wrapperToPrimitive(c.getArgs()[2].getClass()) : c.getArgs()[2].getClass()});
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        this.config.save();
    }
}