package fr.aym.acsguis.api;

import fr.aym.acslib.api.ACsService;

/**
 * ACsGuiApi service interface
 *
 * @see ACsGuiApi
 */
public interface ACsGuiApiService extends ACsService
{
    String RES_LOC_ID = "acsguis";

    @Override
    default String getName() {
        return RES_LOC_ID;
    }
}
