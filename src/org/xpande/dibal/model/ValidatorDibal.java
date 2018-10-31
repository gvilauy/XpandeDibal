package org.xpande.dibal.model;

import org.compiere.model.*;
import org.compiere.util.DB;
import org.xpande.core.model.I_Z_ProductoUPC;
import org.xpande.core.model.MZProductoUPC;

import java.util.List;

/**
 * Model Validator para interface con Sistema de Balanzas DIBAL.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/21/17.
 */
public class ValidatorDibal implements ModelValidator {

    private int adClientID = 0;

    @Override
    public void initialize(ModelValidationEngine engine, MClient client) {

        // Guardo compañia
        if (client != null){
            this.adClientID = client.get_ID();
        }

        // DB Validations
        engine.addModelChange(I_M_Product.Table_Name, this);
        engine.addModelChange(I_M_ProductPrice.Table_Name, this);

    }

    @Override
    public int getAD_Client_ID() {
        return this.adClientID;
    }

    @Override
    public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
        return null;
    }

    @Override
    public String modelChange(PO po, int type) throws Exception {

        if (po.get_TableName().equalsIgnoreCase(I_M_Product.Table_Name)){
            return modelChange((MProduct) po, type);
        }
        else if (po.get_TableName().equalsIgnoreCase(I_M_ProductPrice.Table_Name)){
            return modelChange((MProductPrice) po, type);
        }

        return null;

    }

    @Override
    public String docValidate(PO po, int timing) {
        return null;
    }

    /***
     * Validaciones para el modelo de Productos
     * Xpande. Created by Gabriel Vila on 8/21/17.
     * @param model
     * @param type
     * @return
     * @throws Exception
     */
    public String modelChange(MProduct model, int type) throws Exception {

        String mensaje = null;

        // Dibal. Interface salida Balanzas
        if ((type == ModelValidator.TYPE_AFTER_NEW) || (type == ModelValidator.TYPE_AFTER_CHANGE)){

            // Obtengo configurador general y único de dibal
            MZDibalConfig dibalConfig = MZDibalConfig.getDefault(model.getCtx(), model.get_TrxName());
            List<MZDibalConfigOrg> configOrgList = dibalConfig.getOrganization();
            for (MZDibalConfigOrg configOrg: configOrgList){

                if (type == ModelValidator.TYPE_AFTER_NEW){

                    // Si el producto no se vende, o no esta activo, o no es balanza al momento de crearse, no hago nada
                    if ((!model.isSold()) || (!model.isActive()) || (!model.get_ValueAsBoolean("EsProductoBalanza"))){
                        return mensaje;
                    }

                    // Marca de Creacion de Producto
                    MZDibalInterfaceOut dibalInterfaceOut = new MZDibalInterfaceOut(model.getCtx(), 0, model.get_TrxName());
                    dibalInterfaceOut.setCRUDType(X_Z_DibalInterfaceOut.CRUDTYPE_CREATE);
                    dibalInterfaceOut.setSeqNo(10);
                    dibalInterfaceOut.setAD_Table_ID(I_M_Product.Table_ID);
                    dibalInterfaceOut.setRecord_ID(model.get_ID());
                    dibalInterfaceOut.setAD_OrgTrx_ID(configOrg.getAD_OrgTrx_ID());
                    dibalInterfaceOut.saveEx();

                }
                else if (type == ModelValidator.TYPE_AFTER_CHANGE){

                    // Si no se cambio flag de Es Producto de Balanza y el producto no es de balanza
                    if (!model.is_ValueChanged("EsProductoBalanza")){
                        if (!model.get_ValueAsBoolean("EsProductoBalanza")){
                            // No hago nada para Dibal.
                            return mensaje;
                        }
                    }


                    // Pregunto por los campos cuyo cambio requiere informar a Balanza
                    if ((model.is_ValueChanged("C_UOM_ID"))
                            || (model.is_ValueChanged("Description"))  || (model.is_ValueChanged("IsSold"))
                            || (model.is_ValueChanged("IsActive")) || (model.is_ValueChanged("EsProductoBalanza"))){

                        // Marca Update
                        MZDibalInterfaceOut dibalInterfaceOut = MZDibalInterfaceOut.getRecord(model.getCtx(), I_M_Product.Table_ID, model.get_ID(),
                                                                        configOrg.getAD_OrgTrx_ID(), model.get_TrxName());
                        if ((dibalInterfaceOut != null) && (dibalInterfaceOut.get_ID() > 0)){
                            // Proceso segun marca que ya tenía este producto antes de su actualización.
                            // Si marca anterior es CREATE
                            if (dibalInterfaceOut.getCRUDType().equalsIgnoreCase(X_Z_DibalInterfaceOut.CRUDTYPE_CREATE)){

                                // Si se desactiva el producto o no es mas de balanza, tengo que eliminar la marca de create
                                if ((!model.isActive()) || (!model.isSold()) || (!model.get_ValueAsBoolean("EsProductoBalanza"))){
                                    dibalInterfaceOut.deleteEx(true);
                                }

                                // No hago nada y respeto primer marca
                                return mensaje;
                            }
                            else if (dibalInterfaceOut.getCRUDType().equalsIgnoreCase(X_Z_DibalInterfaceOut.CRUDTYPE_DELETE)){
                                // Si marca anterior es DELETE, es porque el producto se inactivo anteriormente o se marco como no de balanza
                                // Si este producto sigue estando inactivo o sigue siendo de no balanza
                                if ((!model.isActive()) || (!model.isSold()) || (!model.get_ValueAsBoolean("EsProductoBalanza"))){
                                    // No hago nada y respeto primer marca.
                                    return mensaje;
                                }
                            }
                        }

                        // Si el producto esta activo y es de balanza, creo marca de update
                        if ((model.isActive()) && (model.isSold()) && (model.get_ValueAsBoolean("EsProductoBalanza"))){
                            // Si no tengo marca de update, la creo ahora.
                            if ((dibalInterfaceOut == null) || (dibalInterfaceOut.get_ID() <= 0)){
                                // No existe aun marca de UPDATE sobre este producto, la creo ahora.
                                dibalInterfaceOut = new MZDibalInterfaceOut(model.getCtx(), 0, model.get_TrxName());
                                dibalInterfaceOut.setCRUDType(X_Z_DibalInterfaceOut.CRUDTYPE_UPDATE);
                                dibalInterfaceOut.setAD_Table_ID(I_M_Product.Table_ID);
                                dibalInterfaceOut.setSeqNo(20);
                                dibalInterfaceOut.setRecord_ID(model.get_ID());
                                dibalInterfaceOut.setAD_OrgTrx_ID(configOrg.getAD_OrgTrx_ID());
                                dibalInterfaceOut.saveEx();
                            }
                        }
                        else{
                            // Si tenia marca anterior la elimino y creo una de delete
                            if ((dibalInterfaceOut != null) && (dibalInterfaceOut.get_ID() > 0)){
                                dibalInterfaceOut.deleteEx(true);
                                dibalInterfaceOut = null;
                            }
                            dibalInterfaceOut = new MZDibalInterfaceOut(model.getCtx(), 0, model.get_TrxName());
                            dibalInterfaceOut.setCRUDType(X_Z_DibalInterfaceOut.CRUDTYPE_DELETE);
                            dibalInterfaceOut.setAD_Table_ID(I_M_Product.Table_ID);
                            dibalInterfaceOut.setSeqNo(30);
                            dibalInterfaceOut.setRecord_ID(model.get_ID());
                            dibalInterfaceOut.setAD_OrgTrx_ID(configOrg.getAD_OrgTrx_ID());
                            dibalInterfaceOut.saveEx();
                        }
                    }
                }

            }
        }

        return mensaje;
    }


    /***
     * Validaciones para el modelo de Precios de Productos.
     * Xpande. Created by Gabriel Vila on 6/30/17.
     * @param model
     * @param type
     * @return
     * @throws Exception
     */
    public String modelChange(MProductPrice model, int type) throws Exception {

        String mensaje = null;

        // Retail. Interface salida POS
        if ((type == ModelValidator.TYPE_AFTER_NEW) || (type == ModelValidator.TYPE_AFTER_CHANGE)){

            // Solo listas de ventas con organización distinto de *
            MPriceListVersion priceListVersion = new MPriceListVersion(model.getCtx(), model.getM_PriceList_Version_ID(), model.get_TrxName());
            MPriceList priceList = priceListVersion.getPriceList();
            if (!priceList.isSOPriceList()) return mensaje;
            if (priceList.getAD_Org_ID() == 0) return mensaje;

            MProduct product = (MProduct)model.getM_Product();

            // Si el producto no se vende o no esta activo, o no es de balanza, no hago nada
            if ((!product.isSold()) || (!product.isActive()) || (!product.get_ValueAsBoolean("EsProductoBalanza"))){
                return mensaje;
            }

            // Si existe, obtengo marca de interface de este producto
            MZDibalInterfaceOut dibalInterfaceOut = MZDibalInterfaceOut.getRecord(model.getCtx(), I_M_Product.Table_ID, product.get_ID(),
                                                        priceList.getAD_Org_ID(), model.get_TrxName());
            if ((dibalInterfaceOut != null) && (dibalInterfaceOut.get_ID() > 0)){
                // Proceso segun marca que ya tenía este producto antes de su actualización.
                // Si marca anterior es CREATE
                if (dibalInterfaceOut.getCRUDType().equalsIgnoreCase(X_Z_DibalInterfaceOut.CRUDTYPE_CREATE)){
                    // No hago nada y respeto primer marca
                    return mensaje;
                }
                else if (dibalInterfaceOut.getCRUDType().equalsIgnoreCase(X_Z_DibalInterfaceOut.CRUDTYPE_DELETE)){
                    // Si marca anterior es DELETEAR, es porque el producto se inactivo anteriormente.
                    // Si este producto sigue estando inactivo
                    if (!model.isActive()){
                        // No hago nada y respeto primer marca.
                        return mensaje;
                    }
                }
            }

            // Si no tengo marca de update, la creo ahora.
            if ((dibalInterfaceOut == null) || (dibalInterfaceOut.get_ID() <= 0)) {
                // No existe aun marca de UPDATE sobre este producto, la creo ahora.
                dibalInterfaceOut = new MZDibalInterfaceOut(model.getCtx(), 0, model.get_TrxName());
                dibalInterfaceOut.setCRUDType(X_Z_DibalInterfaceOut.CRUDTYPE_UPDATE);
                dibalInterfaceOut.setAD_Table_ID(I_M_Product.Table_ID);
                dibalInterfaceOut.setRecord_ID(product.get_ID());
                dibalInterfaceOut.setAD_OrgTrx_ID(priceList.getAD_Org_ID());
                dibalInterfaceOut.setSeqNo(30);
            }
            dibalInterfaceOut.saveEx();
        }

        return mensaje;
    }


}
