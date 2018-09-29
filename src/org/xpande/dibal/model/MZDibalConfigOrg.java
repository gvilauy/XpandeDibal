package org.xpande.dibal.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para configuraciones de dibal por organización.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/22/17.
 */
public class MZDibalConfigOrg extends X_Z_DibalConfigOrg {

    public MZDibalConfigOrg(Properties ctx, int Z_DibalConfigOrg_ID, String trxName) {
        super(ctx, Z_DibalConfigOrg_ID, trxName);
    }

    public MZDibalConfigOrg(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
