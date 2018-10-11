package org.xpande.dibal.model;

import org.compiere.model.Query;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * Modelo para configuración de procesos de interface de datos contra sistema de Balanzas Dibal.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/22/17.
 */
public class MZDibalConfig extends X_Z_DibalConfig {

    public MZDibalConfig(Properties ctx, int Z_DibalConfig_ID, String trxName) {
        super(ctx, Z_DibalConfig_ID, trxName);
    }

    public MZDibalConfig(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /***
     * Obtiene modelo único de configuración del proceso de Interface para Dibal.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @param ctx
     * @param trxName
     * @return
     */
    public static MZDibalConfig getDefault(Properties ctx, String trxName){

        MZDibalConfig model = new Query(ctx, I_Z_DibalConfig.Table_Name, "", trxName).first();
        return model;

    }

    /***
     * Obtiene y retorna modelo de configuracion de dibal para una determinada organización.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @param adOrgID
     * @return
     */
    public MZDibalConfigOrg getOrgConfig(int adOrgID){

        String whereClause = X_Z_DibalConfigOrg.COLUMNNAME_Z_DibalConfig_ID + " =" + this.get_ID() +
                " AND " + X_Z_DibalConfigOrg.COLUMNNAME_AD_OrgTrx_ID + " =" + adOrgID;

        MZDibalConfigOrg model = new Query(getCtx(), I_Z_DibalConfigOrg.Table_Name, whereClause, get_TrxName()).first();

        return model;
    }


    /***
     * Obtiene y retorna organizaciones asociadas a esta configuracion de dibal.
     * Xpande. Created by Gabriel Vila on 10/11/18.
     * @return
     */
    public List<MZDibalConfigOrg> getOrganization(){

        String whereClause = X_Z_DibalConfigOrg.COLUMNNAME_Z_DibalConfig_ID + " =" + this.get_ID();

        List<MZDibalConfigOrg> lines = new Query(getCtx(), I_Z_DibalConfigOrg.Table_Name, whereClause, get_TrxName()).list();

        return lines;
    }

}
