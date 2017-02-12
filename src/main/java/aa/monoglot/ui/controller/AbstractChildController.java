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
}
