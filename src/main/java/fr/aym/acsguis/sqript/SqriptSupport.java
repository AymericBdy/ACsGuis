package fr.aym.acsguis.sqript;

/**
 * The Sqript adapter for ACsGuis
 */
public interface SqriptSupport
{
    /**
     * @return True if Sqript mod is loaded
     */
    boolean isSqriptLoaded();

    /**
     * Fired when the css is being initialized
     */
    void onCssInit();
}
