package org.xpande.dibal.utils;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_M_Product;
import org.compiere.model.Query;
import org.xpande.core.utils.FileUtils;
import org.xpande.dibal.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
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

    // Configurador de Dibal
    private MZDibalConfig dibalConfig = null;

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
    public String executeInterfaceOut(int adOrgID){

        String message = null;

        try{

            if (adOrgID <= 0){
                return "Debe indicar una Organización para este proceso";
            }

            // Obtengo configurador de dibal
            this.dibalConfig = MZDibalConfig.getDefaultByOrg(ctx, adOrgID, trxName);

            // Creación de archivos de interface
            this.createFiles();

            // Proceso lineas de interface de salida correspondiente a productos
            message = this.executeInterfaceOutProducts(adOrgID);
            if (message != null) return message;

            // Copiar archivos creados en path destino de dibal
            String pathArchivosDestino = this.dibalConfig.getRutaInterfaceOut() + File.separator;

            // Si tengo lineas en archivos batch
            if (this.contadorLinBatch > 0){
                // Copio archivo batch a path destino
                File fileBatchDest = new File( pathArchivosDestino + this.dibalConfig.getArchivoBatch());
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
     * @param adOrgID
     * @return
     */
    private String executeInterfaceOutProducts(int adOrgID) {

        String message = null;
        BufferedWriter bufferedWriterBatch = null;

        try{

            // Configuracion para organización del proceso
            MZDibalConfigOrg configOrg = this.dibalConfig.getOrgConfig(adOrgID);

            FileWriter fileWriterBatch = new FileWriter(this.fileBatch, true);
            bufferedWriterBatch = new BufferedWriter(fileWriterBatch);

            // Obtengo y recorro lineas de interface aun no ejecutadas para productos
            List<MZDibalInterfaceOut> interfaceOuts = this.getLinesProdsNotExecuted();
            for (MZDibalInterfaceOut interfaceOut: interfaceOuts){

                List<String> lineasArchivo = interfaceOut.getLineasArchivoProducto(configOrg);
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
     * Obtiene y retorna lista de marcas de interface aún no ejecutadas.
     * Xpande. Created by Gabriel Vila on 8/22/17.
     * @return
     */
    private List<MZDibalInterfaceOut> getLinesProdsNotExecuted() {

        String whereClause = X_Z_DibalInterfaceOut.COLUMNNAME_IsExecuted + " ='N' " +
                " AND " + X_Z_DibalInterfaceOut.COLUMNNAME_AD_Table_ID + " =" + I_M_Product.Table_ID;

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

            String pathArchivos = dibalConfig.getRutaInterfaceOutHist() + File.separator + this.fechaHoy;

            fileBatch = new File(pathArchivos + dibalConfig.getArchivoBatch());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

}
