package aa.monoglot.ui.controller;

/**
 * @author cofl
 * @date 2/12/2017
 */
abstract class AbstractChildController<P> {
    protected P parentController;

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

    public abstract void projectChanged();
}
