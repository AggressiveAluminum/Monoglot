package aa.monoglot.ui.controller;

abstract class AbstractChildController<P> {
    protected P parentController;

    void registerMaster(P parent){
        this.parentController = parent;
        postInitialize();
    }

    protected abstract void postInitialize();

    /**
     * Clears project-specific information out of this tab
     * to prevent outdated information when switching projects.
     */
    protected abstract void clearInfo();

    /**
     * Called when this tab is switched away from.
     * @return success
     */
    public boolean unload(){
        return true;
    }

    /**
     * Called when this tab is switched in to.
     * @return success
     */
    public boolean load(){
        return true;
    }
}
