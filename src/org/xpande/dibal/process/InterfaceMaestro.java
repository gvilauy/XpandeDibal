package org.xpande.dibal.process;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.xpande.dibal.utils.ProcesadorInterfaceOut;

import java.math.BigDecimal;

/**
 * Proceso para ejecutar interface maestro de Dibal.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 10/12/18.
 */
public class InterfaceMaestro extends SvrProcess {

    private ProcesadorInterfaceOut procesadorInterfaceOut = null;
    private int adOrgID = 0;

    @Override
    protected void prepare() {

        ProcessInfoParameter[] para = getParameter();

        for (int i = 0; i < para.length; i++){

            String name = para[i].getParameterName();

            if (name != null){
                if (para[i].getParameter() != null){
                    if (name.trim().equalsIgnoreCase("AD_Org_ID")){
                        this.adOrgID = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }
            }
        }

        this.procesadorInterfaceOut = new ProcesadorInterfaceOut(getCtx(), get_TrxName());

    }

    @Override
    protected String doIt() throws Exception {

        String message = this.procesadorInterfaceOut.executeInterfaceOut(this.adOrgID, true);

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }

}
