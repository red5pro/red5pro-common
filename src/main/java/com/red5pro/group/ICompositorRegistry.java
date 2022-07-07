package com.red5pro.group;

/**
 * For custom mixing consoles, this interface is used to register class handlers
 * and to stop instances of those handlers.
 *
 * @author Andy
 *
 */
public interface ICompositorRegistry {
    /**
     * Call release when all references are removed, and the registry will call
     * 'stop' on the Compositor instance after it is removed for the active mixers
     * map.
     *
     * @param path
     *            context path of group. Provision guid or Provision contextPath.
     */
    void release(String path);

    /**
     * Call to register class alias in the registry to be instantiated as Compositor
     * implementation. Provision parameter 'group' = alias.
     *
     * @param friendly
     *            names such as "mymixer"
     * @param clazz
     *            definition of class.pacage.and.name.of.ExpressionCompositor
     *            implementation.
     */
    void register(String alias, String clazz);
}
