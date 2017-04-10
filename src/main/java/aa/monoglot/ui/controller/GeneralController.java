package aa.monoglot.ui.controller;

/**
 * Some simple methods tab controllers must implement.
 */
public interface GeneralController {
    /**
     * Called when the tab becomes active.
     * @return True on success, else false.
     */
    default boolean onLoad(){
        return true;
    }

    /**
     * Called when the tab becomes inactive. If false is returned, blocks becoming inactive, unless the application
     * doesn't care.
     * @return True on success, else false.
     */
    default boolean onUnload(){
        return true;
    }

    /**
     * Called when project controls are disabled, and before saving the project.
     * @return True on success, else false.
     */
    default boolean save(){
        return true;
    }

    /**
     * Called when the project is closed.
     */
    default void onProjectClosed(){}
}
