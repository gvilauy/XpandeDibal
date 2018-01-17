package org.xpande.dibal.model;

import org.adempiere.exceptions.AdempiereException;
import org.apache.commons.lang.math.NumberUtils;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.core.utils.PriceListUtils;
import org.xpande.retail.model.MZProductoFamilia;
import org.xpande.retail.model.MZProductoRubro;
import org.xpande.retail.model.MZProductoSubfamilia;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Modelo para interface de datos con sistema de Balanzas DIBAL.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/21/17.
 */
public class MZDibalInterfaceOut extends X_Z_DibalInterfaceOut {

    public MZDibalInterfaceOut(Properties ctx, int Z_DibalInterfaceOut_ID, String trxName) {
        super(ctx, Z_DibalInterfaceOut_ID, trxName);
    }

    public MZDibalInterfaceOut(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /***
     * Obtiene y retorna modelo según parametros recibidos
     * Xpande. Created by Gabriel Vila on 8/21/17.
     * @param ctx
     * @param adTableID
     * @param recordID
     * @param trxName
     * @return
     */
    public static MZDibalInterfaceOut getRecord(Properties ctx, int adTableID, int recordID, String trxName){

        String whereClause = X_Z_DibalInterfaceOut.COLUMNNAME_AD_Table_ID + " =" + adTableID +
                " AND " + X_Z_DibalInterfaceOut.COLUMNNAME_Record_ID + " =" + recordID +
                " AND " + X_Z_DibalInterfaceOut.COLUMNNAME_IsExecuted + " ='N'";

        MZDibalInterfaceOut model = new Query(ctx, I_Z_DibalInterfaceOut.Table_Name, whereClause, trxName).first();

        return model;

    }


    /***
     * Obtiene y retorna lineas para archivos de interface de salida con información de producto, a partir de la información de este modelo.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @param configOrg
     * @return
     */
    public List<String> getLineasArchivoProducto(MZDibalConfigOrg configOrg) {

        List<String> lineas = new ArrayList<String>();

        String lineaArchivo = "";

        String tienda = "", seccion = "", familia = "", subfamilia = "";
        String accion = "", codRapido = "", diasVencimiento = "000";
        String codigoProducto = "", nombreProducto = "", tipoIVA = "0";
        String precioVenta = "", codPlu = "2", tagFormat = "21";

        String formatoPrecio = String.format("%%0%dd", 10);
        String formatoCodProd = String.format("%%0%dd", 6);

        try{

            if (this.getAD_Table_ID() != I_M_Product.Table_ID){
                return lineas;
            }

            int adOrgID = configOrg.getAD_OrgTrx_ID();

            MProduct product = new MProduct(getCtx(), this.getRecord_ID(), get_TrxName());
            MPriceList priceList = null;
            if (this.getM_PriceList_ID() > 0){
                priceList = (MPriceList)this.getM_PriceList();
            }
            else{
                priceList = PriceListUtils.getPriceListByOrg(getCtx(), this.getAD_Client_ID(), adOrgID, 142, true, get_TrxName());
                if ((priceList == null) || (priceList.get_ID() <= 0)){
                    priceList = PriceListUtils.getPriceListByOrg(getCtx(), this.getAD_Client_ID(), adOrgID, 100, true, get_TrxName());
                }
            }

            // Tienda. Desde configuración de organizacion para dibal
            // Me aseguro de tener este dato
            if ((configOrg.getDibal_Tienda() == null) || (configOrg.getDibal_Tienda().trim().equalsIgnoreCase(""))){
                throw new AdempiereException("Falta indicar Tienda para la organización seleccionada para este proceso");
            }
            tienda = configOrg.getDibal_Tienda().trim();

            // Seccion. Atributo codigo balanza del Rubro del producto. Si el producto no tiene Rubro, se considera el valor de la configuracion de la organizacion.
            if (product.get_ValueAsInt("Z_ProductoRubro_ID") > 0){
                MZProductoRubro productoRubro = new MZProductoRubro(getCtx(), product.get_ValueAsInt("Z_ProductoRubro_ID"), null);
                if ((productoRubro.getCodigoBalanza() != null) && (!productoRubro.getCodigoBalanza().trim().equalsIgnoreCase(""))){
                    seccion = productoRubro.getCodigoBalanza().trim();
                }
            }
            if (seccion.equalsIgnoreCase("")){
                // Tomo seccion desde configuracion de organizacion
                seccion = configOrg.getDibal_Seccion();
                if ((seccion == null) || (seccion.trim().equalsIgnoreCase(""))){
                    throw new AdempiereException("Falta indicar Sección en el producto o en la configuración para la organización seleccionada para este proceso");
                }
            }

            // Familia. Atributo codigo balanza de la Familia del producto. Si el producto no tiene Familia, se considera el valor de la configuracion de la organizacion.
            if (product.get_ValueAsInt("Z_ProductoFamilia_ID") > 0){
                MZProductoFamilia productoFamilia = new MZProductoFamilia(getCtx(), product.get_ValueAsInt("Z_ProductoFamilia_ID"), null);
                if ((productoFamilia.getCodigoBalanza() != null) && (!productoFamilia.getCodigoBalanza().trim().equalsIgnoreCase(""))){
                    familia = productoFamilia.getCodigoBalanza().trim();
                }
            }
            if (familia.equalsIgnoreCase("")){
                // Tomo Familia desde configuracion de organizacion
                familia = configOrg.getDibal_Familia();
                if ((familia == null) || (familia.trim().equalsIgnoreCase(""))){
                    throw new AdempiereException("Falta indicar Familia en el producto o en la configuración para la organización seleccionada para este proceso");
                }
            }

            // Subfamilia. Atributo codigo balanza de la Subfamilia del producto. Si el producto no tiene Subfamilia, se considera el valor de la configuracion de la organizacion.
            if (product.get_ValueAsInt("Z_ProductoSubfamilia_ID") > 0){
                MZProductoSubfamilia productoSubfamilia = new MZProductoSubfamilia(getCtx(), product.get_ValueAsInt("Z_ProductoSubfamilia_ID"), null);
                if ((productoSubfamilia.getCodigoBalanza() != null) && (!productoSubfamilia.getCodigoBalanza().trim().equalsIgnoreCase(""))){
                    subfamilia = productoSubfamilia.getCodigoBalanza().trim();
                }
            }
            if (subfamilia.equalsIgnoreCase("")){
                // Tomo Subfamilia desde configuracion de organizacion
                subfamilia = configOrg.getDibal_Subfamilia();
                if ((subfamilia == null) || (subfamilia.trim().equalsIgnoreCase(""))){
                    throw new AdempiereException("Falta indicar Subfamilia en el producto o en la configuración para la organización seleccionada para este proceso");
                }
            }

            // CodRapido. Del producto.
            if (product.get_Value("CodigoBalanza") != null){
                String codBalanza = ((String) product.get_Value("CodigoBalanza")).trim();
                if (codBalanza.length() > 3){
                    throw new AdempiereException("Código de Balanza parametrizado en el producto, debe tener como máximo 3 caracteres.");
                }
                if (codBalanza.length() == 1){
                    codBalanza = "00" + codBalanza;
                }
                else if (codBalanza.length() == 2){
                    codBalanza = "0" + codBalanza;
                }
                codRapido = codBalanza;
            }
            if (codRapido.equalsIgnoreCase("")){
                // Tomo codrapido desde la configuracion de la organizacion
                codRapido = configOrg.getDibal_CodRapido();
                if ((codRapido == null) || (codRapido.trim().equalsIgnoreCase(""))){
                    throw new AdempiereException("Falta indicar CodRapido en el producto o en la configuración para la organización seleccionada para este proceso");
                }
            }

            // Dias vencimiento. Del producto.
            if (product.get_ValueAsInt("DueDays") > 0){
                diasVencimiento = String.valueOf(product.get_ValueAsInt("DueDays"));
                if (diasVencimiento.length() == 2){
                    diasVencimiento = "0" + diasVencimiento;
                }
                else if (diasVencimiento.length() == 1){
                    diasVencimiento = "00" + diasVencimiento;
                }
            }

            // Codigo de producto. Solo numeros hasta el : 999999 (6 digitos)
            if ((!NumberUtils.isNumber(product.getValue())) || (product.getValue().length() > 4)){
                throw new AdempiereException("Código interno del Producto debe ser númerico y no mayor a 9999 (4 dígitos) ");
            }
            codigoProducto = product.getValue().trim();
            codigoProducto = String.format(formatoCodProd, Integer.parseInt(codigoProducto));

            // Nombre corto del producto.
            nombreProducto = String.format("%1$-30s", product.getDescription().trim());
            if (nombreProducto.length() > 30){
                nombreProducto = nombreProducto.substring(0, 30);
            }

            // Precio de venta
            MPriceListVersion priceListVersion = priceList.getPriceListVersion(null);
            MProductPrice productPrice = MProductPrice.get(getCtx(), priceListVersion.get_ID(), product.get_ID(), get_TrxName());

            if (productPrice == null){
                throw new AdempiereException("No se obtuvo precio de venta para el producto con ID : " + product.get_ID());
            }

            BigDecimal priceSO = productPrice.getPriceList();
            priceSO = priceSO.setScale(2, RoundingMode.HALF_UP);

            precioVenta = priceSO.toString();
            precioVenta = precioVenta.replace(".", "");
            precioVenta = String.format(formatoPrecio, Integer.parseInt(precioVenta));
            if(precioVenta.length() > 10){
                throw new AdempiereException("Cantidad de dígitos del precio de venta del producto : " + codigoProducto + " excede el tope permitido");
            }

            // Unidad de medida
            MUOM unidadMedida = (MUOM) product.getC_UOM();
            if (unidadMedida.getUOMSymbol().equalsIgnoreCase("Kg")){
                codPlu = "1";
            }

            // Accion según tipo operacion de la marca
            if ((this.getCRUDType().equalsIgnoreCase(X_Z_DibalInterfaceOut.CRUDTYPE_CREATE))
                    || (this.getCRUDType().equalsIgnoreCase(X_Z_DibalInterfaceOut.CRUDTYPE_UPDATE))){

                accion = "M";
            }
            else if (this.getCRUDType().equalsIgnoreCase(X_Z_DibalInterfaceOut.CRUDTYPE_DELETE)){

                accion = "B";
            }

            // Salto de linea de Windows requerido por Dibal.
            lineaArchivo = tienda + seccion + codigoProducto + familia + subfamilia + nombreProducto + precioVenta +
                    tipoIVA + accion + codRapido + codPlu + tagFormat + diasVencimiento + "\r\n";

            lineas.add(lineaArchivo);

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return lineas;

    }
}
