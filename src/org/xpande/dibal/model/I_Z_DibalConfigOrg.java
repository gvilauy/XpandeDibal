/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.										*
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/
package org.xpande.dibal.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for Z_DibalConfigOrg
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_DibalConfigOrg 
{

    /** TableName=Z_DibalConfigOrg */
    public static final String Table_Name = "Z_DibalConfigOrg";

    /** AD_Table_ID=1000129 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID(int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID(int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Dibal_CodRapido */
    public static final String COLUMNNAME_Dibal_CodRapido = "Dibal_CodRapido";

	/** Set Dibal_CodRapido.
	  * Codigo para CodRapido de interface Dibal
	  */
	public void setDibal_CodRapido(String Dibal_CodRapido);

	/** Get Dibal_CodRapido.
	  * Codigo para CodRapido de interface Dibal
	  */
	public String getDibal_CodRapido();

    /** Column name Dibal_Familia */
    public static final String COLUMNNAME_Dibal_Familia = "Dibal_Familia";

	/** Set Dibal_Familia.
	  * Codigo de familia para interface Dibal
	  */
	public void setDibal_Familia(String Dibal_Familia);

	/** Get Dibal_Familia.
	  * Codigo de familia para interface Dibal
	  */
	public String getDibal_Familia();

    /** Column name Dibal_Seccion */
    public static final String COLUMNNAME_Dibal_Seccion = "Dibal_Seccion";

	/** Set Dibal_Seccion.
	  * Codigo de seccion para interface Dibal
	  */
	public void setDibal_Seccion(String Dibal_Seccion);

	/** Get Dibal_Seccion.
	  * Codigo de seccion para interface Dibal
	  */
	public String getDibal_Seccion();

    /** Column name Dibal_Subfamilia */
    public static final String COLUMNNAME_Dibal_Subfamilia = "Dibal_Subfamilia";

	/** Set Dibal_Subfamilia.
	  * Codigo de subfamilia para interface Dibal
	  */
	public void setDibal_Subfamilia(String Dibal_Subfamilia);

	/** Get Dibal_Subfamilia.
	  * Codigo de subfamilia para interface Dibal
	  */
	public String getDibal_Subfamilia();

    /** Column name Dibal_Tienda */
    public static final String COLUMNNAME_Dibal_Tienda = "Dibal_Tienda";

	/** Set Dibal_Tienda.
	  * Valor de Tienda para Dibal
	  */
	public void setDibal_Tienda(String Dibal_Tienda);

	/** Get Dibal_Tienda.
	  * Valor de Tienda para Dibal
	  */
	public String getDibal_Tienda();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive(boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name Z_DibalConfig_ID */
    public static final String COLUMNNAME_Z_DibalConfig_ID = "Z_DibalConfig_ID";

	/** Set Z_DibalConfig ID	  */
	public void setZ_DibalConfig_ID(int Z_DibalConfig_ID);

	/** Get Z_DibalConfig ID	  */
	public int getZ_DibalConfig_ID();

	public I_Z_DibalConfig getZ_DibalConfig() throws RuntimeException;

    /** Column name Z_DibalConfigOrg_ID */
    public static final String COLUMNNAME_Z_DibalConfigOrg_ID = "Z_DibalConfigOrg_ID";

	/** Set Z_DibalConfigOrg ID	  */
	public void setZ_DibalConfigOrg_ID(int Z_DibalConfigOrg_ID);

	/** Get Z_DibalConfigOrg ID	  */
	public int getZ_DibalConfigOrg_ID();
}
