package aa.monoglot.ui.controller;

import javafx.event.ActionEvent;

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

    //public abstract void projectChanged();
}
