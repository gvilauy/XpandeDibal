package org.xpande.dibal.utils;

import org.adempiere.exceptions.AdempiereException;
import org.apache.commons.lang.math.NumberUtils;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.TimeUtil;
import org.xpande.comercial.model.MZProdSalesOffer;
import org.xpande.core.utils.FileUtils;
import org.xpande.core.utils.PriceListUtils;
import org.xpande.dibal.model.*;
import org.xpande.retail.model.MZProductoFamilia;
import org.xpande.retail.model.MZProductoRubro;
import org.xpande.retail.model.MZProductoSubfamilia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Clase para el proceso de interface de salida de datos desde el sistema hacia balanzas Dibal.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/22/17.
 */
public class ProcesadorInterfaceOut {

    private Properties ctx = null;
    private String trxName = null;

    // Configurador de Dibal para organización a procesar.
    private MZDibalConfigOrg dibalConfigOrg = null;

    // Archivo
    private File fileBatch = null;

    private String fechaHoy = null;

    private int contadorLinBatch = 0;


    /***
     * Constructor
     * @param ctx
     * @param trxName
     */
    public ProcesadorInterfaceOut(Properties ctx, String trxName) {
        this.ctx = ctx;
        this.trxName = trxName;
    }

    /***
     * Metodo que ejecuta interface de salida de datos desde el sistema hacia Dibal.
     * Se genera un archivo de texto con todos los cambios para la organización recibida.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @param adOrgID
     * @return
     */
    public String executeInterfaceOut(int adOrgID, boolean esInterfaceMaestro){

        String message = null;

        try{

            if (adOrgID <= 0){
                return "Debe indicar una Organización para este proceso";
            }

            // Obtengo configurador general y único de dibal
            MZDibalConfig dibalConfig = MZDibalConfig.getDefault(ctx, trxName);
            if ((dibalConfig == null) || (dibalConfig.get_ID() <= 0)){
                return "Falta configurar Dibal para este proceso.";
            }

            // Configuracion para organización del proceso
            this.dibalConfigOrg = dibalConfig.getOrgConfig(adOrgID);
            if ((this.dibalConfigOrg == null) || (this.dibalConfigOrg.get_ID() <= 0)){
                return "Falta configurar esta organización para este proceso en Dibal.";
            }

            // Creación de archivos de interface
            this.createFiles();

            // Proceso lineas de interface de salida correspondiente a productos
            // Si no es interface maestro
            if (!esInterfaceMaestro){
                message = this.executeInterfaceOutProducts();
                if (message != null) return message;
            }
            else{
                message = this.executeInterfaceMaestroProducts();
                if (message != null) return message;
            }

            // Copiar archivos creados en path destino de dibal
            String pathArchivosDestino = this.dibalConfigOrg.getRutaInterfaceOut() + File.separator;

            // Si tengo lineas en archivos batch
            if (this.contadorLinBatch > 0){
                // Copio archivo batch a path destino
                File fileBatchDest = new File( pathArchivosDestino + this.dibalConfigOrg.getArchivoBatch());
                FileUtils.copyFile(this.fileBatch, fileBatchDest);
            }


        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }


    /***
     * Genera lineas en el archivo de interface, según marcas actuales de cambios en productos.
     * Considera precio de la organización recibida.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @return
     */
    private String executeInterfaceOutProducts() {

        String message = null;
        BufferedWriter bufferedWriterBatch = null;

        try{

            FileWriter fileWriterBatch = new FileWriter(this.fileBatch, true);
            bufferedWriterBatch = new BufferedWriter(fileWriterBatch);

            // Obtengo y recorro lineas de interface aun no ejecutadas para productos
            List<MZDibalInterfaceOut> interfaceOuts = this.getLinesProdsNotExecuted();
            for (MZDibalInterfaceOut interfaceOut: interfaceOuts){

                List<String> lineasArchivo = interfaceOut.getLineasArchivoProducto(this.dibalConfigOrg);
                for (String lineaArchivo: lineasArchivo){

                    bufferedWriterBatch.append(lineaArchivo);
                    bufferedWriterBatch.newLine();

                    this.contadorLinBatch++;
                }

                if (lineasArchivo.size() > 0){
                    // Marco linea como ejecutada
                    interfaceOut.setIsExecuted(true);
                    interfaceOut.setDateExecuted(new Timestamp(System.currentTimeMillis()));
                    interfaceOut.saveEx();
                }
            }

            if (bufferedWriterBatch != null){
                bufferedWriterBatch.close();
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            if (bufferedWriterBatch != null){
                try {
                    bufferedWriterBatch.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return message;
    }


    /***
     * Genera lineas en el archivo de interface Maestro para todos los productos de balanza.
     * Considera precio de la organización recibida.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @return
     */
    private String executeInterfaceMaestroProducts() {

        String message = null;
        BufferedWriter bufferedWriterBatch = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            FileWriter fileWriterBatch = new FileWriter(this.fileBatch, true);
            bufferedWriterBatch = new BufferedWriter(fileWriterBatch);


            sql = " select m_product_id " +
                    " from m_product  " +
                    " where isactive ='Y' " +
                    " and issold ='Y' " +
                    " and EsProductoBalanza ='Y' ";

        	pstmt = DB.prepareStatement(sql, null);
        	rs = pstmt.executeQuery();

        	while(rs.next()){

                MProduct product = new MProduct(this.ctx, rs.getInt("m_product_id"), null);

                List<String> lineasArchivo = this.getLineasArchivoProducto(product);
                for (String lineaArchivo: lineasArchivo){

                    bufferedWriterBatch.append(lineaArchivo);
                    bufferedWriterBatch.newLine();

                    this.contadorLinBatch++;
                }
            }

            if (bufferedWriterBatch != null){
                bufferedWriterBatch.close();
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
        	rs = null; pstmt = null;

            if (bufferedWriterBatch != null){
                try {
                    bufferedWriterBatch.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return message;
    }


    /***
     * Obtiene y retorna lista de marcas de interface aún no ejecutadas.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @return
     */
    private List<MZDibalInterfaceOut> getLinesProdsNotExecuted() {

        String whereClause = X_Z_DibalInterfaceOut.COLUMNNAME_IsExecuted + " ='N' " +
                " AND " + X_Z_DibalInterfaceOut.COLUMNNAME_AD_Table_ID + " =" + I_M_Product.Table_ID +
                " AND " + X_Z_DibalInterfaceOut.COLUMNNAME_AD_OrgTrx_ID + " =" + this.dibalConfigOrg.getAD_OrgTrx_ID();

        List<MZDibalInterfaceOut> lines = new Query(ctx, I_Z_DibalInterfaceOut.Table_Name, whereClause, trxName).setOrderBy(" SeqNo, Created  ").list();

        return lines;

    }

    /***
     * Creación de archivos de interface hacia Dibal.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     */
    private void createFiles() {
        try{

            String[] hora = (new Timestamp(System.currentTimeMillis()).toString().split(":"));
            String fecha =hora[0].replace("-", "").replace(" ", "_") + hora[1];

            this.fechaHoy = fecha;

            String pathArchivos = this.dibalConfigOrg.getRutaInterfaceOutHist() + File.separator + this.fechaHoy;

            fileBatch = new File(pathArchivos + this.dibalConfigOrg.getArchivoBatch());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

    /***
     * Obtiene y retorna lineas para archivos de interface de salida con información del producto recibido..
     * Xpande. Created by Gabriel Vila on 10/12/18.
     * @param product
     * @return
     */
    public List<String> getLineasArchivoProducto(MProduct product) {

        List<String> lineas = new ArrayList<String>();

        String lineaArchivo = "";

        String tienda = "", seccion = "", familia = "", subfamilia = "";
        String accion = "", codRapido = "", diasVencimiento = "000";
        String codigoProducto = "", nombreProducto = "", tipoIVA = "0";
        String precioVenta = "", codPlu = "2", tagFormat = "21";

        String formatoPrecio = String.format("%%0%dd", 10);
        String formatoCodProd = String.format("%%0%dd", 6);

        try{

            MPriceList priceList = PriceListUtils.getPriceListByOrg(this.ctx, this.dibalConfigOrg.getAD_Client_ID(), this.dibalConfigOrg.getAD_OrgTrx_ID(),
                                                            142, true, null, null);
            if ((priceList == null) || (priceList.get_ID() <= 0)){
                priceList = PriceListUtils.getPriceListByOrg(this.ctx, this.dibalConfigOrg.getAD_Client_ID(), this.dibalConfigOrg.getAD_OrgTrx_ID(),
                                                            100, true, null, null);
            }

            // Tienda. Desde configuración de organizacion para dibal
            // Me aseguro de tener este dato
            if ((this.dibalConfigOrg.getDibal_Tienda() == null) || (this.dibalConfigOrg.getDibal_Tienda().trim().equalsIgnoreCase(""))){
                throw new AdempiereException("Falta indicar Tienda para la organización seleccionada para este proceso");
            }
            tienda = this.dibalConfigOrg.getDibal_Tienda().trim();

            // Seccion. Atributo codigo balanza del Rubro del producto. Si el producto no tiene Rubro, se considera el valor de la configuracion de la organizacion.
            if (product.get_ValueAsInt("Z_ProductoRubro_ID") > 0){
                MZProductoRubro productoRubro = new MZProductoRubro(this.ctx, product.get_ValueAsInt("Z_ProductoRubro_ID"), null);
                if ((productoRubro.getCodigoBalanza() != null) && (!productoRubro.getCodigoBalanza().trim().equalsIgnoreCase(""))){
                    seccion = productoRubro.getCodigoBalanza().trim();
                }
            }
            if (seccion.equalsIgnoreCase("")){
                // Tomo seccion desde configuracion de organizacion
                seccion = this.dibalConfigOrg.getDibal_Seccion();
                if ((seccion == null) || (seccion.trim().equalsIgnoreCase(""))){
                    throw new AdempiereException("Falta indicar Sección en el producto o en la configuración para la organización seleccionada para este proceso");
                }
            }

            // Familia. Atributo codigo balanza de la Familia del producto. Si el producto no tiene Familia, se considera el valor de la configuracion de la organizacion.
            if (product.get_ValueAsInt("Z_ProductoFamilia_ID") > 0){
                MZProductoFamilia productoFamilia = new MZProductoFamilia(this.ctx, product.get_ValueAsInt("Z_ProductoFamilia_ID"), null);
                if ((productoFamilia.getCodigoBalanza() != null) && (!productoFamilia.getCodigoBalanza().trim().equalsIgnoreCase(""))){
                    familia = productoFamilia.getCodigoBalanza().trim();
                }
            }
            if (familia.equalsIgnoreCase("")){
                // Tomo Familia desde configuracion de organizacion
                familia = this.dibalConfigOrg.getDibal_Familia();
                if ((familia == null) || (familia.trim().equalsIgnoreCase(""))){
                    throw new AdempiereException("Falta indicar Familia en el producto o en la configuración para la organización seleccionada para este proceso");
                }
            }

            // Subfamilia. Atributo codigo balanza de la Subfamilia del producto. Si el producto no tiene Subfamilia, se considera el valor de la configuracion de la organizacion.
            if (product.get_ValueAsInt("Z_ProductoSubfamilia_ID") > 0){
                MZProductoSubfamilia productoSubfamilia = new MZProductoSubfamilia(this.ctx, product.get_ValueAsInt("Z_ProductoSubfamilia_ID"), null);
                if ((productoSubfamilia.getCodigoBalanza() != null) && (!productoSubfamilia.getCodigoBalanza().trim().equalsIgnoreCase(""))){
                    subfamilia = productoSubfamilia.getCodigoBalanza().trim();
                }
            }
            if (subfamilia.equalsIgnoreCase("")){
                // Tomo Subfamilia desde configuracion de organizacion
                subfamilia = this.dibalConfigOrg.getDibal_Subfamilia();
                if ((subfamilia == null) || (subfamilia.trim().equalsIgnoreCase(""))){
                    throw new AdempiereException("Falta indicar Subfamilia en el producto o en la configuración para la organización seleccionada para este proceso");
                }
            }

            // CodRapido. Del producto.
            if (product.get_Value("CodigoBalanza") != null){
                String codBalanza = ((String) product.get_Value("CodigoBalanza")).trim();
                if (codBalanza.length() > 3){
                    throw new AdempiereException("Código de Balanza parametrizado en el producto: " + product.getValue() + ", debe tener como máximo 3 caracteres.");
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
                codRapido = this.dibalConfigOrg.getDibal_CodRapido();
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

            // Codigo de producto. Solo numeros hasta el : 9999 (4 digitos)
            if ((!NumberUtils.isNumber(product.getValue())) || (product.getValue().length() > 4)){
                throw new AdempiereException("Código interno del Producto debe ser númerico y no mayor a 9999 : " + product.getValue() + " - " + product.getName());
            }
            codigoProducto = product.getValue().trim();
            codigoProducto = String.format(formatoCodProd, Integer.parseInt(codigoProducto));

            // Nombre corto del producto.
            nombreProducto = String.format("%1$-30s", product.getDescription().trim());
            if (nombreProducto.length() > 30){
                nombreProducto = nombreProducto.substring(0, 30);
            }

            // Si tengo oferta de venta vigente para este producto y organización me aseguro de setear este precio de oferta
            // De esta manera la marca se crea pero el precio es el de oferta
            BigDecimal salesOfferPrice = null;
            Timestamp today = TimeUtil.trunc(new Timestamp(System.currentTimeMillis()), TimeUtil.TRUNC_DAY);
            String sql = " select max(z_prodsalesoffer_id) as z_prodsalesoffer_id " +
                    " from z_prodsalesoffer " +
                    " where ad_org_id =" + this.dibalConfigOrg.getAD_OrgTrx_ID() +
                    " and m_product_id =" + product.get_ID() +
                    " and enddate >= '" + today + "' ";
            int offerID = DB.getSQLValueEx(trxName, sql);
            if (offerID > 0) {
                MZProdSalesOffer prodSalesOffer = new MZProdSalesOffer(ctx, offerID, trxName);
                salesOfferPrice = prodSalesOffer.getPrice();
            }

            // Precio de venta. Admito en el maestro precio cero por ahora, para poder detectarlos.
            BigDecimal priceSO = Env.ZERO;
            MPriceListVersion priceListVersion = priceList.getPriceListVersion(null);
            MProductPrice productPrice = MProductPrice.get(this.ctx, priceListVersion.get_ID(), product.get_ID(), null);
            if (productPrice != null){
                priceSO = productPrice.getPriceList();
                if ((salesOfferPrice != null) && (salesOfferPrice.compareTo(Env.ZERO) > 0)){
                    priceSO = salesOfferPrice;
                }
            }

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

            accion = "M";

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
