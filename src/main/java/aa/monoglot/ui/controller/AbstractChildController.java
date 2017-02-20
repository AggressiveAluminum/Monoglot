package aa.monoglot.ui.controller;

/**
 * @author cofl
 * @date 2/12/2017
 */
public abstract class AbstractChildController<P> {
    public P parentController;

    protected void registerMaster(P parent){
        this.parentController = parent;
        postInitialize();
    }

    protected abstract void postInitialize();

    /**
     * Clears project-specific information out of this tab
     * to prevent outdated information when switching projects.
     */
    protected abstract void clearInfo();
}
